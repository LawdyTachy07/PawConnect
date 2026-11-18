package com.example.pawconnect.UserManagement.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val id: String = "",
    val email: String = "",
    @SerialName("full_name") val fullName: String = "",
    val role: String = "user", // "user", "business", "admin"
    @SerialName("phone_number") val phoneNumber: String? = null,
    val address: String? = null,
    @SerialName("organization_name") val organizationName: String? = null,
    @SerialName("avatar_url") val avatarUrl: String? = null,
    @SerialName("is_deactivated") val isDeactivated: Boolean = false
)