package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val category: String, // Gadgets, Three-Piece Collection, Fashion, Lifestyle
    val price: Double,
    val discountPrice: Double = 0.0,
    val stock: Long = 10L,
    val imageUrl: String,
    val sku: String = "",
    val rating: Double = 4.9,
    val reviewCount: Int = 120,
    val badge: String = "In Stock"
)
