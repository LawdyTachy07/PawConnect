package com.example.pawconnect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.pawconnect.business.PetProduct.logic.PetProductBusinessViewModel
import com.example.pawconnect.business.PetProduct.ui.PetProductBusinessScreen
import com.example.pawconnect.ui.components.PawConnectBottomBar
import com.example.pawconnect.ui.components.PawConnectTopAppBar
import com.example.pawconnect.ui.theme.PawConnectTheme

// Import User Screens
import com.example.pawconnect.user.adopt.ui.UserAdoptScreen
import com.example.pawconnect.user.shop.ui.UserShopScreen
import com.example.pawconnect.user.book.ui.UserBookScreen
import com.example.pawconnect.user.profile.ui.UserProfileScreen

// Import Business Screens
import com.example.pawconnect.business.overview.ui.BusinessOverviewScreen
import com.example.pawconnect.business.listings.ui.BusinessListingsScreen
import com.example.pawconnect.business.stock.ui.BusinessStockScreen
import com.example.pawconnect.business.schedule.ui.BusinessScheduleScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PawConnectTheme(darkTheme = false) {
                PetProductBusinessScreen(PetProductBusinessViewModel())
            }
        }
    }
}

@Composable
fun PawConnectMainShell() {
    // Role Switch: false = User Mode, true = Business Mode
    // Tap the top-right initials badge ("US" / "HT") to switch roles anytime during testing!
    var isBusinessRole by remember { mutableStateOf(false) }

    // Track currently selected bottom tab index (0 to 3)
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            PawConnectTopAppBar(
                subtitle = if (isBusinessRole) "Happy Tails Pet Centre" else "User Access • Adopter",
                initials = if (isBusinessRole) "HT" else "US",
                onProfileClick = {
                    // Toggle between User and Business mode, and reset tab to default
                    isBusinessRole = !isBusinessRole
                    selectedTabIndex = if (isBusinessRole) 1 else 0 // Business defaults to Listings (1), User defaults to Adopt (0)
                }
            )
        },
        bottomBar = {
            PawConnectBottomBar(
                isBusiness = isBusinessRole,
                selectedIndex = selectedTabIndex,
                onItemSelected = { index ->
                    selectedTabIndex = index
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (isBusinessRole) {
                // ================= BUSINESS SCREENS =================
                when (selectedTabIndex) {
                    0 -> BusinessOverviewScreen()   // Teammate
                    1 -> BusinessListingsScreen()   // Your Part (Module 3 & 4)
                    2 -> BusinessStockScreen()      // Teammate
                    3 -> BusinessScheduleScreen()   // Teammate
                }
            } else {
                // ================= USER SCREENS =================
                when (selectedTabIndex) {
                    0 -> UserAdoptScreen()          // Your Part (Module 4 & Compatibility)
                    1 -> UserShopScreen()           // Teammate
                    2 -> UserBookScreen()           // Teammate
                    3 -> UserProfileScreen()        // Teammate
                }
            }
        }
    }
}