package com.veeransh.aifashion.enterprise.data.local.dao

import androidx.room.*
import com.veeransh.aifashion.enterprise.data.local.entity.StockBalanceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StockBalanceDao {
    @Query("SELECT * FROM stock_balances WHERE productId = :productId AND locationId = :locationId")
    suspend fun getBalance(productId: String, locationId: String): StockBalanceEntity?

    @Query("SELECT * FROM stock_balances WHERE productId = :productId AND locationId = :locationId")
    fun observeBalance(productId: String, locationId: String): Flow<StockBalanceEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(balance: StockBalanceEntity): Long

    @Update
    suspend fun update(balance: StockBalanceEntity)

    @Query("UPDATE stock_balances SET totalStock = totalStock + :quantity, updatedAt = :timestamp WHERE productId = :productId AND locationId = :locationId")
    suspend fun increaseBalance(productId: String, locationId: String, quantity: Int, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE stock_balances SET totalStock = totalStock - :quantity, updatedAt = :timestamp WHERE productId = :productId AND locationId = :locationId AND totalStock >= :quantity")
    suspend fun decreaseBalance(productId: String, locationId: String, quantity: Int, timestamp: Long = System.currentTimeMillis()): Int
}
