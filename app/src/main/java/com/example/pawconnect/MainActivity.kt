package com.example.pawconnect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pawconnect.UserManagement.data.UserProfile
import com.example.pawconnect.UserManagement.logic.AuthViewModel
import com.example.pawconnect.UserManagement.ui.LoginScreen
import com.example.pawconnect.UserManagement.ui.ProfileScreen
import com.example.pawconnect.UserManagement.ui.RegisterScreen
import com.example.pawconnect.ui.components.PawConnectBottomBar
import com.example.pawconnect.ui.components.PawConnectTopAppBar
import com.example.pawconnect.ui.theme.PawConnectTheme
import com.example.pawconnect.user.product.logic.UserProductViewModel
import com.example.pawconnect.user.order.logic.OrderViewModel

// Import User Screens
import com.example.pawconnect.user.adopt.ui.UserAdoptScreen
import com.example.pawconnect.user.product.ui.UserShopScreen
import com.example.pawconnect.user.book.ui.UserBookScreen

// Import Business Screens
import com.example.pawconnect.business.overview.ui.BusinessOverviewScreen
import com.example.pawconnect.business.listings.ui.BusinessListingsHubScreen
import com.example.pawconnect.business.schedule.ui.BusinessScheduleScreen
import com.example.pawconnect.business.orders.BusinessOrdersScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PawConnectTheme(darkTheme = false) {
                val authViewModel: AuthViewModel = viewModel()
                val authState by authViewModel.uiState.collectAsState()
                var registerRole by remember { mutableStateOf<String?>(null) }

                val currentUser = authState.currentUser
                if (currentUser == null) {
                    val targetRole = registerRole
                    if (targetRole != null) {
                        RegisterScreen(
                            authViewModel = authViewModel,
                            initialRole = targetRole,
                            onNavigateBack = { registerRole = null },
                            onRegisterSuccess = { registerRole = null }
                        )
                    } else {
                        LoginScreen(
                            authViewModel = authViewModel,
                            onNavigateToRegister = { role -> registerRole = role },
                            onLoginSuccess = { }
                        )
                    }
                } else {
                    PawConnectMainShell(
                        currentUser = currentUser,
                        authViewModel = authViewModel
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PawConnectMainShell(
    currentUser: UserProfile,
    authViewModel: AuthViewModel
) {
    // Automatically set mode based on authenticated user's role ("business" vs "user")
    val isBusinessRole = currentUser.role == "business"

    // Track currently selected bottom tab index (0 to 3)
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    // Controls opening ProfileScreen overlay for Business / Partner accounts
    var showPartnerProfile by remember { mutableStateOf(false) }

    val userProductViewModel: UserProductViewModel = viewModel()
    val orderViewModel: OrderViewModel = viewModel() // Initialized shared OrderViewModel for Module 6

    // True while a full-screen Add / Edit form is open: hides the top bar and bottom bar
    var hideAppChrome by remember { mutableStateOf(false) }

    // Compute initials from user's full name or organization name
    val initials = remember(currentUser.fullName, currentUser.organizationName) {
        val sourceName = if (isBusinessRole && !currentUser.organizationName.isNullOrBlank()) {
            currentUser.organizationName
        } else {
            currentUser.fullName
        }
        sourceName
            .split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .joinToString("") { it.first().uppercase() }
            .ifBlank { "PC" }
    }

    if (isBusinessRole && showPartnerProfile) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Partner Profile") },
                    navigationIcon = {
                        IconButton(onClick = { showPartnerProfile = false }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                ProfileScreen(
                    authViewModel = authViewModel,
                    onLoggedOut = {
                        showPartnerProfile = false
                    }
                )
            }
        }
        return
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (!hideAppChrome) {
                PawConnectTopAppBar(
                    subtitle = if (isBusinessRole) {
                        currentUser.organizationName ?: currentUser.fullName
                    } else {
                        currentUser.fullName
                    },
                    initials = initials,
                    onProfileClick = {
                        if (isBusinessRole) {
                            showPartnerProfile = true
                        } else {
                            selectedTabIndex = 3
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (!hideAppChrome) {
                PawConnectBottomBar(
                    isBusiness = isBusinessRole,
                    selectedIndex = selectedTabIndex,
                    onItemSelected = { index ->
                        selectedTabIndex = index
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                // The top/bottom bars already cover the status & navigation bars.
                // Without this, every inner Scaffold adds that space again (the white gaps).
                .consumeWindowInsets(innerPadding)
        ) {
            if (isBusinessRole) {
                // BUSINESS SCREENS
                when (selectedTabIndex) {
                    0 -> BusinessOverviewScreen()
                    1 -> BusinessListingsHubScreen(
                        // "View Order" in the Products section jumps to the Orders tab
                        onViewOrderClick = { selectedTabIndex = 2 },
                        // Add / Edit Pet or Product opens full screen
                        onEditorOpenChange = { isOpen -> hideAppChrome = isOpen }
                    )
                    2 -> BusinessOrdersScreen(orderViewModel = orderViewModel)
                    3 -> BusinessScheduleScreen()
                }
            } else {
                // USER SCREENS
                when (selectedTabIndex) {
                    0 -> UserAdoptScreen()
                    1 -> UserShopScreen(
                        shopViewModel = userProductViewModel,
                        orderViewModel = orderViewModel,
                        onProfileClick = {
                            selectedTabIndex = 3
                        },
                        onBottomItemSelected = { index ->
                            selectedTabIndex = index
                        },
                        showAppChrome = false
                    )
                    2 -> UserBookScreen()
                    3 -> ProfileScreen(
                        authViewModel = authViewModel,
                        onLoggedOut = { }
                    )
                }
            }
        }
    }
}