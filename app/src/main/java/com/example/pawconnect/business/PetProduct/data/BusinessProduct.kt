
package com.example.pawconnect.business.PetProduct.data

data class BusinessProduct(
    val id: String,
    val name: String,
    val category: String,
    val price: Double,
    val originalPrice: Double = 0.0,
    val discountPercent: Int = 0,
    val stockQuantity: Int,
    val minStockLevel: Int = 5,
    val status: String = "ACTIVE",
    val imageUrl: String? = null,
    val imageRes: Int? = null,
    val imageUri: String? = null,
    val subtitle: String = ""
) {

    /**
     * Unique Feature 3: a product needs a low-stock alert when its stock
     * reaches or falls below its minimum stock level (sold out included).
     */
    val isLowStock: Boolean
        get() = !status.equals("INACTIVE", ignoreCase = true) &&
                stockQuantity <= minStockLevel

    val displayStatus: String
        get() = when {
            status.equals("INACTIVE", ignoreCase = true) ->
                "INACTIVE"

            stockQuantity <= 0 ->
                "SOLD OUT"

            stockQuantity <= minStockLevel ->
                "LOW STOCK"

            else ->
                "ACTIVE"
        }
}
