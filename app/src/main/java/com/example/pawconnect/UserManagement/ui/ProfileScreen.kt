package com.example.pawconnect.UserManagement.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pawconnect.UserManagement.logic.AuthViewModel
import com.example.pawconnect.ui.theme.PawConnectColors

@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    onLoggedOut: () -> Unit
) {
    val uiState by authViewModel.uiState.collectAsState()
    val user = uiState.currentUser

    var fullName by remember(user) { mutableStateOf(user?.fullName ?: "") }
    var phoneNumber by remember(user) { mutableStateOf(user?.phoneNumber ?: "") }
    var address by remember(user) { mutableStateOf(user?.address ?: "") }
    var organizationName by remember(user) { mutableStateOf(user?.organizationName ?: "") }
    var showDeactivateDialog by remember { mutableStateOf(false) }

    if (user == null) return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PawConnectColors.Background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Soft Blue Hero Card matching Adopt Screen
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = PawConnectColors.SoftBlue),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    modifier = Modifier.size(56.dp),
                    tint = PawConnectColors.PrimaryBlue
                )
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = user.fullName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = PawConnectColors.HeaderBlue
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = user.email,
                        fontSize = 13.sp,
                        color = PawConnectColors.TextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = PawConnectColors.PrimaryBlue
                    ) {
                        Text(
                            text = if (user.role == "business") "Partner Account" else "Pet Owner Account",
                            color = PawConnectColors.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // White Form Card matching UserCheckoutScreen
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = PawConnectColors.White),
            border = BorderStroke(1.dp, PawConnectColors.Border)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Account Details",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = PawConnectColors.TextPrimary
                )

                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Name") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = PawConnectColors.Border,
                        focusedBorderColor = PawConnectColors.PrimaryBlue
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                if (user.role == "business") {
                    OutlinedTextField(
                        value = organizationName,
                        onValueChange = { organizationName = it },
                        label = { Text("Organization / Shop Name") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = PawConnectColors.Border,
                            focusedBorderColor = PawConnectColors.PrimaryBlue
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Phone Number") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = PawConnectColors.Border,
                        focusedBorderColor = PawConnectColors.PrimaryBlue
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Delivery / Home Address") },
                    minLines = 2,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = PawConnectColors.Border,
                        focusedBorderColor = PawConnectColors.PrimaryBlue
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                uiState.successMessage?.let { msg ->
                    Surface(
                        color = PawConnectColors.SuccessBackground,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = msg,
                            color = PawConnectColors.Success,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                uiState.errorMessage?.let { err ->
                    Surface(
                        color = PawConnectColors.ErrorBackground,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = err,
                            color = PawConnectColors.Error,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                Button(
                    onClick = {
                        authViewModel.updateProfile(
                            fullName = fullName,
                            phoneNumber = phoneNumber,
                            address = address,
                            organizationName = organizationName
                        )
                    },
                    enabled = !uiState.isLoading,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PawConnectColors.ButtonBlue),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("Save Changes", fontWeight = FontWeight.Bold, color = PawConnectColors.White)
                }
            }
        }

        OutlinedButton(
            onClick = { authViewModel.logout(onLoggedOut) },
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, PawConnectColors.PrimaryBlue),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = PawConnectColors.White,
                contentColor = PawConnectColors.PrimaryBlue
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Log Out", fontWeight = FontWeight.Bold)
        }

        TextButton(
            onClick = { showDeactivateDialog = true },
            colors = ButtonDefaults.textButtonColors(contentColor = PawConnectColors.Coral),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.DeleteForever, contentDescription = null)
            Spacer(modifier = Modifier.width(6.dp))
            Text("Deactivate Account", fontWeight = FontWeight.SemiBold)
        }
    }

    if (showDeactivateDialog) {
        AlertDialog(
            onDismissRequest = { showDeactivateDialog = false },
            title = { Text("Deactivate Account?") },
            text = { Text("Your account will be disabled and you will be signed out immediately.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeactivateDialog = false
                        authViewModel.deactivateAccount(onLoggedOut)
                    }
                ) {
                    Text("Deactivate", color = PawConnectColors.Error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeactivateDialog = false }) {
                    Text("Cancel", color = PawConnectColors.PrimaryBlue)
                }
            }
        )
    }
}