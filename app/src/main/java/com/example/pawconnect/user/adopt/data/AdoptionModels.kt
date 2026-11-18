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
    val status: String = "Ready to adopt",
    val imageUrl: String = "",
    val idealSpace: String = "Apartment",
    val activityLevel: String = "Medium",
    val location: String = "",
    val description: String = ""
)

/**
 * Represents an adopter's lifestyle profile used for compatibility matching.
 */
data class AdopterProfile(
    val housingType: String = "Apartment",
    val dailyFreeHours: Int = 1,
    val hasExperience: Boolean = false,
    val hasCompletedQuiz: Boolean = false
)

/**
 * Represents an adoption application submitted by a user.
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
