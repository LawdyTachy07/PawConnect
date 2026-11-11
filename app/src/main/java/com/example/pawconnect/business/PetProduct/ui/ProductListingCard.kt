
package com.example.pawconnect.business.PetProduct.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pawconnect.business.PetProduct.data.BusinessProduct

@Composable
fun ProductListingCard(
    product: BusinessProduct,
    onEditClick: () -> Unit
) {
    val isSoldOut = product.stockQuantity <= 0
    val isLowStock = !isSoldOut &&
            product.stockQuantity <= product.minStockLevel

    val statusText = when {
        isSoldOut -> "Sold Out"
        isLowStock -> "Low Stock"
        else -> "Active"
    }

    val statusColor = when {
        isSoldOut -> MaterialTheme.colorScheme.errorContainer
        isLowStock -> MaterialTheme.colorScheme.tertiaryContainer
        else -> MaterialTheme.colorScheme.primaryContainer
    }

    val statusTextColor = when {
        isSoldOut -> MaterialTheme.colorScheme.onErrorContainer
        isLowStock -> MaterialTheme.colorScheme.onTertiaryContainer
        else -> MaterialTheme.colorScheme.onPrimaryContainer
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Product image
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                if (product.imageRes != null) {
                    Image(
                        painter = painterResource(
                            id = product.imageRes
                        ),
                        contentDescription = product.name,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Pets,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Product information
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = "RM %.2f".format(product.price),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = "${product.stockQuantity} in stock",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(7.dp))

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = statusColor
                ) {
                    Text(
                        text = statusText,
                        modifier = Modifier.padding(
                            horizontal = 9.dp,
                            vertical = 4.dp
                        ),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = statusTextColor
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Edit button
            OutlinedButton(
                onClick = onEditClick,
                shape = RoundedCornerShape(9.dp),
                contentPadding = PaddingValues(
                    horizontal = 13.dp,
                    vertical = 5.dp
                ),
                border = null,
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "Edit",
                    fontSize = 11.sp
                )
            }
        }
    }
}
