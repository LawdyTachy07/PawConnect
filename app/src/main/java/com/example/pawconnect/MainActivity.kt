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
import com.example.pawconnect.ui.components.PawConnectBottomBar
import com.example.pawconnect.ui.components.PawConnectTopAppBar
import com.example.pawconnect.ui.theme.PawConnectTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pawconnect.user.product.logic.UserProductViewModel

// Import User Screens
import com.example.pawconnect.user.adopt.ui.UserAdoptScreen
import com.example.pawconnect.user.product.ui.UserShopScreen
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
                PawConnectMainShell()
            }
        }
    }
}

@Composable
fun PawConnectMainShell() {
    // Role Switch: false = User Mode, true = Business Mode
    // Tap the top-right initials badge ("US" / "HT") to switch roles anytime during testing
    var isBusinessRole by remember { mutableStateOf(false) }

    // Track currently selected bottom tab index (0 to 3)
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val userProductViewModel: UserProductViewModel = viewModel()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            PawConnectTopAppBar(
                subtitle = if (isBusinessRole) "Happy Tails Pet Centre" else "User Access",
                initials = if (isBusinessRole) "HT" else "US",
                onProfileClick = {
                    isBusinessRole = !isBusinessRole
                    selectedTabIndex = if (isBusinessRole) 1 else 0
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
                // BUSINESS SCREENS
                when (selectedTabIndex) {
                    0 -> BusinessOverviewScreen()
                    1 -> BusinessListingsScreen()
                    2 -> BusinessStockScreen()
                    3 -> BusinessScheduleScreen()
                }
            } else {
                // USER SCREENS
                when (selectedTabIndex) {
                    0 -> UserAdoptScreen()
                    1 -> UserShopScreen(
                        shopViewModel = userProductViewModel,
                        onProfileClick = {
                            selectedTabIndex = 3
                        },
                        onBottomItemSelected = { index ->
                            selectedTabIndex = index
                        },
                        showAppChrome = false
                    )
                    2 -> UserBookScreen()
                    3 -> UserProfileScreen()
                }
            }
        }
    }
}