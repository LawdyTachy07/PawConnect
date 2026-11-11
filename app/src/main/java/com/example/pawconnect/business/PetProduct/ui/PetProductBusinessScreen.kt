
package com.example.pawconnect.business.PetProduct.ui

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pawconnect.business.PetProduct.data.BusinessProduct
import com.example.pawconnect.business.PetProduct.logic.PetProductBusinessViewModel
import com.example.pawconnect.ui.components.PawConnectTopAppBar
import com.example.pawconnect.ui.components.PawConnectBottomBar
import com.example.pawconnect.ui.theme.PawConnectTheme

@Composable
fun PetProductBusinessScreen(
    viewModel: PetProductBusinessViewModel,
    onNotificationClicked: () -> Unit = {},
    onProfileClicked: () -> Unit = {},
    onBottomNavigationClicked: (Int) -> Unit = {}
) {

    val products by viewModel.products.collectAsState()

    var selectedTab by remember {
        mutableIntStateOf(1)
    }

    var selectedBottomItem by remember {
        mutableIntStateOf(1)
    }

    // Control Add/Edit Product pages
    var showProductForm by remember {
        mutableStateOf(false)
    }

    var editingProduct by remember {
        mutableStateOf<BusinessProduct?>(null)
    }

    // Control delete confirmation
    var productToDelete by remember {
        mutableStateOf<BusinessProduct?>(null)
    }

    // ---------------------------------
    // ADD / EDIT PRODUCT SCREEN
    // ---------------------------------

    if (showProductForm) {

        AddEditProductScreen(

            existingProduct = editingProduct,

            onBackClick = {
                showProductForm = false
                editingProduct = null
            },

            onSaveProduct = { product ->

                if (editingProduct == null) {

                    // CREATE
                    viewModel.addProduct(product)

                } else {

                    // UPDATE
                    viewModel.updateProduct(product)
                }

                showProductForm = false
                editingProduct = null
            }
        )

    } else {

        // ---------------------------------
        // MAIN SCAFFOLD
        // ---------------------------------

        Scaffold(

            containerColor = MaterialTheme.colorScheme.background,

            // FIXED TOP APP BAR
            topBar = {

                PawConnectTopAppBar(
                    subtitle = "Happy Tails Pet Centre",
                    initials = "HT",
                    onProfileClick = onProfileClicked
                )
            },

            // FIXED BOTTOM NAVIGATION
            bottomBar = {

                PawConnectBottomBar(
                    isBusiness = true,
                    selectedIndex = selectedBottomItem,
                    onItemSelected = { index ->

                        selectedBottomItem = index
                        onBottomNavigationClicked(index)
                    }
                )
            }

        ) { innerPadding ->

            // Responsive layout for portrait and landscape
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
            ) {

                // Adjust horizontal padding for wider screens
                val horizontalPadding = if (maxWidth > 600.dp) {
                    32.dp
                } else {
                    20.dp
                }

                // Limit content width on large landscape screens
                val contentWidth = if (maxWidth > 760.dp) {
                    760.dp
                } else {
                    maxWidth
                }

                // ---------------------------------
                // MAIN SCROLLABLE CONTENT
                // ---------------------------------

                LazyColumn(

                    modifier = Modifier
                        .width(contentWidth)
                        .fillMaxHeight()
                        .align(Alignment.TopCenter),

                    contentPadding = PaddingValues(
                        start = horizontalPadding,
                        end = horizontalPadding,
                        top = 16.dp,
                        bottom = 24.dp
                    ),

                    verticalArrangement = Arrangement.spacedBy(16.dp)

                ) {

                    // ---------------------------------
                    // 1. WORKSPACE HEADER
                    // ---------------------------------

                    item {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {

                                Text(
                                    text = "BUSINESS WORKSPACE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.5.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Spacer(
                                    modifier = Modifier.height(5.dp)
                                )

                                Text(
                                    text = "Listings",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = MaterialTheme.colorScheme.onBackground
                                )

                                Text(
                                    text = "Manage everything in one simple place.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Notification button
                            Surface(
                                onClick = onNotificationClicked,
                                shape = RoundedCornerShape(14.dp),
                                color = MaterialTheme.colorScheme.surface,
                                shadowElevation = 1.dp,
                                modifier = Modifier.size(44.dp)
                            ) {

                                Box(
                                    contentAlignment = Alignment.Center
                                ) {

                                    Icon(
                                        imageVector = Icons.Default.Notifications,
                                        contentDescription = "Notifications",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }

                    // ---------------------------------
                    // 2. CATEGORY TABS
                    // ---------------------------------

                    item {

                        val tabNames = listOf(
                            "Pets",
                            "Products",
                            "Services"
                        )

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {

                            Row(
                                modifier = Modifier.padding(4.dp)
                            ) {

                                tabNames.forEachIndexed { index, title ->

                                    val isSelected = selectedTab == index

                                    Surface(
                                        onClick = {
                                            selectedTab = index
                                        },

                                        modifier = Modifier.weight(1f),

                                        shape = RoundedCornerShape(11.dp),

                                        color = if (isSelected) {
                                            MaterialTheme.colorScheme.surface
                                        } else {
                                            MaterialTheme.colorScheme.surfaceVariant
                                        }
                                    ) {

                                        Box(
                                            modifier = Modifier.padding(
                                                vertical = 11.dp
                                            ),

                                            contentAlignment = Alignment.Center
                                        ) {

                                            Text(
                                                text = title,

                                                fontSize = 12.sp,

                                                fontWeight = if (isSelected) {
                                                    FontWeight.SemiBold
                                                } else {
                                                    FontWeight.Normal
                                                },

                                                color = if (isSelected) {
                                                    MaterialTheme.colorScheme.primary
                                                } else {
                                                    MaterialTheme.colorScheme.onSurfaceVariant
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // ---------------------------------
                    // 3. CATALOGUE TITLE + ADD BUTTON
                    // ---------------------------------

                    item {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Column {

                                Text(
                                    text = "YOUR CATALOGUE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.5.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Spacer(
                                    modifier = Modifier.height(4.dp)
                                )

                                Text(
                                    text = when (selectedTab) {
                                        0 -> "Pet listings"
                                        1 -> "Product listings"
                                        else -> "Services listings"
                                    },

                                    style = MaterialTheme.typography.titleLarge,

                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }

                            // Add Product button
                            if (selectedTab == 1) {

                                Button(
                                    onClick = {

                                        editingProduct = null
                                        showProductForm = true
                                    },

                                    shape = RoundedCornerShape(12.dp),

                                    contentPadding = PaddingValues(
                                        horizontal = 13.dp,
                                        vertical = 9.dp
                                    )
                                ) {

                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )

                                    Spacer(
                                        modifier = Modifier.width(4.dp)
                                    )

                                    Text(
                                        text = "Add New",
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }

                    // ---------------------------------
                    // 4. PRODUCT LISTINGS
                    // ---------------------------------

                    when (selectedTab) {

                        1 -> {

                            if (products.isEmpty()) {

                                item {

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(220.dp),

                                        contentAlignment = Alignment.Center
                                    ) {

                                        Text(
                                            text = "No products available yet.\nAdd your first product!",

                                            color = MaterialTheme.colorScheme.onSurfaceVariant,

                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }

                            } else {

                                items(
                                    items = products,
                                    key = { it.id }
                                ) { product ->

                                    ProductListingCard(

                                        product = product,

                                        onEditClick = {

                                            editingProduct = product
                                            showProductForm = true
                                        },

                                        onDeleteClick = {

                                            productToDelete = product
                                        }
                                    )
                                }
                            }
                        }

                        0 -> {

                            item {

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(220.dp),

                                    contentAlignment = Alignment.Center
                                ) {

                                    Text(
                                        text = "Your pet listings will appear here.",

                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        2 -> {

                            item {

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(220.dp),

                                    contentAlignment = Alignment.Center
                                ) {

                                    Text(
                                        text = "Your service listings will appear here.",

                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // ---------------------------------
    // DELETE CONFIRMATION DIALOG
    // ---------------------------------

    productToDelete?.let { product ->

        AlertDialog(

            onDismissRequest = {
                productToDelete = null
            },

            title = {
                Text("Delete Product?")
            },

            text = {
                Text(
                    "Are you sure you want to delete \"${product.name}\"? This action cannot be undone."
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        viewModel.deleteProduct(product.id)

                        productToDelete = null
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
                        productToDelete = null
                    }
                ) {

                    Text("Cancel")
                }
            }
        )
    }
}

// ---------------------------------
// LIGHT MODE PREVIEW
// ---------------------------------

@Preview(
    name = "Product Listing - Light Mode",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun ProductListingLightPreview() {

    PawConnectTheme(darkTheme = false) {

        PetProductBusinessScreen(
            viewModel = PetProductBusinessViewModel()
        )
    }
}

// ---------------------------------
// DARK MODE PREVIEW
// ---------------------------------

@Preview(
    name = "Product Listing - Dark Mode",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    showSystemUi = true
)
@Composable
fun ProductListingDarkPreview() {

    PawConnectTheme(darkTheme = true) {

        PetProductBusinessScreen(
            viewModel = PetProductBusinessViewModel()
        )
    }
}



