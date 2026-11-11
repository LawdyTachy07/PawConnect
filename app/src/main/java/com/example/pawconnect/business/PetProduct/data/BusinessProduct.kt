package com.example.pawconnect.business.PetProduct.data

data class BusinessProduct(
    val id: String,
    val name: String,
    val category: String,
    val price: Double,
    val originalPrice: Double = 0.0,
    val discountPercent: Int = 0,
    var stockQuantity: Int,
    val minStockLevel: Int = 5,
    val status: String = "ACTIVE",
    val imageUrl: String? = null,
    val imageRes: Int? = null
)
