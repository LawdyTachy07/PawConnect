
package com.example.pawconnect.business.PetProduct.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import com.example.pawconnect.business.PetProduct.data.BusinessProduct
import com.example.pawconnect.business.PetProduct.logic.ProductValidator
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditProductScreen(
    existingProduct: BusinessProduct? = null,
    otherProductNames: List<String> = emptyList(),
    onBackClick: () -> Unit = {},
    onSaveProduct: (BusinessProduct) -> Unit
) {
    val isEditing = existingProduct != null

    var productName by remember(existingProduct?.id) {
        mutableStateOf(existingProduct?.name ?: "")
    }
    var category by remember(existingProduct?.id) {
        mutableStateOf(existingProduct?.category ?: "")
    }
    var subtitle by remember(existingProduct?.id) {
        mutableStateOf(existingProduct?.subtitle ?: "")
    }
    var price by remember(existingProduct?.id) {
        mutableStateOf(existingProduct?.price?.toString() ?: "")
    }
    var stockQuantity by remember(existingProduct?.id) {
        mutableStateOf(existingProduct?.stockQuantity?.toString() ?: "")
    }
    var minimumStock by remember(existingProduct?.id) {
        mutableStateOf(existingProduct?.minStockLevel?.toString() ?: "5")
    }

    var selectedImageUri by remember(existingProduct?.id) {
        mutableStateOf<String?>(existingProduct?.imageUri)
    }
    // Per-field error messages (null = no error)
    var attemptedSave by remember { mutableStateOf(false) }
    var photoError by remember { mutableStateOf<String?>(null) }
    var nameError by remember { mutableStateOf<String?>(null) }
    var categoryError by remember { mutableStateOf<String?>(null) }
    var descriptionError by remember { mutableStateOf<String?>(null) }
    var priceError by remember { mutableStateOf<String?>(null) }
    var stockError by remember { mutableStateOf<String?>(null) }
    var minimumStockError by remember { mutableStateOf<String?>(null) }

    val hasPhoto = selectedImageUri != null ||
            existingProduct?.imageUrl != null ||
            existingProduct?.imageRes != null

    fun validateAll(): Boolean {
        photoError = ProductValidator.validatePhoto(hasPhoto, isEditing)
        nameError = ProductValidator.validateName(productName, otherProductNames)
        categoryError = ProductValidator.validateCategory(category)
        descriptionError = ProductValidator.validateDescription(subtitle)
        priceError = ProductValidator.validatePrice(price)
        stockError = ProductValidator.validateStock(stockQuantity)
        minimumStockError = ProductValidator.validateMinimumStock(minimumStock)
        return listOf(
            photoError, nameError, categoryError, descriptionError,
            priceError, stockError, minimumStockError
        ).all { it == null }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri.toString()
            photoError = null
        }
    }

    var categoryExpanded by remember {
        mutableStateOf(false)
    }

    val productCategories = listOf(
        "Food",
        "Toys",
        "Accessories",
        "Health",
        "Other"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditing) "Edit Product" else "Add New Product",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = "Product Information",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            // Product image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (photoError != null) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.35f)
                        else Color(0xFFF3F3F3)
                    )
                    .border(
                        width = if (photoError != null) 1.5.dp else 0.dp,
                        color = if (photoError != null) MaterialTheme.colorScheme.error else Color.Transparent,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable {
                        imagePickerLauncher.launch("image/*")
                    },
                contentAlignment = Alignment.Center
            ) {
                when {
                    selectedImageUri != null -> {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = "Product image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    existingProduct?.imageUrl != null -> {
                        AsyncImage(
                            model = existingProduct.imageUrl,
                            contentDescription = "Product image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    existingProduct?.imageRes != null -> {
                        ImageResourcePlaceholder(
                            imageRes = existingProduct.imageRes
                        )
                    }

                    else -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddAPhoto,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Add Product Photo",
                                color = if (photoError != null) MaterialTheme.colorScheme.error else Color.Gray
                            )
                        }
                    }
                }
            }

            photoError?.let { message ->
                FieldErrorText(message)
            }

            OutlinedButton(
                onClick = { imagePickerLauncher.launch("image/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.AddAPhoto,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Change Photo")
            }

            // Product name
            OutlinedTextField(
                value = productName,
                onValueChange = {
                    productName = it
                    if (attemptedSave) nameError = ProductValidator.validateName(it, otherProductNames)
                },
                label = { Text("Product Name *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = nameError != null,
                supportingText = {
                    SupportingLine(
                        error = nameError,
                        hint = "${productName.trim().length}/${ProductValidator.MAX_NAME_LENGTH}"
                    )
                }
            )

            // Category
            // Product Category Dropdown
            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = {
                    categoryExpanded = !categoryExpanded
                },
                modifier = Modifier.fillMaxWidth()
            ) {

                OutlinedTextField(
                    value = category,
                    onValueChange = {},
                    readOnly = true,
                    label = {
                        Text("Product Category *")
                    },
                    isError = categoryError != null,
                    supportingText = {
                        SupportingLine(error = categoryError)
                    },
                    placeholder = {
                        Text("Select a category")
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = categoryExpanded
                        )
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = {
                        categoryExpanded = false
                    }
                ) {

                    productCategories.forEach { item ->

                        DropdownMenuItem(
                            text = {
                                Text(item)
                            },
                            onClick = {
                                category = item
                                categoryExpanded = false
                                categoryError = null
                            }
                        )
                    }
                }
            }

            // Product subtitle / description
            OutlinedTextField(
                value = subtitle,
                onValueChange = {
                    subtitle = it
                    if (attemptedSave) descriptionError = ProductValidator.validateDescription(it)
                },
                label = { Text("Product Description *") },
                placeholder = { Text("e.g. Adult dog food · 2 kg") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = descriptionError != null,
                supportingText = {
                    SupportingLine(
                        error = descriptionError,
                        hint = "${subtitle.trim().length}/${ProductValidator.MAX_DESCRIPTION_LENGTH}"
                    )
                }
            )

            // Price
            OutlinedTextField(
                value = price,
                onValueChange = {
                    price = it
                    if (attemptedSave) priceError = ProductValidator.validatePrice(it)
                },
                label = { Text("Price (RM) *") },
                placeholder = { Text("e.g. 25.90") },
                isError = priceError != null,
                supportingText = { SupportingLine(error = priceError) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                ),
                singleLine = true
            )

            // Stock quantity
            OutlinedTextField(
                value = stockQuantity,
                onValueChange = {
                    stockQuantity = it
                    if (attemptedSave) stockError = ProductValidator.validateStock(it)
                },
                label = { Text("Stock Quantity *") },
                isError = stockError != null,
                supportingText = {
                    val stock = stockQuantity.trim().toIntOrNull()
                    val min = minimumStock.trim().toIntOrNull()
                    SupportingLine(
                        error = stockError,
                        hint = if (stock != null && min != null && stock <= min) {
                            "At or below minimum level, so you'll get a low-stock alert."
                        } else ""
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                singleLine = true
            )

            // Minimum stock level
            OutlinedTextField(
                value = minimumStock,
                onValueChange = {
                    minimumStock = it
                    if (attemptedSave) minimumStockError = ProductValidator.validateMinimumStock(it)
                },
                label = { Text("Minimum Stock Level *") },
                isError = minimumStockError != null,
                supportingText = {
                    SupportingLine(
                        error = minimumStockError,
                        hint = "You'll be alerted when stock reaches this level."
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                singleLine = true
            )

            val errorCount = listOf(
                photoError, nameError, categoryError, descriptionError,
                priceError, stockError, minimumStockError
            ).count { it != null }

            if (attemptedSave && errorCount > 0) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.errorContainer
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (errorCount == 1) "Please fix 1 highlighted field before saving."
                                   else "Please fix $errorCount highlighted fields before saving.",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Button(
                onClick = {
                    attemptedSave = true
                    if (validateAll()) {
                        val newProduct = BusinessProduct(
                            id = existingProduct?.id ?: UUID.randomUUID().toString(),
                            name = productName.trim(),
                            category = category.trim(),
                            subtitle = subtitle.trim(),
                            price = price.trim().toDouble(),
                            originalPrice = existingProduct?.originalPrice ?: 0.0,
                            discountPercent = existingProduct?.discountPercent ?: 0,
                            stockQuantity = stockQuantity.trim().toInt(),
                            minStockLevel = minimumStock.trim().toInt(),
                            status = existingProduct?.status ?: "ACTIVE",
                            imageUrl = existingProduct?.imageUrl,
                            imageRes = if (selectedImageUri == null) {
                                existingProduct?.imageRes
                            } else {
                                null
                            },
                            imageUri = selectedImageUri
                        )

                        onSaveProduct(newProduct)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (isEditing) "Save Changes" else "Add Product",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            OutlinedButton(
                onClick = onBackClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Cancel")
            }
        }
    }
}

/** Red error text, or a grey hint when there is no error. */
@Composable
private fun SupportingLine(error: String?, hint: String = "") {
    when {
        error != null -> Text(text = error, color = MaterialTheme.colorScheme.error)
        hint.isNotEmpty() -> Text(text = hint, color = Color.Gray)
    }
}

@Composable
private fun FieldErrorText(message: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Default.ErrorOutline,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = message, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
    }
}

@Composable
private fun ImageResourcePlaceholder(imageRes: Int) {
    androidx.compose.foundation.Image(
        painter = painterResource(id = imageRes),
        contentDescription = "Product image",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
}
