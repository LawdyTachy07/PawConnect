package com.example.pawconnect.user.adopt.logic

import androidx.lifecycle.ViewModel
import com.example.pawconnect.user.adopt.data.AdopterProfile
import com.example.pawconnect.user.adopt.data.AdoptionApplication
import com.example.pawconnect.user.adopt.data.Pet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

/**
 * ViewModel managing state and business logic for the Pet Adoption module.
 */
class UserAdoptViewModel : ViewModel() {

    private val _adopterProfile = MutableStateFlow(
        AdopterProfile(
            housingType = "Apartment",
            dailyFreeHours = 1,
            hasExperience = false,
            hasCompletedQuiz = false
        )
    )
    val adopterProfile: StateFlow<AdopterProfile> = _adopterProfile.asStateFlow()

    private val _favoritePetIds = MutableStateFlow<Set<String>>(emptySet())
    val favoritePetIds: StateFlow<Set<String>> = _favoritePetIds.asStateFlow()

    private val _pets = MutableStateFlow(
        listOf(
            Pet(
                id = "pet_1",
                businessId = "shelter_01",
                name = "Luna",
                breed = "Domestic Shorthair Cat",
                age = 1,
                gender = "Female",
                healthStatus = "Vaccinated • Neutered • Microchipped",
                status = "Ready to adopt",
                imageUrl = "",
                idealSpace = "Apartment",
                activityLevel = "Low",
                location = "Petaling Jaya",
                description = "Luna is a gentle and calm rescue cat who loves sunny window spots, quiet naps, and sweet head scratches."
            ),
            Pet(
                id = "pet_2",
                businessId = "shelter_02",
                name = "Milo",
                breed = "Mixed Breed Dog",
                age = 2,
                gender = "Male",
                healthStatus = "Vaccinated • Neutered • Microchipped",
                status = "Ready to adopt",
                imageUrl = "",
                idealSpace = "Apartment",
                activityLevel = "Medium",
                location = "Subang Jaya",
                description = "Milo is an affectionate and playful rescue dog who enjoys strolls in the park, cuddling, and learning tricks."
            ),
            Pet(
                id = "pet_3",
                businessId = "shelter_01",
                name = "Coco",
                breed = "French Bulldog Mix",
                age = 3,
                gender = "Female",
                healthStatus = "Vaccinated • Neutered • Microchipped",
                status = "Ready to adopt",
                imageUrl = "",
                idealSpace = "Apartment",
                activityLevel = "Low",
                location = "Bangsar",
                description = "Coco is a chill Frenchie mix who gets along well with everyone. Perfect companion for cozy indoor living."
            ),
            Pet(
                id = "pet_4",
                businessId = "shelter_03",
                name = "Bella",
                breed = "Golden Retriever Dog",
                age = 2,
                gender = "Female",
                healthStatus = "Vaccinated • Neutered • Microchipped",
                status = "Ready to adopt",
                imageUrl = "",
                idealSpace = "House with Yard",
                activityLevel = "High",
                location = "Kuala Lumpur",
                description = "Bella is a happy, high-energy Golden Retriever who thrives with active owners, outdoor games, and yard space."
            ),
            Pet(
                id = "pet_5",
                businessId = "shelter_02",
                name = "Nibbles",
                breed = "Rabbit",
                age = 1,
                gender = "Male",
                healthStatus = "Vaccinated • Neutered",
                status = "Ready to adopt",
                imageUrl = "",
                idealSpace = "Apartment",
                activityLevel = "Low",
                location = "Shah Alam",
                description = "Nibbles is a sweet indoor bunny who loves fresh greens, gentle petting, and hopping around softly."
            )
        )
    )
    val pets: StateFlow<List<Pet>> = _pets.asStateFlow()

