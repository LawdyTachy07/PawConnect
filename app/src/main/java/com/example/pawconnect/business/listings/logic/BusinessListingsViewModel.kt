package com.example.pawconnect.business.listings.logic

import androidx.lifecycle.ViewModel
import com.example.pawconnect.user.adopt.data.AdoptionApplication
import com.example.pawconnect.user.adopt.data.Pet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

 //ViewModel for Business Pet Listing Management and Adoption Application Review.
class BusinessListingsViewModel : ViewModel() {

    private val _petListings = MutableStateFlow(
        listOf(
            Pet(
                id = "pet_1",
                businessId = "shelter_01",
                name = "Luna",
                breed = "Domestic Shorthair",
                age = 1,
                gender = "Female",
                healthStatus = "Vaccinated • Neutered • Microchipped",
                status = "Available",
                imageUrl = "",
                idealSpace = "Apartment",
                activityLevel = "Low",
                location = "Petaling Jaya",
                description = "Luna is a calm and gentle rescue cat who loves cozy windows, sunny spots, and head scratches."
            ),
            Pet(
                id = "pet_2",
                businessId = "shelter_01",
                name = "Milo",
                breed = "Mixed Breed",
                age = 2,
                gender = "Male",
                healthStatus = "Vaccinated • Neutered • Microchipped",
                status = "Available",
                imageUrl = "",
                idealSpace = "Apartment",
                activityLevel = "Medium",
                location = "Subang Jaya",
                description = "Milo is a friendly and playful rescue dog who loves family walks and learning new tricks."
            ),
            Pet(
                id = "pet_3",
                businessId = "shelter_01",
                name = "Coco",
                breed = "French Bulldog Mix",
                age = 3,
                gender = "Female",
                healthStatus = "Vaccinated • Neutered • Microchipped",
                status = "Available",
                imageUrl = "",
                idealSpace = "Apartment",
                activityLevel = "Low",
                location = "Bangsar",
                description = "Coco is a chill Frenchie mix who gets along well with everyone. Perfect for indoor living."
            ),
            Pet(
                id = "pet_4",
                businessId = "shelter_01",
                name = "Bella",
                breed = "Golden Retriever",
                age = 2,
                gender = "Female",
                healthStatus = "Vaccinated • Neutered • Microchipped",
                status = "Adopted",
                imageUrl = "",
                idealSpace = "House with Yard",
                activityLevel = "High",
                location = "Kuala Lumpur",
                description = "Bella is an energetic Golden Retriever who loves running outdoors and playing fetch."
            )
        )
    )
    val petListings: StateFlow<List<Pet>> = _petListings.asStateFlow()

    private val _incomingApplications = MutableStateFlow(
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
                notes = "I work remotely from home and have a quiet apartment ready for Luna.",
                status = "Pending"
            ),
            AdoptionApplication(
                id = "app_102",
                petId = "pet_2",
                userId = "user_02",
                applicantName = "Sarah Lee",
                housingType = "House with Yard",
                dailyFreeHours = 4,
                hasExperience = true,
                matchScore = 88,
                notes = "We have a fully fenced yard and experience caring for rescue dogs.",
                status = "Pending"
            )
        )
    )
    val incomingApplications: StateFlow<List<AdoptionApplication>> = _incomingApplications.asStateFlow()

    fun addPet(pet: Pet) {
        val newPet = if (pet.id.isBlank()) pet.copy(id = "pet_${UUID.randomUUID().toString().take(6)}") else pet
        _petListings.value = listOf(newPet) + _petListings.value
    }

    fun updatePet(pet: Pet) {
        _petListings.value = _petListings.value.map { if (it.id == pet.id) pet else it }
    }

    fun deletePet(petId: String) {
        _petListings.value = _petListings.value.filter { it.id != petId }
        _incomingApplications.value = _incomingApplications.value.filter { it.petId != petId }
    }

    fun approveApplication(applicationId: String) {
        var targetPetId: String? = null
        _incomingApplications.value = _incomingApplications.value.map { app ->
            if (app.id == applicationId) {
                targetPetId = app.petId
                app.copy(status = "Approved")
            } else app
        }

        // Set pet status to Adopted
        if (targetPetId != null) {
            _petListings.value = _petListings.value.map { pet ->
                if (pet.id == targetPetId) pet.copy(status = "Adopted") else pet
            }
        }
    }

    fun rejectApplication(applicationId: String) {
        _incomingApplications.value = _incomingApplications.value.map { app ->
            if (app.id == applicationId) app.copy(status = "Rejected") else app
        }
    }
}
