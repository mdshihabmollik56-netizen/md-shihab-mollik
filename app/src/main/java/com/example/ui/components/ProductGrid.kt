package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.data.local.ProductEntity
import com.example.data.repository.LocalProductRepository
import com.example.ui.theme.HindSiliguriFontFamily
import com.example.ui.theme.MkDarkGreen
import com.example.ui.theme.MkEmerald
import com.example.ui.theme.MkGold
import com.example.ui.theme.MkGoldLight
import kotlinx.coroutines.launch

/**
 * ProductGrid Composable that fetches and displays items directly from a Room Database
 * for the MK SHOPS gadget and fashion inventory, using Coil for high-performance image loading.
 */
@Composable
fun ProductGrid(
    repository: LocalProductRepository,
    modifier: Modifier = Modifier,
    categoryFilter: String = "All",
    onProductClick: (ProductEntity) -> Unit = {},
    onAddToCart: (ProductEntity) -> Unit = {}
) {
    val coroutineScope = rememberCoroutineScope()
    val productsFlow = remember(categoryFilter) {
        repository.getProductsByCategory(categoryFilter)
    }
    val products by productsFlow.collectAsStateWithLifecycle(initialValue = emptyList())

    // Auto-seed initial Room inventory if currently empty
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            repository.seedIfEmpty()
        }
    }

    ProductGridContent(
        products = products,
        modifier = modifier,
        onProductClick = onProductClick,
        onAddToCart = onAddToCart
    )
}

/**
 * Pure state-driven ProductGridContent to display a grid of products loaded from Room.
 */
@Composable
fun ProductGridContent(
    products: List<ProductEntity>,
    modifier: Modifier = Modifier,
    onProductClick: (ProductEntity) -> Unit = {},
    onAddToCart: (ProductEntity) -> Unit = {}
) {
    if (products.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(40.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(
                    modifier = Modifier.size(36.dp),
                    color = MkEmerald
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "রুম ডেটাবেজ থেকে ইনভেন্টরি লোড হচ্ছে...",
                    fontFamily = HindSiliguriFontFamily,
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 175.dp),
            modifier = modifier
                .fillMaxSize()
                .widthIn(max = 1200.dp)
                .testTag("product_grid"),
            contentPadding = PaddingValues(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(
                items = products,
                key = { it.id }
            ) { product ->
                ProductGridItemCard(
                    product = product,
                    onClick = { onProductClick(product) },
                    onAddToCart = { onAddToCart(product) }
                )
            }
        }
    }
}

/**
 * Individual Product Card within the ProductGrid, using Coil (SubcomposeAsyncImage)
 * for loading product images with elegant placeholders, error states, and badges.
 */
@Composable
fun ProductGridItemCard(
    product: ProductEntity,
    onClick: () -> Unit,
    onAddToCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isFavorite by remember { mutableStateOf(false) }

    val discountPercent = if (product.price > 0 && product.discountPrice > 0 && product.discountPrice < product.price) {
        (((product.price - product.discountPrice) / product.price) * 100).toInt()
    } else 0

    val displayPrice = if (product.discountPrice > 0) product.discountPrice else product.price

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("product_card_${product.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Column {
            // Coil Image Loading Box with Badges
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.08f)
                    .background(Color(0xFFF3F4F6))
            ) {
                // Coil SubcomposeAsyncImage for robust image loading
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(product.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = product.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("product_image_${product.id}"),
                    contentScale = ContentScale.Crop,
                    loading = {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MkEmerald,
                                strokeWidth = 2.dp
                            )
                        }
                    },
                    error = {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ShoppingBag,
                                contentDescription = "Default Product",
                                tint = Color.LightGray,
                                modifier = Modifier.size(44.dp)
                            )
                        }
                    }
                )

                // Discount Badge (Top-Start)
                if (discountPercent > 0) {
                    Surface(
                        color = Color(0xFFDC2626),
                        shape = RoundedCornerShape(bottomEnd = 8.dp),
                        modifier = Modifier.align(Alignment.TopStart)
                    ) {
                        Text(
                            text = "-$discountPercent%",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = HindSiliguriFontFamily,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                // Wishlist Toggle Icon (Top-End)
                IconButton(
                    onClick = { isFavorite = !isFavorite },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(2.dp)
                        .size(32.dp)
                        .testTag("favorite_button_${product.id}")
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.size(26.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = if (isFavorite) "Remove from wishlist" else "Add to wishlist",
                                tint = if (isFavorite) Color.Red else Color.Gray,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }

                // Category Tag Pill (Bottom-Start)
                Surface(
                    color = MkDarkGreen.copy(alpha = 0.88f),
                    shape = RoundedCornerShape(topEnd = 6.dp),
                    modifier = Modifier.align(Alignment.BottomStart)
                ) {
                    Text(
                        text = product.category,
                        color = MkGoldLight,
                        fontSize = 9.sp,
                        fontFamily = HindSiliguriFontFamily,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // Product Details Section
            Column(modifier = Modifier.padding(10.dp)) {
                // Ratings and Stock Status
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = MkGold,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${product.rating} (${product.reviewCount})",
                            fontFamily = HindSiliguriFontFamily,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF475569)
                        )
                    }

                    Text(
                        text = "• ${product.badge}",
                        fontFamily = HindSiliguriFontFamily,
                        fontSize = 9.5.sp,
                        color = Color(0xFF16A34A),
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Product Name
                Text(
                    text = product.name,
                    fontFamily = HindSiliguriFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.5.sp,
                    color = Color(0xFF1E293B),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Price display with discount strikethrough
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "৳${displayPrice.toInt()}",
                        fontFamily = HindSiliguriFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MkEmerald
                    )
                    if (product.discountPrice > 0 && product.discountPrice < product.price) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "৳${product.price.toInt()}",
                            fontFamily = HindSiliguriFontFamily,
                            fontSize = 11.sp,
                            color = Color.Gray,
                            textDecoration = TextDecoration.LineThrough
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Dual Action Buttons: Add to Cart + WhatsApp Direct Order
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Button(
                        onClick = onAddToCart,
                        modifier = Modifier
                            .weight(1f)
                            .height(32.dp)
                            .testTag("add_to_cart_${product.id}"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MkDarkGreen,
                            contentColor = MkGold
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = null,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "কার্ট",
                                fontFamily = HindSiliguriFontFamily,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Direct WhatsApp Buy
                    OutlinedButton(
                        onClick = {
                            val waText = "হ্যালো MK SHOPS, আমি '${product.name}' (মূল্য: ৳${displayPrice.toInt()}) অর্ডার করতে চাই।"
                            val uri = Uri.parse("https://wa.me/8801765820192?text=${Uri.encode(waText)}")
                            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                        },
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("whatsapp_order_${product.id}"),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFF25D366)),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Chat,
                            contentDescription = "WhatsApp Order",
                            tint = Color(0xFF25D366),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