    private val _applications = MutableStateFlow(
        listOf(
            AdoptionApplication(
                id = "app_101",
                petId = "pet_1",
                userId = "user_01",
                applicantName = "Alex Tan",
                housingType = "Apartment",
                dailyFreeHours = 3,
                hasExperience = true,
                matchScore = 92,
                notes = "I work remotely and have a warm, quiet apartment ready for Luna.",
                status = "Pending"
            )
        )
    )
    val applications: StateFlow<List<AdoptionApplication>> = _applications.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedFilter = MutableStateFlow("All pets")
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedFilter(filter: String) {
        _selectedFilter.value = filter
    }

    fun toggleFavorite(petId: String) {
        _favoritePetIds.value = if (_favoritePetIds.value.contains(petId)) {
            _favoritePetIds.value - petId
        } else {
            _favoritePetIds.value + petId
        }
    }

    fun hasPendingApplication(petId: String): Boolean {
        return _applications.value.any { it.petId == petId && it.status == "Pending" }
    }

    fun updateAdopterProfile(housingType: String, dailyFreeHours: Int, hasExperience: Boolean) {
        _adopterProfile.value = AdopterProfile(
            housingType = housingType,
            dailyFreeHours = dailyFreeHours,
            hasExperience = hasExperience,
            hasCompletedQuiz = true
        )
    }

    fun submitApplication(petId: String, applicantName: String, notes: String) {
        val currentProfile = _adopterProfile.value
        val pet = _pets.value.find { it.id == petId } ?: return
        val score = LifestyleCompatibilityMatcher.calculateMatchScore(currentProfile, pet)

        val newApp = AdoptionApplication(
            id = "app_${UUID.randomUUID().toString().take(6)}",
            petId = petId,
            userId = "user_curr",
            applicantName = applicantName,
            housingType = currentProfile.housingType,
            dailyFreeHours = currentProfile.dailyFreeHours,
            hasExperience = currentProfile.hasExperience,
            matchScore = score,
            notes = notes,
            status = "Pending"
        )
        _applications.value = listOf(newApp) + _applications.value
    }

    fun updateApplication(applicationId: String, applicantName: String, notes: String) {
        val currentProfile = _adopterProfile.value
        _applications.value = _applications.value.map { app ->
            if (app.id == applicationId) {
                val pet = _pets.value.find { it.id == app.petId }
                val newScore = if (pet != null) LifestyleCompatibilityMatcher.calculateMatchScore(currentProfile, pet) else app.matchScore
                app.copy(
                    applicantName = applicantName,
                    housingType = currentProfile.housingType,
                    dailyFreeHours = currentProfile.dailyFreeHours,
                    hasExperience = currentProfile.hasExperience,
                    matchScore = newScore,
                    notes = notes
                )
            } else app
        }
    }

    fun withdrawApplication(applicationId: String) {
        _applications.value = _applications.value.filter { it.id != applicationId }
    }

    fun getFilteredAndSortedPets(): List<Pair<Pet, Int>> {
        val currentProfile = _adopterProfile.value
        val query = _searchQuery.value.lowercase().trim()
        val filter = _selectedFilter.value
        val favs = _favoritePetIds.value

        val petsWithScores = _pets.value.map { pet ->
            val score = LifestyleCompatibilityMatcher.calculateMatchScore(currentProfile, pet)
            pet to score
        }

        val filtered = petsWithScores.filter { (pet, score) ->
            val matchesSearch = query.isEmpty() ||
                    pet.name.lowercase().contains(query) ||
                    pet.breed.lowercase().contains(query) ||
                    pet.location.lowercase().contains(query)

            val breedLower = pet.breed.lowercase()
            val isDog = breedLower.contains("dog") || breedLower.contains("retriever") || breedLower.contains("bulldog")
            val isCat = breedLower.contains("cat") || breedLower.contains("shorthair")

            val matchesFilter = when (filter) {
                "Dogs" -> isDog
                "Cats" -> isCat
                "Others" -> !isDog && !isCat
                "Saved ♥" -> favs.contains(pet.id)
                "Best Match" -> score >= 80
                else -> true
            }

            matchesSearch && matchesFilter
        }

        return if (filter == "Best Match") {
            filtered.sortedByDescending { it.second }
        } else {
            filtered
        }
    }
}
