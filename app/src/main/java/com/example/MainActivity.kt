package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.Order
import com.example.data.model.Product
import com.example.ui.chat.AiChatViewModel
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.AiChatScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.CartCheckoutScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.OrderHistoryScreen
import com.example.ui.screens.OrderVoucherScreen
import com.example.ui.screens.ProductDetailsSheet
import com.example.ui.screens.ResellerScreen
import com.example.ui.screens.TutorialsScreen
import com.example.ui.theme.HindSiliguriFontFamily
import com.example.ui.theme.MkDarkGreen
import com.example.ui.theme.MkEmerald
import com.example.ui.theme.MkGold
import com.example.ui.theme.MkGoldLight
import com.example.ui.theme.MkShopsTheme
import com.example.ui.viewmodel.ShopViewModel
import com.example.ui.viewmodel.UiMessage
import com.example.util.configureAppCheck
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

enum class CurrentScreen {
    HOME,
    CART_CHECKOUT,
    ORDER_HISTORY,
    ORDER_VOUCHER,
    RESELLER,
    ADMIN,
    AI_CHAT,
    TUTORIALS
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // STEP 1: Mandatory App Check configuration with launch intent before any UI or Firebase calls
        configureAppCheck(this, intent)

        enableEdgeToEdge()

        setContent {
            MkShopsTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppRoot()
                }
            }
        }
    }
}

@Composable
fun AppRoot() {
    var currentUser by remember { mutableStateOf(Firebase.auth.currentUser) }

    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            currentUser = auth.currentUser
        }
        Firebase.auth.addAuthStateListener(listener)
        onDispose {
            Firebase.auth.removeAuthStateListener(listener)
        }
    }

    if (currentUser == null) {
        AuthScreen(
            onAuthSuccess = {
                currentUser = Firebase.auth.currentUser
            }
        )
    } else {
        val uid = currentUser!!.uid
        key(uid) {
            MainStoreApp(currentUserId = uid)
        }
    }
}

