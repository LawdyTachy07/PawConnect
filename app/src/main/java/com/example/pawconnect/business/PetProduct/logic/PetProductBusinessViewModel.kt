package com.example.pawconnect.business.PetProduct.logic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.pawconnect.R
import com.example.pawconnect.business.PetProduct.data.BusinessProduct
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PetProductBusinessViewModel : ViewModel() {

    private val _products = MutableStateFlow<List<BusinessProduct>>(
        listOf(
            BusinessProduct(
                id = "1",
                name = "Premium Adult Dog Food 10kg",
                category = "Food",
                price = 45.99,
                originalPrice = 55.99,
                discountPercent = 18,
                stockQuantity = 4,
                minStockLevel = 5,
                status = "ACTIVE",
                imageRes = R.drawable.dog_food
            )
        )
    )
    val products: StateFlow<List<BusinessProduct>> = _products.asStateFlow()

    var selectedProductForEdit: BusinessProduct? by mutableStateOf(null)

    fun restockProduct(productId: String, additionalStock: Int = 10) {
        _products.value = _products.value.map { product ->
            if (product.id == productId) {
                product.copy(stockQuantity = product.stockQuantity + additionalStock)
            } else {
                product
            }
        }
    }

    fun deleteProduct(productId: String) {
        _products.value = _products.value.filter { it.id != productId }
    }
}