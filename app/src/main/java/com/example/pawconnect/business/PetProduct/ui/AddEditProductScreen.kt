
package com.example.pawconnect.business.PetProduct.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    // Product form states
    var productName by remember(existingProduct?.id) {
        mutableStateOf(existingProduct?.name ?: "")
    }

    var category by remember(existingProduct?.id) {
        mutableStateOf(existingProduct?.category ?: "")
    }

    var price by remember(existingProduct?.id) {
        mutableStateOf(
            existingProduct?.price?.toString() ?: ""
        )
    }

    var stockQuantity by remember(existingProduct?.id) {
        mutableStateOf(
            existingProduct?.stockQuantity?.toString() ?: ""
        )
    }

    var minimumStock by remember(existingProduct?.id) {
        mutableStateOf(
            existingProduct?.minStockLevel?.toString() ?: "5"
        )
    }

    var selectedImageUri by remember(existingProduct?.id) {
        mutableStateOf(
            existingProduct?.imageUri
        )
    }

    var validationMessage by remember {
        mutableStateOf("")
    }

    // Android photo picker
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->

        if (uri != null) {
            selectedImageUri = uri.toString()
        }
    }

    Scaffold(

        containerColor = MaterialTheme.colorScheme.background,

        topBar = {

            TopAppBar(
                title = {
                    Text(
                        text = if (isEditing) {
                            "Edit Product"
                        } else {
                            "Add New Product"
                        },
                        fontWeight = FontWeight.SemiBold
                    )
                },

                navigationIcon = {

                    IconButton(
                        onClick = onBackClick
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },

                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }

    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),

            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Page description
            Column {

                Text(
                    text = if (isEditing) {
                        "Update your product information."
                    } else {
                        "Enter the details of your new product."
                    },

                    style = MaterialTheme.typography.bodyMedium,

                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Product photo section
            Text(
                text = "Product Photo",
                style = MaterialTheme.typography.titleMedium
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable {
                        imagePicker.launch("image/*")
                    },

                contentAlignment = Alignment.Center
            ) {

                when {
                    selectedImageUri != null -> {

                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = "Selected product image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    existingProduct?.imageRes != null -> {

                        Image(
                            painter = painterResource(
                                existingProduct.imageRes
                            ),
                            contentDescription = "Product image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    else -> {

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Icon(
                                imageVector = Icons.Default.AddAPhoto,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(38.dp)
                            )

                            Spacer(
                                modifier = Modifier.height(8.dp)
                            )

                            Text(
                                text = "Tap to upload product photo",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Text(
                                text = "Choose an image from your gallery",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Change image button
            OutlinedButton(
                onClick = {
                    imagePicker.launch("image/*")
                },

                modifier = Modifier.fillMaxWidth()
            ) {

                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = null
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = if (selectedImageUri == null) {
                        "Choose Photo"
                    } else {
                        "Change Photo"
                    }
                )
            }

            // Product name
            OutlinedTextField(
                value = productName,

                onValueChange = {
                    productName = it
                    validationMessage = ""
                },

                label = {
                    Text("Product Name")
                },

                placeholder = {
                    Text("e.g. Premium Dog Food")
                },

                modifier = Modifier.fillMaxWidth(),

                singleLine = true,

                shape = RoundedCornerShape(12.dp)
            )

            // Product category
            OutlinedTextField(
                value = category,

                onValueChange = {
                    category = it
                    validationMessage = ""
                },

                label = {
                    Text("Product Category")
                },

                placeholder = {
                    Text("e.g. Food, Toys, Grooming")
                },

                modifier = Modifier.fillMaxWidth(),

                singleLine = true,

                shape = RoundedCornerShape(12.dp)
            )

            // Price
            OutlinedTextField(
                value = price,

                onValueChange = {
                    price = it
                    validationMessage = ""
                },

                label = {
                    Text("Product Price (RM)")
                },

                placeholder = {
                    Text("e.g. 25.90")
                },

                modifier = Modifier.fillMaxWidth(),

                singleLine = true,

                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                ),

                shape = RoundedCornerShape(12.dp),

                prefix = {
                    Text("RM ")
                }
            )

            // Stock quantity
            OutlinedTextField(
                value = stockQuantity,

                onValueChange = { value ->

                    if (value.isEmpty() || value.all { it.isDigit() }) {
                        stockQuantity = value
                        validationMessage = ""
                    }
                },

                label = {
                    Text("Stock Quantity")
                },

                placeholder = {
                    Text("e.g. 20")
                },

                modifier = Modifier.fillMaxWidth(),

                singleLine = true,

                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),

                shape = RoundedCornerShape(12.dp)
            )

            // Minimum stock level
            OutlinedTextField(
                value = minimumStock,

                onValueChange = { value ->

                    if (value.isEmpty() || value.all { it.isDigit() }) {
                        minimumStock = value
                        validationMessage = ""
                    }
                },

                label = {
                    Text("Minimum Stock Level")
                },

                placeholder = {
                    Text("Default: 5")
                },

                supportingText = {
                    Text(
                        "Low-stock warning appears when quantity reaches this value."
                    )
                },

                modifier = Modifier.fillMaxWidth(),

                singleLine = true,

                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),

                shape = RoundedCornerShape(12.dp)
            )

            // Stock status preview
            val quantityValue = stockQuantity.toIntOrNull() ?: 0
            val minimumValue = minimumStock.toIntOrNull() ?: 5

            val previewStatus = when {
                quantityValue <= 0 -> "SOLD OUT"
                quantityValue <= minimumValue -> "LOW STOCK"
                else -> "ACTIVE"
            }

            val previewColor = when (previewStatus) {
                "SOLD OUT" -> MaterialTheme.colorScheme.error
                "LOW STOCK" -> MaterialTheme.colorScheme.tertiary
                else -> MaterialTheme.colorScheme.primary
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),

                    verticalAlignment = Alignment.CenterVertically,

                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Text(
                        text = "Product Stock Status",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        text = previewStatus,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = previewColor
                    )
                }
            }

            // Validation message
            if (validationMessage.isNotEmpty()) {

                Text(
                    text = validationMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Save button
            Button(
                onClick = {

                    val priceValue = price.toDoubleOrNull()
                    val stockValue = stockQuantity.toIntOrNull()
                    val minimumValue = minimumStock.toIntOrNull()

                    validationMessage = when {

                        productName.isBlank() ->
                            "Please enter a product name."

                        category.isBlank() ->
                            "Please enter a product category."

                        priceValue == null || priceValue <= 0 ->
                            "Please enter a valid price greater than zero."

                        stockValue == null || stockValue < 0 ->
                            "Please enter a valid stock quantity."

                        minimumValue == null || minimumValue < 0 ->
                            "Please enter a valid minimum stock level."

                        else -> ""
                    }

                    if (validationMessage.isEmpty()) {

                        val newProduct = BusinessProduct(

                            id = existingProduct?.id
                                ?: UUID.randomUUID().toString(),

                            name = productName.trim(),

                            category = category.trim(),

                            price = priceValue!!,

                            originalPrice = existingProduct?.originalPrice
                                ?: 0.0,

                            discountPercent = existingProduct?.discountPercent
                                ?: 0,

                            stockQuantity = stockValue!!,

                            minStockLevel = minimumValue!!,

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
                    text = if (isEditing) {
                        "Save Changes"
                    } else {
                        "Add Product"
                    },

                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Cancel button
            OutlinedButton(
                onClick = onBackClick,

                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),

                shape = RoundedCornerShape(12.dp)
            ) {

                Text("Cancel")
            }
        }
    }
}
