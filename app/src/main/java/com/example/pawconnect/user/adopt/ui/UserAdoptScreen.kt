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
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pawconnect.user.adopt.data.AdopterProfile
import com.example.pawconnect.user.adopt.data.AdoptionApplication
import com.example.pawconnect.user.adopt.data.Pet
import com.example.pawconnect.user.adopt.logic.LifestyleCompatibilityMatcher
import com.example.pawconnect.user.adopt.logic.UserAdoptViewModel

/**
 * Modern Figma-inspired User Adoption Screen.
 * Connected to UserAdoptViewModel with Material Design 3 styling.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserAdoptScreen(
    modifier: Modifier = Modifier,
    viewModel: UserAdoptViewModel = viewModel()
) {
    // Collect ViewModel StateFlows
    val adopterProfile by viewModel.adopterProfile.collectAsState()
    val applications by viewModel.applications.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()

    // Get filtered and calculated match scores for pets
    val filteredPetsWithScores = viewModel.getFilteredAndSortedPets()

    // UI state
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var showQuizModal by remember { mutableStateOf(false) }
    var selectedPetForDetail by remember { mutableStateOf<Pair<Pet, Int>?>(null) }
    var petForApplication by remember { mutableStateOf<Pet?>(null) }
    var editingApplication by remember { mutableStateOf<AdoptionApplication?>(null) }
    var applicationToWithdraw by remember { mutableStateOf<AdoptionApplication?>(null) }
    val favoritesMap = remember { mutableStateMapOf<String, Boolean>() }

    val filterOptions = listOf("All pets", "Dogs", "Cats", "Best Match")

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                // Top Header Section
                Column(
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 12.dp)
                ) {
                    Text(
                        text = "Find your forever friend",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Every paw deserves a loving home.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }

                // Two clean tabs: Adoptable Pets & My Applications (count)
                SecondaryTabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    Tab(
                        selected = selectedTabIndex == 0,
                        onClick = { selectedTabIndex = 0 },
                        text = {
                            Text(
                                text = "Adoptable Pets",
                                fontWeight = if (selectedTabIndex == 0) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    )
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = { selectedTabIndex = 1 },
                        text = {
                            Text(
                                text = "My Applications (${applications.size})",
                                fontWeight = if (selectedTabIndex == 1) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (selectedTabIndex == 0) {
                // Tab 1: Adoptable Pets
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    // Search Bar & Filter Chips
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { viewModel.setSearchQuery(it) },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("Search by breed or name...") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "Search",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                },
                                trailingIcon = {
                                    if (searchQuery.isNotEmpty()) {
                                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                            Icon(Icons.Default.Close, contentDescription = "Clear")
                                        }
                                    }
                                },
                                shape = RoundedCornerShape(24.dp),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                                )
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Filter Chips Row
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(filterOptions) { filter ->
                                    FilterChip(
                                        selected = selectedFilter == filter,
                                        onClick = { viewModel.setSelectedFilter(filter) },
                                        label = { Text(filter) },
                                        leadingIcon = if (selectedFilter == filter) {
                                            {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        } else null,
                                        shape = RoundedCornerShape(20.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Compatibility Banner Card (Figma Inspired)
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(18.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onPrimary,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Find your perfect match",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Answer 3 quick questions to discover pets that fit your lifestyle.",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Button(
                                        onClick = { showQuizModal = true },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.primary
                                        ),
                                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Tune,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Start matching",
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Section Title: Best Matches / Waiting for you
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (selectedFilter == "Best Match") "BEST MATCHES FOR YOU" else "Waiting for a Home",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                            Text(
                                text = "${filteredPetsWithScores.size} rescued pets",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // 2-Column Pet Grid (Chunked into pairs for LazyColumn)
                    if (filteredPetsWithScores.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No adoptable pets found matching your criteria.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        val pairs = filteredPetsWithScores.chunked(2)
                        items(pairs) { pair ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                for ((pet, score) in pair) {
                                    Box(modifier = Modifier.weight(1f)) {
                                        PetGridCard(
                                            pet = pet,
                                            matchScore = score,
                                            isFavorite = favoritesMap[pet.id] == true,
                                            onFavoriteToggle = {
                                                favoritesMap[pet.id] = !(favoritesMap[pet.id] ?: false)
                                            },
                                            onClick = {
                                                selectedPetForDetail = pet to score
                                            }
                                        )
                                    }
                                }
                                // Fill empty column if odd item
                                if (pair.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            } else {
                // Tab 2: My Applications
                MyApplicationsTab(
                    applications = applications,
                    pets = viewModel.pets.collectAsState().value,
                    onEdit = { app ->
                        editingApplication = app
                        petForApplication = viewModel.pets.value.find { it.id == app.petId }
                    },
                    onWithdraw = { app ->
                        applicationToWithdraw = app
                    }
                )
            }
        }
    }

    // Modal 1: Lifestyle Quiz Dialog
    if (showQuizModal) {
        LifestyleQuizModal(
            currentProfile = adopterProfile,
            onDismiss = { showQuizModal = false },
            onSave = { housing, hours, exp ->
                viewModel.updateAdopterProfile(housing, hours, exp)
                showQuizModal = false
            }
        )
    }

    // Modal 2: Pet Detail Sheet
    if (selectedPetForDetail != null) {
        val (pet, score) = selectedPetForDetail!!
        PetDetailModalSheet(
            pet = pet,
            matchScore = score,
            adopterProfile = adopterProfile,
            onDismiss = { selectedPetForDetail = null },
            onInquireAdopt = {
                petForApplication = pet
                editingApplication = null
                selectedPetForDetail = null
            }
        )
    }

    // Modal 3: Adoption Application Form Dialog
    if (petForApplication != null) {
        val pet = petForApplication!!
        val score = LifestyleCompatibilityMatcher.calculateMatchScore(adopterProfile, pet)
        AdoptionApplicationFormDialog(
            pet = pet,
            matchScore = score,
            existingApplication = editingApplication,
            onDismiss = {
                petForApplication = null
                editingApplication = null
            },
            onSubmit = { appName, notes ->
                if (editingApplication != null) {
                    viewModel.updateApplication(editingApplication!!.id, appName, notes)
                } else {
                    viewModel.submitApplication(pet.id, appName, notes)
                }
                petForApplication = null
                editingApplication = null
                selectedTabIndex = 1
            }
        )
    }

    // Modal 4: Withdraw Application Confirmation Dialog
    if (applicationToWithdraw != null) {
        AlertDialog(
            onDismissRequest = { applicationToWithdraw = null },
            title = { Text("Withdraw Application?", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to withdraw your adoption application for this pet? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.withdrawApplication(applicationToWithdraw!!.id)
                        applicationToWithdraw = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Withdraw")
                }
            },
            dismissButton = {
                TextButton(onClick = { applicationToWithdraw = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

/**
 * Clean 2-Column Pet Grid Card Component
 */
