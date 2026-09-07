package com.veeransh.aifashion.enterprise.data.repository

import androidx.room.withTransaction
import com.veeransh.aifashion.enterprise.data.local.AppDatabase
import com.veeransh.aifashion.enterprise.data.local.dao.ProductDao
import com.veeransh.aifashion.enterprise.data.local.dao.StockBalanceDao
import com.veeransh.aifashion.enterprise.data.local.dao.StockTransactionDao
import com.veeransh.aifashion.enterprise.data.local.entity.StockBalanceEntity
import com.veeransh.aifashion.enterprise.data.local.entity.StockTransactionEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockRepository @Inject constructor(
    private val db: AppDatabase,
    private val productDao: ProductDao,
    private val transactionDao: StockTransactionDao,
    private val balanceDao: StockBalanceDao
) {
    // Transaction Methods
    suspend fun insertTransaction(transaction: StockTransactionEntity): Long =
        transactionDao.insert(transaction)

    suspend fun getTransactionsByProduct(productId: String): List<StockTransactionEntity> =
        transactionDao.getTransactionsByProduct(productId)

    fun observeTransactionsByProduct(productId: String): Flow<List<StockTransactionEntity>> =
        transactionDao.observeTransactionsByProduct(productId)

    suspend fun getLatestTransaction(productId: String): StockTransactionEntity? =
        transactionDao.getLatestTransaction(productId)

    // Balance Methods
    suspend fun getBalance(productId: String, locationId: String = ""): StockBalanceEntity? =
        balanceDao.getBalance(productId, locationId)

    fun observeBalance(productId: String, locationId: String = ""): Flow<StockBalanceEntity?> =
        balanceDao.observeBalance(productId, locationId)

    suspend fun updateBalance(balance: StockBalanceEntity) =
        balanceDao.update(balance)
        
    suspend fun increaseBalance(productId: String, locationId: String, quantity: Int) =
        balanceDao.increaseBalance(productId, locationId, quantity)
        
    suspend fun decreaseBalance(productId: String, locationId: String, quantity: Int): Int =
        balanceDao.decreaseBalance(productId, locationId, quantity)

    suspend fun adjustStock(
        productId: String,
        adjustmentType: String, // ADJUSTMENT_ADD, ADJUSTMENT_SUB
        quantity: Int,
        reason: String,
        locationId: String = "",
        unitCost: Double? = null
    ): Result<Unit> = try {
        if (quantity <= 0) throw Exception("Quantity must be positive")
        if (reason.isBlank()) throw Exception("Reason is required")
        if (adjustmentType != "ADJUSTMENT_ADD" && adjustmentType != "ADJUSTMENT_SUB") {
            throw Exception("Invalid adjustment type")
        }

        db.withTransaction {
            val p = productDao.getById(productId) ?: throw Exception("Product not found")
            val currentMasterStock = p.stock
            
            // 1. Synchronize / Validate StockBalance
            var balanceRecord = balanceDao.getBalance(productId, locationId)
            if (balanceRecord == null) {
                // Initialize if missing
                balanceDao.insert(StockBalanceEntity(
                    productId = productId,
                    locationId = locationId,
                    totalStock = currentMasterStock
                ))
                balanceRecord = balanceDao.getBalance(productId, locationId)
            } else if (balanceRecord.totalStock != currentMasterStock) {
                throw Exception("Stock synchronization error. Master: $currentMasterStock, Balance: ${balanceRecord?.totalStock}")
            }

            // 2. Perform Operational Updates
            if (adjustmentType == "ADJUSTMENT_ADD") {
                productDao.incrementStock(productId, quantity)
                balanceDao.increaseBalance(productId, locationId, quantity)
            } else {
                val updatedRows = productDao.deductStock(productId, quantity)
                if (updatedRows == 0) throw Exception("Insufficient stock for adjustment")
                val balanceUpdated = balanceDao.decreaseBalance(productId, locationId, quantity)
                if (balanceUpdated == 0) throw Exception("Insufficient balance record")
            }

            // 3. Record in Ledger
            val finalBalance = if (adjustmentType == "ADJUSTMENT_ADD") currentMasterStock + quantity 
                              else currentMasterStock - quantity
            
            val referenceId = "ADJ-${System.currentTimeMillis()}-${UUID.randomUUID().toString().take(4)}"
            
            transactionDao.insert(StockTransactionEntity(
                productId = productId,
                transactionType = adjustmentType,
                quantity = quantity,
                referenceType = "ADJUSTMENT",
                referenceId = referenceId,
                locationId = locationId,
                unitCost = unitCost ?: p.purchasePrice,
                balanceAfter = finalBalance,
                notes = reason
            ))
            
            Result.success(Unit)
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}
