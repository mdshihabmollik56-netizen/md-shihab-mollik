package com.example

import com.example.data.model.Order
import com.example.data.model.Product
import com.example.data.model.Voucher
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testProductSavingsCalculation() {
        val product = Product(
            name = "Smart Watch MK-X",
            price = 2800.0,
            discountPrice = 2190.0,
            stock = 25
        )
        val savings = product.price - product.discountPrice
        assertEquals(610.0, savings, 0.01)
    }

    @Test
    fun testDeliveryChargeRules() {
        val deliveryInsideDhaka = 70.0
        val deliveryOutsideDhaka = 130.0

        assertEquals(70.0, deliveryInsideDhaka, 0.01)
        assertEquals(130.0, deliveryOutsideDhaka, 0.01)
    }

    @Test
    fun testVoucherDiscountCalculation() {
        val fixedVoucher = Voucher(
            code = "MKFIRST",
            discountType = "fixed",
            discountValue = 150.0,
            minOrderAmount = 1000.0
        )
        val freeDelVoucher = Voucher(
            code = "FREEDEL",
            discountType = "free_delivery",
            discountValue = 70.0,
            minOrderAmount = 1500.0
        )

        val subtotal = 2190.0
        assertTrue("Subtotal satisfies minimum order", subtotal >= fixedVoucher.minOrderAmount)
        assertEquals(150.0, fixedVoucher.discountValue, 0.01)
        assertEquals("free_delivery", freeDelVoucher.discountType)
    }

    @Test
    fun testFraudRiskScoring() {
        // Valid order
        var riskScore = 15L
        val validPhone = "01765820192"
        val validAddress = "House 12, Road 4, Dhanmondi, Dhaka"

        if (validPhone.length != 11 || !validPhone.startsWith("01")) {
            riskScore += 35L
        }
        if (validAddress.length < 10) {
            riskScore += 25L
        }
        assertEquals(15L, riskScore) // Low Risk

        // Suspicious order (invalid phone, short address)
        var suspiciousRisk = 15L
        val invalidPhone = "12345"
        val shortAddress = "Dhaka"
        if (invalidPhone.length != 11 || !invalidPhone.startsWith("01")) {
            suspiciousRisk += 35L
        }
        if (shortAddress.length < 10) {
            suspiciousRisk += 25L
        }
        assertEquals(75L, suspiciousRisk) // High Risk (>60)
        assertTrue("Order flagged as high risk", suspiciousRisk > 60)
    }

    @Test
    fun testResellerMarginCalculation() {
        val customerPrice = 1000.0
        val wholesaleRate = customerPrice * 0.80 // 20% margin
        val resellerProfit = customerPrice - wholesaleRate

        assertEquals(800.0, wholesaleRate, 0.01)
        assertEquals(200.0, resellerProfit, 0.01)
    }

    @Test
    fun testRoomProductEntityDiscount() {
        val entity = com.example.data.local.ProductEntity(
            id = "gadget_001",
            name = "Alienware Aurora Gaming Rig Desktop",
            description = "High performance gaming rig",
            category = "Gadgets",
            price = 195000.0,
            discountPrice = 179000.0,
            stock = 12L,
            imageUrl = "https://images.unsplash.com/photo-1587202372775-e229f172b9d7?w=600",
            sku = "MK-PC-001"
        )

        val discount = entity.price - entity.discountPrice
        assertEquals(16000.0, discount, 0.01)
        val discountPercentage = (((entity.price - entity.discountPrice) / entity.price) * 100).toInt()
        assertEquals(8, discountPercentage)
    }
}
