package com.example.pawconnect.business.PetProduct.ui

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.pawconnect.business.PetProduct.data.BusinessProduct
import com.example.pawconnect.ui.theme.PawConnectTheme

@Composable
fun ProductListingCard(
    product: BusinessProduct,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {

    val statusBackground = when (product.displayStatus) {
        "SOLD OUT" -> MaterialTheme.colorScheme.errorContainer
        "LOW STOCK" -> MaterialTheme.colorScheme.tertiaryContainer
        "INACTIVE" -> MaterialTheme.colorScheme.surfaceVariant
        else -> MaterialTheme.colorScheme.primaryContainer
    }

    val statusTextColor = when (product.displayStatus) {
        "SOLD OUT" -> MaterialTheme.colorScheme.onErrorContainer
        "LOW STOCK" -> MaterialTheme.colorScheme.onTertiaryContainer
        "INACTIVE" -> MaterialTheme.colorScheme.onSurfaceVariant
        else -> MaterialTheme.colorScheme.onPrimaryContainer
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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(11.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Product image
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
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

            // Product information
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

                Text(
                    text = "RM %.2f".format(product.price),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "${product.stockQuantity} in stock",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Product status
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = statusBackground
                ) {

                    Text(
                        text = product.displayStatus,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusTextColor,
                        modifier = Modifier.padding(
                            horizontal = 8.dp,
                            vertical = 4.dp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Edit and Delete actions
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                TextButton(
                    onClick = onEditClick,
                    contentPadding = PaddingValues(
                        horizontal = 10.dp,
                        vertical = 3.dp
                    )
                ) {
                    Text(
                        text = "Edit",
                        fontSize = 11.sp
                    )
                }

                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete product",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Preview(name = "Product Card - Light", showBackground = true)
@Preview(name = "Product Card - Dark", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun ProductListingCardPreview() {
    PawConnectTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            ProductListingCard(
                product = BusinessProduct(
                    id = "1",
                    name = "Salmon Skin Bites",
                    category = "Treats",
                    price = 18.00,
                    stockQuantity = 14,
                    status = "ACTIVE"
                ),
                onEditClick = {},
                onDeleteClick = {}
            )
        }
    }
}