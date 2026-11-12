package com.example.pawconnect.user.adopt.data

/**
 * Represents a pet available for adoption.
 */
data class Pet(
    val id: String = "",
    val businessId: String = "",
    val name: String = "",
    val breed: String = "",
    val age: Int = 0,
    val gender: String = "",
    val healthStatus: String = "",
    val status: String = "Available",
    val imageUrl: String = "",
    val idealSpace: String = "Apartment",
    val activityLevel: String = "Medium",
    val location: String = "",
    val description: String = ""
)

/**
 * Profile of an adopter used for lifestyle compatibility matching.
 */
data class AdopterProfile(
    val housingType: String = "",
    val dailyFreeHours: Int = 0,
    val hasExperience: Boolean = false
)

/**
 * An adoption application submitted by a user for a pet.
 */
data class AdoptionApplication(
    val id: String = "",
    val petId: String = "",
    val userId: String = "",
    val applicantName: String = "",
    val housingType: String = "",
    val dailyFreeHours: Int = 0,
    val hasExperience: Boolean = false,
    val matchScore: Int = 0,
    val notes: String = "",
    val status: String = "Pending"
)
