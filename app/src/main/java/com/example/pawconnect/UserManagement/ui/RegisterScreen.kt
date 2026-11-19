package com.example.pawconnect.UserManagement.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.rounded.Pets
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pawconnect.UserManagement.data.UserProfile
import com.example.pawconnect.UserManagement.logic.AuthViewModel

private val WarmPeachBg = Color(0xFFFFE8D6)
private val WarmBrownPrimary = Color(0xFF8B4513)
private val WarmBrownDark = Color(0xFF5D4037)
private val InputBorder = Color(0xFFE2D6CE)

@Composable
fun RegisterScreen(
    authViewModel: AuthViewModel,
    initialRole: String = "user",
    onNavigateBack: () -> Unit,
    onRegisterSuccess: (UserProfile) -> Unit
) {
    val uiState by authViewModel.uiState.collectAsState()
    val isPartner = initialRole == "business"

    var fullName by remember { mutableStateOf("") }
    var organizationName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var localError by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmPeachBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Top Brand Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(38.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = WarmBrownPrimary
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Rounded.Pets,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(color = WarmBrownDark, fontWeight = FontWeight.ExtraBold)) {
                            append("Paw")
                        }
                        withStyle(SpanStyle(color = WarmBrownPrimary, fontWeight = FontWeight.ExtraBold)) {
                            append("Connect")
                        }
                    },
                    fontSize = 20.sp
                )
            }

            // Main White Card
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                color = Color.White,
                shadowElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 22.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isPartner) "PARTNER REGISTRATION" else "USER REGISTRATION",
                                color = WarmBrownPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isPartner) "Create partner account" else "Create your account",
                                color = Color(0xFF1E293B),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isPartner) {
                                    "Manage pet adoptions, products and bookings."
                                } else {
                                    "Find pets, shop essentials and book trusted pet care."
                                },
                                color = Color.Gray,
                                fontSize = 13.sp
                            )
                        }

                        Surface(
                            modifier = Modifier
                                .size(38.dp)
                                .clickable {
                                    authViewModel.clearMessages()
                                    onNavigateBack()
                                },
                            shape = CircleShape,
                            border = BorderStroke(1.dp, InputBorder),
                            color = Color.White
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = WarmBrownPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    RegLabel("Full name")
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it; localError = null },
                        placeholder = { Text("e.g. Aina Nabila", fontSize = 13.sp, color = Color.Gray) },
                        leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null, tint = WarmBrownPrimary) },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = InputBorder,
                            focusedBorderColor = WarmBrownPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (isPartner) {
                        Spacer(modifier = Modifier.height(12.dp))
                        RegLabel("Organization / Business name")
                        OutlinedTextField(
                            value = organizationName,
                            onValueChange = { organizationName = it; localError = null },
                            placeholder = { Text("e.g. Happy Tails Pet Centre", fontSize = 13.sp, color = Color.Gray) },
                            leadingIcon = { Icon(Icons.Outlined.Business, contentDescription = null, tint = WarmBrownPrimary) },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = InputBorder,
                                focusedBorderColor = WarmBrownPrimary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    RegLabel("Email address")
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it; localError = null },
                        placeholder = { Text("e.g. name@email.com", fontSize = 13.sp, color = Color.Gray) },
                        leadingIcon = {
                            Text("@", color = WarmBrownPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = InputBorder,
                            focusedBorderColor = WarmBrownPrimary
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    RegLabel("Phone number")
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it; localError = null },
                        placeholder = { Text("e.g. 60 12 345 6789", fontSize = 13.sp, color = Color.Gray) },
                        leadingIcon = {
                            Text("+", color = WarmBrownPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = InputBorder,
                            focusedBorderColor = WarmBrownPrimary
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    RegLabel("Password")
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it; localError = null },
                        placeholder = { Text("Minimum 6 characters", fontSize = 13.sp, color = Color.Gray) },
                        leadingIcon = {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .background(WarmBrownPrimary, CircleShape)
                            )
                        },
                        trailingIcon = {
                            TextButton(onClick = { passwordVisible = !passwordVisible }) {
                                Text(
                                    text = if (passwordVisible) "Hide" else "Show",
                                    color = WarmBrownPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = InputBorder,
                            focusedBorderColor = WarmBrownPrimary
                        ),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    RegLabel("Confirm password")
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it; localError = null },
                        placeholder = { Text("Repeat your password", fontSize = 13.sp, color = Color.Gray) },
                        leadingIcon = {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .background(WarmBrownPrimary, CircleShape)
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = InputBorder,
                            focusedBorderColor = WarmBrownPrimary
                        ),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth()
                    )

                    val displayError = localError ?: uiState.errorMessage
                    displayError?.let { err ->
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = err, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            if (password != confirmPassword) {
                                localError = "Passwords do not match."
                            } else {
                                authViewModel.register(
                                    email = email,
                                    password = password,
                                    fullName = fullName,
                                    role = initialRole,
                                    phoneNumber = phoneNumber,
                                    organizationName = organizationName,
                                    onSuccess = onRegisterSuccess
                                )
                            }
                        },
                        enabled = !uiState.isLoading,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = WarmBrownPrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = if (isPartner) "Create Partner account" else "Create User account",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                authViewModel.clearMessages()
                                onNavigateBack()
                            },
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = "Already have an account? ", color = Color.Gray, fontSize = 12.sp)
                        Text(
                            text = "Login",
                            color = WarmBrownPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun RegLabel(text: String) {
    Text(text = text, color = WarmBrownDark, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    Spacer(modifier = Modifier.height(6.dp))
}