
package com.example.pawconnect.business.PetProduct.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowBack
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
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditProductScreen(
    existingProduct: BusinessProduct? = null,
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
    var validationMessage by remember { mutableStateOf("") }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri?.toString()
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
                    .background(Color(0xFFF3F3F3))
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
                                color = Color.Gray
                            )
                        }
                    }
                }
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
                onValueChange = { productName = it },
                label = { Text("Product Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
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
                        Text("Product Category")
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
                                validationMessage = ""
                            }
                        )
                    }
                }
            }

            // Product subtitle / description
            OutlinedTextField(
                value = subtitle,
                onValueChange = { subtitle = it },
                label = { Text("Product Description") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Price
            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Price (RM)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                ),
                singleLine = true
            )

            // Stock quantity
            OutlinedTextField(
                value = stockQuantity,
                onValueChange = { stockQuantity = it },
                label = { Text("Stock Quantity") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                singleLine = true
            )

            // Minimum stock level
            OutlinedTextField(
                value = minimumStock,
                onValueChange = { minimumStock = it },
                label = { Text("Minimum Stock Level") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                singleLine = true
            )

            if (validationMessage.isNotEmpty()) {
                Text(
                    text = validationMessage,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp
                )
            }

            Button(
                onClick = {
                    val parsedPrice = price.toDoubleOrNull()
                    val parsedStock = stockQuantity.toIntOrNull()
                    val parsedMinimumStock = minimumStock.toIntOrNull()

                    when {
                        productName.isBlank() -> {
                            validationMessage = "Please enter a product name."
                        }

                        category.isBlank() -> {
                            validationMessage = "Please enter a category."
                        }

                        parsedPrice == null || parsedPrice < 0 -> {
                            validationMessage = "Please enter a valid price."
                        }

                        parsedStock == null || parsedStock < 0 -> {
                            validationMessage = "Please enter a valid stock quantity."
                        }

                        parsedMinimumStock == null || parsedMinimumStock < 0 -> {
                            validationMessage = "Please enter a valid minimum stock level."
                        }

                        else -> {
                            val newProduct = BusinessProduct(
                                id = existingProduct?.id ?: UUID.randomUUID().toString(),
                                name = productName.trim(),
                                category = category.trim(),
                                subtitle = subtitle.trim(),
                                price = parsedPrice,
                                originalPrice = existingProduct?.originalPrice ?: 0.0,
                                discountPercent = existingProduct?.discountPercent ?: 0,
                                stockQuantity = parsedStock,
                                minStockLevel = parsedMinimumStock,
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

@Composable
private fun ImageResourcePlaceholder(imageRes: Int) {
    androidx.compose.foundation.Image(
        painter = painterResource(id = imageRes),
        contentDescription = "Product image",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
}
