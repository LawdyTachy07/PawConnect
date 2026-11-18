package com.example.pawconnect.business.listings.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.pawconnect.business.listings.logic.BusinessListingsViewModel
import com.example.pawconnect.ui.components.CollapsingHeaderLayout
import com.example.pawconnect.user.adopt.data.AdoptionApplication
import com.example.pawconnect.user.adopt.data.Pet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessListingsScreen(
    modifier: Modifier = Modifier,
    viewModel: BusinessListingsViewModel = viewModel(),
    // Added for the Listings hub: tells the parent when Add / Edit Pet opens or closes
    onEditorOpenChange: (Boolean) -> Unit = {}
) {
    val petListings by viewModel.petListings.collectAsState()
    val applications by viewModel.incomingApplications.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var showAddEditScreen by remember { mutableStateOf(false) }
    var petToEdit by remember { mutableStateOf<Pet?>(null) }
    var petToDelete by remember { mutableStateOf<Pet?>(null) }

    LaunchedEffect(showAddEditScreen) {
        onEditorOpenChange(showAddEditScreen)
    }

    // Phone back button / gesture closes the Add / Edit form instead of leaving the app
    BackHandler(enabled = showAddEditScreen) {
        showAddEditScreen = false
    }

    // Application Action Confirmation states
    var applicationToApprove by remember { mutableStateOf<AdoptionApplication?>(null) }
    var applicationToReject by remember { mutableStateOf<AdoptionApplication?>(null) }

    val pendingAppsCount = applications.count { it.status == "Pending" }

    // Full screen Add / Edit Pet Listing mode
    if (showAddEditScreen) {
        AddEditPetScreen(
            initialPet = petToEdit,
            onDismiss = { showAddEditScreen = false },
            onSave = { updatedPet ->
                if (petToEdit != null) {
                    viewModel.updatePet(updatedPet)
                } else {
                    viewModel.addPet(updatedPet)
                }
                showAddEditScreen = false
            }
        )
        return
    }

    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        // Header + Pet Listings / Adoption Requests tabs scroll away together with the list
        CollapsingHeaderLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            header = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    // Header
                    Column(
                        modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 12.dp)
                    ) {
                        Text(
                            text = "BUSINESS WORKSPACE",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Pet & Adoption Management",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }

                    // Segmented Tabs
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
                                    text = "Pet Listings (${petListings.size})",
                                    fontWeight = if (selectedTabIndex == 0) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        )
                        Tab(
                            selected = selectedTabIndex == 1,
                            onClick = { selectedTabIndex = 1 },
                            text = {
                                Text(
                                    text = "Adoption Requests (${applications.size})",
                                    fontWeight = if (selectedTabIndex == 1) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        )
                    }
                }
            }
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                if (selectedTabIndex == 0) {
                    // Tab 1: Pet Listings Catalogue
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "YOUR CATALOGUE",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                )

                                Button(
                                    onClick = {
                                        petToEdit = null
                                        showAddEditScreen = true
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Add New", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                                }
                            }
                        }

                        if (petListings.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(40.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No pet listings added yet. Tap '+ Add New' to create one.", style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        } else {
                            items(petListings, key = { it.id }) { pet ->
                                PetListingCard(
                                    pet = pet,
                                    onEdit = {
                                        petToEdit = pet
                                        showAddEditScreen = true
                                    },
                                    onDelete = {
                                        petToDelete = pet
                                    }
                                )
                            }
                        }
                    }
                } else {
                    // Tab 2: Adoption Requests
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "INCOMING APPLICATIONS",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                )
                                if (pendingAppsCount > 0) {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = MaterialTheme.colorScheme.tertiaryContainer
                                    ) {
                                        Text(
                                            text = "$pendingAppsCount Pending",
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onTertiaryContainer
                                        )
                                    }
                                }
                            }
                        }

                        if (applications.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(40.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No incoming adoption requests.", style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        } else {
                            items(applications, key = { it.id }) { app ->
                                val pet = petListings.find { it.id == app.petId }
                                val petName = pet?.name ?: "Rescued Pet"

                                AdoptionRequestCard(
                                    application = app,
                                    petName = petName,
                                    onApproveClick = { applicationToApprove = app },
                                    onRejectClick = { applicationToReject = app }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Delete Pet Confirmation Dialog
    if (petToDelete != null) {
        AlertDialog(
            onDismissRequest = { petToDelete = null },
            title = { Text("Delete Pet Listing?", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to remove ${petToDelete!!.name} from your catalogue? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deletePet(petToDelete!!.id)
                        petToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { petToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Approve Application Confirmation Dialog
    if (applicationToApprove != null) {
        val app = applicationToApprove!!
        val pet = petListings.find { it.id == app.petId }
        val petName = pet?.name ?: "Rescued Pet"

        AlertDialog(
            onDismissRequest = { applicationToApprove = null },
            title = { Text("Confirm Approval", fontWeight = FontWeight.Bold) },
            text = {
                Text("Are you sure you want to approve ${app.applicantName}'s application for $petName? Approving will set the application status to Approved and mark $petName as Adopted.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.approveApplication(app.id)
                        applicationToApprove = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Approve Application")
                }
            },
            dismissButton = {
                TextButton(onClick = { applicationToApprove = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Reject Application Confirmation Dialog
    if (applicationToReject != null) {
        val app = applicationToReject!!

        AlertDialog(
            onDismissRequest = { applicationToReject = null },
            title = { Text("Confirm Rejection", fontWeight = FontWeight.Bold) },
            text = {
                Text("Are you sure you want to reject ${app.applicantName}'s application? The applicant will be notified.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.rejectApplication(app.id)
                        applicationToReject = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Reject Application")
                }
            },
            dismissButton = {
                TextButton(onClick = { applicationToReject = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

/**
 * Pet Listing Catalogue Card with 68.dp Rounded Thumbnail Box
 */
@Composable
private fun PetListingCard(
    pet: Pet,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 68.dp Rounded Thumbnail Box (Left side)
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    if (pet.imageUrl.isNotBlank()) {
                        AsyncImage(
                            model = pet.imageUrl,
                            contentDescription = pet.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Pets,
                            contentDescription = pet.name,
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Info & Status
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = pet.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        val isAvailable = pet.status.equals("Available", ignoreCase = true) || pet.status.contains("Ready", ignoreCase = true)
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isAvailable) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = pet.status,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (isAvailable) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Text(
                        text = "${pet.breed} • ${pet.age} yrs • ${pet.gender}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Location: ${pet.location}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Compatibility Requirements Badges
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.secondaryContainer) {
                    Text(
                        text = "Space: ${pet.idealSpace}",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }

                Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.tertiaryContainer) {
                    Text(
                        text = "Activity: ${pet.activityLevel}",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }

            if (pet.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = pet.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Actions: Edit and Delete Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = onEdit,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit")
                }

                TextButton(
                    onClick = onDelete,
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Delete")
                }
            }
        }
    }
}

/**
 * Adoption Request Card (Business Review)
 */
@Composable
private fun AdoptionRequestCard(
    application: AdoptionApplication,
    petName: String,
    onApproveClick: () -> Unit,
    onRejectClick: () -> Unit
) {
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Application for $petName",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Applicant: ${application.applicantName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Match Score Badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "${application.matchScore}% Match",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Adopter lifestyle summary
            Text(
                text = "• Housing: ${application.housingType}\n• Free Hours: ${application.dailyFreeHours} hrs/day\n• Experience: ${if (application.hasExperience) "Yes" else "First-time owner"}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (application.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Notes: \"${application.notes}\"",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Application Status & Approval Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val (statusBg, statusFg) = when (application.status) {
                    "Approved" -> Color(0xFFD4EDDA) to Color(0xFF155724)
                    "Rejected" -> Color(0xFFF8D7DA) to Color(0xFF721C24)
                    else -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = statusBg
                ) {
                    Text(
                        text = "Status: ${application.status}",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = statusFg
                    )
                }

                if (application.status == "Pending") {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = onRejectClick,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Reject")
                        }

                        Button(
                            onClick = onApproveClick,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Approve")
                        }
                    }
                }
            }
        }
    }
}
