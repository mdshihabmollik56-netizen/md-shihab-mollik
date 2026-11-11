package com.example.data.repository

import android.content.Context
import com.example.data.local.MkShopsDatabase
import com.example.data.local.ProductDao
import com.example.data.local.ProductEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class LocalProductRepository(private val productDao: ProductDao) {

    constructor(context: Context) : this(
        MkShopsDatabase.getDatabase(context).productDao()
    )

    val allProducts: Flow<List<ProductEntity>> = productDao.getAllProducts()

    fun getProductsByCategory(category: String): Flow<List<ProductEntity>> {
        return if (category == "All") {
            productDao.getAllProducts()
        } else {
            productDao.getProductsByCategory(category)
        }
    }

    suspend fun getProductById(id: String): ProductEntity? = withContext(Dispatchers.IO) {
        productDao.getProductById(id)
    }

    suspend fun insertProduct(product: ProductEntity) = withContext(Dispatchers.IO) {
        productDao.insertProduct(product)
    }

    suspend fun insertProducts(products: List<ProductEntity>) = withContext(Dispatchers.IO) {
        productDao.insertProducts(products)
    }

    suspend fun seedIfEmpty() = withContext(Dispatchers.IO) {
        MkShopsDatabase.populateInitialInventory(productDao)
    }
}
