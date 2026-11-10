package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TutorialVideo
import com.example.ui.theme.HindSiliguriFontFamily
import com.example.ui.theme.MkDarkGreen
import com.example.ui.theme.MkEmerald
import com.example.ui.theme.MkGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TutorialsScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val tutorials = listOf(
        TutorialVideo(
            id = "1",
            title = "অ্যান্ড্রয়েড অ্যাপ দিয়ে অর্ডার করার নিয়ম",
            description = "কীভাবে পণ্য পছন্দ করবেন, কার্টে যোগ করবেন ও ক্যাশ অন ডেলিভারিতে অর্ডার কনফার্ম করবেন।",
            youtubeId = "dQw4w9WgXcQ",
            category = "Product Order",
            isFeatured = true
        ),
        TutorialVideo(
            id = "2",
            title = "বিকাশ ও নগদে পেমেন্ট এবং TrxID বসানোর গাইড",
            description = "বিকাশ ও নগদে সেন্ড মানি করে কীভাবে TrxID দিয়ে পেমেন্ট ভেরিফাই করবেন।",
            youtubeId = "dQw4w9WgXcQ",
            category = "Payment"
        ),
        TutorialVideo(
            id = "3",
            title = "রিসেলার একাউন্ট রেজিস্ট্রেশন ও ইনকাম করার উপায়",
            description = "৳৫০০ বার্ষিক মেম্বারশিপ নিয়ে কীভাবে পণ্য বিক্রি করে লাভ নিজের ওয়ালেটে নিবেন।",
            youtubeId = "dQw4w9WgXcQ",
            category = "Reseller Training",
            isFeatured = true
        ),
        TutorialVideo(
            id = "4",
            title = "কুপন কোড ও ফ্রি ডেলিভারি ভাউচার ব্যবহারের নিয়ম",
            description = "MKFIRST এবং FREEDEL ভাউচার ব্যবহার করে ডিসকাউন্ট পাওয়ার সহজ নিয়ম।",
            youtubeId = "dQw4w9WgXcQ",
            category = "Voucher"
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "ভিডিও টিউটোরিয়াল ও সহায়তা",
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF7F5F0))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Surface(
                    color = MkDarkGreen,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "MK SHOPS ভিডিও গাইডলাইন",
                            fontFamily = HindSiliguriFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MkGold
                        )
                        Text(
                            text = "অ্যাপ ও ওয়েবসাইট সহজে ব্যবহারের জন্য আমাদের ভিডিও টিউটোরিয়ালগুলো দেখে নিন। যেকোনো প্রয়োজনে আমাদের সহায়তা চ্যাট রয়েছে।",
                            fontFamily = HindSiliguriFontFamily,
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            items(tutorials) { tutorial ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val ytUrl = "https://www.youtube.com/watch?v=${tutorial.youtubeId}"
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(ytUrl)))
                        },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(50.dp),
                            color = Color(0xFFFFEBEE),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.PlayCircle,
                                    contentDescription = "Play",
                                    tint = Color(0xFFD32F2F),
                                    modifier = Modifier.size(34.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Surface(
                                color = MkEmerald.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = tutorial.category,
                                    fontFamily = HindSiliguriFontFamily,
                                    fontSize = 10.sp,
                                    color = MkEmerald,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = tutorial.title,
                                fontFamily = HindSiliguriFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MkDarkGreen
                            )
                            Text(
                                text = tutorial.description,
                                fontFamily = HindSiliguriFontFamily,
                                fontSize = 11.sp,
                                color = Color.Gray,
                                maxLines = 2
                            )
                        }
                    }
                }
            }
        }
    }
}
