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
import com.example.pawconnect.user.order.data.OrderItem
import com.example.pawconnect.user.order.logic.OrderViewModel
import com.example.pawconnect.user.order.ui.UserCheckoutScreen
import com.example.pawconnect.user.order.ui.UserOrdersScreen
import com.example.pawconnect.user.product.logic.UserProductViewModel

@Composable
fun UserShopScreen(
    shopViewModel: UserProductViewModel,
    orderViewModel: OrderViewModel, // Pass your shared OrderViewModel here
    onProfileClick: () -> Unit = {},
    onBottomItemSelected: (Int) -> Unit = {},
    showAppChrome: Boolean = true
) {
    // Screen states: "shop", "cart", "checkout", or "orders"
    var currentScreen by remember { mutableStateOf("shop") }

    val cart by shopViewModel.cart.collectAsState()
    val products by shopViewModel.products.collectAsState()

    Scaffold(
        bottomBar = {
            if (showAppChrome && currentScreen == "shop") {
                PawConnectBottomBar(
                    isBusiness = false,
                    selectedIndex = 1,
                    onItemSelected = onBottomItemSelected
                )
            }
        }
    ) { innerPadding ->
        when (currentScreen) {
            "cart" -> {
                UserCartScreen(
                    shopViewModel = shopViewModel,
                    modifier = Modifier.padding(innerPadding),
                    onBackClick = { currentScreen = "shop" },
                    onProceedToCheckout = {
                        currentScreen = "checkout" // Opens Module 6 Checkout!
                    }
                )
            }
            "checkout" -> {
                // Convert current cart map to OrderItems for the checkout summary
                val cartItems = remember(products, cart) {
                    products.mapNotNull { product ->
                        val qty = cart[product.id] ?: 0
                        if (qty > 0) {
                            OrderItem(
                                productId = product.id,
                                productName = product.name,
                                price = product.price,
                                quantity = qty
                            )
                        } else null
                    }
                }

                UserCheckoutScreen(
                    cartItems = cartItems,
                    orderViewModel = orderViewModel,
                    onBackClick = { currentScreen = "cart" },
                    onClearCart = {
                        shopViewModel.clearCart()
                    },
                    onOrderPlaced = { _ ->
                        currentScreen = "orders" // Jumps straight to My Orders after placing!
                    }
                )
            }
            "orders" -> {
                UserOrdersScreen(
                    orderViewModel = orderViewModel,
                    onBackClick = { currentScreen = "shop" }
                )
            }
            else -> {
                UserShopContent(
                    shopViewModel = shopViewModel,
                    cartItemCount = cart.values.sum(),
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    onCartClick = { currentScreen = "cart" },
                    showAppChrome = showAppChrome,
                    onProfileClick = onProfileClick
                )
            }
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

    val categories = listOf("All", "Food", "Toys", "Accessories", "Health")

    val filteredProducts = products.filter { product ->
        val matchesSearch = product.name.contains(searchText, ignoreCase = true) ||
                product.description.contains(searchText, ignoreCase = true)

        val matchesCategory = when (selectedCategory) {
            "All" -> true
            "Health" -> product.category.equals("Health", ignoreCase = true) ||
                    product.category.equals("Care", ignoreCase = true)
            else -> product.category.equals(selectedCategory, ignoreCase = true)
        }
        matchesSearch && matchesCategory
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        if (showAppChrome) {
            PawConnectTopAppBar(
                subtitle = "Pet Shop",
                initials = "U",
                onProfileClick = onProfileClick
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Weekly Pick Banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE8D6)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(text = "🐾 Weekly Pick", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF8B4513))
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "Discover something special for your furry friend!", color = Color(0xFF5D4037))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Search & Cart Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Search products...", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )

                Surface(
                    onClick = onCartClick,
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFFE8D6)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color(0xFF8B4513))
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(text = "Cart ($cartItemCount)", color = Color(0xFF8B4513), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(text = "Categories", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = { Text(text = category, maxLines = 1) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(text = "Popular Now", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            if (filteredProducts.isEmpty()) {
                Text(text = "No products found.", modifier = Modifier.padding(vertical = 24.dp), color = Color.Gray)
            } else {
                filteredProducts.chunked(2).forEach { productRow ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        productRow.forEach { product ->
                            UserProductCard(
                                product = product,
                                quantityInCart = cart[product.id] ?: 0,
                                onAddToCart = { shopViewModel.addToCart(product.id) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (productRow.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}