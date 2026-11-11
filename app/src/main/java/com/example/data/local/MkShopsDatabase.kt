package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [ProductEntity::class], version = 1, exportSchema = false)
abstract class MkShopsDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao

    companion object {
        @Volatile
        private var INSTANCE: MkShopsDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope = CoroutineScope(Dispatchers.IO)): MkShopsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MkShopsDatabase::class.java,
                    "mk_shops_local.db"
                )
                    .addCallback(DatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(private val scope: CoroutineScope) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch {
                        populateInitialInventory(database.productDao())
                    }
                }
            }
        }

        suspend fun populateInitialInventory(dao: ProductDao) {
            val initialInventory = listOf(
                // 1. Gadgets
                ProductEntity(
                    id = "gadget_001",
                    name = "Alienware Aurora Gaming Rig Desktop",
                    description = "Intel Core i9 14th Gen, RTX 4080 16GB GPU, 32GB DDR5 RAM, Liquid Cooling System.",
                    category = "Gadgets",
                    price = 195000.0,
                    discountPrice = 179000.0,
                    stock = 12L,
                    imageUrl = "https://images.unsplash.com/photo-1587202372775-e229f172b9d7?w=600",
                    sku = "MK-PC-001",
                    rating = 4.9,
                    reviewCount = 145,
                    badge = "Hot Deal"
                ),
                ProductEntity(
                    id = "gadget_002",
                    name = "Boombox Pro Waterproof Outdoor Speaker",
                    description = "360° Deep Bass Stereo Sound, IPX7 Waterproof, 24 Hours Battery Playtime.",
                    category = "Gadgets",
                    price = 4500.0,
                    discountPrice = 3650.0,
                    stock = 25L,
                    imageUrl = "https://images.unsplash.com/photo-1545454675-3531b543be5d?w=600",
                    sku = "MK-SPK-002",
                    rating = 4.8,
                    reviewCount = 98,
                    badge = "Best Seller"
                ),
                ProductEntity(
                    id = "gadget_003",
                    name = "Ultra Watch Series 9 AMOLED Smartwatch",
                    description = "AMOLED Always-On Display, Bluetooth HD Calling, Blood Oxygen Sensor, Fast Charging.",
                    category = "Gadgets",
                    price = 2800.0,
                    discountPrice = 2190.0,
                    stock = 35L,
                    imageUrl = "https://images.unsplash.com/photo-1579586337278-3befd40fd17a?w=600",
                    sku = "MK-GDG-001",
                    rating = 4.9,
                    reviewCount = 210,
                    badge = "-22% OFF"
                ),
                ProductEntity(
                    id = "gadget_004",
                    name = "ANC Wireless Studio Over-Ear Headphones",
                    description = "Active Noise Cancellation 40dB, 40mm Titanium Drivers, 45H Battery Life.",
                    category = "Gadgets",
                    price = 3800.0,
                    discountPrice = 2950.0,
                    stock = 20L,
                    imageUrl = "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600",
                    sku = "MK-HDP-003",
                    rating = 4.7,
                    reviewCount = 85,
                    badge = "Trending"
                ),
                ProductEntity(
                    id = "gadget_005",
                    name = "Ultra Slim Pro 14\" Laptop",
                    description = "FHD IPS Display, 16GB RAM, 512GB NVMe SSD, Backlit Keyboard, All-day Battery.",
                    category = "Gadgets",
                    price = 68000.0,
                    discountPrice = 62500.0,
                    stock = 15L,
                    imageUrl = "https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=600",
                    sku = "MK-LPT-004",
                    rating = 4.9,
                    reviewCount = 64,
                    badge = "Premium"
                ),
                ProductEntity(
                    id = "gadget_006",
                    name = "True Wireless Earbuds Pro with ANC",
                    description = "Spatial Audio with Dynamic Head Tracking, Adaptive EQ, Sweat & Water Resistant.",
                    category = "Gadgets",
                    price = 1850.0,
                    discountPrice = 1450.0,
                    stock = 40L,
                    imageUrl = "https://images.unsplash.com/photo-1590658268037-6bf12165a8df?w=600",
                    sku = "MK-TWS-005",
                    rating = 4.8,
                    reviewCount = 175,
                    badge = "Top Rated"
                ),

                // 2. Fashion & Three-Piece Collection
                ProductEntity(
                    id = "fashion_001",
                    name = "Exclusive Organza Silk Embroidered Three-Piece",
                    description = "Premium Organza Kameez with Zari & Sequence Hand Embroidery, Soft Butter Silk Salwar, Digital Printed Dupatta.",
                    category = "Three-Piece Collection",
                    price = 3450.0,
                    discountPrice = 2850.0,
                    stock = 30L,
                    imageUrl = "https://images.unsplash.com/photo-1610030469983-98e550d6193c?w=600",
                    sku = "MK-3PC-001",
                    rating = 5.0,
                    reviewCount = 188,
                    badge = "Eid Collection"
                ),
                ProductEntity(
                    id = "fashion_002",
                    name = "Kashmiri Aari Work Georgette Three-Piece",
                    description = "Authentic Kashmiri Needlework on Pure Georgette, Santoon Inner & Bottom, Heavy Border Chiffon Dupatta.",
                    category = "Three-Piece Collection",
                    price = 4200.0,
                    discountPrice = 3600.0,
                    stock = 18L,
                    imageUrl = "https://images.unsplash.com/photo-1583391733956-3750e0ff4e8b?w=600",
                    sku = "MK-3PC-002",
                    rating = 4.9,
                    reviewCount = 92,
                    badge = "Luxury"
                ),
                ProductEntity(
                    id = "fashion_003",
                    name = "Royal Silk Jacquard Men's Panjabi",
                    description = "Tailored Fit Jacquard Silk, Metallic Brass Buttons, Subtle Collar Work, Ideal for Festive Occasions.",
                    category = "Fashion",
                    price = 2650.0,
                    discountPrice = 2150.0,
                    stock = 25L,
                    imageUrl = "https://images.unsplash.com/photo-1602810318383-e386cc2a3ccf?w=600",
                    sku = "MK-FSH-001",
                    rating = 4.8,
                    reviewCount = 76,
                    badge = "New Arrival"
                ),
                ProductEntity(
                    id = "fashion_004",
                    name = "Arabian Royal Oud & Amber Luxury Attar",
                    description = "Pure Non-Alcoholic Concentrated Perfume Oil, Long-Lasting Warm Woody & Floral Fragrance, 12ml Crystal Bottle.",
                    category = "Lifestyle",
                    price = 1500.0,
                    discountPrice = 1200.0,
                    stock = 50L,
                    imageUrl = "https://images.unsplash.com/photo-1592945403244-b3fbafd7f539?w=600",
                    sku = "MK-LFT-001",
                    rating = 4.9,
                    reviewCount = 130,
                    badge = "Authentic"
                )
            )
            dao.insertProducts(initialInventory)
        }
    }
}
