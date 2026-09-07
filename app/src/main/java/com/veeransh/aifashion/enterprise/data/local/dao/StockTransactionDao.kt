package com.veeransh.aifashion.enterprise.data.local.dao

import androidx.room.*
import com.veeransh.aifashion.enterprise.data.local.entity.StockTransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StockTransactionDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(transaction: StockTransactionEntity): Long

    @Query("SELECT * FROM stock_transactions WHERE productId = :productId ORDER BY createdAt DESC")
    suspend fun getTransactionsByProduct(productId: String): List<StockTransactionEntity>

    @Query("SELECT * FROM stock_transactions WHERE productId = :productId ORDER BY createdAt DESC")
    fun observeTransactionsByProduct(productId: String): Flow<List<StockTransactionEntity>>

    @Query("SELECT * FROM stock_transactions WHERE productId = :productId ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLatestTransaction(productId: String): StockTransactionEntity?

    @Query("SELECT * FROM stock_transactions WHERE referenceType = :referenceType AND referenceId = :referenceId")
    suspend fun getTransactionsByReference(referenceType: String, referenceId: String): List<StockTransactionEntity>

    @Query("SELECT * FROM stock_transactions WHERE transactionType = :transactionType ORDER BY createdAt DESC")
    suspend fun getTransactionsByType(transactionType: String): List<StockTransactionEntity>

    @Query("SELECT * FROM stock_transactions ORDER BY createdAt DESC")
    suspend fun getAllTransactions(): List<StockTransactionEntity>
}
