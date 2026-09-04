package com.veeransh.aifashion.enterprise.data.repository

import com.veeransh.aifashion.enterprise.data.local.dao.ProductDao
import com.veeransh.aifashion.enterprise.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepository @Inject constructor(
    private val productDao: ProductDao
) {
    val allProducts: Flow<List<ProductEntity>> = productDao.getAllProducts()

    suspend fun getProductById(id: String): ProductEntity? = productDao.getById(id)

    suspend fun insertProduct(product: ProductEntity) = productDao.insert(product)

    suspend fun updateProduct(product: ProductEntity) = productDao.update(product)

    fun searchProducts(query: String): Flow<List<ProductEntity>> = productDao.search(query)

    suspend fun deleteProduct(id: String) = productDao.deleteById(id)
}
