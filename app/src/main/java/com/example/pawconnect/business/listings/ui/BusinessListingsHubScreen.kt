package com.example.pawconnect.business.listings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCut
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pawconnect.business.PetProduct.data.BusinessProduct
import com.example.pawconnect.business.PetProduct.logic.PetProductBusinessViewModel
import com.example.pawconnect.business.PetProduct.ui.PetProductBusinessScreen
import com.example.pawconnect.ui.components.CollapsingHeaderLayout
import com.example.pawconnect.ui.theme.PawConnectColors

/**
 * The three listing types a Business can manage.
 * PETS     -> Module 3 (Member 2) Pet Listing Management
 * PRODUCTS -> Module 5 (Member 3) Product Listing Management
 * SERVICES -> Module 7 (Member 4) Service Listing Management
 */
enum class ListingSection(val label: String) {
    PETS("Pets"),
    PRODUCTS("Products"),
    SERVICES("Services")
}

/**
 * Business "Listings" tab.
 * Shows a header, a low-stock notification bell and a segmented selector
 * (Pets / Products / Services). Each option opens that member's own screen.
 */
@Composable
fun BusinessListingsHubScreen(
    modifier: Modifier = Modifier,
    productViewModel: PetProductBusinessViewModel = viewModel(),
    onViewOrderClick: () -> Unit = {},
    /** Called with true when an Add / Edit form opens, false when it closes. */
    onEditorOpenChange: (Boolean) -> Unit = {}
) {
    var selectedSection by rememberSaveable { mutableStateOf(ListingSection.PETS) }
    var showLowStockAlerts by remember { mutableStateOf(false) }

    // Is an Add / Edit form open in the Pets or Products section?
    var editorOpen by remember { mutableStateOf(false) }
    val reportEditorOpen = { isOpen: Boolean ->
        editorOpen = isOpen
        onEditorOpenChange(isOpen)
    }

    // Make sure the app bars come back if this screen is closed while a form is open
    DisposableEffect(Unit) {
        onDispose { onEditorOpenChange(false) }
    }

    // Unique Feature 3: Low-Stock Notification (stock at or below minimum level)
    val products by productViewModel.products.collectAsState()
    val lowStockProducts = products.filter { it.isLowStock }

    // The header (title, bell and Pets / Products / Services selector) scrolls away
    // together with the list below it, and comes back when you scroll to the top.
    // The content always stays in the same place, so opening Add / Edit keeps its state.
    CollapsingHeaderLayout(
        modifier = modifier
            .fillMaxSize()
            .background(PawConnectColors.Background),
        header = {
            if (!editorOpen) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // ---------------- Header ----------------
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp, end = 20.dp, top = 18.dp, bottom = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Listings",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PawConnectColors.TextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Manage everything in one simple place.",
                                fontSize = 12.sp,
                                color = PawConnectColors.TextSecondary
                            )
                        }

                        LowStockBell(
                            alertCount = lowStockProducts.size,
                            onClick = { showLowStockAlerts = true }
                        )
                    }

                    // ---------------- Pets / Products / Services ----------------
                    ListingSectionSelector(
                        selected = selectedSection,
                        onSelected = { selectedSection = it },
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    ) {
        // ---------------- Selected module screen ----------------
        when (selectedSection) {
            ListingSection.PETS -> BusinessListingsScreen(
                onEditorOpenChange = reportEditorOpen
            )

            ListingSection.PRODUCTS -> PetProductBusinessScreen(
                viewModel = productViewModel,
                onViewOrderClick = onViewOrderClick,
                onEditorOpenChange = reportEditorOpen
            )

            ListingSection.SERVICES -> ServicesComingSoon()
        }
    }

    if (showLowStockAlerts) {
        LowStockAlertsDialog(
            lowStockProducts = lowStockProducts,
            onDismiss = { showLowStockAlerts = false },
            onViewProducts = {
                showLowStockAlerts = false
                selectedSection = ListingSection.PRODUCTS
            }
        )
    }
}

@Composable
private fun ListingSectionSelector(
    selected: ListingSection,
    onSelected: (ListingSection) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFE6ECF0))
            .padding(4.dp)
            .selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        ListingSection.values().forEach { section ->
            val isSelected = section == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .then(
                        if (isSelected) Modifier.shadow(2.dp, RoundedCornerShape(12.dp))
                        else Modifier
                    )
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) PawConnectColors.White else Color.Transparent)
                    .selectable(
                        selected = isSelected,
                        onClick = { onSelected(section) },
                        role = Role.Tab
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = section.label,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (isSelected) PawConnectColors.TextPrimary else PawConnectColors.TextSecondary
                )
            }
        }
    }
}

@Composable
private fun LowStockBell(
    alertCount: Int,
    onClick: () -> Unit
) {
    BadgedBox(
        badge = {
            if (alertCount > 0) {
                Badge(
                    containerColor = PawConnectColors.Coral,
                    contentColor = PawConnectColors.White
                ) {
                    Text(text = if (alertCount > 9) "9+" else alertCount.toString())
                }
            }
        }
    ) {
        Surface(
            onClick = onClick,
            modifier = Modifier.size(48.dp),
            shape = CircleShape,
            color = PawConnectColors.White,
            shadowElevation = 2.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Outlined.NotificationsNone,
                    contentDescription = if (alertCount > 0) "$alertCount low-stock alerts" else "No notifications",
                    tint = PawConnectColors.TextPrimary
                )
            }
        }
    }
}

@Composable
private fun LowStockAlertsDialog(
    lowStockProducts: List<BusinessProduct>,
    onDismiss: () -> Unit,
    onViewProducts: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Outlined.WarningAmber,
                contentDescription = null,
                tint = PawConnectColors.Warning
            )
        },
        title = { Text("Low-Stock Alerts", fontWeight = FontWeight.Bold) },
        text = {
            if (lowStockProducts.isEmpty()) {
                Text("All products are above their minimum stock level. No action needed.")
            } else {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 280.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(lowStockProducts, key = { it.id }) { product ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (product.stockQuantity <= 0) PawConnectColors.ErrorBackground
                            else PawConnectColors.WarningBackground
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                            ) {
                                Text(product.name, fontWeight = FontWeight.SemiBold)
                                Text(
                                    text = if (product.stockQuantity <= 0) {
                                        "Sold out · minimum level ${product.minStockLevel}"
                                    } else {
                                        "Only ${product.stockQuantity} left · minimum level ${product.minStockLevel}"
                                    },
                                    fontSize = 12.sp,
                                    color = if (product.stockQuantity <= 0) PawConnectColors.Error
                                    else PawConnectColors.Warning
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (lowStockProducts.isNotEmpty()) {
                TextButton(onClick = onViewProducts) { Text("Restock now") }
            } else {
                TextButton(onClick = onDismiss) { Text("OK") }
            }
        },
        dismissButton = {
            if (lowStockProducts.isNotEmpty()) {
                TextButton(onClick = onDismiss) { Text("Later") }
            }
        }
    )
}

/** Placeholder until Member 4's Service Listing (Module 7) screen is ready. */
@Composable
private fun ServicesComingSoon() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(72.dp),
            shape = CircleShape,
            color = PawConnectColors.SoftBlue
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Outlined.ContentCut,
                    contentDescription = null,
                    tint = PawConnectColors.PrimaryBlue,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Pet-Care Services",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = PawConnectColors.TextPrimary
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Grooming, bathing and other service listings will appear here.",
            fontSize = 13.sp,
            color = PawConnectColors.TextSecondary,
            textAlign = TextAlign.Center
        )
    }
}
