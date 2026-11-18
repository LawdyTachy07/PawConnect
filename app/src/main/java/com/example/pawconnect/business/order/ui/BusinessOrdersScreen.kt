package com.example.pawconnect.business.orders

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.pawconnect.user.order.data.OrderStatus
import com.example.pawconnect.user.order.data.UserOrder
import com.example.pawconnect.user.order.logic.OrderViewModel
import com.example.pawconnect.user.order.ui.OrderStatusBadge

@Composable
fun BusinessOrdersScreen(
    orderViewModel: OrderViewModel,
    modifier: Modifier = Modifier
) {
    val orders by orderViewModel.orders.collectAsState()
    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Processing", "Shipped", "Completed")

    val filteredOrders = remember(orders, selectedFilter) {
        if (selectedFilter == "All") orders
        else orders.filter { it.status.label == selectedFilter }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Filter Chips Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filters.forEach { filter ->
                val isSelected = selectedFilter == filter
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedFilter = filter },
                    label = { Text(filter, style = MaterialTheme.typography.labelMedium) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    border = null
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredOrders, key = { it.orderId }) { order ->
                BusinessOrderCard(
                    order = order,
                    onMarkShipped = {
                        orderViewModel.updateOrderStatus(order.orderId, OrderStatus.SHIPPED)
                    },
                    onMarkCompleted = {
                        orderViewModel.updateOrderStatus(order.orderId, OrderStatus.COMPLETED)
                    }
                )
            }
        }
    }
}

@Composable
private fun BusinessOrderCard(
    order: UserOrder,
    onMarkShipped: () -> Unit,
    onMarkCompleted: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "#${order.orderId} • ${order.customerName}",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${order.customerPhone} | ${order.orderDate}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                OrderStatusBadge(status = order.status)
            }

            Text(
                text = "Address: ${order.deliveryAddress}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

            order.items.forEach { item ->
                Text(
                    text = "• ${item.productName} × ${item.quantity} (RM %.2f)".format(item.price * item.quantity),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Net Payout Box (95% to partner after 5% platform fee)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(10.dp))
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Gross: RM %.2f (-5%% fee)".format(order.subtotal),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Net Payout: RM %.2f".format(order.partnerPayout),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Action Buttons for Business Partner
            when (order.status) {
                OrderStatus.PROCESSING -> {
                    Button(
                        onClick = onMarkShipped,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Dispatch / Mark as Shipped", style = MaterialTheme.typography.labelLarge)
                    }
                }
                OrderStatus.SHIPPED -> {
                    Button(
                        onClick = onMarkCompleted,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary
                        )
                    ) {
                        Text("Confirm Delivery Completed", style = MaterialTheme.typography.labelLarge)
                    }
                }
                else -> Unit
            }
        }
    }
}