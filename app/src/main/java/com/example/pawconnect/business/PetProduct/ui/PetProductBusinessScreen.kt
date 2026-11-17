
package com.example.pawconnect.business.PetProduct.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pawconnect.business.PetProduct.data.BusinessProduct
import com.example.pawconnect.business.PetProduct.logic.PetProductBusinessViewModel

@Composable
fun PetProductBusinessScreen(
    viewModel: PetProductBusinessViewModel = viewModel(),
    onBottomNavigationClicked: (Int) -> Unit = {},
    onViewOrderClick: () -> Unit = {}
) {

    val products by viewModel.products.collectAsState()

    // Add / Edit screen
    var showAddEditScreen by remember {
        mutableStateOf(false)
    }

    var editingProduct by remember {
        mutableStateOf<BusinessProduct?>(null)
    }

    // Delete confirmation
    var productPendingDelete by remember {
        mutableStateOf<BusinessProduct?>(null)
    }

    // Restock
    var selectedProductId by remember {
        mutableStateOf<String?>(null)
    }

    var restockAmount by remember {
        mutableStateOf("10")
    }

    val selectedProduct = products.find {
        it.id == selectedProductId
    }

    // -----------------------------------------
    // ADD / EDIT PRODUCT SCREEN
    // -----------------------------------------

    if (showAddEditScreen) {

        AddEditProductScreen(
            existingProduct = editingProduct,

            onBackClick = {
                showAddEditScreen = false
                editingProduct = null
            },

            onSaveProduct = { product ->

                if (editingProduct == null) {

                    // ADD NEW PRODUCT
                    viewModel.addProduct(product)

                } else {

                    // UPDATE EXISTING PRODUCT
                    viewModel.updateProduct(product)
                }

                showAddEditScreen = false
                editingProduct = null
            }
        )

        return
    }

    // -----------------------------------------
    // MAIN BUSINESS SCREEN
    // -----------------------------------------

    Scaffold { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),

            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {

            // -----------------------------------------
            // PAGE HEADING
            // -----------------------------------------

            Column {

                Text(
                    text = "Stock & Shopping Orders",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Manage your products and inventory",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }

            // -----------------------------------------
            // LOW STOCK INFORMATION
            // -----------------------------------------

            Card(
                modifier = Modifier.fillMaxWidth(),

                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFF3E0)
                ),

                shape = RoundedCornerShape(16.dp)
            ) {

                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = Icons.Default.WarningAmber,
                        contentDescription = null,
                        tint = Color(0xFFE58A00),
                        modifier = Modifier.size(30.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {

                        Text(
                            text = "${viewModel.lowStockCount} Low Stock Products",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF8A5200)
                        )

                        Text(
                            text = "Check your inventory and restock when needed.",
                            color = Color(0xFF8A5200),
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // -----------------------------------------
            // INVENTORY HEADING
            // -----------------------------------------

            Row(
                modifier = Modifier.fillMaxWidth(),

                horizontalArrangement = Arrangement.SpaceBetween,

                verticalAlignment = Alignment.CenterVertically
            ) {

                Column {

                    Text(
                        text = "Product Inventory",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "${products.size} products · ${products.sumOf { it.stockQuantity }} units",
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                }

                // ADD PRODUCT BUTTON

                Button(
                    onClick = {
                        editingProduct = null
                        showAddEditScreen = true
                    }
                ) {

                    Text("+ Add")
                }
            }

            // -----------------------------------------
            // PRODUCT LISTINGS
            // -----------------------------------------

            if (products.isEmpty()) {

                Text(
                    text = "No products added yet.",
                    color = Color.Gray
                )

            } else {

                products.forEach { product ->

                    // ALL ACTIONS ARE INSIDE THIS CARD

                    ProductListingCard(
                        product = product,

                        // EDIT PRODUCT
                        onEditClick = {

                            editingProduct = product
                            showAddEditScreen = true
                        },

                        // DELETE PRODUCT
                        onDeleteClick = {

                            productPendingDelete = product
                        },

                        // RESTOCK PRODUCT
                        onRestockClick = {

                            selectedProductId = product.id
                            restockAmount = "10"
                        }
                    )

                    // IMPORTANT:
                    // No additional Edit / Delete row here.
                }
            }

            // -----------------------------------------
            // RECENT ORDER SECTION
            // -----------------------------------------

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {

                Column(
                    modifier = Modifier.padding(16.dp),

                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Text(
                        text = "Recent Shopping Order",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Order #PC2381",
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = "Aisha Rahman · 16 Nov 2026",
                        color = Color.Gray,
                        fontSize = 13.sp
                    )

                    Text(
                        text = "1 × Daily Balance + 1 × Play Ball Duo"
                    )

                    Text(
                        text = "RM72.00",
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Delivery delayed",
                        color = Color(0xFFE58A00),
                        fontSize = 13.sp
                    )

                    TextButton(
                        onClick = onViewOrderClick
                    ) {

                        Text("View Order")
                    }
                }
            }
        }
    }

    // -----------------------------------------
    // DELETE CONFIRMATION DIALOG
    // -----------------------------------------

    productPendingDelete?.let { product ->

        AlertDialog(

            onDismissRequest = {
                productPendingDelete = null
            },

            title = {
                Text("Delete Product?")
            },

            text = {
                Text(
                    "Are you sure you want to delete ${product.name}? This action cannot be undone."
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        viewModel.deleteProduct(product.id)

                        productPendingDelete = null
                    }
                ) {

                    Text(
                        text = "Delete",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        productPendingDelete = null
                    }
                ) {

                    Text("Cancel")
                }
            }
        )
    }

    // -----------------------------------------
    // RESTOCK DIALOG
    // -----------------------------------------

    if (selectedProduct != null) {

        AlertDialog(

            onDismissRequest = {

                selectedProductId = null
                restockAmount = "10"
            },

            title = {

                Text("Restock ${selectedProduct.name}")
            },

            text = {

                Column {

                    Text(
                        text = "Current stock: ${selectedProduct.stockQuantity} units"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(

                        value = restockAmount,

                        onValueChange = { value ->

                            if (value.isEmpty() || value.all { it.isDigit() }) {

                                restockAmount = value
                            }
                        },

                        label = {
                            Text("Quantity")
                        },

                        placeholder = {
                            Text("Enter additional stock")
                        },

                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),

                        singleLine = true
                    )
                }
            },

            confirmButton = {

                TextButton(

                    onClick = {

                        val amount = restockAmount.toIntOrNull()

                        if (amount != null && amount > 0) {

                            viewModel.restockProduct(
                                productId = selectedProduct.id,
                                additionalStock = amount
                            )

                            selectedProductId = null
                            restockAmount = "10"
                        }
                    },

                    enabled = (restockAmount.toIntOrNull() ?: 0) > 0
                ) {

                    Text("Restock")
                }
            },

            dismissButton = {

                TextButton(

                    onClick = {

                        selectedProductId = null
                        restockAmount = "10"
                    }
                ) {

                    Text("Cancel")
                }
            }
        )
    }
}
