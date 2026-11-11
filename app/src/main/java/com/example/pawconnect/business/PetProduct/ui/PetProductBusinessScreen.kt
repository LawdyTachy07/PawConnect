
package com.example.pawconnect.business.PetProduct.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pawconnect.business.PetProduct.logic.PetProductBusinessViewModel
import com.example.pawconnect.ui.components.PawConnectTopAppBar
import com.example.pawconnect.ui.components.PawConnectBottomBar
import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview
import com.example.pawconnect.ui.theme.PawConnectTheme

@Composable
fun PetProductBusinessScreen(
    viewModel: PetProductBusinessViewModel,
    onAddNewProductClicked: () -> Unit = {},
    onEditProductClicked: (String) -> Unit = {},
    onNotificationClicked: () -> Unit = {},
    onProfileClicked: () -> Unit = {},
    onBottomNavigationClicked: (Int) -> Unit = {}
) {
    val products by viewModel.products.collectAsState()

    var selectedTab by remember { mutableIntStateOf(1) }
    var selectedBottomItem by remember { mutableIntStateOf(1) }

    val tabNames = listOf("Pets", "Products", "Services")

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,

        // Top App Bar
        // Top App Bar
        topBar = {
            PawConnectTopAppBar(
                subtitle = "Happy Tails Pet Centre",
                initials = "HT",
                onProfileClick = onProfileClicked
            )
        },

        // Bottom Navigation Bar
        bottomBar = {
            PawConnectBottomBar(
                selectedIndex = selectedBottomItem,
                onItemSelected = {
                    selectedBottomItem = it
                    onBottomNavigationClicked(it)
                }
            )
        }

    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {

            // Workspace heading and notification
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "BUSINESS WORKSPACE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.5.sp
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = "Listings",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Text(
                        text = "Manage everything in one simple place.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    onClick = onNotificationClicked,
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 1.dp,
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Category Tabs
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Row(
                    modifier = Modifier.padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    tabNames.forEachIndexed { index, title ->

                        val isSelected = selectedTab == index

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(11.dp))
                                .background(
                                    if (isSelected) {
                                        MaterialTheme.colorScheme.surface
                                    } else {
                                        MaterialTheme.colorScheme.surfaceVariant
                                    }
                                )
                                .clickable {
                                    selectedTab = index
                                }
                                .padding(vertical = 11.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = title,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) {
                                    FontWeight.SemiBold
                                } else {
                                    FontWeight.Normal
                                },
                                color = if (isSelected) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Catalogue heading
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column {
                    Text(
                        text = "YOUR CATALOGUE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = when (selectedTab) {
                            0 -> "Pet listings"
                            1 -> "Product listings"
                            else -> "Services listings"
                        },
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                if (selectedTab == 1) {
                    Button(
                        onClick = onAddNewProductClicked,
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(
                            horizontal = 13.dp,
                            vertical = 9.dp
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )

                        Spacer(modifier = Modifier.width(3.dp))

                        Text(
                            text = "Add new",
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Catalogue content
            when (selectedTab) {

                1 -> {
                    if (products.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No products available yet.\nAdd your first product!",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 12.dp)
                        ) {
                            items(
                                items = products,
                                key = { it.id }
                            ) { product ->

                                ProductListingCard(
                                    product = product,
                                    onEditClick = {
                                        onEditProductClicked(product.id)
                                    }
                                )
                            }
                        }
                    }
                }

                0, 2 -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (selectedTab == 0) {
                                "Your pet listings will appear here."
                            } else {
                                "Your service listings will appear here."
                            },
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}


@Preview(
    name = "PawConnect Business - Light Mode",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun PetProductBusinessLightPreview() {
    PawConnectTheme(darkTheme = false) {
        PetProductBusinessScreen(
            viewModel = PetProductBusinessViewModel()
        )
    }
}

@Preview(
    name = "PawConnect Business - Dark Mode",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    showSystemUi = true
)
@Composable
fun PetProductBusinessDarkPreview() {
    PawConnectTheme(darkTheme = true) {
        PetProductBusinessScreen(
            viewModel = PetProductBusinessViewModel()
        )
    }
}
