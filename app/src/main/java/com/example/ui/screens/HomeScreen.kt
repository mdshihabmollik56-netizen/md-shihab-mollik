package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Product
import com.example.ui.theme.HindSiliguriFontFamily
import com.example.ui.theme.MkDarkGreen
import com.example.ui.theme.MkEmerald
import com.example.ui.theme.MkGold
import com.example.ui.theme.MkGoldLight
import com.example.ui.theme.PlayfairDisplayFontFamily
import com.example.ui.viewmodel.ShopViewModel
import kotlinx.coroutines.delay

data class HeroSlide(
    val badge: String,
    val title: String,
    val subtitle: String,
    val priceNotice: String,
    val ctaText: String,
    val gradientColors: List<Color>,
    val imageUrl: String,
    val targetCategory: String
)

data class CircularCategoryItem(
    val key: String,
    val labelBn: String,
    val iconEmoji: String,
    val countLabel: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: ShopViewModel,
    onProductClick: (Product) -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToReseller: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onNavigateToAiChat: () -> Unit,
    onNavigateToTutorials: () -> Unit,
    onOpenDeals: () -> Unit = {}
) {
    val context = LocalContext.current
    val products by viewModel.products.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val cartCount = cartItems.sumOf { it.quantity }

    // Wishlist local state for visual feedback
    val wishlistState = remember { mutableStateMapOf<String, Boolean>() }

    // Live countdown timer for "Deals of the Day"
    var remainingSeconds by remember { mutableIntStateOf(8 * 3600 + 42 * 60 + 15) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000L)
            if (remainingSeconds > 0) remainingSeconds--
        }
    }

    val hours = remainingSeconds / 3600
    val minutes = (remainingSeconds % 3600) / 60
    val seconds = remainingSeconds % 60
    val countdownFormatted = String.format("%02d : %02d : %02d", hours, minutes, seconds)

    // Circular Category List matching eCommax
    val circularCategories = listOf(
        CircularCategoryItem("All", "সব আইটেম", "🛍️", "১০০+"),
        CircularCategoryItem("Gadgets", "গ্যাজেটস", "⚡", "৪০+"),
        CircularCategoryItem("Three-Piece Collection", "থ্রি-পিস", "👗", "৩০+"),
        CircularCategoryItem("Fashion", "ফ্যাশন", "👔", "২৫+"),
        CircularCategoryItem("Lifestyle", "লাইফস্টাইল", "✨", "২০+")
    )

    // Hero Slides matching eCommax electronics and lifestyle banners
    val heroSlides = listOf(
        HeroSlide(
            badge = "হট ডিল • ৩০% ছাড়",
            title = "ALIENWARE GAMING RIG",
            subtitle = "RTX 4080 GPU, i9 14th Gen, লিকুইড কুলিং গেমিং পাওয়ার",
            priceNotice = "৳১,৭৯,০০০",
            ctaText = "অর্ডার করুন",
            gradientColors = listOf(Color(0xFF0F172A), Color(0xFF1E293B), Color(0xFF0369A1)),
            imageUrl = "https://images.unsplash.com/photo-1587202372775-e229f172b9d7?w=600",
            targetCategory = "Gadgets"
        ),
        HeroSlide(
            badge = "সুপার সাউন্ড • ওয়াটারপ্রুফ",
            title = "BOOMBOX BASSLINE X",
            subtitle = "৩৬০° স্টেরিও ডিপ বাস, ২৪ ঘণ্টা প্লেটাইম স্পিকার",
            priceNotice = "৳৩,৬৫০",
            ctaText = "এখনই কিনুন",
            gradientColors = listOf(MkDarkGreen, MkEmerald, Color(0xFF265A46)),
            imageUrl = "https://images.unsplash.com/photo-1545454675-3531b543be5d?w=600",
            targetCategory = "Gadgets"
        ),
        HeroSlide(
            badge = "স্মার্ট হেলথ • কলিং",
            title = "ULTRA WATCH SERIES 9",
            subtitle = "AMOLED অলওয়েজ-অন ডিসপ্লে ও ওয়্যারলেস ফাস্ট চার্জিং",
            priceNotice = "৳২,১৯০",
            ctaText = "ডিল নিন",
            gradientColors = listOf(Color(0xFF2B0938), Color(0xFF4A154B), Color(0xFF6B21A8)),
            imageUrl = "https://images.unsplash.com/photo-1579586337278-3befd40fd17a?w=600",
            targetCategory = "Gadgets"
        ),
        HeroSlide(
            badge = "ঈদ ও ওয়েডিং এক্সক্লুসিভ",
            title = "MK SHOPS থ্রি-পিস",
            subtitle = "লাক্সারি অরগ্যানজা ও কাশ্মীরি এমব্রয়ডারি কালেকশন",
            priceNotice = "৳২,৪৫০",
            ctaText = "কালেকশন দেখুন",
            gradientColors = listOf(Color(0xFF102B22), Color(0xFF2E4F41), Color(0xFF785B24)),
            imageUrl = "https://images.unsplash.com/photo-1610030469983-98e550d6193c?w=600",
            targetCategory = "Three-Piece Collection"
        )
    )

    val pagerState = rememberPagerState(pageCount = { heroSlides.size })

    // Auto-scroll hero slider every 4.5 seconds
    LaunchedEffect(pagerState) {
        while (true) {
            delay(4500L)
            val nextPage = (pagerState.currentPage + 1) % heroSlides.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    val filteredProducts = products.filter { prod ->
        val matchesCategory = selectedCategory == "All" || prod.category.equals(selectedCategory, ignoreCase = true)
        val matchesSearch = searchQuery.isBlank() ||
                prod.name.contains(searchQuery, ignoreCase = true) ||
                prod.category.contains(searchQuery, ignoreCase = true) ||
                prod.description.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    // Separate deals of the day products (those with discounts)
    val dealsProducts = products.filter { it.discountPrice > 0 && it.discountPrice < it.price }.take(6)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { viewModel.setSelectedCategory("All") }
                    ) {
                        Surface(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape),
                            color = MkGoldLight.copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, MkGold)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "MK",
                                    fontFamily = PlayfairDisplayFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MkGold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "MK SHOPS",
                                    fontFamily = PlayfairDisplayFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = MkGold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = MkGold.copy(alpha = 0.25f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = " OFFICIAL ",
                                        fontFamily = HindSiliguriFontFamily,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MkGoldLight
                                    )
                                }
                            }
                            Text(
                                text = "Premium Online Shopping • Bangladesh",
                                fontFamily = HindSiliguriFontFamily,
                                fontSize = 10.sp,
                                color = Color.White.copy(alpha = 0.82f)
                            )
                        }
                    }
                },
                actions = {
                    // WhatsApp Quick Call/Order
                    IconButton(onClick = {
                        val waUrl = "https://wa.me/8801765820192?text=Hello%20MK%20SHOPS%2C%20I%20want%20to%20order."
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(waUrl)))
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.Chat,
                            contentDescription = "WhatsApp Chat",
                            tint = Color(0xFF25D366)
                        )
                    }

                    // Order Tracking Icon
                    IconButton(onClick = onNavigateToOrders) {
                        Icon(
                            imageVector = Icons.Default.ReceiptLong,
                            contentDescription = "Orders",
                            tint = Color.White
                        )
                    }

                    // Cart Icon with Badge
                    IconButton(onClick = onNavigateToCart) {
                        BadgedBox(
                            badge = {
                                if (cartCount > 0) {
                                    Badge(
                                        containerColor = MkGold,
                                        contentColor = MkDarkGreen
                                    ) {
                                        Text("$cartCount", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Shopping Cart",
                                tint = Color.White
                            )
                        }
                    }

                    // Admin Shortcut
                    IconButton(onClick = onNavigateToAdmin) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = "Admin",
                            tint = MkGold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MkDarkGreen)
            )
        },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // WhatsApp Order Floating Button
                FloatingActionButton(
                    onClick = {
                        val waUrl = "https://wa.me/8801765820192?text=আসসালামু%20আলাইকুম%2C%20MK%20SHOPS%20থেকে%20পণ্য%20অর্ডার%20করতে%20চাই।"
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(waUrl))
                        context.startActivity(intent)
                    },
                    containerColor = Color(0xFF25D366),
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier.size(50.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Chat,
                        contentDescription = "WhatsApp Order",
                        modifier = Modifier.size(26.dp)
                    )
                }

                // AI Support Assistant Floating Button
                FloatingActionButton(
                    onClick = onNavigateToAiChat,
                    containerColor = MkGold,
                    contentColor = MkDarkGreen,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.SmartToy,
                            contentDescription = "এআই সাপোর্ট",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "AI সাপোর্ট",
                            fontFamily = HindSiliguriFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF7F8F7)),
            contentPadding = PaddingValues(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Search Bar with Modern Border (Full Width)
            item(span = { GridItemSpan(2) }) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            "পণ্য, গ্যাজেট বা থ্রি-পিস সার্চ করুন...",
                            fontFamily = HindSiliguriFontFamily,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MkEmerald
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MkEmerald,
                        unfocusedBorderColor = Color(0xFFDDE3DF),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    singleLine = true
                )
            }

            // 2. High-Tech Hero Slider (HorizontalPager with Indicator Dots)
            item(span = { GridItemSpan(2) }) {
                Column {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        shape = RoundedCornerShape(18.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                    ) {
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.fillMaxSize()
                        ) { page ->
                            val slide = heroSlides[page]
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Brush.horizontalGradient(slide.gradientColors))
                                    .clickable {
                                        viewModel.setSelectedCategory(slide.targetCategory)
                                    }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(
                                        modifier = Modifier.weight(1.3f)
                                    ) {
                                        Surface(
                                            color = MkGold,
                                            shape = RoundedCornerShape(4.dp),
                                            modifier = Modifier.padding(bottom = 6.dp)
                                        ) {
                                            Text(
                                                text = " ${slide.badge} ",
                                                color = MkDarkGreen,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = HindSiliguriFontFamily
                                            )
                                        }

                                        Text(
                                            text = slide.title,
                                            fontFamily = PlayfairDisplayFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 17.sp,
                                            color = Color.White,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        Spacer(modifier = Modifier.height(2.dp))

                                        Text(
                                            text = slide.subtitle,
                                            fontFamily = HindSiliguriFontFamily,
                                            fontSize = 11.sp,
                                            color = Color.White.copy(alpha = 0.85f),
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis,
                                            lineHeight = 15.sp
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = slide.priceNotice,
                                                fontFamily = HindSiliguriFontFamily,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp,
                                                color = MkGoldLight
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Surface(
                                                color = MkGold,
                                                shape = RoundedCornerShape(6.dp)
                                            ) {
                                                Text(
                                                    text = " ${slide.ctaText} ▶ ",
                                                    color = MkDarkGreen,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    fontFamily = HindSiliguriFontFamily,
                                                    modifier = Modifier.padding(vertical = 3.dp, horizontal = 6.dp)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    // Product visual thumbnail on slide
                                    Box(
                                        modifier = Modifier
                                            .size(105.dp)
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(Color.White.copy(alpha = 0.15f))
                                    ) {
                                        AsyncImage(
                                            model = slide.imageUrl,
                                            contentDescription = slide.title,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Pager Indicator Dots
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(heroSlides.size) { index ->
                            val isSelected = pagerState.currentPage == index
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 3.dp)
                                    .height(6.dp)
                                    .width(if (isSelected) 22.dp else 6.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) MkEmerald else Color.LightGray.copy(alpha = 0.6f))
                            )
                        }
                    }
                }
            }

            // 3. Circular Category Icons Row (eCommax Style)
            item(span = { GridItemSpan(2) }) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "জনপ্রিয় ক্যাটাগরি",
                            fontFamily = HindSiliguriFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MkDarkGreen
                        )
                        Text(
                            text = "সব দেখুন",
                            fontFamily = HindSiliguriFontFamily,
                            fontSize = 12.sp,
                            color = MkEmerald,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable { viewModel.setSelectedCategory("All") }
                        )
                    }

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        contentPadding = PaddingValues(vertical = 6.dp)
                    ) {
                        items(circularCategories) { item ->
                            val isSelected = selectedCategory == item.key
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clickable { viewModel.setSelectedCategory(item.key) }
                                    .width(68.dp)
                            ) {
                                Surface(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(CircleShape),
                                    color = if (isSelected) MkDarkGreen else Color.White,
                                    border = BorderStroke(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) MkGold else Color(0xFFE2E8E4)
                                    ),
                                    shadowElevation = if (isSelected) 4.dp else 1.dp
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = item.iconEmoji,
                                            fontSize = 24.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = item.labelBn,
                                    fontFamily = HindSiliguriFontFamily,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) MkDarkGreen else Color(0xFF334155),
                                    textAlign = TextAlign.Center,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Text(
                                    text = item.countLabel,
                                    fontFamily = HindSiliguriFontFamily,
                                    fontSize = 9.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }

            // 4. "DEALS OF THE DAY" (ডিলস অব দ্য ডে) Countdown Section (eCommax Signature)
            if (dealsProducts.isNotEmpty()) {
                item(span = { GridItemSpan(2) }) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, MkGold.copy(alpha = 0.4f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            // Section Header with Countdown Timer
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        color = Color(0xFFFF5722),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Whatshot,
                                            contentDescription = "Hot Deals",
                                            tint = Color.White,
                                            modifier = Modifier
                                                .padding(4.dp)
                                                .size(16.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "ডিলস অব দ্য ডে",
                                            fontFamily = HindSiliguriFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = MkDarkGreen
                                        )
                                        Text(
                                            text = "DEALS OF THE DAY",
                                            fontFamily = PlayfairDisplayFontFamily,
                                            fontSize = 10.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }

                                // Countdown Timer Pill
                                Surface(
                                    color = Color(0xFF1E293B),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.FlashOn,
                                            contentDescription = null,
                                            tint = MkGold,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = countdownFormatted,
                                            fontFamily = PlayfairDisplayFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = MkGold
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Horizontal Scrolling Deals Cards
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(dealsProducts) { dealProd ->
                                    DealCardItem(
                                        product = dealProd,
                                        onClick = { onProductClick(dealProd) },
                                        onAddToCart = { viewModel.addToCart(dealProd) },
                                        isWishlisted = wishlistState[dealProd.id] == true,
                                        onToggleWishlist = {
                                            wishlistState[dealProd.id] = !(wishlistState[dealProd.id] ?: false)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 5. Trust Badges Row (eCommax 4-Column Feature Bar)
            item(span = { GridItemSpan(2) }) {
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFE2E8E4)),
                    shadowElevation = 1.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp, horizontal = 6.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TrustBadgeItem(
                            icon = Icons.Default.LocalShipping,
                            title = "ফ্রি ডেলিভারি",
                            subtitle = "কোড FREEDEL",
                            tint = MkEmerald
                        )
                        TrustBadgeItem(
                            icon = Icons.Default.Security,
                            title = "১০০% অরিজিনাল",
                            subtitle = "ব্র্যান্ড গ্যারান্টি",
                            tint = Color(0xFF0284C7)
                        )
                        TrustBadgeItem(
                            icon = Icons.Default.CheckCircle,
                            title = "৭ দিনে রিটার্ন",
                            subtitle = "সহজ রিপ্লেসমেন্ট",
                            tint = Color(0xFF16A34A)
                        )
                        TrustBadgeItem(
                            icon = Icons.Default.SupportAgent,
                            title = "২৪/৭ সাপোর্ট",
                            subtitle = "এআই ও হোয়াটসঅ্যাপ",
                            tint = MkGold
                        )
                    }
                }
            }

            // 6. Delivery Rate Strip with Copyable Coupon Code
            item(span = { GridItemSpan(2) }) {
                Surface(
                    color = MkDarkGreen,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "🚚 ডেলিভারি চার্জ:",
                                fontFamily = HindSiliguriFontFamily,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MkGoldLight
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "ঢাকা ৳৭০ | বাইরে ৳১৩০ | ক্যাশ অন ডেলিভারি",
                                fontFamily = HindSiliguriFontFamily,
                                fontSize = 11.sp,
                                color = Color.White
                            )
                        }

                        Text(
                            text = "টিউটোরিয়াল ▶",
                            fontFamily = HindSiliguriFontFamily,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MkGold,
                            modifier = Modifier.clickable { onNavigateToTutorials() }
                        )
                    }
                }
            }

            // 7. Catalog Section Header with Total Items Counter
            item(span = { GridItemSpan(2) }) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (selectedCategory == "All") "সকল প্রিমিয়াম পণ্য" else selectedCategory,
                            fontFamily = HindSiliguriFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = MkDarkGreen
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = MkEmerald.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = " (${filteredProducts.size} টি পণ্য) ",
                                fontFamily = HindSiliguriFontFamily,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MkEmerald
                            )
                        }
                    }

                    if (selectedCategory != "All") {
                        Text(
                            text = "সব দেখুন ✕",
                            fontFamily = HindSiliguriFontFamily,
                            fontSize = 12.sp,
                            color = Color.Red,
                            modifier = Modifier.clickable { viewModel.setSelectedCategory("All") }
                        )
                    }
                }
            }

            // 8. Product Grid Items
            if (filteredProducts.isEmpty()) {
                item(span = { GridItemSpan(2) }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Outlined.ShoppingBag,
                                contentDescription = null,
                                tint = Color.LightGray,
                                modifier = Modifier.size(54.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "কোনো পণ্য পাওয়া যায়নি",
                                fontFamily = HindSiliguriFontFamily,
                                color = Color.Gray,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            } else {
                items(filteredProducts) { product ->
                    ProductCard(
                        product = product,
                        onClick = { onProductClick(product) },
                        onAddToCart = { viewModel.addToCart(product) },
                        onBuyNowDirect = {
                            viewModel.addToCart(product)
                            onNavigateToCart()
                        },
                        isWishlisted = wishlistState[product.id] == true,
                        onToggleWishlist = {
                            wishlistState[product.id] = !(wishlistState[product.id] ?: false)
                        }
                    )
                }
            }

            // Bottom Spacing for Floating Buttons & Navigation
            item(span = { GridItemSpan(2) }) {
                Spacer(modifier = Modifier.height(70.dp))
            }
        }
    }
}

