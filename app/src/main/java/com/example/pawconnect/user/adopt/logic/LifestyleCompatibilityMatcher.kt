package com.example.pawconnect.user.adopt.logic

import com.example.pawconnect.user.adopt.data.AdopterProfile
import com.example.pawconnect.user.adopt.data.Pet
import kotlin.math.roundToInt

/**
 * Calculates the lifestyle compatibility match score between an adopter and a pet.
 */
object LifestyleCompatibilityMatcher {

    /**
     * Calculates match score (0 - 100) using weighted scoring:
     * - 40% Housing space compatibility
     * - 40% Daily free hours vs pet activity level
     * - 20% Prior pet experience
     */
    fun calculateMatchScore(adopter: AdopterProfile, pet: Pet): Int {
        val housingScore = calculateHousingScore(adopter.housingType, pet.idealSpace)
        val activityScore = calculateActivityScore(adopter.dailyFreeHours, pet.activityLevel)
        val experienceScore = if (adopter.hasExperience) 100.0 else 60.0

        val totalScore = (housingScore * 0.40) + (activityScore * 0.40) + (experienceScore * 0.20)
        return totalScore.roundToInt().coerceIn(0, 100)
    }

    private fun calculateHousingScore(adopterHousing: String, petIdealSpace: String): Double {
        val housing = adopterHousing.trim().lowercase()
        val space = petIdealSpace.trim().lowercase()

        if (housing.isEmpty() || space.isEmpty()) return 70.0
        if (space == "any" || space == "apartment" || housing.contains(space) || space.contains(housing)) {
            return 100.0
        }
        if ((space.contains("yard") || space.contains("house")) && housing.contains("apartment")) {
            return 50.0
        }
        return 75.0
    }

    private fun calculateActivityScore(freeHours: Int, petActivityLevel: String): Double {
        val activity = petActivityLevel.trim().lowercase()
        return when {
            activity.contains("high") || activity.contains("active") -> when {
                freeHours >= 4 -> 100.0
                freeHours >= 2 -> 70.0
                else -> 40.0
            }
            activity.contains("medium") || activity.contains("moderate") -> when {
                freeHours >= 2 -> 100.0
                freeHours >= 1 -> 75.0
                else -> 50.0
            }
            activity.contains("low") || activity.contains("calm") -> when {
                freeHours >= 1 -> 100.0
                else -> 60.0
            }
            else -> when {
                freeHours >= 4 -> 100.0
                freeHours >= 2 -> 80.0
                else -> 60.0
            }
        }
    }
}
