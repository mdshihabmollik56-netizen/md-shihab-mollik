package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AppConfig
import com.example.data.model.CartItem
import com.example.data.model.Order
import com.example.data.model.Product
import com.example.data.model.UserProfile
import com.example.data.model.Voucher
import com.example.data.repository.MkShopsRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

sealed interface UiMessage {
    data class Success(val message: String) : UiMessage
    data class Error(val message: String) : UiMessage
}

class ShopViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MkShopsRepository(application)
    private val auth = Firebase.auth

    val currentUserId: String?
        get() = auth.currentUser?.uid

    val currentUserEmail: String?
        get() = auth.currentUser?.email

    val currentUserName: String?
        get() = auth.currentUser?.displayName

    // Products
    val products: StateFlow<List<Product>> = repository.observeProducts()
        .catch { emit(emptyList()) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    // User Profile
    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

    // Cart
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    // Orders
    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    // Selected Order for Voucher display
    private val _selectedOrderVoucher = MutableStateFlow<Order?>(null)
    val selectedOrderVoucher: StateFlow<Order?> = _selectedOrderVoucher.asStateFlow()

    // Vouchers
    private val _availableVouchers = MutableStateFlow<List<Voucher>>(emptyList())
    val availableVouchers: StateFlow<List<Voucher>> = _availableVouchers.asStateFlow()

    private val _appliedVoucher = MutableStateFlow<Voucher?>(null)
    val appliedVoucher: StateFlow<Voucher?> = _appliedVoucher.asStateFlow()

    // Delivery Area selection
    private val _deliveryArea = MutableStateFlow("Inside Dhaka")
    val deliveryArea: StateFlow<String> = _deliveryArea.asStateFlow()

    // Search & Filter
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    // App Config & Rates
    private val _appConfig = MutableStateFlow(AppConfig())
    val appConfig: StateFlow<AppConfig> = _appConfig.asStateFlow()

    // Status Message / Toast
    private val _uiMessage = MutableStateFlow<UiMessage?>(null)
    val uiMessage: StateFlow<UiMessage?> = _uiMessage.asStateFlow()

    init {
        viewModelScope.launch {
            repository.seedInitialProductsIfEmpty()
            loadVouchers()
            currentUserId?.let { uid ->
                loadUserData(uid)
            }
        }
    }

    fun onUserAuthenticated(userId: String) {
        loadUserData(userId)
    }

    private fun loadUserData(userId: String) {
        viewModelScope.launch {
            repository.observeUserProfile(userId)
                .catch { emit(null) }
                .collect { profile ->
                    if (profile == null) {
                        val newProfile = UserProfile(
                            userId = userId,
                            email = currentUserEmail.orEmpty(),
                            displayName = currentUserName ?: "Customer"
                        )
                        try {
                            repository.saveUserProfile(newProfile)
                        } catch (e: Exception) {
                            // Ignored - fallback profile in memory
                        }
                        _userProfile.value = newProfile
                    } else {
                        _userProfile.value = profile
                    }
                }
        }

        viewModelScope.launch {
            repository.observeCart(userId)
                .catch { emit(emptyList()) }
                .collect { items ->
                    _cartItems.value = items
                }
        }

        viewModelScope.launch {
            repository.observeUserOrders(userId)
                .catch { emit(emptyList()) }
                .collect { orderList ->
                    _orders.value = orderList.sortedByDescending { it.createdAt?.seconds ?: 0L }
                }
        }
    }

    private fun loadVouchers() {
        viewModelScope.launch {
            _availableVouchers.value = repository.getVouchers()
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setDeliveryArea(area: String) {
        _deliveryArea.value = area
    }

    fun addToCart(product: Product, quantity: Long = 1L) {
        val uid = currentUserId ?: return
        viewModelScope.launch {
            try {
                repository.addToCart(uid, product, quantity)
                _uiMessage.value = UiMessage.Success("কার্টে যোগ করা হয়েছে: ${product.name}")
            } catch (e: Exception) {
                _uiMessage.value = UiMessage.Error("কার্টে যোগ করতে সমস্যা হয়েছে")
            }
        }
    }

    fun updateCartQuantity(itemId: String, quantity: Long) {
        val uid = currentUserId ?: return
        viewModelScope.launch {
            try {
                repository.updateCartQuantity(uid, itemId, quantity)
            } catch (e: Exception) {
                _uiMessage.value = UiMessage.Error("কার্ট আপডেট করা সম্ভব হয়নি")
            }
        }
    }

    fun removeFromCart(itemId: String) {
        val uid = currentUserId ?: return
        viewModelScope.launch {
            try {
                repository.removeFromCart(uid, itemId)
            } catch (e: Exception) {
                _uiMessage.value = UiMessage.Error("পণ্য সরানো যায়নি")
            }
        }
    }

    fun applyVoucher(code: String) {
        val trimmed = code.trim().uppercase()
        val found = _availableVouchers.value.find { it.code.uppercase() == trimmed && it.isActive }
        if (found != null) {
            val subtotal = calculateSubtotal()
            if (subtotal >= found.minOrderAmount) {
                _appliedVoucher.value = found
                _uiMessage.value = UiMessage.Success("ভাউচার কোড '${found.code}' সফলভাবে প্রযোজ্য হয়েছে!")
            } else {
                _uiMessage.value = UiMessage.Error("এই ভাউচারের জন্য সর্বনিম্ন অর্ডার ৳${found.minOrderAmount.toInt()} হতে হবে।")
            }
        } else {
            _uiMessage.value = UiMessage.Error("ভাউচার কোডটি সঠিক নয় বা মেয়াদোত্তীর্ণ।")
        }
    }

    fun removeVoucher() {
        _appliedVoucher.value = null
    }

    fun calculateSubtotal(): Double {
        return _cartItems.value.sumOf { it.price * it.quantity }
    }

    fun calculateDeliveryCharge(): Double {
        val voucher = _appliedVoucher.value
        if (voucher?.discountType == "free_delivery") return 0.0
        return if (_deliveryArea.value == "Inside Dhaka") {
            _appConfig.value.deliveryChargeInsideDhaka
        } else {
            _appConfig.value.deliveryChargeOutsideDhaka
        }
    }

    fun calculateDiscount(): Double {
        val subtotal = calculateSubtotal()
        val voucher = _appliedVoucher.value ?: return 0.0
        return when (voucher.discountType) {
            "fixed" -> voucher.discountValue
            "percentage" -> (subtotal * voucher.discountValue) / 100.0
            "free_delivery" -> if (_deliveryArea.value == "Inside Dhaka") 70.0 else 130.0
            else -> 0.0
        }
    }

    fun calculateTotal(): Double {
        val subtotal = calculateSubtotal()
        val delivery = calculateDeliveryCharge()
        val discount = calculateDiscount()
        return (subtotal + delivery - discount).coerceAtLeast(0.0)
    }

    // Place an Order
    fun placeOrder(
        customerName: String,
        customerPhone: String,
        shippingAddress: String,
        paymentMethod: String,
        transactionId: String,
        isResellerOrder: Boolean = false,
        resellerProfit: Double = 0.0,
        onOrderPlaced: (Order) -> Unit
    ) {
        val uid = currentUserId ?: return
        val subtotal = calculateSubtotal()
        val deliveryCharge = calculateDeliveryCharge()
        val discount = calculateDiscount()
        val total = calculateTotal()
        val itemCount = _cartItems.value.sumOf { it.quantity }

        // Fraud detection logic
        var calculatedRisk = 15L
        if (customerPhone.trim().length != 11 || !customerPhone.trim().startsWith("01")) {
            calculatedRisk += 35L
        }
        if (shippingAddress.trim().length < 10) {
            calculatedRisk += 25L
        }
        if (paymentMethod == "COD" && total > 5000) {
            calculatedRisk += 20L
        }
        if (paymentMethod != "COD" && transactionId.trim().length < 6) {
            calculatedRisk += 40L
        }

        val voucherNumber = "MK-" + SimpleDateFormat("yyMMdd", Locale.US).format(Date()) + "-" +
                (1000..9999).random()

        val order = Order(
            userId = uid,
            voucherNumber = voucherNumber,
            customerName = customerName.trim(),
            customerPhone = customerPhone.trim(),
            shippingAddress = shippingAddress.trim(),
            deliveryArea = _deliveryArea.value,
            deliveryCharge = deliveryCharge,
            subtotal = subtotal,
            discountAmount = discount,
            totalAmount = total,
            paymentMethod = paymentMethod,
            transactionId = transactionId.trim(),
            paymentStatus = if (paymentMethod == "COD") "Pending" else "Submitted",
            orderStatus = "New",
            riskScore = calculatedRisk.coerceIn(0L, 100L),
            itemCount = itemCount,
            isResellerOrder = isResellerOrder,
            resellerId = if (isResellerOrder) uid else "",
            resellerProfit = resellerProfit
        )

        viewModelScope.launch {
            try {
                repository.placeOrder(uid, order)
                _appliedVoucher.value = null
                _selectedOrderVoucher.value = order
                _uiMessage.value = UiMessage.Success("অর্ডার সফল হয়েছে! ভাউচার নং: $voucherNumber")
                onOrderPlaced(order)
            } catch (e: Exception) {
                _uiMessage.value = UiMessage.Error("অর্ডার সম্পন্ন করা সম্ভব হয়নি: ${e.message}")
            }
        }
    }

    fun viewOrderVoucher(order: Order) {
        _selectedOrderVoucher.value = order
    }

    fun clearOrderVoucher() {
        _selectedOrderVoucher.value = null
    }

    fun clearMessage() {
        _uiMessage.value = null
    }

    // Reseller actions
    fun registerAsReseller(fullName: String, phone: String, trxId: String) {
        val uid = currentUserId ?: return
        viewModelScope.launch {
            try {
                val updated = _userProfile.value?.copy(
                    displayName = fullName,
                    phone = phone,
                    role = "reseller",
                    resellerStatus = "pending"
                ) ?: UserProfile(
                    userId = uid,
                    email = currentUserEmail.orEmpty(),
                    displayName = fullName,
                    phone = phone,
                    role = "reseller",
                    resellerStatus = "pending"
                )
                repository.saveUserProfile(updated)
                _userProfile.value = updated
                _uiMessage.value = UiMessage.Success("রিসেলার আবেদন ও ৳৫০০ ফি ট্রানজেকশন সফলভাবে জমা হয়েছে। অ্যাডমিন ভেরিফিকেশনের পর সক্রিয় হবে।")
            } catch (e: Exception) {
                _uiMessage.value = UiMessage.Error("রিসেলার আবেদন জমা দেওয়া যায়নি।")
            }
        }
    }

    // Admin product actions
    fun adminSaveProduct(product: Product) {
        viewModelScope.launch {
            try {
                repository.saveProduct(product)
                _uiMessage.value = UiMessage.Success("পণ্য সংরক্ষণ করা হয়েছে: ${product.name}")
            } catch (e: Exception) {
                _uiMessage.value = UiMessage.Error("পণ্য সংরক্ষণে সমস্যা হয়েছে।")
            }
        }
    }

    fun adminDeleteProduct(productId: String) {
        viewModelScope.launch {
            try {
                repository.deleteProduct(productId)
                _uiMessage.value = UiMessage.Success("পণ্য মুছে ফেলা হয়েছে")
            } catch (e: Exception) {
                _uiMessage.value = UiMessage.Error("পণ্য মুছতে সমস্যা হয়েছে")
            }
        }
    }

    fun adminUpdateOrderStatus(order: Order, newStatus: String) {
        viewModelScope.launch {
            try {
                repository.updateOrderStatus(order.userId, order.id, newStatus)
                _orders.value = _orders.value.map {
                    if (it.id == order.id) it.copy(orderStatus = newStatus) else it
                }
                _uiMessage.value = UiMessage.Success("অর্ডার স্ট্যাটাস আপডেট: $newStatus")
            } catch (e: Exception) {
                _uiMessage.value = UiMessage.Error("স্ট্যাটাস পরিবর্তন করা সম্ভব হয়নি")
            }
        }
    }

    fun adminUpdatePaymentStatus(order: Order, newPaymentStatus: String) {
        viewModelScope.launch {
            try {
                repository.updatePaymentStatus(order.userId, order.id, newPaymentStatus)
                _orders.value = _orders.value.map {
                    if (it.id == order.id) it.copy(paymentStatus = newPaymentStatus) else it
                }
                _uiMessage.value = UiMessage.Success("পেমেন্ট স্ট্যাটাস আপডেট: $newPaymentStatus")
            } catch (e: Exception) {
                _uiMessage.value = UiMessage.Error("পেমেন্ট পরিবর্তন করা সম্ভব হয়নি")
            }
        }
    }
}
