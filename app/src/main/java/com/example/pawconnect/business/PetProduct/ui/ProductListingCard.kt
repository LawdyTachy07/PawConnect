
package com.example.pawconnect.business.PetProduct.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.pawconnect.business.PetProduct.data.BusinessProduct
import com.example.pawconnect.ui.theme.PawConnectTheme

@Composable
fun ProductListingCard(
    product: BusinessProduct,
    onRestockClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {

    val statusBackground = when (product.displayStatus) {
        "SOLD OUT" -> MaterialTheme.colorScheme.errorContainer
        "LOW STOCK" -> MaterialTheme.colorScheme.tertiaryContainer
        "INACTIVE" -> MaterialTheme.colorScheme.surfaceVariant
        else -> MaterialTheme.colorScheme.secondaryContainer
    }

    val statusTextColor = when (product.displayStatus) {
        "SOLD OUT" -> MaterialTheme.colorScheme.onErrorContainer
        "LOW STOCK" -> MaterialTheme.colorScheme.onTertiaryContainer
        "INACTIVE" -> MaterialTheme.colorScheme.onSurfaceVariant
        else -> MaterialTheme.colorScheme.onSecondaryContainer
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(17.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {

            // ---------------------------------
            // PRODUCT INFORMATION
            // ---------------------------------

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                // PRODUCT IMAGE

                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant
                        ),

                    contentAlignment = Alignment.Center
                ) {

                    when {

                        !product.imageUri.isNullOrBlank() -> {

                            AsyncImage(
                                model = product.imageUri,
                                contentDescription = product.name,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        !product.imageUrl.isNullOrBlank() -> {

                            AsyncImage(
                                model = product.imageUrl,
                                contentDescription = product.name,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        product.imageRes != null -> {

                            Image(
                                painter = painterResource(product.imageRes),
                                contentDescription = product.name,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        else -> {

                            Icon(
                                imageVector = Icons.Default.Pets,
                                contentDescription = null,
                                modifier = Modifier.size(30.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // PRODUCT DETAILS

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = product.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Product Category
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = product.category,
                            modifier = Modifier.padding(
                                horizontal = 8.dp,
                                vertical = 4.dp
                            ),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Spacer(modifier = Modifier.height(5.dp))

                    // Product Description
                    if (product.subtitle.isNotBlank()) {
                        Text(
                            text = product.subtitle,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    // Product Price
                    Text(
                        text = "RM %.2f".format(product.price),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.width(5.dp))

                // STOCK INFORMATION

                Column(
                    horizontalAlignment = Alignment.End
                ) {

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = statusBackground
                    ) {

                        Text(
                            text = product.displayStatus,

                            modifier = Modifier.padding(
                                horizontal = 8.dp,
                                vertical = 5.dp
                            ),

                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = statusTextColor
                        )
                    }

                    Spacer(modifier = Modifier.height(7.dp))

                    Text(
                        text = "${product.stockQuantity} units",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ---------------------------------
            // EDIT, DELETE AND RESTOCK BUTTONS
            // ALL INSIDE PRODUCT CARD
            // ---------------------------------

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                // EDIT BUTTON

                OutlinedButton(
                    onClick = onEditClick,
                    modifier = Modifier.weight(1f),

                    contentPadding = PaddingValues(
                        horizontal = 4.dp,
                        vertical = 7.dp
                    ),

                    shape = RoundedCornerShape(10.dp)
                ) {

                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        modifier = Modifier.size(15.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "Edit",
                        fontSize = 11.sp
                    )
                }

                // DELETE BUTTON

                OutlinedButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.weight(1f),

                    contentPadding = PaddingValues(
                        horizontal = 4.dp,
                        vertical = 7.dp
                    ),

                    shape = RoundedCornerShape(10.dp),

                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {

                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        modifier = Modifier.size(15.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "Delete",
                        fontSize = 11.sp
                    )
                }

                // RESTOCK BUTTON

                Button(
                    onClick = onRestockClick,
                    modifier = Modifier.weight(1f),

                    contentPadding = PaddingValues(
                        horizontal = 4.dp,
                        vertical = 7.dp
                    ),

                    shape = RoundedCornerShape(10.dp)
                ) {

                    Text(
                        text = "Restock",
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

// ---------------------------------
// PREVIEW
// ---------------------------------

@androidx.compose.ui.tooling.preview.Preview(
    showBackground = true
)
@Composable
private fun ProductListingCardPreview() {

    PawConnectTheme {

        Surface(
            modifier = Modifier.padding(16.dp)
        ) {

            ProductListingCard(
                product = BusinessProduct(
                    id = "1",
                    name = "Daily Balance",
                    category = "Food",
                    subtitle = "Dog food · 2 kg",
                    price = 48.00,
                    stockQuantity = 3,
                    minStockLevel = 5
                )
            )
        }
    }
}