@Composable
fun MainStoreApp(
    currentUserId: String,
    viewModel: ShopViewModel = viewModel(key = currentUserId),
    chatViewModel: AiChatViewModel = viewModel(key = "chat_$currentUserId")
) {
    val context = LocalContext.current
    var currentScreen by remember { mutableStateOf(CurrentScreen.HOME) }
    var selectedProductForDetails by remember { mutableStateOf<Product?>(null) }
    var selectedOrderForVoucher by remember { mutableStateOf<Order?>(null) }

    val uiMessage by viewModel.uiMessage.collectAsState()
    val orders by viewModel.orders.collectAsState()

    LaunchedEffect(currentUserId) {
        viewModel.onUserAuthenticated(currentUserId)
    }

    LaunchedEffect(uiMessage) {
        uiMessage?.let { msg ->
            when (msg) {
                is UiMessage.Success -> Toast.makeText(context, msg.message, Toast.LENGTH_SHORT).show()
                is UiMessage.Error -> Toast.makeText(context, msg.message, Toast.LENGTH_LONG).show()
            }
            viewModel.clearMessage()
        }
    }

    // Handle Back Press when not on HOME
    if (currentScreen != CurrentScreen.HOME) {
        BackHandler {
            currentScreen = CurrentScreen.HOME
        }
    }

    Scaffold(
        bottomBar = {
            // Show bottom navigation on primary screens
            if (currentScreen == CurrentScreen.HOME || currentScreen == CurrentScreen.ORDER_HISTORY || currentScreen == CurrentScreen.RESELLER || currentScreen == CurrentScreen.TUTORIALS) {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = currentScreen == CurrentScreen.HOME,
                        onClick = { currentScreen = CurrentScreen.HOME },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = "হোম",
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = "হোম",
                                fontFamily = HindSiliguriFontFamily,
                                fontSize = 11.sp,
                                fontWeight = if (currentScreen == CurrentScreen.HOME) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MkEmerald,
                            selectedTextColor = MkDarkGreen,
                            indicatorColor = MkEmerald.copy(alpha = 0.15f),
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )

                    NavigationBarItem(
                        selected = false,
                        onClick = {
                            // Filter or focus on Hot Deals in Home
                            viewModel.setSelectedCategory("Gadgets")
                            currentScreen = CurrentScreen.HOME
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Whatshot,
                                contentDescription = "ডিলস",
                                tint = Color(0xFFFF5722),
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = "ডিলস",
                                fontFamily = HindSiliguriFontFamily,
                                fontSize = 11.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            unselectedIconColor = Color(0xFFFF5722),
                            unselectedTextColor = Color.Gray
                        )
                    )

                    NavigationBarItem(
                        selected = false,
                        onClick = {
                            val waUrl = "https://wa.me/8801765820192?text=আসসালামু%20আলাইকুম%2C%20MK%20SHOPS%20থেকে%20পণ্য%20অর্ডার%20করতে%20চাই।"
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(waUrl))
                            context.startActivity(intent)
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.Chat,
                                contentDescription = "হোয়াটসঅ্যাপ",
                                tint = Color(0xFF25D366),
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = "হোয়াটসঅ্যাপ",
                                fontFamily = HindSiliguriFontFamily,
                                fontSize = 11.sp,
                                color = Color(0xFF25D366),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    )

                    NavigationBarItem(
                        selected = currentScreen == CurrentScreen.ORDER_HISTORY,
                        onClick = { currentScreen = CurrentScreen.ORDER_HISTORY },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (orders.isNotEmpty()) {
                                        Badge(containerColor = MkDarkGreen, contentColor = MkGold) {
                                            Text("${orders.size}", fontSize = 10.sp)
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ReceiptLong,
                                    contentDescription = "অর্ডার",
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        },
                        label = {
                            Text(
                                text = "অর্ডার",
                                fontFamily = HindSiliguriFontFamily,
                                fontSize = 11.sp,
                                fontWeight = if (currentScreen == CurrentScreen.ORDER_HISTORY) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MkEmerald,
                            selectedTextColor = MkDarkGreen,
                            indicatorColor = MkEmerald.copy(alpha = 0.15f),
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )

                    NavigationBarItem(
                        selected = currentScreen == CurrentScreen.RESELLER,
                        onClick = { currentScreen = CurrentScreen.RESELLER },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.BusinessCenter,
                                contentDescription = "রিসেলার",
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = "রিসেলার",
                                fontFamily = HindSiliguriFontFamily,
                                fontSize = 11.sp,
                                fontWeight = if (currentScreen == CurrentScreen.RESELLER) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MkEmerald,
                            selectedTextColor = MkDarkGreen,
                            indicatorColor = MkEmerald.copy(alpha = 0.15f),
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                CurrentScreen.HOME -> {
                    HomeScreen(
                        viewModel = viewModel,
                        onProductClick = { prod ->
                            selectedProductForDetails = prod
                        },
                        onNavigateToCart = { currentScreen = CurrentScreen.CART_CHECKOUT },
                        onNavigateToOrders = { currentScreen = CurrentScreen.ORDER_HISTORY },
                        onNavigateToReseller = { currentScreen = CurrentScreen.RESELLER },
                        onNavigateToAdmin = { currentScreen = CurrentScreen.ADMIN },
                        onNavigateToAiChat = { currentScreen = CurrentScreen.AI_CHAT },
                        onNavigateToTutorials = { currentScreen = CurrentScreen.TUTORIALS }
                    )
                }
                CurrentScreen.CART_CHECKOUT -> {
                    CartCheckoutScreen(
                        viewModel = viewModel,
                        onBack = { currentScreen = CurrentScreen.HOME },
                        onOrderPlaced = { order ->
                            selectedOrderForVoucher = order
                            currentScreen = CurrentScreen.ORDER_VOUCHER
                        }
                    )
                }
                CurrentScreen.ORDER_HISTORY -> {
                    OrderHistoryScreen(
                        viewModel = viewModel,
                        onBack = { currentScreen = CurrentScreen.HOME },
                        onViewVoucher = { order ->
                            selectedOrderForVoucher = order
                            currentScreen = CurrentScreen.ORDER_VOUCHER
                        }
                    )
                }
                CurrentScreen.ORDER_VOUCHER -> {
                    val order = selectedOrderForVoucher ?: viewModel.selectedOrderVoucher.collectAsState().value
                    if (order != null) {
                        OrderVoucherScreen(
                            order = order,
                            onBack = { currentScreen = CurrentScreen.HOME }
                        )
                    } else {
                        currentScreen = CurrentScreen.HOME
                    }
                }
                CurrentScreen.RESELLER -> {
                    ResellerScreen(
                        viewModel = viewModel,
                        onBack = { currentScreen = CurrentScreen.HOME }
                    )
                }
                CurrentScreen.ADMIN -> {
                    AdminScreen(
                        viewModel = viewModel,
                        onBack = { currentScreen = CurrentScreen.HOME }
                    )
                }
                CurrentScreen.AI_CHAT -> {
                    AiChatScreen(
                        chatViewModel = chatViewModel,
                        onBack = { currentScreen = CurrentScreen.HOME }
                    )
                }
                CurrentScreen.TUTORIALS -> {
                    TutorialsScreen(
                        onBack = { currentScreen = CurrentScreen.HOME }
                    )
                }
            }

            // Product Details Bottom Sheet
            if (selectedProductForDetails != null) {
                ProductDetailsSheet(
                    product = selectedProductForDetails!!,
                    onDismiss = { selectedProductForDetails = null },
                    onAddToCart = { prod, qty ->
                        viewModel.addToCart(prod, qty)
                    },
                    onBuyNow = { prod, qty ->
                        viewModel.addToCart(prod, qty)
                        currentScreen = CurrentScreen.CART_CHECKOUT
                    }
                )
            }
        }
    }
}
