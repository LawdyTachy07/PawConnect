package com.example.pawconnect.user.adopt.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.pawconnect.user.adopt.data.AdopterProfile
import com.example.pawconnect.user.adopt.data.AdoptionApplication
import com.example.pawconnect.user.adopt.data.Pet
import com.example.pawconnect.user.adopt.logic.LifestyleCompatibilityMatcher
import java.util.UUID

@Composable
fun UserAdoptScreen(
    modifier: Modifier = Modifier
) {
    // State for User's Adopter Lifestyle Profile
    var adopterProfile by remember {
        mutableStateOf(
            AdopterProfile(
                housingType = "Apartment",
                dailyFreeHours = 3,
                hasExperience = true
            )
        )
    }

    // Tab state (0 = Browse Pets, 1 = My Applications)
    var selectedTab by remember { mutableIntStateOf(0) }

    // Search & Filter State
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("For you") }
    val filters = listOf("For you", "Dogs", "Cats", "Nearby")

    // Sample Rescued Pets
    val petsList = remember {
        mutableStateListOf(
            Pet(
                id = "pet_1",
                businessId = "shelter_pj",
                name = "Milo",
                breed = "Mixed breed",
                age = 2,
                gender = "Male",
                healthStatus = "Vaccinated • Neutered • Microchipped",
                status = "Ready to adopt",
                imageUrl = "",
                idealSpace = "Apartment",
                activityLevel = "Medium",
                location = "Rescued • Petaling Jaya",
                description = "Milo is a friendly and energetic rescue who loves cuddles, playful walks, and sunny nap spots. Great with families!"
            ),
            Pet(
                id = "pet_2",
                businessId = "shelter_sj",
                name = "Luna",
                breed = "Domestic Shorthair",
                age = 1,
                gender = "Female",
                healthStatus = "Vaccinated • Neutered • Microchipped",
                status = "Ready to adopt",
                imageUrl = "",
                idealSpace = "Apartment",
                activityLevel = "Low",
                location = "Rescued • Subang Jaya",
                description = "Luna is a calm, gentle rescue cat who enjoys quiet rooms, window views, and gentle head scratches."
            ),
            Pet(
                id = "pet_3",
                businessId = "shelter_kl",
                name = "Buster",
                breed = "Golden Mix",
                age = 3,
                gender = "Male",
                healthStatus = "Vaccinated • Neutered • Microchipped",
                status = "Ready to adopt",
                imageUrl = "",
                idealSpace = "House with Yard",
                activityLevel = "High",
                location = "Rescued • Kuala Lumpur",
                description = "Buster is an active and happy rescue dog who thrives with outdoor playtime, open spaces, and an adventurous owner."
            )
        )
    }

    // User Applications State (CRUD)
    val applicationsList = remember {
        mutableStateListOf(
            AdoptionApplication(
                id = "app_101",
                petId = "pet_1",
                userId = "user_curr",
                applicantName = "Alex Tan",
                housingType = "Apartment",
                dailyFreeHours = 3,
                hasExperience = true,
                matchScore = 85,
                notes = "I work from home and have a spacious balcony area.",
                status = "Pending"
            )
        )
    }

    // Dialog control states
    var showProfileDialog by remember { mutableStateOf(false) }
    var showApplyDialog by remember { mutableStateOf(false) }
    var targetPetForApply by remember { mutableStateOf<Pet?>(null) }
    var editingApplication by remember { mutableStateOf<AdoptionApplication?>(null) }

    // Filtered pets computation
    val filteredPets = remember(searchQuery, selectedFilter, petsList) {
        petsList.filter { pet ->
            val matchesQuery = pet.name.contains(searchQuery, ignoreCase = true) ||
                    pet.breed.contains(searchQuery, ignoreCase = true) ||
                    pet.location.contains(searchQuery, ignoreCase = true)

            val matchesFilter = when (selectedFilter) {
                "Dogs" -> pet.breed.contains("Dog", ignoreCase = true) || pet.breed.contains("Mix", ignoreCase = true) || pet.breed.contains("Golden", ignoreCase = true)
                "Cats" -> pet.breed.contains("Cat", ignoreCase = true) || pet.breed.contains("Shorthair", ignoreCase = true)
                "Nearby" -> pet.location.contains("Petaling", ignoreCase = true) || pet.location.contains("Subang", ignoreCase = true)
                else -> true
            }
            matchesQuery && matchesFilter
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                // Main Header Title & Subtitle
                Column(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
                ) {
                    Text(
                        text = "Find your forever friend",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Text(
                        text = "Rescued with love. Ready for a new beginning.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }

                // Tab Selector (Browse Pets vs My Applications)
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Adoptable Pets (${filteredPets.size})") }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("My Applications (${applicationsList.size})") }
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (selectedTab == 0) {
                // Browse & Search View
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    // Search & Filter Header item
                    item {
                        SearchAndFilterHeader(
                            searchQuery = searchQuery,
                            onSearchQueryChange = { searchQuery = it },
                            selectedFilter = selectedFilter,
                            onFilterSelect = { selectedFilter = it },
                            filters = filters,
                            onOpenProfileDialog = { showProfileDialog = true }
                        )
                    }

                    // Pet Cards List
                    if (filteredPets.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No adoptable pets found matching your criteria.",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        items(filteredPets, key = { it.id }) { pet ->
                            val matchScore = LifestyleCompatibilityMatcher.calculateMatchScore(adopterProfile, pet)
                            PetDetailCard(
                                pet = pet,
                                matchScore = matchScore,
                                onAdjustProfile = { showProfileDialog = true },
                                onInquireAdopt = {
                                    targetPetForApply = pet
                                    editingApplication = null
                                    showApplyDialog = true
                                }
                            )
                        }
                    }
                }
            } else {
                // My Applications View (CRUD)
                MyApplicationsSection(
                    applications = applicationsList,
                    pets = petsList,
                    onEditApplication = { app ->
                        editingApplication = app
                        targetPetForApply = petsList.find { it.id == app.petId }
                        showApplyDialog = true
                    },
                    onDeleteApplication = { app ->
                        applicationsList.remove(app)
                    }
                )
            }
        }
    }

    // Dialog 1: Adjust Lifestyle Profile
    if (showProfileDialog) {
        LifestyleProfileDialog(
            currentProfile = adopterProfile,
            onDismiss = { showProfileDialog = false },
            onSave = { updated ->
                adopterProfile = updated
                showProfileDialog = false
            }
        )
    }

    // Dialog 2: Application Submission/Edit Form
    if (showApplyDialog && targetPetForApply != null) {
        val pet = targetPetForApply!!
        val currentMatchScore = LifestyleCompatibilityMatcher.calculateMatchScore(adopterProfile, pet)

        AdoptionApplicationDialog(
            pet = pet,
            adopterProfile = adopterProfile,
            matchScore = currentMatchScore,
            existingApplication = editingApplication,
            onDismiss = {
                showApplyDialog = false
                targetPetForApply = null
                editingApplication = null
            },
            onSubmit = { appName, notes ->
                if (editingApplication != null) {
                    // Update existing pending application
                    val index = applicationsList.indexOfFirst { it.id == editingApplication!!.id }
                    if (index != -1) {
                        applicationsList[index] = applicationsList[index].copy(
                            applicantName = appName,
                            housingType = adopterProfile.housingType,
                            dailyFreeHours = adopterProfile.dailyFreeHours,
                            hasExperience = adopterProfile.hasExperience,
                            matchScore = currentMatchScore,
                            notes = notes
                        )
                    }
                } else {
                    // Create new application
                    val newApp = AdoptionApplication(
                        id = "app_${UUID.randomUUID().toString().take(6)}",
                        petId = pet.id,
                        userId = "user_curr",
                        applicantName = appName,
                        housingType = adopterProfile.housingType,
                        dailyFreeHours = adopterProfile.dailyFreeHours,
                        hasExperience = adopterProfile.hasExperience,
                        matchScore = currentMatchScore,
                        notes = notes,
                        status = "Pending"
                    )
                    applicationsList.add(0, newApp)
                }
                showApplyDialog = false
                targetPetForApply = null
                editingApplication = null
                selectedTab = 1 // Switch to Applications tab to view
            }
        )
    }
}

