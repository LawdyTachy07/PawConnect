package com.example.pawconnect.business.listings.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.pawconnect.user.adopt.data.Pet


// Full-screen page to Create or Edit a Pet Listing.
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditPetScreen(
    initialPet: Pet? = null,
    onDismiss: () -> Unit,
    onSave: (Pet) -> Unit
) {
    var name by remember { mutableStateOf(initialPet?.name ?: "") }
    var breed by remember { mutableStateOf(initialPet?.breed ?: "") }
    var age by remember { mutableIntStateOf(initialPet?.age ?: 1) }
    var gender by remember { mutableStateOf(initialPet?.gender.takeIf { !it.isNullOrBlank() } ?: "Male") }
    var idealSpace by remember { mutableStateOf(initialPet?.idealSpace.takeIf { !it.isNullOrBlank() } ?: "Apartment") }
    var activityLevel by remember { mutableStateOf(initialPet?.activityLevel.takeIf { !it.isNullOrBlank() } ?: "Medium") }
    var location by remember { mutableStateOf(initialPet?.location ?: "Petaling Jaya") }
    var description by remember { mutableStateOf(initialPet?.description ?: "") }
    var status by remember { mutableStateOf(initialPet?.status.takeIf { !it.isNullOrBlank() } ?: "Available") }
    var imageUrl by remember { mutableStateOf(initialPet?.imageUrl ?: "") }

    // Health tags state
    var isVaccinated by remember { mutableStateOf(initialPet?.healthStatus?.contains("Vaccinated") ?: true) }
    var isNeutered by remember { mutableStateOf(initialPet?.healthStatus?.contains("Neutered") ?: true) }
    var isMicrochipped by remember { mutableStateOf(initialPet?.healthStatus?.contains("Microchipped") ?: true) }

    // Image Picker Launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { imageUrl = it.toString() }
    }

    val isFormValid = name.isNotBlank() && breed.isNotBlank() && location.isNotBlank()

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
            // Pet Photo Upload Box (Coil + Gallery Launcher)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable { imagePickerLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (imageUrl.isNotBlank()) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Pet Photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
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
                            text = "Tap to pick pet photo from gallery",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }

            // Pet Name
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Pet Name *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Breed
            OutlinedTextField(
                value = breed,
                onValueChange = { breed = it },
                label = { Text("Breed / Type *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Age Slider
            Column {
                Text(
                    text = "Age: $age yrs",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
                Slider(
                    value = age.toFloat(),
                    onValueChange = { age = it.toInt() },
                    valueRange = 1f..15f,
                    steps = 13
                )
            }

            // Gender Selector
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
                        shape = RoundedCornerShape(12.dp)
                    )
                    FilterChip(
                        selected = isNeutered,
                        onClick = { isNeutered = !isNeutered },
                        label = { Text("Neutered") },
                        shape = RoundedCornerShape(12.dp)
                    )
                    FilterChip(
                        selected = isMicrochipped,
                        onClick = { isMicrochipped = !isMicrochipped },
                        label = { Text("Microchipped") },
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
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }

            // Location
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location / Shelter City *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Status
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
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }

            // Description
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Pet Bio / Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Action Buttons
            Button(
                onClick = {
                    if (isFormValid) {
                        val healthTags = mutableListOf<String>()
                        if (isVaccinated) healthTags.add("Vaccinated")
                        if (isNeutered) healthTags.add("Neutered")
                        if (isMicrochipped) healthTags.add("Microchipped")

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
                            location = location.trim(),
                            description = description.trim()
                        )
                        onSave(updatedPet)
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
}
