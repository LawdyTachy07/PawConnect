package com.example.pawconnect.business.listings.ui

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.pawconnect.business.listings.data.PetLocationData.breedPresets
import com.example.pawconnect.business.listings.data.PetLocationData.malaysiaCitiesMap
import com.example.pawconnect.business.listings.data.PetLocationData.malaysiaStates
import com.example.pawconnect.user.adopt.data.Pet
import java.io.File

//Full-screen page to Create or Edit a Pet Listing with strict validations and high-contrast styling.
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditPetScreen(
    initialPet: Pet? = null,
    onDismiss: () -> Unit,
    onSave: (Pet) -> Unit
) {
    val context = LocalContext.current

    // Parse initial location ($city, $state)
    val parsedParts = initialPet?.location?.split(", ") ?: emptyList()
    val initialSelectedState = parsedParts.getOrNull(1)?.takeIf { malaysiaStates.contains(it) } ?: "Selangor"
    val initialSelectedCity = parsedParts.getOrNull(0)?.takeIf { (malaysiaCitiesMap[initialSelectedState] ?: emptyList()).contains(it) }
        ?: (malaysiaCitiesMap[initialSelectedState]?.firstOrNull() ?: "Petaling Jaya")

    // Form fields state
    var name by remember { mutableStateOf(initialPet?.name ?: "") }
    var breed by remember { mutableStateOf(initialPet?.breed ?: "") }
    var age by remember { mutableIntStateOf(initialPet?.age ?: 1) }
    var gender by remember { mutableStateOf(initialPet?.gender.takeIf { !it.isNullOrBlank() } ?: "Male") }
    var idealSpace by remember { mutableStateOf(initialPet?.idealSpace.takeIf { !it.isNullOrBlank() } ?: "Apartment") }
    var activityLevel by remember { mutableStateOf(initialPet?.activityLevel.takeIf { !it.isNullOrBlank() } ?: "Medium") }
    var selectedState by remember { mutableStateOf(initialSelectedState) }
    var selectedCity by remember { mutableStateOf(initialSelectedCity) }
    var description by remember { mutableStateOf(initialPet?.description ?: "") }
    var status by remember { mutableStateOf(initialPet?.status.takeIf { !it.isNullOrBlank() } ?: "Available") }
    var imageUrl by remember { mutableStateOf(initialPet?.imageUrl ?: "") }

    // Health tags state
    var isVaccinated by remember { mutableStateOf(initialPet?.healthStatus?.contains("Vaccinated") ?: true) }
    var isNeutered by remember { mutableStateOf(initialPet?.healthStatus?.contains("Neutered") ?: true) }
    var isMicrochipped by remember { mutableStateOf(initialPet?.healthStatus?.contains("Microchipped") ?: true) }

    // UI Dialog States
    var showFullScreenPhotoViewer by remember { mutableStateOf(false) }
    var showNoPhotoWarningDialog by remember { mutableStateOf(false) }

    // Dropdown expanded states
    var breedDropdownExpanded by remember { mutableStateOf(false) }
    var stateDropdownExpanded by remember { mutableStateOf(false) }
    var cityDropdownExpanded by remember { mutableStateOf(false) }

    // Validations Regex & Conditions
    val nameRegex = remember { Regex("^[a-zA-Z\\s\\-']+$") }
    val breedRegex = remember { Regex("^[a-zA-Z\\s\\-/()]+$") }

    val nameErrorText = when {
        name.isBlank() -> "Pet name is required."
        name.length > 25 -> "Max 25 characters allowed."
        !name.matches(nameRegex) -> "Only letters, spaces, hyphens, and apostrophes allowed."
        else -> null
    }

    val breedErrorText = when {
        breed.isBlank() -> "Breed / species is required."
        breed.length > 35 -> "Max 35 characters allowed."
        !breed.matches(breedRegex) -> "Only letters, spaces, slashes, and parentheses allowed."
        else -> null
    }

    val isFormValid = nameErrorText == null && breedErrorText == null && description.length <= 1000

    // Gallery Picker Launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { imageUrl = it.toString() }
    }

    // Camera Capture Launcher
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success && tempCameraUri != null) {
            imageUrl = tempCameraUri.toString()
        }
    }

    fun launchCamera() {
        try {
            val photoUri = createTempPhotoUri(context)
            tempCameraUri = photoUri
            cameraLauncher.launch(photoUri)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun executeSave() {
        val healthTags = mutableListOf<String>()
        if (isVaccinated) healthTags.add("Vaccinated")
        if (isNeutered) healthTags.add("Neutered")
        if (isMicrochipped) healthTags.add("Microchipped")

        val locationString = "$selectedCity, $selectedState"

        val updatedPet = Pet(
            id = initialPet?.id ?: "",
            businessId = initialPet?.businessId ?: "shelter_01",
            name = name.trim(),
            breed = breed.trim(),
            age = age,
            gender = gender,
            healthStatus = healthTags.joinToString(" • "),
            status = status,
            imageUrl = imageUrl,
            idealSpace = idealSpace,
            activityLevel = activityLevel,
            location = locationString,
            description = description.trim()
        )
        onSave(updatedPet)
    }

    // High-Contrast Filter Chip Styling
    val highContrastChipColors = FilterChipDefaults.filterChipColors(
        selectedContainerColor = MaterialTheme.colorScheme.primary,
        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
        selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (initialPet != null) "Edit Pet Listing" else "Add New Rescued Pet",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Pet Photo Upload Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable {
                        if (imageUrl.isNotBlank()) {
                            showFullScreenPhotoViewer = true
                        } else {
                            galleryLauncher.launch("image/*")
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                if (imageUrl.isNotBlank()) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Pet Photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Small top-right circular delete button
                    IconButton(
                        onClick = { imageUrl = "" },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .size(32.dp)
                            .background(MaterialTheme.colorScheme.errorContainer, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove Photo",
                            tint = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.AddPhotoAlternate,
                            contentDescription = "Upload Photo",
                            modifier = Modifier.size(44.dp),
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Tap to view photo or select option below",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }

            // Photo Source Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { launchCamera() },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Take Photo")
                }

                OutlinedButton(
                    onClick = { galleryLauncher.launch("image/*") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoLibrary,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Gallery")
                }
            }

            // Pet Name Field with Max 25 chars and live counter
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        if (it.length <= 25) name = it
                    },
                    label = { Text("Pet Name *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = nameErrorText != null && name.isNotBlank(),
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        Text(
                            text = "${name.length}/25",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (name.length == 25) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                    },
                    supportingText = {
                        if (nameErrorText != null && name.isNotBlank()) {
                            Text(text = nameErrorText, color = MaterialTheme.colorScheme.error)
                        }
                    }
                )
            }

            // Breed / Species Field with ExposedDropdownMenuBox presets & custom input
            Column {
                ExposedDropdownMenuBox(
                    expanded = breedDropdownExpanded,
                    onExpandedChange = { breedDropdownExpanded = !breedDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = breed,
                        onValueChange = {
                            if (it.length <= 35) breed = it
                        },
                        label = { Text("Breed / Species *") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        singleLine = true,
                        isError = breedErrorText != null && breed.isNotBlank(),
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = breedDropdownExpanded)
                        },
                        supportingText = {
                            if (breedErrorText != null && breed.isNotBlank()) {
                                Text(text = breedErrorText, color = MaterialTheme.colorScheme.error)
                            }
                        }
                    )

                    ExposedDropdownMenu(
                        expanded = breedDropdownExpanded,
                        onDismissRequest = { breedDropdownExpanded = false }
                    ) {
                        breedPresets.forEach { preset ->
                            DropdownMenuItem(
                                text = { Text(preset) },
                                onClick = {
                                    breed = preset
                                    breedDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Age Selector (Range: 0..30 yrs)
            Column {
                Text(
                    text = "Age: " + if (age == 0) "Under 1 year (Baby / Puppy / Kitten)" else "$age yrs old",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
                Slider(
                    value = age.toFloat(),
                    onValueChange = { age = it.toInt() },
                    valueRange = 0f..30f,
                    steps = 29
                )
            }

            // Gender Segmented Selector Chips
            Column {
                Text(
                    text = "Gender",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Male", "Female").forEach { g ->
                        FilterChip(
                            selected = gender == g,
                            onClick = { gender = g },
                            label = { Text(g) },
                            colors = highContrastChipColors,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }

            // Health Status Chips using FlowRow
            Column {
                Text(
                    text = "Health & Medical Status",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(4.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = isVaccinated,
                        onClick = { isVaccinated = !isVaccinated },
                        label = { Text("Vaccinated") },
                        colors = highContrastChipColors,
                        shape = RoundedCornerShape(12.dp)
                    )
                    FilterChip(
                        selected = isNeutered,
                        onClick = { isNeutered = !isNeutered },
                        label = { Text("Neutered") },
                        colors = highContrastChipColors,
                        shape = RoundedCornerShape(12.dp)
                    )
                    FilterChip(
                        selected = isMicrochipped,
                        onClick = { isMicrochipped = !isMicrochipped },
                        label = { Text("Microchipped") },
                        colors = highContrastChipColors,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            // Compatibility Requirements (Ideal Space & Activity Level)
            Column {
                Text(
                    text = "Ideal Housing Space",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Apartment", "House with Yard").forEach { space ->
                        FilterChip(
                            selected = idealSpace == space,
                            onClick = { idealSpace = space },
                            label = { Text(space) },
                            colors = highContrastChipColors,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }

            Column {
                Text(
                    text = "Activity Level",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Low", "Medium", "High").forEach { act ->
                        FilterChip(
                            selected = activityLevel == act,
                            onClick = { activityLevel = act },
                            label = { Text(act) },
                            colors = highContrastChipColors,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }

            // Malaysia State & City Cascading Dropdowns
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Location (Malaysia State & City)",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )

                // State Dropdown
                ExposedDropdownMenuBox(
                    expanded = stateDropdownExpanded,
                    onExpandedChange = { stateDropdownExpanded = !stateDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedState,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("State / Territory *") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = stateDropdownExpanded)
                        }
                    )

                    ExposedDropdownMenu(
                        expanded = stateDropdownExpanded,
                        onDismissRequest = { stateDropdownExpanded = false }
                    ) {
                        malaysiaStates.forEach { st ->
                            DropdownMenuItem(
                                text = { Text(st) },
                                onClick = {
                                    selectedState = st
                                    val availableCities = malaysiaCitiesMap[st] ?: emptyList()
                                    selectedCity = availableCities.firstOrNull() ?: ""
                                    stateDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // City Dropdown
                val cityOptions = malaysiaCitiesMap[selectedState] ?: emptyList()
                ExposedDropdownMenuBox(
                    expanded = cityDropdownExpanded,
                    onExpandedChange = { cityDropdownExpanded = !cityDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedCity,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("City / Area *") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = cityDropdownExpanded)
                        }
                    )

                    ExposedDropdownMenu(
                        expanded = cityDropdownExpanded,
                        onDismissRequest = { cityDropdownExpanded = false }
                    ) {
                        cityOptions.forEach { ct ->
                            DropdownMenuItem(
                                text = { Text(ct) },
                                onClick = {
                                    selectedCity = ct
                                    cityDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Status Selector
            Column {
                Text(
                    text = "Listing Status",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Available", "Adopted").forEach { st ->
                        FilterChip(
                            selected = status == st,
                            onClick = { status = st },
                            label = { Text(st) },
                            colors = highContrastChipColors,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }

            // Description with Max 1000 Live Counter
            Column {
                OutlinedTextField(
                    value = description,
                    onValueChange = {
                        if (it.length <= 1000) description = it
                    },
                    label = { Text("Pet Bio / Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 6,
                    shape = RoundedCornerShape(12.dp),
                    supportingText = {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            Text(
                                text = "${description.length} / 1000",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (description.length == 1000) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Save Action Button
            Button(
                onClick = {
                    if (isFormValid) {
                        if (imageUrl.isBlank()) {
                            showNoPhotoWarningDialog = true
                        } else {
                            executeSave()
                        }
                    }
                },
                enabled = isFormValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = if (initialPet != null) "Save Changes" else "Save Pet Listing",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
            }

            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Cancel")
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Full-Screen Photo Viewer Dialog
    if (showFullScreenPhotoViewer && imageUrl.isNotBlank()) {
        Dialog(
            onDismissRequest = { showFullScreenPhotoViewer = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.9f)),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Full Screen Pet Photo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    contentScale = ContentScale.Fit
                )

                IconButton(
                    onClick = { showFullScreenPhotoViewer = false },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(24.dp)
                        .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White
                    )
                }
            }
        }
    }

    // Friendly Non-blocking No Photo Warning Dialog
    if (showNoPhotoWarningDialog) {
        AlertDialog(
            onDismissRequest = { showNoPhotoWarningDialog = false },
            title = { Text("No Photo Added", fontWeight = FontWeight.Bold) },
            text = { Text("Listings with photos receive significantly higher adopter inquiries. Would you like to add a photo now?") },
            confirmButton = {
                Button(onClick = { showNoPhotoWarningDialog = false }) {
                    Text("Add Photo")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showNoPhotoWarningDialog = false
                        executeSave()
                    }
                ) {
                    Text("Save Anyway")
                }
            }
        )
    }
}

/**
 * Creates a FileProvider Uri for a temporary photo file in cache/pet_photos.
 */
private fun createTempPhotoUri(context: Context): Uri {
    val photoDir = File(context.cacheDir, "pet_photos").apply {
        if (!exists()) mkdirs()
    }
    val photoFile = File(photoDir, "pet_${System.currentTimeMillis()}.jpg")
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        photoFile
    )
}
