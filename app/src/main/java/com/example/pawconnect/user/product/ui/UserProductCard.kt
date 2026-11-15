package com.example.pawconnect.user.product.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pawconnect.user.product.data.UserPetProduct
import kotlin.text.uppercase
import androidx.compose.ui.tooling.preview.Preview
import com.example.pawconnect.ui.theme.PawConnectTheme
@Composable
fun UserProductCard(
    product: UserPetProduct,
    quantityInCart: Int,
    onAddToCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(17.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(Modifier.padding(9.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.08f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                if (!product.imageUrl.isNullOrBlank()) {
                    coil.compose.AsyncImage(
                        model = product.imageUrl,
                        contentDescription = product.name,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text(
                        text = when (product.category) {
                            "Food" -> "🥣"
                            "Toys" -> "🧸"
                            else -> "🐾"
                        },
                        fontSize = 42.sp
                    )
                }
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(6.dp),
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)
                ) {
                    Text(
                        product.badge,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
                        fontSize = 8.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(
                product.category.uppercase(),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Text(
                product.name,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
            Text(
                product.description,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 9.sp,
                maxLines = 1
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "RM %.2f".format(product.price),
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(7.dp))
            Button(
                onClick = onAddToCart,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 5.dp, vertical = 7.dp),
                shape = RoundedCornerShape(11.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(3.dp))
                Text(
                    if (quantityInCart == 0) "Add to Cart" else "Add more ($quantityInCart)",
                    fontSize = 10.sp
                )
            }
        }
    }
}



@Preview(
    name = "User Product Card - Light",
    showBackground = true,
    backgroundColor = 0xFFF5F8FA,
    widthDp = 220,
    heightDp = 390
)
@Composable
fun UserProductCardLightPreview() {
    PawConnectTheme(darkTheme = false) {
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.padding(12.dp)
        ) {
            UserProductCard(
                product = UserPetProduct(
                    id = "1",
                    name = "Daily Balance",
                    description = "Adult dog food · 2 kg",
                    category = "Food",
                    price = 48.00,
                    imageUrl = null,
                    badge = "In stock"
                ),
                quantityInCart = 0,
                onAddToCart = {},
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(
    name = "User Product Card - Dark",
    showBackground = true,
    backgroundColor = 0xFF121212,
    widthDp = 220,
    heightDp = 390
)
@Composable
fun UserProductCardDarkPreview() {
    PawConnectTheme(darkTheme = true) {
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.padding(12.dp)
        ) {
            UserProductCard(
                product = UserPetProduct(
                    id = "2",
                    name = "Play Ball Duo",
                    description = "Rubber toys · Set of 2",
                    category = "Toys",
                    price = 18.00,
                    imageUrl = null,
                    badge = "In stock"
                ),
                quantityInCart = 2,
                onAddToCart = {},
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
