package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class Product(
    @DocumentId val id: String = "",
    val name: String = "",
    val description: String = "",
    val category: String = "",
    val price: Double = 0.0,
    val discountPrice: Double = 0.0,
    val stock: Long = 0L,
    val imageUrl: String = "",
    val sku: String = "",
    val status: String = "in_stock",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class UserProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val email: String = "",
    val displayName: String = "",
    val phone: String = "",
    val address: String = "",
    val role: String = "customer", // customer, reseller, admin
    val resellerStatus: String = "none", // none, pending, active, expired
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class Order(
    @DocumentId val id: String = "",
    val userId: String = "",
    val voucherNumber: String = "",
    val customerName: String = "",
    val customerPhone: String = "",
    val shippingAddress: String = "",
    val deliveryArea: String = "Inside Dhaka",
    val deliveryCharge: Double = 70.0,
    val subtotal: Double = 0.0,
    val discountAmount: Double = 0.0,
    val totalAmount: Double = 0.0,
    val paymentMethod: String = "COD", // COD, bKash, Nagad
    val transactionId: String = "",
    val paymentStatus: String = "Pending", // Pending, Verified
    val orderStatus: String = "New", // New, Confirmed, Processing, Shipped, Delivered, Cancelled
    val riskScore: Long = 15L,
    val itemCount: Long = 1L,
    val isResellerOrder: Boolean = false,
    val resellerId: String = "",
    val resellerProfit: Double = 0.0,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class CartItem(
    @DocumentId val id: String = "",
    val productId: String = "",
    val productName: String = "",
    val productImage: String = "",
    val price: Double = 0.0,
    val quantity: Long = 1L,
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class Voucher(
    @DocumentId val id: String = "",
    val code: String = "",
    val title: String = "",
    val discountType: String = "fixed", // fixed, percentage, free_delivery
    val discountValue: Double = 0.0,
    val minOrderAmount: Double = 0.0,
    val isActive: Boolean = true
)

@IgnoreExtraProperties
data class AppConfig(
    @DocumentId val id: String = "app_config",
    val deliveryChargeInsideDhaka: Double = 70.0,
    val deliveryChargeOutsideDhaka: Double = 130.0,
    val bkashNumber: String = "01765820192",
    val nagadNumber: String = "01635080349",
    val whatsappNumber: String = "01765820192",
    val resellerMembershipFee: Double = 500.0
)

data class TutorialVideo(
    val id: String,
    val title: String,
    val description: String,
    val youtubeId: String,
    val category: String,
    val isFeatured: Boolean = false
)