/**
 * Search Bar and Filter Chips Composable
 */
@Composable
private fun SearchAndFilterHeader(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedFilter: String,
    onFilterSelect: (String) -> Unit,
    filters: List<String>,
    onOpenProfileDialog: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Search TextField
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search by pet name, breed, or location...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear search")
                    }
                }
            },
            shape = RoundedCornerShape(24.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Filter Chips & Lifestyle Profile Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LazyRow(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filters) { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { onFilterSelect(filter) },
                        label = { Text(filter) },
                        leadingIcon = if (selectedFilter == filter) {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        } else null
                    )
                }
            }

            IconButton(
                onClick = onOpenProfileDialog,
                modifier = Modifier
                    .padding(start = 4.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = "Adjust Profile",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

/**
 * Pet Detail Card Composable
 */
@Composable
private fun PetDetailCard(
    pet: Pet,
    matchScore: Int,
    onAdjustProfile: () -> Unit,
    onInquireAdopt: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Pet Banner Placeholder Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Pets,
                    contentDescription = pet.name,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Title & Age/Gender
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${pet.name} • ${pet.age} yrs old",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${pet.gender} • ${pet.breed}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Ready to adopt badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = pet.status,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Location Badge
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location",
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = pet.location,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.tertiary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Description
            Text(
                text = pet.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Medical Tags
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val medicalTags = listOf("Vaccinated", "Neutered", "Microchipped")
                medicalTags.forEach { tag ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = tag,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Compatibility Match Card (Unique Feature)
            CompatibilityMatchCard(
                matchScore = matchScore,
                onAdjustProfile = onAdjustProfile
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Primary Inquire to Adopt Button
            Button(
                onClick = onInquireAdopt,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.Favorite, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Inquire to Adopt ${pet.name}",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

/**
 * Compatibility Match Card Component (Unique Feature)
 */
@Composable
private fun CompatibilityMatchCard(
    matchScore: Int,
    onAdjustProfile: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Circular Match Score Badge
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(56.dp)
            ) {
                CircularProgressIndicator(
                    progress = { matchScore / 100f },
                    modifier = Modifier.fillMaxSize(),
                    color = if (matchScore >= 75) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    strokeWidth = 5.dp
                )
                Text(
                    text = "$matchScore%",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Lifestyle Match Score",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (matchScore >= 80) "Excellent match for your housing space & activity!"
                    else "Good match. Adjust your profile for higher accuracy.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Adjust my lifestyle profile",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .clickable { onAdjustProfile() }
                        .padding(top = 4.dp)
                )
            }
        }
    }
}

/**
 * Dialog to adjust user's lifestyle profile
 */
@Composable
private fun LifestyleProfileDialog(
    currentProfile: AdopterProfile,
    onDismiss: () -> Unit,
    onSave: (AdopterProfile) -> Unit
) {
    var housingType by remember { mutableStateOf(currentProfile.housingType) }
    var dailyFreeHours by remember { mutableIntStateOf(currentProfile.dailyFreeHours) }
    var hasExperience by remember { mutableStateOf(currentProfile.hasExperience) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Adjust Lifestyle Profile", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Update your living conditions to see live match scores across all pets.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Housing Type Selector
                Text("Housing Type", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Apartment", "House with Yard").forEach { option ->
                        FilterChip(
                            selected = housingType == option,
                            onClick = { housingType = option },
                            label = { Text(option) }
                        )
                    }
                }

                // Daily Free Hours
                Text("Daily Free Hours: $dailyFreeHours hrs", style = MaterialTheme.typography.labelLarge)
                Slider(
                    value = dailyFreeHours.toFloat(),
                    onValueChange = { dailyFreeHours = it.toInt() },
                    valueRange = 1f..10f,
                    steps = 8
                )

                // Prior Experience Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Prior Pet Experience", style = MaterialTheme.typography.labelLarge)
                    Switch(
                        checked = hasExperience,
                        onCheckedChange = { hasExperience = it }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        AdopterProfile(
                            housingType = housingType,
                            dailyFreeHours = dailyFreeHours,
                            hasExperience = hasExperience
                        )
                    )
                }
            ) {
                Text("Save Profile")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

/**
 * Dialog / BottomSheet Form to Submit or Edit an Adoption Application
 */
@Composable
private fun AdoptionApplicationDialog(
    pet: Pet,
    adopterProfile: AdopterProfile,
    matchScore: Int,
    existingApplication: AdoptionApplication?,
    onDismiss: () -> Unit,
    onSubmit: (applicantName: String, notes: String) -> Unit
) {
    var applicantName by remember { mutableStateOf(existingApplication?.applicantName ?: "Alex Tan") }
    var notes by remember { mutableStateOf(existingApplication?.notes ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (existingApplication != null) "Update Adoption Application" else "Inquire to Adopt ${pet.name}",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Compatibility Banner
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Compatibility Match Score: $matchScore%",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                // Applicant Name Field
                OutlinedTextField(
                    value = applicantName,
                    onValueChange = { applicantName = it },
                    label = { Text("Applicant Full Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Living Summary Info (Read-only from profile)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Text(
                        text = "Submitted Profile:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "• Housing: ${adopterProfile.housingType}\n• Free Hours: ${adopterProfile.dailyFreeHours} hrs/day\n• Experience: ${if (adopterProfile.hasExperience) "Yes" else "First-time owner"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Notes Field
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Message / Additional Notes for Shelter") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 4
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (applicantName.isNotBlank()) {
                        onSubmit(applicantName.trim(), notes.trim())
                    }
                },
                enabled = applicantName.isNotBlank()
            ) {
                Text(if (existingApplication != null) "Save Changes" else "Submit Application")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

/**
 * Section for Viewing & Managing Submitted Applications (CRUD: Read, Update, Delete)
 */
@Composable
private fun MyApplicationsSection(
    applications: List<AdoptionApplication>,
    pets: List<Pet>,
    onEditApplication: (AdoptionApplication) -> Unit,
    onDeleteApplication: (AdoptionApplication) -> Unit
) {
    if (applications.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "No submitted adoption applications yet.",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Browse adoptable pets and tap 'Inquire to Adopt' to apply.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(applications, key = { it.id }) { app ->
                val pet = pets.find { it.id == app.petId }
                val petName = pet?.name ?: "Rescued Pet"

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Application for $petName",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Applicant: ${app.applicantName}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Status Badge
                            val (badgeBg, badgeFg) = when (app.status) {
                                "Approved" -> Color(0xFFD4EDDA) to Color(0xFF155724)
                                "Rejected" -> Color(0xFFF8D7DA) to Color(0xFF721C24)
                                else -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = badgeBg
                            ) {
                                Text(
                                    text = app.status,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = badgeFg
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Match Score: ${app.matchScore}% • Housing: ${app.housingType} • Free Hours: ${app.dailyFreeHours}h",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        if (app.notes.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Notes: \"${app.notes}\"",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // CRUD Actions (Update and Withdraw / Delete for Pending status)
                        if (app.status == "Pending") {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedButton(
                                    onClick = { onEditApplication(app) },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.padding(end = 8.dp)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Edit Application")
                                }

                                TextButton(
                                    onClick = { onDeleteApplication(app) },
                                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Withdraw", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Withdraw")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
