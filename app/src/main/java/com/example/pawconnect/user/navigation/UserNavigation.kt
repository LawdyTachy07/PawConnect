package com.example.pawconnect.user.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.pawconnect.user.order.data.OrderItem
import com.example.pawconnect.user.order.logic.OrderViewModel
import com.example.pawconnect.user.order.ui.UserCheckoutScreen
import com.example.pawconnect.user.order.ui.UserOrdersScreen
import com.example.pawconnect.user.product.logic.UserProductViewModel
import com.example.pawconnect.user.product.ui.UserCartScreen

object UserRoutes {
    const val CART = "user_cart"
    const val CHECKOUT = "user_checkout"
    const val ORDERS = "user_orders"
}

fun NavGraphBuilder.userOrderNavGraph(
    navController: NavController,
    shopViewModel: UserProductViewModel,
    orderViewModel: OrderViewModel
) {
    // 1. Your Existing Cart Screen Route
    composable(UserRoutes.CART) {
        UserCartScreen(
            shopViewModel = shopViewModel,
            onBackClick = { navController.popBackStack() },
            onProceedToCheckout = {
                // Navigate to Checkout when the button is clicked!
                navController.navigate(UserRoutes.CHECKOUT)
            }
        )
    }

    // 2. Module 6: Checkout Screen Route
    composable(UserRoutes.CHECKOUT) {
        val products by shopViewModel.products.collectAsState()
        val cart by shopViewModel.cart.collectAsState()

        // Convert the active cart map into OrderItems for checkout summary
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
            onBackClick = { navController.popBackStack() },
            onClearCart = {
                // Clear cart items in your shopViewModel after placing the order
                shopViewModel.clearCart() // (or reset your cart state here)
            },
            onOrderPlaced = { _ ->
                // Navigate to My Orders and clear checkout from backstack
                navController.navigate(UserRoutes.ORDERS) {
                    popUpTo(UserRoutes.CART)
                }
            }
        )
    }

    // 3. Module 6: User Order History Screen Route
    composable(UserRoutes.ORDERS) {
        UserOrdersScreen(
            orderViewModel = orderViewModel,
            onBackClick = { navController.popBackStack() }
        )
    }
}