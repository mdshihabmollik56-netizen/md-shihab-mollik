package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Query("SELECT * FROM local_products ORDER BY price DESC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM local_products WHERE category = :category ORDER BY price DESC")
    fun getProductsByCategory(category: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM local_products WHERE id = :id LIMIT 1")
    suspend fun getProductById(id: String): ProductEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)

    @Query("DELETE FROM local_products WHERE id = :id")
    suspend fun deleteProductById(id: String)

    @Query("DELETE FROM local_products")
    suspend fun clearAllProducts()
}
