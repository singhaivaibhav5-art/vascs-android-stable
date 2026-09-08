package com.veeransh.aifashion.enterprise.data.repository

import androidx.room.withTransaction
import com.veeransh.aifashion.enterprise.data.local.AppDatabase
import com.veeransh.aifashion.enterprise.data.local.dao.*
import com.veeransh.aifashion.enterprise.data.local.entity.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepository @Inject constructor(
    private val db: AppDatabase,
    private val orderMasterDao: OrderMasterDao,
    private val orderItemDao: OrderItemDao,
    private val productDao: ProductDao,
    private val stockBalanceDao: StockBalanceDao,
    private val stockTransactionDao: StockTransactionDao
) {
    val allOrders: Flow<List<OrderMasterEntity>> = orderMasterDao.getAll()

    suspend fun getOrderById(id: Long): OrderMasterEntity? = orderMasterDao.getById(id)

    fun getItemsForOrder(orderId: Long): Flow<List<OrderItemEntity>> = orderItemDao.getItemsForOrder(orderId)

    suspend fun createOrder(order: OrderMasterEntity, items: List<OrderItemEntity>): Result<Long> {
        return try {
            db.withTransaction {
                // 1. Insert Order Master first to get ID for Ledger
                val orderId = orderMasterDao.insert(order)

                // 2. Process each item: Stock Master -> Balance -> Ledger
                for (item in items) {
                    if (item.qty <= 0) throw Exception("Invalid quantity for ${item.productName}")

                    // Fetch Product Master for initial stock and cost snapshot
                    val p = productDao.getById(item.productId) ?: throw Exception("Product not found: ${item.productId}")

                    // Final MOQ Guard (Phase 3.4.4)
                    val effectiveMin = if (!p.isMoqEnabled) 1
                                     else if (order.dealerId.isNotBlank()) p.dealerMoq
                                     else p.moq

                    if (item.qty < effectiveMin) {
                        throw IllegalArgumentException("Minimum quantity for ${p.name} is $effectiveMin. (Requested: ${item.qty})")
                    }

                    val beforeStock = p.stock

                    // A. Deduct ProductEntity.stock (Operational Cache)
                    val updatedRows = productDao.deductStock(item.productId, item.qty)
                    if (updatedRows == 0) {
                        throw Exception("Insufficient stock for product: ${item.productName}")
                    }

                    // B. Manage StockBalanceEntity (Operational Balance)
                    val locationId = "" // Default global location
                    var balanceRecord = stockBalanceDao.getBalance(item.productId, locationId)

                    if (balanceRecord == null) {
                        // INITIALIZATION: Sync with current Master Stock if missing
                        stockBalanceDao.insert(StockBalanceEntity(
                            productId = item.productId,
                            locationId = locationId,
                            totalStock = beforeStock
                        ))
                    } else {
                        // MISMATCH PROTECTION: Ensure Balance and Master are in sync
                        if (balanceRecord.totalStock != beforeStock) {
                            throw Exception("Inventory synchronization error for ${item.productName}. Expected $beforeStock, found ${balanceRecord.totalStock}")
                        }
                    }

                    // C. Deduct StockBalance
                    val balanceDecreased = stockBalanceDao.decreaseBalance(item.productId, locationId, item.qty)
                    if (balanceDecreased == 0) {
                        throw Exception("Insufficient balance record for product: ${item.productName}")
                    }

                    // D. Insert SALE StockTransaction (Immutable Ledger)
                    val afterStock = beforeStock - item.qty
                    stockTransactionDao.insert(StockTransactionEntity(
                        productId = item.productId,
                        transactionType = "SALE",
                        quantity = item.qty,
                        referenceType = "ORDER",
                        referenceId = orderId.toString(),
                        unitCost = p.purchasePrice,
                        balanceAfter = afterStock,
                        locationId = locationId
                    ))
                }

                // 3. Insert Order Items
                val itemsWithId = items.map { it.copy(orderId = orderId) }
                orderItemDao.insertAll(itemsWithId)

                Result.success(orderId)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateOrderStatus(order: OrderMasterEntity) = orderMasterDao.update(order)
}
