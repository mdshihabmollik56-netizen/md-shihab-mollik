package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.HindSiliguriFontFamily
import com.example.ui.theme.MkDarkGreen
import com.example.ui.theme.MkEmerald
import com.example.ui.theme.MkGold
import com.example.ui.theme.MkGoldLight
import com.example.ui.viewmodel.ShopViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResellerScreen(
    viewModel: ShopViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val userProfile by viewModel.userProfile.collectAsState()
    val products by viewModel.products.collectAsState()
    val appConfig by viewModel.appConfig.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    var registrationName by remember { mutableStateOf(userProfile?.displayName ?: "") }
    var registrationPhone by remember { mutableStateOf(userProfile?.phone ?: "") }
    var membershipTrxId by remember { mutableStateOf("") }
    var withdrawAmount by remember { mutableStateOf("") }
    var withdrawNumber by remember { mutableStateOf("") }

    val isResellerActive = userProfile?.resellerStatus == "active"
    val isPending = userProfile?.resellerStatus == "pending"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "রিসেলার ড্যাশবোর্ড (Reseller Hub)",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF7F5F0))
        ) {
            if (!isResellerActive && !isPending) {
                // Non-reseller registration flow
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MkDarkGreen)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(
                                    text = "MK SHOPS রিসেলার নেটওয়ার্ক",
                                    fontFamily = HindSiliguriFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    color = MkGold
                                )
                                Text(
                                    text = "জিরো ইনভেস্টমেন্টে শুরু করুন আপনার অনলাইন বিজনেস। আমরা পণ্য প্যাক ও ডেলিভারি করব কাস্টমারের ঠিকানায়, আপনি পাবেন সরাসরি মার্জিন ও কমিশন!",
                                    fontFamily = HindSiliguriFontFamily,
                                    fontSize = 13.sp,
                                    color = Color.White.copy(alpha = 0.9f),
                                    lineHeight = 18.sp,
                                    modifier = Modifier.padding(top = 6.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Surface(
                                    color = MkGold,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = " বার্ষিক মেম্বারশিপ ফি: ৳৫০০ মাত্র ",
                                        fontFamily = HindSiliguriFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = MkDarkGreen,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "রিসেলার আবেদন ও ফি পেমেন্ট",
                                    fontFamily = HindSiliguriFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = MkDarkGreen
                                )

                                OutlinedTextField(
                                    value = registrationName,
                                    onValueChange = { registrationName = it },
                                    label = { Text("আপনার নাম", fontFamily = HindSiliguriFontFamily) },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp)
                                )

                                OutlinedTextField(
                                    value = registrationPhone,
                                    onValueChange = { registrationPhone = it },
                                    label = { Text("মোবাইল নম্বর", fontFamily = HindSiliguriFontFamily) },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp)
                                )

                                Surface(
                                    color = Color(0xFFFFF9EF),
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, MkGold)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(
                                            text = "বিকাশ: 01765820192 | নগদ: 01635080349",
                                            fontFamily = HindSiliguriFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = MkDarkGreen
                                        )
                                        Text(
                                            text = "উপরের নম্বরে বার্ষিক ৳৫০০ ফি Send Money করে নিচের ঘরে TrxID লিখুন।",
                                            fontFamily = HindSiliguriFontFamily,
                                            fontSize = 12.sp,
                                            color = Color.DarkGray
                                        )
                                    }
                                }

                                OutlinedTextField(
                                    value = membershipTrxId,
                                    onValueChange = { membershipTrxId = it },
                                    label = { Text("পেমেন্ট ট্রানজেকশন আইডি (TrxID) *", fontFamily = HindSiliguriFontFamily) },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp)
                                )

                                Button(
                                    onClick = {
                                        if (registrationName.isBlank() || registrationPhone.isBlank() || membershipTrxId.isBlank()) {
                                            Toast.makeText(context, "অনুগ্রহ করে সব তথ্য ও TrxID প্রদান করুন", Toast.LENGTH_LONG).show()
                                            return@Button
                                        }
                                        viewModel.registerAsReseller(registrationName, registrationPhone, membershipTrxId)
                                    },
                                    modifier = Modifier.fillMaxWidth().height(48.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MkDarkGreen, contentColor = MkGold)
                                ) {
                                    Text("ফি জমা ও আবেদন নিশ্চিত করুন", fontFamily = HindSiliguriFontFamily, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                            }
                        }
                    }
                }
            } else if (isPending) {
                // Pending Verification state
                Box(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
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
                                imageVector = Icons.Default.VerifiedUser,
                                contentDescription = null,
                                tint = MkGold,
                                modifier = Modifier.size(60.dp)
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "আবেদন ভেরিফিকেশন চলছে",
                                fontFamily = HindSiliguriFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = MkDarkGreen
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "আপনার রিসেলার মেম্বারশিপ ফি (৳৫০০) জমা হয়েছে। অ্যাডমিন প্যানেল থেকে পেমেন্ট যাচাই শেষে ২৪ ঘণ্টার মধ্যে আপনার রিসেলার অ্যাকাউন্ট অ্যাক্টিভেট করা হবে।",
                                fontFamily = HindSiliguriFontFamily,
                                fontSize = 13.sp,
                                color = Color.Gray,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            } else {
                // Active Reseller Dashboard
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
                        text = { Text("রিসেলার রেট", fontFamily = HindSiliguriFontFamily, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("ওয়ালেট ও উত্তোলন", fontFamily = HindSiliguriFontFamily, fontWeight = FontWeight.Bold) }
                    )
                }

                if (selectedTab == 0) {
                    // Wholesale / Reseller Rate Catalog
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Surface(
                                color = MkEmerald.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(imageVector = Icons.Default.MonetizationOn, contentDescription = null, tint = MkEmerald)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "রিসেলার পাইকারি রেট এবং সম্ভাব্য মার্জিন ক্যালকুলেটর",
                                        fontFamily = HindSiliguriFontFamily,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MkDarkGreen
                                    )
                                }
                            }
                        }

                        items(products) { product ->
                            val regularPrice = if (product.discountPrice > 0) product.discountPrice else product.price
                            val wholesalePrice = (regularPrice * 0.80).toInt() // 20% margin
                            val profit = (regularPrice - wholesalePrice).toInt()

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = product.name,
                                        fontFamily = HindSiliguriFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = MkDarkGreen
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = "খুচরা মূল্য: ৳${regularPrice.toInt()}", fontFamily = HindSiliguriFontFamily, fontSize = 12.sp)
                                        Text(text = "রিসেলার রেট: ৳$wholesalePrice", fontFamily = HindSiliguriFontFamily, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MkEmerald)
                                        Surface(
                                            color = Color(0xFFE8F5E9),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = " লাভ: ৳$profit ",
                                                fontFamily = HindSiliguriFontFamily,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF2E7D32)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Wallet & Withdrawals
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MkDarkGreen)
                            ) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                    Text(text = "রিসেলার ওয়ালেট ব্যালেন্স", fontFamily = HindSiliguriFontFamily, color = MkGoldLight, fontSize = 13.sp)
                                    Text(text = "৳২,৪৫০.০০", fontFamily = HindSiliguriFontFamily, fontWeight = FontWeight.Bold, fontSize = 28.sp, color = MkGold)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(text = "সর্বশেষ উত্তোলনযোগ্য কমিশন (উইথড্রয়াল)", fontFamily = HindSiliguriFontFamily, fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                                }
                            }
                        }

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
                                    Text(text = "কমিশন উত্তোলনের আবেদন (Withdrawal Request)", fontFamily = HindSiliguriFontFamily, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MkDarkGreen)

                                    OutlinedTextField(
                                        value = withdrawAmount,
                                        onValueChange = { withdrawAmount = it },
                                        label = { Text("উত্তোলনের পরিমাণ (টাকা)", fontFamily = HindSiliguriFontFamily) },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp)
                                    )

                                    OutlinedTextField(
                                        value = withdrawNumber,
                                        onValueChange = { withdrawNumber = it },
                                        label = { Text("বিকাশ বা নগদ নম্বর", fontFamily = HindSiliguriFontFamily) },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp)
                                    )

                                    Button(
                                        onClick = {
                                            if (withdrawAmount.isNotBlank() && withdrawNumber.isNotBlank()) {
                                                Toast.makeText(context, "৳$withdrawAmount উত্তোলন আবেদন জমা দেওয়া হয়েছে", Toast.LENGTH_SHORT).show()
                                                withdrawAmount = ""
                                                withdrawNumber = ""
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth().height(44.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = MkDarkGreen, contentColor = MkGold)
                                    ) {
                                        Text("উত্তোলনের রিকোয়েস্ট পাঠান", fontFamily = HindSiliguriFontFamily, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