@Composable
fun DealCardItem(
    product: Product,
    onClick: () -> Unit,
    onAddToCart: () -> Unit,
    isWishlisted: Boolean,
    onToggleWishlist: () -> Unit
) {
    val discountPercent = if (product.price > 0 && product.discountPrice > 0) {
        (((product.price - product.discountPrice) / product.price) * 100).toInt()
    } else 0

    Card(
        modifier = Modifier
            .width(180.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA)),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            // Image Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(125.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
            ) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Discount Tag
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

                // Wishlist Icon
                IconButton(
                    onClick = onToggleWishlist,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(28.dp)
                ) {
                    Icon(
                        imageVector = if (isWishlisted) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Wishlist",
                        tint = if (isWishlisted) Color.Red else Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Rating & Reviews
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = MkGold,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = "4.9 (১২৮)",
                    fontFamily = HindSiliguriFontFamily,
                    fontSize = 10.sp,
                    color = Color.Gray
                )
            }

            // Title
            Text(
                text = product.name,
                fontFamily = HindSiliguriFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                color = Color(0xFF1E293B),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Price Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 3.dp)
            ) {
                Text(
                    text = "৳${product.discountPrice.toInt()}",
                    fontFamily = HindSiliguriFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MkEmerald
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "৳${product.price.toInt()}",
                    fontFamily = HindSiliguriFontFamily,
                    fontSize = 10.sp,
                    color = Color.Gray,
                    textDecoration = TextDecoration.LineThrough
                )
            }

            // Stock Progress Bar
            Column(modifier = Modifier.padding(vertical = 2.dp)) {
                LinearProgressIndicator(
                    progress = { 0.72f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = Color(0xFFFF5722),
                    trackColor = Color(0xFFFFCCBC)
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "স্টক বাকি: ১২ টি | বিক্রি: ৩৮",
                    fontFamily = HindSiliguriFontFamily,
                    fontSize = 9.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Add to Cart Button
            Button(
                onClick = onAddToCart,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp),
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MkDarkGreen,
                    contentColor = MkGold
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = "কার্টে নিন",
                    fontFamily = HindSiliguriFontFamily,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun TrustBadgeItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    tint: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(76.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = title,
            fontFamily = HindSiliguriFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            color = MkDarkGreen,
            textAlign = TextAlign.Center
        )
        Text(
            text = subtitle,
            fontFamily = HindSiliguriFontFamily,
            fontSize = 8.5.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ProductCard(
    product: Product,
    onClick: () -> Unit,
    onAddToCart: () -> Unit,
    onBuyNowDirect: () -> Unit,
    isWishlisted: Boolean,
    onToggleWishlist: () -> Unit
) {
    val context = LocalContext.current
    val discountPercent = if (product.price > 0 && product.discountPrice > 0 && product.discountPrice < product.price) {
        (((product.price - product.discountPrice) / product.price) * 100).toInt()
    } else 0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, Color(0xFFEAEAEA))
    ) {
        Column {
            // Product Image Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.05f)
                    .background(Color(0xFFF3F4F6))
            ) {
                if (product.imageUrl.isNotBlank()) {
                    AsyncImage(
                        model = product.imageUrl,
                        contentDescription = product.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ShoppingBag,
                            contentDescription = null,
                            tint = Color.LightGray,
                            modifier = Modifier.size(44.dp)
                        )
                    }
                }

                // Discount Badge
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

                // Wishlist Icon
                IconButton(
                    onClick = onToggleWishlist,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(2.dp)
                        .size(32.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.85f),
                        modifier = Modifier.size(26.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isWishlisted) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (isWishlisted) Color.Red else Color.Gray,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }

                // Category pill on bottom
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

            // Product Details
            Column(modifier = Modifier.padding(10.dp)) {
                // Rating
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = MkGold,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "4.9",
                        fontFamily = HindSiliguriFontFamily,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF475569)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "• ইন স্টক",
                        fontFamily = HindSiliguriFontFamily,
                        fontSize = 9.sp,
                        color = Color(0xFF16A34A),
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

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

                // Price display
                val displayPrice = if (product.discountPrice > 0) product.discountPrice else product.price
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "৳${displayPrice.toInt()}",
                        fontFamily = HindSiliguriFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MkEmerald
                    )
                    if (product.discountPrice > 0 && product.discountPrice < product.price) {
                        Spacer(modifier = Modifier.width(5.dp))
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

                // Dual Actions: Cart & WhatsApp Order
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Button(
                        onClick = onAddToCart,
                        modifier = Modifier
                            .weight(1f)
                            .height(32.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MkDarkGreen,
                            contentColor = MkGold
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = "কার্ট",
                            fontFamily = HindSiliguriFontFamily,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Direct WhatsApp Buy
                    OutlinedButton(
                        onClick = {
                            val waText = "হ্যালো MK SHOPS, আমি '${product.name}' (মূল্য: ৳${displayPrice.toInt()}) অর্ডার করতে চাই।"
                            val uri = Uri.parse("https://wa.me/8801765820192?text=${Uri.encode(waText)}")
                            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                        },
                        modifier = Modifier
                            .size(32.dp),
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
