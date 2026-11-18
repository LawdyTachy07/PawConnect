package com.example.pawconnect.user.product.data

data class UserPetProduct(
    val id: String,
    val name: String,
    val description: String,
    val category: String,
    val price: Double,
    val imageUrl: String? = null,
    val badge: String = "In stock"
)