@Composable
private fun PetGridCard(
    pet: Pet,
    matchScore: Int,
    isFavorite: Boolean,
    onFavoriteToggle: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Visual Header Banner with Match Score Badge and Favorite Icon
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(115.dp)
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Pets,
                    contentDescription = pet.name,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )

                // Match Score Pill Badge (Top-Left)
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = if (matchScore >= 80) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary
                ) {
                    Text(
                        text = "$matchScore% match",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }

                // Favorite Heart Toggle (Top-Right)
                IconButton(
                    onClick = onFavoriteToggle,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            // Pet Info Body
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = pet.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = pet.breed,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Age & Gender Pill Tag
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = "${pet.age} yrs • ${pet.gender}",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Pet Detail Modal Sheet Composable
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PetDetailModalSheet(
    pet: Pet,
    matchScore: Int,
    adopterProfile: AdopterProfile,
    onDismiss: () -> Unit,
    onInquireAdopt: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Header Image Placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(16.dp))
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

            Spacer(modifier = Modifier.height(14.dp))

            // Name, Breed & Location
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = pet.name,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${pet.breed} • ${pet.age} yrs • ${pet.gender}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "$matchScore% Match",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Bio / Description
            Text(
                text = pet.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Medical Checkmarks
            Text(
                text = "Medical Status",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Vaccinated", "Neutered", "Microchipped").forEach { medical ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = medical,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Detailed Lifestyle Compatibility Breakdown
            Text(
                text = "Lifestyle Compatibility Breakdown",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))

            val spaceFit = LifestyleCompatibilityMatcher.calculateSpaceFit(adopterProfile, pet)
            val timeFit = LifestyleCompatibilityMatcher.calculateTimeFit(adopterProfile, pet)
            val expFit = LifestyleCompatibilityMatcher.calculateExperienceFit(adopterProfile)

            FitBreakdownRow(label = "Space Fit (${pet.idealSpace})", score = spaceFit)
            Spacer(modifier = Modifier.height(6.dp))
            FitBreakdownRow(label = "Time Fit (${pet.activityLevel} Activity)", score = timeFit)
            Spacer(modifier = Modifier.height(6.dp))
            FitBreakdownRow(label = "Experience Fit", score = expFit)

            Spacer(modifier = Modifier.height(20.dp))

            // Primary Inquire to Adopt Button
            Button(
                onClick = onInquireAdopt,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(imageVector = Icons.Default.Favorite, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Inquire to Adopt ${pet.name}",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun FitBreakdownRow(label: String, score: Int) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = "$score%", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
        }
        Spacer(modifier = Modifier.height(2.dp))
        LinearProgressIndicator(
            progress = { score / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

/**
 * Lifestyle Quiz Dialog / BottomSheet
 */
@Composable
private fun LifestyleQuizModal(
    currentProfile: AdopterProfile,
    onDismiss: () -> Unit,
    onSave: (housing: String, freeHours: Int, hasExp: Boolean) -> Unit
) {
    var housingType by remember { mutableStateOf(currentProfile.housingType) }
    var dailyFreeHours by remember { mutableIntStateOf(currentProfile.dailyFreeHours) }
    var hasExperience by remember { mutableStateOf(currentProfile.hasExperience) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Lifestyle Matching Quiz", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = "Answer 3 quick questions to calculate live compatibility scores for all adoptable pets.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Q1: Housing Type
                Column {
                    Text("1. What is your living space?", style = MaterialTheme.typography.labelLarge)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Apartment", "House with Yard").forEach { option ->
                            FilterChip(
                                selected = housingType == option,
                                onClick = { housingType = option },
                                label = { Text(option) }
                            )
                        }
                    }
                }

                // Q2: Daily Free Hours
                Column {
                    Text("2. Daily free hours for pet care: $dailyFreeHours hrs", style = MaterialTheme.typography.labelLarge)
                    Slider(
                        value = dailyFreeHours.toFloat(),
                        onValueChange = { dailyFreeHours = it.toInt() },
                        valueRange = 1f..10f,
                        steps = 8
                    )
                }

                // Q3: Experience
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("3. Prior pet ownership experience?", style = MaterialTheme.typography.labelLarge)
                    Switch(
                        checked = hasExperience,
                        onCheckedChange = { hasExperience = it }
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = { onSave(housingType, dailyFreeHours, hasExperience) }) {
                Text("Update Matches Live")
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
 * Adoption Application Form Dialog (Create / Update)
 */
@Composable
private fun AdoptionApplicationFormDialog(
    pet: Pet,
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
                text = if (existingApplication != null) "Edit Adoption Application" else "Inquire to Adopt ${pet.name}",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Favorite, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Calculated Match Score: $matchScore%",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                OutlinedTextField(
                    value = applicantName,
                    onValueChange = { applicantName = it },
                    label = { Text("Applicant Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Message / Notes for shelter") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 4
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (applicantName.isNotBlank()) onSubmit(applicantName.trim(), notes.trim()) },
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
 * My Applications Tab View
 */
@Composable
private fun MyApplicationsTab(
    applications: List<AdoptionApplication>,
    pets: List<Pet>,
    onEdit: (AdoptionApplication) -> Unit,
    onWithdraw: (AdoptionApplication) -> Unit
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
                    text = "No applications submitted yet.",
                    style = MaterialTheme.typography.titleMedium,
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

                        // Actions for Pending applications
                        if (app.status == "Pending") {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedButton(
                                    onClick = { onEdit(app) },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.padding(end = 8.dp)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Edit Details")
                                }

                                TextButton(
                                    onClick = { onWithdraw(app) },
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
