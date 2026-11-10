package com.example.ui.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.content
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: String, // "user" or "model"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

sealed interface ChatUiState {
    data object Idle : ChatUiState
    data object Loading : ChatUiState
    data class Error(val message: String) : ChatUiState
}

private const val TAG = "AiChatViewModel"

class AiChatViewModel : ViewModel() {

    private val model by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            systemInstruction = content {
                text(
                    """
                    You are MK SHOPS Smart Customer Assistant (এমকে শপস স্মার্ট সাপোর্ট অ্যাসিস্ট্যান্ট).
                    MK SHOPS is a premium Bangladeshi e-commerce brand specializing in:
                    1. Gadgets (Smartwatches, Wireless Earbuds, Powerbanks, Cables)
                    2. Three-Piece Collection (Luxury Organza, Kashmiri Cotton, Embroidered Designer Sets)
                    3. Fashion (Semi-Formal Panjabi, Kurtis, Sarees, Bags)
                    4. Lifestyle (Arabian Oud, Attar, Fragrances, Home Decor)

                    Key Store Policies:
                    - Delivery Charge: Inside Dhaka City ৳70, Outside Dhaka ৳130 (Cash on Delivery available nationwide).
                    - Payment Options: Cash on Delivery (COD), bKash (01765820192), Nagad (01635080349).
                    - Official WhatsApp Support & Direct Order: 01765820192 (wa.me/8801765820192).
                    - Facebook Page: https://www.facebook.com/share/1JKn1X5J2L/
                    - Reseller Program: ৳500 annual membership, direct wholesale margins, and doorstep delivery to customer.

                    Tone: Warm, polite, professional, and helpful. Respond primarily in standard, friendly Bengali (বাংলা) or English based on user query. Always provide accurate store guidelines.
                    """.trimIndent()
                )
            }
        )
    }

    private val _messages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                sender = "model",
                text = "আসসালামু আলাইকুম! MK SHOPS-এ আপনাকে স্বাগতম। গ্যাজেট, থ্রি-পিস, ফ্যাশন বা লাইফস্টাইল পণ্য, অর্ডার ও ডেলিভারি সম্পর্কে যেকোনো প্রশ্ন করুন।"
            )
        )
    )
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _uiState = MutableStateFlow<ChatUiState>(ChatUiState.Idle)
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    fun sendMessage(userText: String) {
        if (userText.isBlank()) return
        val userMsg = ChatMessage(sender = "user", text = userText)
        _messages.value = _messages.value + userMsg

        val historyList = _messages.value.map { msg ->
            content(if (msg.sender == "user") "user" else "model") {
                text(msg.text)
            }
        }

        viewModelScope.launch {
            _uiState.value = ChatUiState.Loading
            val botMsgId = java.util.UUID.randomUUID().toString()
            val initialBotMsg = ChatMessage(id = botMsgId, sender = "model", text = "")
            _messages.value = _messages.value + initialBotMsg

            val accumulatedText = StringBuilder()
            try {
                withTimeout(60_000L) {
                    val chat = model.startChat(history = historyList.dropLast(1))
                    chat.sendMessageStream(userText).collect { chunk ->
                        val textChunk = chunk.text.orEmpty()
                        accumulatedText.append(textChunk)
                        _messages.value = _messages.value.map {
                            if (it.id == botMsgId) it.copy(text = accumulatedText.toString()) else it
                        }
                    }
                }
                _uiState.value = ChatUiState.Idle
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "Chat request timed out", e)
                val errMsg = "দুঃখিত, সংযোগে বিলম্ব হচ্ছে। অনুগ্রহ করে পুনরায় চেষ্টা করুন বা সরাসরি হোয়াটসঅ্যাপে (01765820192) যোগাযোগ করুন।"
                _messages.value = _messages.value.map {
                    if (it.id == botMsgId) it.copy(text = errMsg) else it
                }
                _uiState.value = ChatUiState.Error("Timeout: Please try again.")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Firebase AI Chat failed", e)
                val fallbackText = "দুঃখিত, তাৎক্ষণিক সার্ভার সাড়া পাওয়া যায়নি। অনুগ্রহ করে কিছুক্ষণের মধ্যে আবার চেষ্টা করুন অথবা আমাদের হোয়াটসঅ্যাপে ম্যাসেজ দিন: 01765820192"
                _messages.value = _messages.value.map {
                    if (it.id == botMsgId) it.copy(text = fallbackText) else it
                }
                _uiState.value = ChatUiState.Error(e.localizedMessage ?: "AI Chat error")
            }
        }
    }
}
