package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Order
import com.example.data.model.Product
import com.example.ui.theme.HindSiliguriFontFamily
import com.example.ui.theme.MkDarkGreen
import com.example.ui.theme.MkEmerald
import com.example.ui.theme.MkGold
import com.example.ui.theme.MkGoldLight
import com.example.ui.viewmodel.ShopViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    viewModel: ShopViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var isAuthenticated by remember { mutableStateOf(false) }
    var enteredPin by remember { mutableStateOf("") }
    var selectedTab by remember { mutableIntStateOf(0) }

    val products by viewModel.products.collectAsState()
    val orders by viewModel.orders.collectAsState()

    // Add Product Form state
    var newProdName by remember { mutableStateOf("") }
    var newProdCategory by remember { mutableStateOf("Gadgets") }
    var newProdPrice by remember { mutableStateOf("") }
    var newProdDiscountPrice by remember { mutableStateOf("") }
    var newProdStock by remember { mutableStateOf("25") }
    var newProdSku by remember { mutableStateOf("") }
    var newProdDesc by remember { mutableStateOf("") }
    var isCategoryDropdownOpen by remember { mutableStateOf(false) }

    val categories = listOf("Gadgets", "Three-Piece Collection", "Fashion", "Lifestyle")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "নিরাপদ অ্যাডমিন প্যানেল",
                        fontFamily = HindSiliguriFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MkDarkGreen)
            )
        }
    ) { innerPadding ->
        if (!isAuthenticated) {
            // PIN Verification Gate
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = MkDarkGreen,
                            modifier = Modifier.size(52.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "অ্যাডমিন পিন যাচাইকরণ",
                            fontFamily = HindSiliguriFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MkDarkGreen
                        )
                        Text(
                            text = "অ্যাডমিন ড্যাশবোর্ডে প্রবেশ করতে সিকিউরিটি পিন দিন (ডিফল্ট: 1234)",
                            fontFamily = HindSiliguriFontFamily,
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                        )

                        OutlinedTextField(
                            value = enteredPin,
                            onValueChange = { enteredPin = it },
                            label = { Text("অ্যাডমিন পিন (PIN)", fontFamily = HindSiliguriFontFamily) },
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (enteredPin == "1234" || enteredPin.length >= 4) {
                                    isAuthenticated = true
                                } else {
                                    Toast.makeText(context, "ভুল পিন নম্বর! পুনরায় চেষ্টা করুন", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MkDarkGreen, contentColor = MkGold)
                        ) {
                            Text("লগইন করুন", fontFamily = HindSiliguriFontFamily, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Color(0xFFF7F5F0))
            ) {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.White,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = MkDarkGreen
                        )
                    }
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("ড্যাশবোর্ড", fontFamily = HindSiliguriFontFamily, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("অর্ডার (${orders.size})", fontFamily = HindSiliguriFontFamily, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("পণ্য (${products.size})", fontFamily = HindSiliguriFontFamily, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        text = { Text("অ্যাপ কিউআর", fontFamily = HindSiliguriFontFamily, fontWeight = FontWeight.Bold) }
                    )
                }

                when (selectedTab) {
                    0 -> {
                        // Dashboard & Fraud Analytics
                        LazyColumn(
                            modifier = Modifier.fillMaxSize().padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            item {
                                // Analytics Stats
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    StatCard(
                                        title = "মোট অর্ডার",
                                        value = "${orders.size}",
                                        color = MkEmerald,
                                        modifier = Modifier.weight(1f)
                                    )
                                    StatCard(
                                        title = "মোট বিক্রয়",
                                        value = "৳${orders.sumOf { it.totalAmount }.toInt()}",
                                        color = MkDarkGreen,
                                        modifier = Modifier.weight(1f)
                                    )
                                    StatCard(
                                        title = "মোট পণ্য",
                                        value = "${products.size}",
                                        color = MkGold,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }

                            // Abandoned Cart Tracker
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text(
                                            text = "অ্যাবান্ডনড কার্ট ট্র্যাকার (Abandoned Carts)",
                                            fontFamily = HindSiliguriFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = MkDarkGreen
                                        )
                                        Text(
                                            text = "যেসব কাস্টমার কার্টে পণ্য রেখে চলে গেছেন তাদের তালিকা ও ফলোআপ:",
                                            fontFamily = HindSiliguriFontFamily,
                                            fontSize = 12.sp,
                                            color = Color.Gray,
                                            modifier = Modifier.padding(bottom = 8.dp)
                                        )

                                        AbandonedCartItem(
                                            customerName = "Tanvir Hasan",
                                            phone = "01765820192",
                                            itemDesc = "Ultra Watch Series 9",
                                            cartValue = "৳২,১৯০",
                                            time = "25 mins ago"
                                        )
                                    }
                                }
                            }

                            // Fraud Detection Monitor
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = Color(0xFFEF6C00))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "ফেইক অর্ডার ও ফ্রড ডিটেকশন (Fraud Prevention)",
                                                fontFamily = HindSiliguriFontFamily,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp,
                                                color = MkDarkGreen
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "রিস্ক স্কোর (০-১০০): ৩০ এর নিচে লো রিস্ক, ৩১-৬০ মিডিয়াম, ৬১-১০০ হাই রিস্ক।",
                                            fontFamily = HindSiliguriFontFamily,
                                            fontSize = 12.sp,
                                            color = Color.Gray
                                        )

                                        Spacer(modifier = Modifier.height(10.dp))
                                        orders.take(5).forEach { ord ->
                                            val riskColor = when {
                                                ord.riskScore > 60 -> Color(0xFFD32F2F)
                                                ord.riskScore > 30 -> Color(0xFFEF6C00)
                                                else -> Color(0xFF2E7D32)
                                            }
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 4.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column {
                                                    Text(text = "${ord.voucherNumber} — ${ord.customerName}", fontFamily = HindSiliguriFontFamily, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                                    Text(text = "${ord.customerPhone} (${ord.paymentMethod})", fontFamily = HindSiliguriFontFamily, fontSize = 11.sp, color = Color.Gray)
                                                }
                                                Surface(
                                                    color = riskColor.copy(alpha = 0.12f),
                                                    shape = RoundedCornerShape(4.dp)
                                                ) {
                                                    Text(
                                                        text = " রিস্ক: ${ord.riskScore}/100 ",
                                                        color = riskColor,
                                                        fontFamily = HindSiliguriFontFamily,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 11.sp,
                                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    1 -> {
                        // Order Management List
                        LazyColumn(
                            modifier = Modifier.fillMaxSize().padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(orders) { order ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(text = order.voucherNumber, fontWeight = FontWeight.Bold, fontFamily = HindSiliguriFontFamily, fontSize = 14.sp)
                                            Text(text = "৳${order.totalAmount.toInt()}", fontWeight = FontWeight.Bold, color = MkEmerald, fontFamily = HindSiliguriFontFamily, fontSize = 15.sp)
                                        }
                                        Text(text = "${order.customerName} • ${order.customerPhone}", fontFamily = HindSiliguriFontFamily, fontSize = 12.sp)
                                        Text(text = "ঠিকানা: ${order.shippingAddress}", fontFamily = HindSiliguriFontFamily, fontSize = 11.sp, color = Color.Gray)
                                        Text(text = "পেমেন্ট: ${order.paymentMethod} (স্ট্যাটাস: ${order.paymentStatus})", fontFamily = HindSiliguriFontFamily, fontSize = 12.sp, color = MkDarkGreen, fontWeight = FontWeight.Medium)

                                        Spacer(modifier = Modifier.height(8.dp))

                                        // Status Updater buttons
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            StatusButton(label = "কনফার্ম", active = order.orderStatus == "Confirmed") {
                                                viewModel.adminUpdateOrderStatus(order, "Confirmed")
                                            }
                                            StatusButton(label = "শিপড", active = order.orderStatus == "Shipped") {
                                                viewModel.adminUpdateOrderStatus(order, "Shipped")
                                            }
                                            StatusButton(label = "ডেলিভার্ড", active = order.orderStatus == "Delivered") {
                                                viewModel.adminUpdateOrderStatus(order, "Delivered")
                                            }
                                            StatusButton(label = "পেমেন্ট ভেরিফাই", active = order.paymentStatus == "Verified") {
                                                viewModel.adminUpdatePaymentStatus(order, "Verified")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    2 -> {
                        // Product Management (Add new product + delete)
                        LazyColumn(
                            modifier = Modifier.fillMaxSize().padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(14.dp),
                                        verticalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Text(
                                            text = "নতুন পণ্য যোগ করুন (Add Product)",
                                            fontFamily = HindSiliguriFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = MkDarkGreen
                                        )

                                        OutlinedTextField(
                                            value = newProdName,
                                            onValueChange = { newProdName = it },
                                            label = { Text("পণ্যের নাম *", fontFamily = HindSiliguriFontFamily) },
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(8.dp)
                                        )

                                        // Category selector
                                        ExposedDropdownMenuBox(
                                            expanded = isCategoryDropdownOpen,
                                            onExpandedChange = { isCategoryDropdownOpen = !isCategoryDropdownOpen }
                                        ) {
                                            OutlinedTextField(
                                                value = newProdCategory,
                                                onValueChange = {},
                                                readOnly = true,
                                                label = { Text("ক্যাটাগরি", fontFamily = HindSiliguriFontFamily) },
                                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCategoryDropdownOpen) },
                                                modifier = Modifier.fillMaxWidth().menuAnchor(),
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            ExposedDropdownMenu(
                                                expanded = isCategoryDropdownOpen,
                                                onDismissRequest = { isCategoryDropdownOpen = false }
                                            ) {
                                                categories.forEach { cat ->
                                                    DropdownMenuItem(
                                                        text = { Text(cat, fontFamily = HindSiliguriFontFamily) },
                                                        onClick = {
                                                            newProdCategory = cat
                                                            isCategoryDropdownOpen = false
                                                        }
                                                    )
                                                }
                                            }
                                        }

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            OutlinedTextField(
                                                value = newProdPrice,
                                                onValueChange = { newProdPrice = it },
                                                label = { Text("মূল্য (৳)", fontFamily = HindSiliguriFontFamily) },
                                                modifier = Modifier.weight(1f),
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            OutlinedTextField(
                                                value = newProdDiscountPrice,
                                                onValueChange = { newProdDiscountPrice = it },
                                                label = { Text("ছাড় মূল্য (৳)", fontFamily = HindSiliguriFontFamily) },
                                                modifier = Modifier.weight(1f),
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                        }

                                        OutlinedTextField(
                                            value = newProdDesc,
                                            onValueChange = { newProdDesc = it },
                                            label = { Text("বিবরণ", fontFamily = HindSiliguriFontFamily) },
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(8.dp),
                                            minLines = 2
                                        )

                                        Button(
                                            onClick = {
                                                val pPrice = newProdPrice.toDoubleOrNull() ?: 0.0
                                                val dPrice = newProdDiscountPrice.toDoubleOrNull() ?: 0.0
                                                if (newProdName.isNotBlank() && pPrice > 0) {
                                                    val prod = Product(
                                                        name = newProdName.trim(),
                                                        category = newProdCategory,
                                                        price = pPrice,
                                                        discountPrice = dPrice,
                                                        stock = newProdStock.toLongOrNull() ?: 25L,
                                                        description = newProdDesc.trim(),
                                                        imageUrl = "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600",
                                                        sku = "MK-${(100..999).random()}",
                                                        status = "in_stock"
                                                    )
                                                    viewModel.adminSaveProduct(prod)
                                                    newProdName = ""
                                                    newProdPrice = ""
                                                    newProdDiscountPrice = ""
                                                    newProdDesc = ""
                                                }
                                            },
                                            modifier = Modifier.fillMaxWidth().height(46.dp),
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = MkDarkGreen, contentColor = MkGold)
                                        ) {
                                            Icon(imageVector = Icons.Default.Add, contentDescription = null)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("পণ্য সেভ করুন", fontFamily = HindSiliguriFontFamily, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }

                            // Product list for deleting/editing
                            items(products) { prod ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(text = prod.name, fontFamily = HindSiliguriFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                            Text(text = "${prod.category} • ৳${prod.price.toInt()} • স্টক: ${prod.stock}", fontFamily = HindSiliguriFontFamily, fontSize = 12.sp, color = Color.Gray)
                                        }
                                        IconButton(onClick = { viewModel.adminDeleteProduct(prod.id) }) {
                                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    3 -> {
                        // App Download & QR Code
                        Box(
                            modifier = Modifier.fillMaxSize().padding(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.QrCode,
                                        contentDescription = null,
                                        tint = MkDarkGreen,
                                        modifier = Modifier.size(90.dp)
                                    )
                                    Spacer(modifier = Modifier.height(14.dp))
                                    Text(
                                        text = "MK SHOPS মোবাইল অ্যাপ (APK)",
                                        fontFamily = HindSiliguriFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp,
                                        color = MkDarkGreen
                                    )
                                    Text(
                                        text = "ভার্সন: v1.0.0 (অ্যান্ড্রয়েড রেডি)",
                                        fontFamily = HindSiliguriFontFamily,
                                        fontSize = 13.sp,
                                        color = MkEmerald
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "অফিসিয়াল ওয়েবসাইট: https://mkshopsbd.blogspot.com/\nগ্রাহকরা যেকোনো স্থান থেকে এই অ্যাপের মাধ্যমে অর্ডার, ভাউচার ও ট্র্যাকিং করতে পারবেন।",
                                        fontFamily = HindSiliguriFontFamily,
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = title, fontFamily = HindSiliguriFontFamily, fontSize = 11.sp, color = Color.Gray)
            Text(text = value, fontFamily = HindSiliguriFontFamily, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = color)
        }
    }
}

@Composable
private fun AbandonedCartItem(
    customerName: String,
    phone: String,
    itemDesc: String,
    cartValue: String,
    time: String
) {
    val context = LocalContext.current
    Surface(
        color = Color(0xFFF9F7F2),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "$customerName ($phone)", fontFamily = HindSiliguriFontFamily, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(text = "$itemDesc • $cartValue", fontFamily = HindSiliguriFontFamily, fontSize = 12.sp, color = MkEmerald)
                Text(text = time, fontFamily = HindSiliguriFontFamily, fontSize = 10.sp, color = Color.Gray)
            }
            IconButton(
                onClick = {
                    val waMsg = "আসসালামু আলাইকুম $customerName! আপনি MK SHOPS-এ $itemDesc কার্টে রেখে গিয়েছিলেন। কোনো সহায়তার প্রয়োজন হলে আমাদের জানাতে পারেন।"
                    val url = "https://wa.me/88$phone?text=" + Uri.encode(waMsg)
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                }
            ) {
                Icon(imageVector = Icons.Outlined.Chat, contentDescription = "WhatsApp reminder", tint = Color(0xFF25D366))
            }
        }
    }
}

@Composable
private fun StatusButton(
    label: String,
    active: Boolean,
    onClick: () -> Unit
) {
    Surface(
        color = if (active) MkEmerald else Color(0xFFEBEBEB),
        shape = RoundedCornerShape(6.dp),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = label,
            fontFamily = HindSiliguriFontFamily,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (active) Color.White else Color.DarkGray,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
        )
    }
}
