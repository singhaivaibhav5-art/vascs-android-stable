package com.veeransh.aifashion.enterprise.data.local.dao

import androidx.room.*
import com.veeransh.aifashion.enterprise.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: ProductEntity)

    @Update
    suspend fun update(product: ProductEntity)

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getById(id: String): ProductEntity?

    @Query("SELECT * FROM products WHERE name LIKE '%' || :query || '%' OR sku LIKE '%' || :query || '%'")
    fun search(query: String): Flow<List<ProductEntity>>

    @Query("DELETE FROM products WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("UPDATE products SET stock = stock - :qty WHERE id = :id AND stock >= :qty")
    suspend fun deductStock(id: String, qty: Int): Int

    @Query("UPDATE products SET stock = stock + :qty WHERE id = :id")
    suspend fun incrementStock(id: String, qty: Int): Int

    @Query("SELECT EXISTS(SELECT 1 FROM products WHERE sku = :sku)")
    suspend fun existsBySku(sku: String): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM products WHERE barcode = :barcode)")
    suspend fun existsByBarcode(barcode: String): Boolean
}
