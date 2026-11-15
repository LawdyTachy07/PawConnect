
package com.example.pawconnect.user.product.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pawconnect.ui.components.PawConnectTopAppBar
import com.example.pawconnect.ui.components.PawConnectBottomBar
import com.example.pawconnect.ui.theme.PawConnectTheme
import com.example.pawconnect.user.product.logic.UserProductViewModel

@Composable
fun UserShopScreen(
    shopViewModel: UserProductViewModel,
    onProfileClick: () -> Unit = {},
    onBottomItemSelected: (Int) -> Unit = {},
    showAppChrome: Boolean = true
) {
    var showCart by remember { mutableStateOf(false) }

    val cart by shopViewModel.cart.collectAsState()

    Scaffold(
        bottomBar = {
            if (showAppChrome) {
                PawConnectBottomBar(
                    isBusiness = false,
                    selectedIndex = 1,
                    onItemSelected = onBottomItemSelected
                )
            }
        }
    ) { innerPadding ->

        if (showCart) {
            UserCartScreen(
                shopViewModel = shopViewModel,
                modifier = Modifier.padding(innerPadding),
                onBackClick = { showCart = false }
            )
        } else {
            UserShopContent(
                shopViewModel = shopViewModel,
                cartItemCount = cart.values.sum(),
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                onCartClick = { showCart = true },
                showAppChrome = showAppChrome,
                onProfileClick = onProfileClick
            )
        }
    }
}
@Composable
private fun UserShopContent(
    shopViewModel: UserProductViewModel,
    cartItemCount: Int,
    modifier: Modifier = Modifier,
    onProfileClick: () -> Unit,
    showAppChrome: Boolean,
    onCartClick: () -> Unit
) {
    val products by shopViewModel.products.collectAsState()
    val cart by shopViewModel.cart.collectAsState()

    var searchText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val categories = listOf(
        "All",
        "Food",
        "Toys",
        "Accessories",
        "Health"
    )

    // Search and category filtering
    val filteredProducts = products.filter { product ->

        val matchesSearch =
            product.name.contains(searchText, ignoreCase = true) ||
                    product.description.contains(searchText, ignoreCase = true)

        // Health also displays products stored under the Care category
        val matchesCategory = when (selectedCategory) {
            "All" -> true

            "Health" ->
                product.category.equals("Health", ignoreCase = true) ||
                        product.category.equals("Care", ignoreCase = true)

            else -> product.category.equals(
                selectedCategory,
                ignoreCase = true
            )
        }

        matchesSearch && matchesCategory
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Top bar scrolls together with the shop content
        if (showAppChrome) {
            PawConnectTopAppBar(
                subtitle = "Pet Shop",
                initials = "U",
                onProfileClick = onProfileClick
            )
        }

        // All shop content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 0.dp,
                    bottom = 16.dp
                )
        ) {

            // 2. Weekly Pick Banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFE8D6)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    Text(
                        text = "🐾 Weekly Pick",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF8B4513)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Discover something special for your furry friend!",
                        color = Color(0xFF5D4037)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Search Bar and Cart Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = {
                        Text(
                            text = "Search products...",
                            fontSize = 12.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    },
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )

                Surface(
                    onClick = onCartClick,
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFFE8D6)
                ) {
                    Row(
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 16.dp
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Open cart",
                            tint = Color(0xFF8B4513)
                        )

                        Spacer(modifier = Modifier.width(5.dp))

                        Text(
                            text = "Cart ($cartItemCount)",
                            color = Color(0xFF8B4513),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 4. Categories Heading
            Text(
                text = "Categories",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 5. Horizontally Scrollable Category Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = {
                            selectedCategory = category
                        },
                        label = {
                            Text(
                                text = category,
                                maxLines = 1
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 6. Popular Products Heading
            Text(
                text = "Popular Now",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 7. Display Products
            if (filteredProducts.isEmpty()) {

                Text(
                    text = "No products found.",
                    modifier = Modifier.padding(vertical = 24.dp),
                    color = Color.Gray
                )

            } else {

                // Display products in two columns
                filteredProducts.chunked(2).forEach { productRow ->

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        productRow.forEach { product ->

                            UserProductCard(
                                product = product,
                                quantityInCart = cart[product.id] ?: 0,
                                onAddToCart = {
                                    shopViewModel.addToCart(product.id)
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Keep the last product aligned to the left
                        if (productRow.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 8. Delivery Banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE8F5E9)
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = "🚚 Fast and reliable delivery for your pets!",
                    modifier = Modifier.padding(16.dp),
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

// Light Mode Preview
@Preview(
    showBackground = true,
    showSystemUi = true,
    name = "PawConnect Shop - Light Mode"
)
@Composable
fun UserShopScreenLightPreview() {
    PawConnectTheme(darkTheme = false) {
        val previewViewModel = remember {
            UserProductViewModel()
        }

        UserShopScreen(
            shopViewModel = previewViewModel,
            showAppChrome = true
        )
    }
}

// Dark Mode Preview
@Preview(
    showBackground = true,
    showSystemUi = true,
    name = "PawConnect Shop - Dark Mode"
)
@Composable
fun UserShopScreenDarkPreview() {
    PawConnectTheme(darkTheme = true) {
        val previewViewModel = remember {
            UserProductViewModel()
        }

        UserShopScreen(
            shopViewModel = previewViewModel,
            showAppChrome = true
        )
    }
}
