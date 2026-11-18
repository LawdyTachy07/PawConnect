
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

    // Sample inventory data
    private val _products = MutableStateFlow(
        listOf(
            BusinessProduct(
                id = "1",
                name = "Daily Balance",
                category = "Food",
                subtitle = "Dog food · 2 kg",
                price = 48.00,
                stockQuantity = 3,
                minStockLevel = 5,
                imageRes = R.drawable.dog_food
            ),

            BusinessProduct(
                id = "2",
                name = "Play Ball Duo",
                category = "Toys",
                subtitle = "Rubber toys · Set of 2",
                price = 18.00,
                stockQuantity = 18,
                minStockLevel = 5
            ),

            BusinessProduct(
                id = "3",
                name = "Feline Feast",
                category = "Food",
                subtitle = "Cat food · 1.5 kg",
                price = 42.00,
                stockQuantity = 12,
                minStockLevel = 5
            ),

            BusinessProduct(
                id = "4",
                name = "Twist & Tug",
                category = "Toys",
                subtitle = "Cotton rope · 30 cm",
                price = 15.00,
                stockQuantity = 4,
                minStockLevel = 5
            )
        )
    )

    val products: StateFlow<List<BusinessProduct>> =
        _products.asStateFlow()

    // Inventory calculations
    val totalProducts: Int
        get() = _products.value.size

    val totalUnits: Int
        get() = _products.value.sumOf { it.stockQuantity }

    val lowStockProducts: List<BusinessProduct>
        get() = _products.value.filter {
            it.isLowStock
        }

    val lowStockCount: Int
        get() = lowStockProducts.size

    // Selected product for editing
    var selectedProductForEdit: BusinessProduct? by
    mutableStateOf(null)
        private set

    // CREATE: Add product
    fun addProduct(product: BusinessProduct) {
        _products.value = _products.value + product
    }

    // READ: Find product
    fun getProductById(productId: String): BusinessProduct? {
        return _products.value.find {
            it.id == productId
        }
    }

    // UPDATE: Edit product
    fun updateProduct(updatedProduct: BusinessProduct) {

        _products.value = _products.value.map { product ->
            if (product.id == updatedProduct.id) {
                updatedProduct
            } else {
                product
            }
        }

        selectedProductForEdit = null
    }

    // DELETE: Remove product
    fun deleteProduct(productId: String) {

        _products.value = _products.value.filter {
            it.id != productId
        }
    }

    // UPDATE: Restock product
    fun restockProduct(
        productId: String,
        additionalStock: Int
    ) {

        if (additionalStock <= 0) return

        _products.value = _products.value.map { product ->

            if (product.id == productId) {

                product.copy(
                    stockQuantity =
                        product.stockQuantity + additionalStock
                )

            } else {
                product
            }
        }
    }

    // Prepare product for editing
    fun selectProductForEdit(productId: String) {
        selectedProductForEdit = getProductById(productId)
    }

    // Clear selected product
    fun clearSelectedProduct() {
        selectedProductForEdit = null
    }
}
