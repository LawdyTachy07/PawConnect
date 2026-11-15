package com.example.pawconnect.user.product.logic

import androidx.lifecycle.ViewModel
import com.example.pawconnect.user.product.data.UserPetProduct
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class UserProductViewModel : ViewModel() {

    private val _products = MutableStateFlow(
        listOf(
            UserPetProduct(
                id = "food-1",
                name = "Daily Balance",
                category = "Food",
                description = "Adult dog food · 2 kg",
                price = 48.00,
                badge = "Popular"
            ),
            UserPetProduct(
                id = "toy-1",
                name = "Play Ball Duo",
                category = "Toys",
                description = "Rubber toys · Set of 2",
                price = 18.00
            ),
            UserPetProduct(
                id = "food-2",
                name = "Feline Feast",
                category = "Food",
                description = "Adult cat food · 1.5 kg",
                price = 42.00
            ),
            UserPetProduct(
                id = "toy-2",
                name = "Twist & Tug",
                category = "Toys",
                description = "Cotton rope · 30 cm",
                price = 15.00
            ),
            UserPetProduct(
                id = "care-1",
                name = "Gentle Paw Shampoo",
                category = "Care",
                description = "Sensitive skin · 250 ml",
                price = 22.50
            ),
            UserPetProduct(
                id = "care-2",
                name = "Daily Fish Oil",
                category = "Care",
                description = "Omega 3 supplement · 100 ml",
                price = 36.00
            )
        )
    )
    val products: StateFlow<List<UserPetProduct>> = _products.asStateFlow()

    private val _cart = MutableStateFlow<Map<String, Int>>(emptyMap())
    val cart: StateFlow<Map<String, Int>> = _cart.asStateFlow()

    fun addToCart(productId: String) {
        _cart.update { current ->
            current + (productId to ((current[productId] ?: 0) + 1))
        }
    }

    fun removeFromCart(productId: String) {
        _cart.update { current ->
            val quantity = current[productId] ?: return@update current
            if (quantity <= 1) current - productId
            else current + (productId to quantity - 1)
        }
    }

    fun cartQuantity(productId: String): Int = _cart.value[productId] ?: 0

    fun totalCartItems(): Int = _cart.value.values.sum()
}