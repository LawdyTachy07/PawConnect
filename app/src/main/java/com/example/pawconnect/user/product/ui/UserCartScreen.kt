package com.example.pawconnect.user.product.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.pawconnect.user.product.logic.UserProductViewModel

@Composable
fun UserCartScreen(
    shopViewModel: UserProductViewModel,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onProceedToCheckout: () -> Unit
) {
    val products by shopViewModel.products.collectAsState()
    val cart by shopViewModel.cart.collectAsState()

    // Only include products that are currently in the cart
    val cartProducts = products.filter { product ->
        (cart[product.id] ?: 0) > 0
    }

    // Calculate subtotal
    val subtotal = cartProducts.sumOf { product ->
        product.price * (cart[product.id] ?: 0)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {

        // Back button and heading
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back to shop"
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "My Cart",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (cartProducts.isEmpty()) {

            // Empty cart
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🛒",
                        fontSize = 48.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Your cart is empty",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Add some products for your furry friend!"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onBackClick
                    ) {
                        Text("Continue Shopping")
                    }
                }
            }

        } else {

            // Cart product list
            cartProducts.forEach { product ->

                val quantity = cart[product.id] ?: 0
                val lineTotal = product.price * quantity

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        // Product image
                        if (!product.imageUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = product.imageUrl,
                                contentDescription = product.name,
                                modifier = Modifier.size(85.dp)
                            )
                        } else {
                            Text(
                                text = "🐾",
                                fontSize = 36.sp,
                                modifier = Modifier.size(85.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Product information
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = product.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "RM %.2f".format(product.price),
                                color = MaterialTheme.colorScheme.primary
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Subtotal: RM %.2f".format(lineTotal),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Quantity controls
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = {
                                        shopViewModel.removeFromCart(product.id)
                                    },
                                    enabled = quantity > 0,
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Remove,
                                        contentDescription = "Decrease quantity"
                                    )
                                }

                                Text(
                                    text = quantity.toString(),
                                    modifier = Modifier.padding(horizontal = 8.dp),
                                    fontWeight = FontWeight.Bold
                                )

                                IconButton(
                                    onClick = {
                                        shopViewModel.addToCart(product.id)
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Increase quantity"
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Order summary
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Order Summary",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Subtotal")

                        Text("RM %.2f".format(subtotal))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    HorizontalDivider()

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Total",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )

                        Text(
                            text = "RM %.2f".format(subtotal),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onProceedToCheckout,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Proceed to Checkout")
                    }
                }
            }
        }
    }
}