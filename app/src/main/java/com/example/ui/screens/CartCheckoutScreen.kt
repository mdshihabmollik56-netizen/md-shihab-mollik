package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Order
import com.example.ui.theme.HindSiliguriFontFamily
import com.example.ui.theme.MkDarkGreen
import com.example.ui.theme.MkEmerald
import com.example.ui.theme.MkGold
import com.example.ui.theme.MkGoldLight
import com.example.ui.viewmodel.ShopViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartCheckoutScreen(
    viewModel: ShopViewModel,
    onBack: () -> Unit,
    onOrderPlaced: (Order) -> Unit
) {
    val context = LocalContext.current
    val cartItems by viewModel.cartItems.collectAsState()
    val appliedVoucher by viewModel.appliedVoucher.collectAsState()
    val deliveryArea by viewModel.deliveryArea.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    var customerName by remember { mutableStateOf(userProfile?.displayName ?: "") }
    var customerPhone by remember { mutableStateOf(userProfile?.phone ?: "") }
    var shippingAddress by remember { mutableStateOf(userProfile?.address ?: "") }
    var paymentMethod by remember { mutableStateOf("COD") } // COD, bKash, Nagad
    var transactionId by remember { mutableStateOf("") }
    var couponInput by remember { mutableStateOf("") }
    var isPlacingOrder by remember { mutableStateOf(false) }

    val subtotal = viewModel.calculateSubtotal()
    val deliveryCharge = viewModel.calculateDeliveryCharge()
    val discount = viewModel.calculateDiscount()
    val total = viewModel.calculateTotal()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "শপিং কার্ট ও চেকআউট",
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
        if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = null,
                        tint = Color.LightGray,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "আপনার কার্ট বর্তমানে খালি আছে",
                        fontFamily = HindSiliguriFontFamily,
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onBack,
                        colors = ButtonDefaults.buttonColors(containerColor = MkDarkGreen)
                    ) {
                        Text("কেনাকাটা করুন", fontFamily = HindSiliguriFontFamily, color = MkGold)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Items in Cart
                item {
                    Text(
                        text = "কার্টের পণ্যসমূহ (${cartItems.size})",
                        fontFamily = HindSiliguriFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MkDarkGreen
                    )
                }

                items(cartItems) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .background(Color(0xFFF2EEE8), RoundedCornerShape(8.dp))
                            ) {
                                if (item.productImage.isNotBlank()) {
                                    AsyncImage(
                                        model = item.productImage,
                                        contentDescription = item.productName,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.productName,
                                    fontFamily = HindSiliguriFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    color = MkDarkGreen,
                                    maxLines = 1
                                )
                                Text(
                                    text = "৳${item.price.toInt()} x ${item.quantity} = ৳${(item.price * item.quantity).toInt()}",
                                    fontFamily = HindSiliguriFontFamily,
                                    fontSize = 13.sp,
                                    color = MkEmerald,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { viewModel.updateCartQuantity(item.id, item.quantity - 1) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(16.dp))
                                }
                                Text(
                                    text = "${item.quantity}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp)
                                )
                                IconButton(
                                    onClick = { viewModel.updateCartQuantity(item.id, item.quantity + 1) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(16.dp))
                                }
                                IconButton(
                                    onClick = { viewModel.removeFromCart(item.id) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Remove", tint = Color.Red, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }

                // 2. Delivery Area Selection
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.LocalShipping, contentDescription = null, tint = MkDarkGreen)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "ডেলিভারি এরিয়া নির্বাচন করুন",
                                    fontFamily = HindSiliguriFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MkDarkGreen
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.setDeliveryArea("Inside Dhaka") },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = deliveryArea == "Inside Dhaka",
                                    onClick = { viewModel.setDeliveryArea("Inside Dhaka") },
                                    colors = RadioButtonDefaults.colors(selectedColor = MkDarkGreen)
                                )
                                Text(
                                    text = "ঢাকা সিটির মধ্যে — ৳৭০ (হোম ডেলিভারি)",
                                    fontFamily = HindSiliguriFontFamily,
                                    fontSize = 13.sp
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.setDeliveryArea("Outside Dhaka") },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = deliveryArea == "Outside Dhaka",
                                    onClick = { viewModel.setDeliveryArea("Outside Dhaka") },
                                    colors = RadioButtonDefaults.colors(selectedColor = MkDarkGreen)
                                )
                                Text(
                                    text = "ঢাকা সিটির বাইরে — ৳১৩০ (সারাদেশে হোম ডেলিভারি)",
                                    fontFamily = HindSiliguriFontFamily,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }

                // 3. Voucher & Coupon Input
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "কুপন / ভাউচার কোড",
                                fontFamily = HindSiliguriFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MkDarkGreen
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                OutlinedTextField(
                                    value = couponInput,
                                    onValueChange = { couponInput = it },
                                    placeholder = { Text("যেমন: MKFIRST বা FREEDEL", fontSize = 13.sp) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    singleLine = true
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        viewModel.applyVoucher(couponInput)
                                        couponInput = ""
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MkDarkGreen),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("প্রয়োগ", fontFamily = HindSiliguriFontFamily, color = MkGold)
                                }
                            }
                            if (appliedVoucher != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    color = Color(0xFFE8F5E9),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 10.dp, vertical = 6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "✓ ভাউচার '${appliedVoucher!!.code}' প্রযোজ্য! (সাশ্রয় ৳${discount.toInt()})",
                                            color = Color(0xFF2E7D32),
                                            fontSize = 12.sp,
                                            fontFamily = HindSiliguriFontFamily,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "বাতিল",
                                            color = Color.Red,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.clickable { viewModel.removeVoucher() }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 4. Customer Shipping Information
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
                                text = "গ্রাহকের ঠিকানা ও যোগাযোগের তথ্য",
                                fontFamily = HindSiliguriFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MkDarkGreen
                            )

                            OutlinedTextField(
                                value = customerName,
                                onValueChange = { customerName = it },
                                label = { Text("আপনার পূর্ণ নাম *", fontFamily = HindSiliguriFontFamily) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = customerPhone,
                                onValueChange = { customerPhone = it },
                                label = { Text("১১ ডিজিট মোবাইল নম্বর * (যেমন: 017xxxxxxxx)", fontFamily = HindSiliguriFontFamily) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = shippingAddress,
                                onValueChange = { shippingAddress = it },
                                label = { Text("সম্পূর্ণ ডেলিভারি ঠিকানা (বাসা/রোড/এলাকা/জেলা) *", fontFamily = HindSiliguriFontFamily) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                minLines = 2
                            )
                        }
                    }
                }

                // 5. Payment Method & Instructions
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Payment, contentDescription = null, tint = MkDarkGreen)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "পেমেন্ট মাধ্যম নির্বাচন করুন",
                                    fontFamily = HindSiliguriFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MkDarkGreen
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))

                            // Cash on Delivery
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { paymentMethod = "COD" },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = paymentMethod == "COD",
                                    onClick = { paymentMethod = "COD" },
                                    colors = RadioButtonDefaults.colors(selectedColor = MkDarkGreen)
                                )
                                Text(
                                    text = "ক্যাশ অন ডেলিভারি (পণ্য হাতে পেয়ে মূল্য পরিশোধ)",
                                    fontFamily = HindSiliguriFontFamily,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            // bKash
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { paymentMethod = "bKash" },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = paymentMethod == "bKash",
                                    onClick = { paymentMethod = "bKash" },
                                    colors = RadioButtonDefaults.colors(selectedColor = MkDarkGreen)
                                )
                                Text(
                                    text = "বিকাশ (bKash) — 01765820192",
                                    fontFamily = HindSiliguriFontFamily,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE2136E)
                                )
                            }

                            // Nagad
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { paymentMethod = "Nagad" },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = paymentMethod == "Nagad",
                                    onClick = { paymentMethod = "Nagad" },
                                    colors = RadioButtonDefaults.colors(selectedColor = MkDarkGreen)
                                )
                                Text(
                                    text = "নগদ (Nagad) — 01635080349",
                                    fontFamily = HindSiliguriFontFamily,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFF7941D)
                                )
                            }

                            // MFS instructions & Transaction ID
                            if (paymentMethod != "COD") {
                                Spacer(modifier = Modifier.height(10.dp))
                                val targetNumber = if (paymentMethod == "bKash") "01765820192" else "01635080349"
                                Surface(
                                    color = Color(0xFFFFF9EE),
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, MkGold)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "$paymentMethod নম্বর: $targetNumber",
                                                fontFamily = HindSiliguriFontFamily,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = MkDarkGreen
                                            )
                                            IconButton(
                                                onClick = {
                                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                    clipboard.setPrimaryClip(ClipData.newPlainText("Payment Number", targetNumber))
                                                    Toast.makeText(context, "$targetNumber কপি করা হয়েছে", Toast.LENGTH_SHORT).show()
                                                },
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(16.dp))
                                            }
                                        }
                                        Text(
                                            text = "অনুগ্রহ করে মোট ৳${total.toInt()} Send Money করে ট্রানজেকশন আইডি (TrxID) নিচে লিখুন।",
                                            fontFamily = HindSiliguriFontFamily,
                                            fontSize = 12.sp,
                                            color = Color.DarkGray
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        OutlinedTextField(
                                            value = transactionId,
                                            onValueChange = { transactionId = it },
                                            label = { Text("ট্রানজেকশন আইডি (TrxID) *", fontFamily = HindSiliguriFontFamily) },
                                            modifier = Modifier.fillMaxWidth(),
                                            singleLine = true,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 6. Pricing Summary Breakdown
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "অর্ডার মূল্য বিবরণ",
                                fontFamily = HindSiliguriFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MkDarkGreen
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("পণ্যের মোট মূল্য:", fontFamily = HindSiliguriFontFamily, fontSize = 13.sp)
                                Text("৳${subtotal.toInt()}", fontFamily = HindSiliguriFontFamily, fontWeight = FontWeight.Medium)
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("ডেলিভারি চার্জ ($deliveryArea):", fontFamily = HindSiliguriFontFamily, fontSize = 13.sp)
                                Text("৳${deliveryCharge.toInt()}", fontFamily = HindSiliguriFontFamily, fontWeight = FontWeight.Medium)
                            }

                            if (discount > 0) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("ভাউচার ডিসকাউন্ট:", fontFamily = HindSiliguriFontFamily, fontSize = 13.sp, color = Color(0xFF2E7D32))
                                    Text("-৳${discount.toInt()}", fontFamily = HindSiliguriFontFamily, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                                }
                            }

                            Divider(modifier = Modifier.padding(vertical = 4.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "সর্বমোট পরিশোধযোগ্য:",
                                    fontFamily = HindSiliguriFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = MkDarkGreen
                                )
                                Text(
                                    text = "৳${total.toInt()}",
                                    fontFamily = HindSiliguriFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    color = MkEmerald
                                )
                            }
                        }
                    }
                }

                // 7. Place Order Button
                item {
                    Button(
                        onClick = {
                            if (customerName.isBlank() || customerPhone.isBlank() || shippingAddress.isBlank()) {
                                Toast.makeText(context, "অনুগ্রহ করে আপনার নাম, মোবাইল নম্বর ও সম্পূর্ণ ঠিকানা পূরণ করুন", Toast.LENGTH_LONG).show()
                                return@Button
                            }
                            if (paymentMethod != "COD" && transactionId.isBlank()) {
                                Toast.makeText(context, "অনুগ্রহ করে পেমেন্টের TrxID প্রদান করুন", Toast.LENGTH_LONG).show()
                                return@Button
                            }

                            isPlacingOrder = true
                            viewModel.placeOrder(
                                customerName = customerName,
                                customerPhone = customerPhone,
                                shippingAddress = shippingAddress,
                                paymentMethod = paymentMethod,
                                transactionId = transactionId,
                                onOrderPlaced = { order ->
                                    isPlacingOrder = false
                                    onOrderPlaced(order)
                                }
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MkDarkGreen,
                            contentColor = MkGold
                        ),
                        enabled = !isPlacingOrder
                    ) {
                        if (isPlacingOrder) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MkGold,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "অর্ডার নিশ্চিত করুন (Confirm Order)",
                                    fontFamily = HindSiliguriFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
