
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
import java.util.UUID

class PetProductBusinessViewModel : ViewModel() {

    // Sample product data
    private val _products = MutableStateFlow(
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
                imageRes = R.drawable.dog_food
            ),

            BusinessProduct(
                id = "2",
                name = "Organic Cat Treats 200g",
                category = "Treats",
                price = 8.50,
                originalPrice = 10.00,
                discountPercent = 15,
                stockQuantity = 25,
                minStockLevel = 5,
                imageRes = R.drawable.organic_cat_treats
            ),

            BusinessProduct(
                id = "3",
                name = "Durable Chew Toy",
                category = "Toys",
                price = 12.99,
                originalPrice = 15.99,
                discountPercent = 18,
                stockQuantity = 0,
                minStockLevel = 5,
                imageRes = R.drawable.toy
            ),

            BusinessProduct(
                id = "4",
                name = "Ergonomic Pet Grooming Brush",
                category = "Grooming",
                price = 19.99,
                originalPrice = 24.99,
                discountPercent = 20,
                stockQuantity = 2,
                minStockLevel = 5
            ),

            BusinessProduct(
                id = "5",
                name = "Premium Cat Shampoo",
                category = "Grooming",
                price = 16.50,
                stockQuantity = 15,
                minStockLevel = 5
            ),

            BusinessProduct(
                id = "6",
                name = "Omega 3 Fish Oil",
                category = "Supplements",
                price = 29.90,
                stockQuantity = 0,
                minStockLevel = 5
            )
        )
    )

    val products: StateFlow<List<BusinessProduct>> =
        _products.asStateFlow()

    var selectedProductForEdit: BusinessProduct? by
    mutableStateOf(null)
        private set

    // CREATE: Add new product
    fun addProduct(product: BusinessProduct) {

        _products.value = _products.value + product
    }

    // READ: Find product by ID
    fun getProductById(productId: String): BusinessProduct? {

        return _products.value.find {
            it.id == productId
        }
    }

    // UPDATE: Edit existing product
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
                    stockQuantity = product.stockQuantity + additionalStock
                )

            } else {
                product
            }
        }
    }

    // Prepare selected product for editing
    fun selectProductForEdit(productId: String) {

        selectedProductForEdit = getProductById(productId)
    }

    fun clearSelectedProduct() {
        selectedProductForEdit = null
    }
}
