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
    val imageUri: String? = null
) {

    // Automatically determine product status
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
