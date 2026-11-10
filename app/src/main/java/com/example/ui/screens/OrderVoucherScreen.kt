package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Order
import com.example.ui.theme.HindSiliguriFontFamily
import com.example.ui.theme.MkDarkGreen
import com.example.ui.theme.MkEmerald
import com.example.ui.theme.MkGold
import com.example.ui.theme.MkGoldLight
import com.example.ui.theme.PlayfairDisplayFontFamily
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderVoucherScreen(
    order: Order,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val dateStr = if (order.createdAt != null) {
        SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
            .format(Date(order.createdAt.seconds * 1000L))
    } else {
        SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date())
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "অর্ডার কনফার্মেশন ভাউচার",
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
                .background(Color(0xFFF4F1EA))
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Receipt Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Success Seal Icon
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE8F5E9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "MK SHOPS",
                        fontFamily = PlayfairDisplayFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = MkDarkGreen
                    )
                    Text(
                        text = "অফিসিয়াল অর্ডার ভাউচার ও রসিদ",
                        fontFamily = HindSiliguriFontFamily,
                        fontSize = 12.sp,
                        color = MkEmerald,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Voucher & Order Info Box
                    Surface(
                        color = Color(0xFFF9FBF9),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDDE7E1)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            VoucherInfoRow(label = "ভাউচার নম্বর:", value = order.voucherNumber, isBold = true)
                            VoucherInfoRow(label = "অর্ডার তারিখ:", value = dateStr)
                            VoucherInfoRow(label = "অর্ডার স্ট্যাটাস:", value = order.orderStatus, valueColor = MkEmerald)
                            VoucherInfoRow(label = "পেমেন্ট মাধ্যম:", value = order.paymentMethod)
                            if (order.transactionId.isNotBlank()) {
                                VoucherInfoRow(label = "TrxID:", value = order.transactionId)
                            }
                            VoucherInfoRow(label = "পেমেন্ট স্ট্যাটাস:", value = order.paymentStatus)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Customer Details
                    Surface(
                        color = Color(0xFFFDFCFA),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEFE7D8)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "গ্রাহক তথ্য:",
                                fontFamily = HindSiliguriFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MkDarkGreen
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "নাম: ${order.customerName}", fontFamily = HindSiliguriFontFamily, fontSize = 13.sp)
                            Text(text = "মোবাইল: ${order.customerPhone}", fontFamily = HindSiliguriFontFamily, fontSize = 13.sp)
                            Text(text = "ঠিকানা: ${order.shippingAddress}", fontFamily = HindSiliguriFontFamily, fontSize = 13.sp)
                            Text(text = "এলাকা: ${order.deliveryArea}", fontFamily = HindSiliguriFontFamily, fontSize = 12.sp, color = Color.Gray)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = Color(0xFFE0E0E0))
                    Spacer(modifier = Modifier.height(14.dp))

                    // Totals
                    VoucherSummaryRow(label = "পণ্যের সাবটোটাল:", value = "৳${order.subtotal.toInt()}")
                    VoucherSummaryRow(label = "ডেলিভারি চার্জ (${order.deliveryArea}):", value = "৳${order.deliveryCharge.toInt()}")
                    if (order.discountAmount > 0) {
                        VoucherSummaryRow(label = "ভাউচার ডিসকাউন্ট:", value = "-৳${order.discountAmount.toInt()}", isGreen = true)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Divider(color = Color(0xFFE0E0E0))
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "সর্বমোট মূল্য:",
                            fontFamily = HindSiliguriFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = MkDarkGreen
                        )
                        Text(
                            text = "৳${order.totalAmount.toInt()}",
                            fontFamily = HindSiliguriFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = MkEmerald
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "MK SHOPS বেছে নেওয়ার জন্য ধন্যবাদ!\nযেকোনো সহায়তায় হোয়াটসঅ্যাপ: 01765820192",
                        fontFamily = HindSiliguriFontFamily,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // WhatsApp Share Button
                Button(
                    onClick = {
                        val shareText = "MK SHOPS অর্ডার ভাউচার\n" +
                                "ভাউচার নং: ${order.voucherNumber}\n" +
                                "গ্রাহক: ${order.customerName} (${order.customerPhone})\n" +
                                "ঠিকানা: ${order.shippingAddress}\n" +
                                "পেমেন্ট মাধ্যম: ${order.paymentMethod}\n" +
                                "মোট মূল্য: ৳${order.totalAmount.toInt()}\n" +
                                "হোয়াটসঅ্যাপ হেল্পলাইন: 01765820192"
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, shareText)
                        }
                        context.startActivity(Intent.createChooser(intent, "ভাউচার শেয়ার করুন"))
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Outlined.Chat, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "হোয়াটসঅ্যাপে ভাউচার শেয়ার করুন",
                        fontFamily = HindSiliguriFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Print / Save Simulation Button
                OutlinedButton(
                    onClick = {
                        Toast.makeText(context, "ভাউচার রসিদ প্রিন্ট প্রস্তুতি সম্পন্ন হয়েছে", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, MkDarkGreen)
                ) {
                    Icon(imageVector = Icons.Default.Print, contentDescription = null, tint = MkDarkGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ভাউচার ডাউনলোড ও প্রিন্ট করুন",
                        fontFamily = HindSiliguriFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = MkDarkGreen
                    )
                }
            }
        }
    }
}

@Composable
private fun VoucherInfoRow(
    label: String,
    value: String,
    isBold: Boolean = false,
    valueColor: Color = Color.Black
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontFamily = HindSiliguriFontFamily, fontSize = 12.sp, color = Color.Gray)
        Text(
            text = value,
            fontFamily = HindSiliguriFontFamily,
            fontSize = 12.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium,
            color = valueColor
        )
    }
}

@Composable
private fun VoucherSummaryRow(
    label: String,
    value: String,
    isGreen: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontFamily = HindSiliguriFontFamily, fontSize = 13.sp, color = if (isGreen) Color(0xFF2E7D32) else Color(0xFF4A5550))
        Text(
            text = value,
            fontFamily = HindSiliguriFontFamily,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = if (isGreen) Color(0xFF2E7D32) else Color(0xFF263238)
        )
    }
}
