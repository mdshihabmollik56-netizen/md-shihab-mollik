package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.model.AppConfig
import com.example.data.model.CartItem
import com.example.data.model.Order
import com.example.data.model.Product
import com.example.data.model.UserProfile
import com.example.data.model.Voucher
import com.example.util.OperationType
import com.example.util.handleFirestoreError
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class MkShopsRepository(private val db: FirebaseFirestore) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val auth = Firebase.auth

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing MK SHOPS data.")
    }

    // 1. Observe Products Catalog
    fun observeProducts(): Flow<List<Product>> = flow {
        val path = "products"
        emitAll(
            db.collection(path)
                .snapshots()
                .map { snapshot -> snapshot.toObjects(Product::class.java) }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val exceptionToLog = firestoreError ?: (error as? Exception)
                    if (exceptionToLog != null) {
                        handleFirestoreError(exceptionToLog, OperationType.LIST, path)
                    }
                    throw error
                }
        )
    }

    // Seed default premium items if database is freshly provisioned
    suspend fun seedInitialProductsIfEmpty() {
        try {
            val snapshot = db.collection("products").get().await()
            if (snapshot.size() < 8) {
                val sampleProducts = listOf(
                    Product(
                        name = "Alienware Aurora Gaming Rig Desktop",
                        description = "Intel Core i9 14th Gen, RTX 4080 16GB GPU, 32GB DDR5 RAM, Liquid Cooling System, RGB Glass Case.",
                        category = "Gadgets",
                        price = 195000.0,
                        discountPrice = 179000.0,
                        stock = 10L,
                        imageUrl = "https://images.unsplash.com/photo-1587202372775-e229f172b9d7?w=600",
                        sku = "MK-PC-001",
                        status = "in_stock"
                    ),
                    Product(
                        name = "Boombox Pro Waterproof Outdoor Speaker",
                        description = "360° Deep Bass Stereo Sound, IPX7 Waterproof, 24 Hours Battery Playtime, PartyBoost Wireless Pairing.",
                        category = "Gadgets",
                        price = 4500.0,
                        discountPrice = 3650.0,
                        stock = 25L,
                        imageUrl = "https://images.unsplash.com/photo-1545454675-3531b543be5d?w=600",
                        sku = "MK-SPK-002",
                        status = "in_stock"
                    ),
                    Product(
                        name = "Ultra Watch Series 9 AMOLED Smartwatch",
                        description = "AMOLED Always-On Display, Bluetooth HD Calling, Blood Oxygen & Heart Rate Sensor, Wireless Fast Charging.",
                        category = "Gadgets",
                        price = 2800.0,
                        discountPrice = 2190.0,
                        stock = 35L,
                        imageUrl = "https://images.unsplash.com/photo-1579586337278-3befd40fd17a?w=600",
                        sku = "MK-GDG-001",
                        status = "in_stock"
                    ),
                    Product(
                        name = "ANC Wireless Studio Over-Ear Headphones",
                        description = "Active Noise Cancellation 40dB, 40mm Titanium Drivers, Transparency Mode, 45H Battery, Ultra Comfort Memory Foam.",
                        category = "Gadgets",
                        price = 3800.0,
                        discountPrice = 2950.0,
                        stock = 20L,
                        imageUrl = "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600",
                        sku = "MK-HDP-003",
                        status = "in_stock"
                    ),
                    Product(
                        name = "Ultra Slim Pro 14\" Laptop",
                        description = "FHD IPS Display, 16GB RAM, 512GB NVMe SSD, Backlit Keyboard, All-day Battery, Fingerprint Reader.",
                        category = "Gadgets",
                        price = 68000.0,
                        discountPrice = 62500.0,
                        stock = 15L,
                        imageUrl = "https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=600",
                        sku = "MK-LAP-004",
                        status = "in_stock"
                    ),
                    Product(
                        name = "ANC Wireless Earbuds Pro 2",
                        description = "Active Noise Cancellation, Deep Bass, 32H Battery Playtime, Wireless Charging Case, Touch Controls.",
                        category = "Gadgets",
                        price = 2200.0,
                        discountPrice = 1650.0,
                        stock = 40L,
                        imageUrl = "https://images.unsplash.com/photo-1590658268037-6bf12165a8df?w=600",
                        sku = "MK-GDG-002",
                        status = "in_stock"
                    ),
                    Product(
                        name = "Luxury Organza Embroidered Three-Piece",
                        description = "Exclusive hand-crafted embroidery with digital printed organza dupatta and comfortable cotton inner.",
                        category = "Three-Piece Collection",
                        price = 4200.0,
                        discountPrice = 3450.0,
                        stock = 20L,
                        imageUrl = "https://images.unsplash.com/photo-1610030469983-98e550d6193c?w=600",
                        sku = "MK-TP-101",
                        status = "in_stock"
                    ),
                    Product(
                        name = "Premium Cotton Kashmiri Three-Piece",
                        description = "100% fine cotton fabric with traditional Kashmiri thread work. Perfect for festive and casual wear.",
                        category = "Three-Piece Collection",
                        price = 2950.0,
                        discountPrice = 2450.0,
                        stock = 25L,
                        imageUrl = "https://images.unsplash.com/photo-1583391733956-3750e0ff4e8b?w=600",
                        sku = "MK-TP-102",
                        status = "in_stock"
                    ),
                    Product(
                        name = "Designer Semi-Formal Royal Panjabi",
                        description = "Premium jacquard cotton fabric with subtle collar embroidery. Elegant tailoring for Eid and celebrations.",
                        category = "Fashion",
                        price = 2600.0,
                        discountPrice = 2100.0,
                        stock = 30L,
                        imageUrl = "https://images.unsplash.com/photo-1602810318383-e386cc2a3ccf?w=600",
                        sku = "MK-FSH-201",
                        status = "in_stock"
                    ),
                    Product(
                        name = "Royal Arabian Oud & Attar Combo",
                        description = "Alcohol-free long-lasting luxury concentrated perfume oil (Attar). Notes of amber, rose, and woody oud.",
                        category = "Lifestyle",
                        price = 1500.0,
                        discountPrice = 1150.0,
                        stock = 50L,
                        imageUrl = "https://images.unsplash.com/photo-1592945403244-b3fbafd7f539?w=600",
                        sku = "MK-LST-301",
                        status = "in_stock"
                    )
                )

                for (prod in sampleProducts) {
                    val docRef = db.collection("products").document()
                    val payload = mapOf(
                        "name" to prod.name,
                        "description" to prod.description,
                        "category" to prod.category,
                        "price" to prod.price,
                        "discountPrice" to prod.discountPrice,
                        "stock" to prod.stock,
                        "imageUrl" to prod.imageUrl,
                        "sku" to prod.sku,
                        "status" to prod.status,
                        "createdAt" to FieldValue.serverTimestamp(),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                    docRef.set(payload).await()
                }

                // Seed sample voucher
                val voucherRef = db.collection("vouchers").document("mkfirst")
                voucherRef.set(
                    mapOf(
                        "code" to "MKFIRST",
                        "title" to "First Order Discount",
                        "discountType" to "fixed",
                        "discountValue" to 150.0,
                        "minOrderAmount" to 1000.0,
                        "isActive" to true
                    )
                ).await()

                val freeDelRef = db.collection("vouchers").document("freedel")
                freeDelRef.set(
                    mapOf(
                        "code" to "FREEDEL",
                        "title" to "Free Delivery Offer",
                        "discountType" to "free_delivery",
                        "discountValue" to 70.0,
                        "minOrderAmount" to 1500.0,
                        "isActive" to true
                    )
                ).await()
            }
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, "products")
        }
    }

    // 2. User Profile
    fun observeUserProfile(userId: String): Flow<UserProfile?> = callbackFlow {
        val docRef = db.collection("users").document(userId)
        val listener = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                handleFirestoreError(error, OperationType.GET, docRef.path)
                trySend(null)
                return@addSnapshotListener
            }
            val profile = snapshot?.toObject(UserProfile::class.java)
            trySend(profile)
        }
        awaitClose { listener.remove() }
    }

    suspend fun saveUserProfile(profile: UserProfile) {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid)
        val payload = mapOf(
            "userId" to uid,
            "email" to profile.email,
            "displayName" to profile.displayName,
            "phone" to profile.phone,
            "address" to profile.address,
            "role" to profile.role,
            "resellerStatus" to profile.resellerStatus,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        try {
            docRef.set(payload).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            throw e
        }
    }

    // 3. Shopping Cart
    fun observeCart(userId: String): Flow<List<CartItem>> = callbackFlow {
        val collection = db.collection("users").document(userId).collection("cart")
        val listener = collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                handleFirestoreError(error, OperationType.LIST, collection.path)
                trySend(emptyList())
                return@addSnapshotListener
            }
            val items = snapshot?.toObjects(CartItem::class.java) ?: emptyList()
            trySend(items)
        }
        awaitClose { listener.remove() }
    }

    suspend fun addToCart(userId: String, product: Product, quantity: Long = 1L) {
        val uid = requireUserId()
        val collection = db.collection("users").document(uid).collection("cart")
        val docId = if (product.id.isNotBlank()) product.id else collection.document().id
        val docRef = collection.document(docId)
        val price = if (product.discountPrice > 0) product.discountPrice else product.price
        val payload = mapOf(
            "productId" to product.id,
            "productName" to product.name,
            "productImage" to product.imageUrl,
            "price" to price,
            "quantity" to quantity,
            "createdAt" to FieldValue.serverTimestamp()
        )
        try {
            docRef.set(payload).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
    }

    suspend fun updateCartQuantity(userId: String, itemId: String, quantity: Long) {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("cart").document(itemId)
        try {
            if (quantity <= 0) {
                docRef.delete().await()
            } else {
                docRef.update("quantity", quantity).await()
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }

    suspend fun removeFromCart(userId: String, itemId: String) {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("cart").document(itemId)
        try {
            docRef.delete().await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            throw e
        }
    }

    suspend fun clearCart(userId: String) {
        val uid = requireUserId()
        val collection = db.collection("users").document(uid).collection("cart")
        try {
            val snapshot = collection.get().await()
            for (doc in snapshot.documents) {
                doc.reference.delete().await()
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, collection.path)
            throw e
        }
    }

    // 4. Orders & Vouchers
    suspend fun placeOrder(userId: String, order: Order): String {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("orders").document()
        val payload = mapOf(
            "userId" to uid,
            "voucherNumber" to order.voucherNumber,
            "customerName" to order.customerName,
            "customerPhone" to order.customerPhone,
            "shippingAddress" to order.shippingAddress,
            "deliveryArea" to order.deliveryArea,
            "deliveryCharge" to order.deliveryCharge,
            "subtotal" to order.subtotal,
            "discountAmount" to order.discountAmount,
            "totalAmount" to order.totalAmount,
            "paymentMethod" to order.paymentMethod,
            "transactionId" to order.transactionId,
            "paymentStatus" to order.paymentStatus,
            "orderStatus" to order.orderStatus,
            "riskScore" to order.riskScore,
            "itemCount" to order.itemCount,
            "isResellerOrder" to order.isResellerOrder,
            "resellerId" to order.resellerId,
            "resellerProfit" to order.resellerProfit,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        try {
            docRef.set(payload).await()
            clearCart(uid)
            return docRef.id
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
    }

    fun observeUserOrders(userId: String): Flow<List<Order>> = callbackFlow {
        val collection = db.collection("users").document(userId).collection("orders")
        val listener = collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                handleFirestoreError(error, OperationType.LIST, collection.path)
                trySend(emptyList())
                return@addSnapshotListener
            }
            val orders = snapshot?.toObjects(Order::class.java) ?: emptyList()
            trySend(orders)
        }
        awaitClose { listener.remove() }
    }

    suspend fun updateOrderStatus(userId: String, orderId: String, newStatus: String) {
        val docRef = db.collection("users").document(userId).collection("orders").document(orderId)
        try {
            docRef.update(
                mapOf(
                    "orderStatus" to newStatus,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }

    suspend fun updatePaymentStatus(userId: String, orderId: String, newPaymentStatus: String) {
        val docRef = db.collection("users").document(userId).collection("orders").document(orderId)
        try {
            docRef.update(
                mapOf(
                    "paymentStatus" to newPaymentStatus,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }

    // 5. Products Management (Admin)
    suspend fun saveProduct(product: Product) {
        val docRef = if (product.id.isNotBlank()) {
            db.collection("products").document(product.id)
        } else {
            db.collection("products").document()
        }
        val payload = mapOf(
            "name" to product.name,
            "description" to product.description,
            "category" to product.category,
            "price" to product.price,
            "discountPrice" to product.discountPrice,
            "stock" to product.stock,
            "imageUrl" to product.imageUrl,
            "sku" to product.sku,
            "status" to product.status,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        try {
            docRef.set(payload).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            throw e
        }
    }

    suspend fun deleteProduct(productId: String) {
        val docRef = db.collection("products").document(productId)
        try {
            docRef.delete().await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            throw e
        }
    }

    // 6. Vouchers
    suspend fun getVouchers(): List<Voucher> {
        return try {
            val snapshot = db.collection("vouchers").get().await()
            snapshot.toObjects(Voucher::class.java)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.LIST, "vouchers")
            emptyList()
        }
    }
}
