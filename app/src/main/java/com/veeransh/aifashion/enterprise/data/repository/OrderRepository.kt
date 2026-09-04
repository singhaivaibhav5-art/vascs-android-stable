package com.veeransh.aifashion.enterprise.data.repository

import androidx.room.withTransaction
import com.veeransh.aifashion.enterprise.data.local.AppDatabase
import com.veeransh.aifashion.enterprise.data.local.dao.OrderItemDao
import com.veeransh.aifashion.enterprise.data.local.dao.OrderMasterDao
import com.veeransh.aifashion.enterprise.data.local.dao.ProductDao
import com.veeransh.aifashion.enterprise.data.local.entity.OrderItemEntity
import com.veeransh.aifashion.enterprise.data.local.entity.OrderMasterEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepository @Inject constructor(
    private val db: AppDatabase,
    private val orderMasterDao: OrderMasterDao,
    private val orderItemDao: OrderItemDao,
    private val productDao: ProductDao
) {
    val allOrders: Flow<List<OrderMasterEntity>> = orderMasterDao.getAll()

    suspend fun getOrderById(id: Long): OrderMasterEntity? = orderMasterDao.getById(id)

    fun getItemsForOrder(orderId: Long): Flow<List<OrderItemEntity>> = orderItemDao.getItemsForOrder(orderId)

    suspend fun createOrder(order: OrderMasterEntity, items: List<OrderItemEntity>): Result<Long> {
        return try {
            db.withTransaction {
                // 1. Deduct Stock first to ensure availability
                for (item in items) {
                    val updatedRows = productDao.deductStock(item.productId, item.qty)
                    if (updatedRows == 0) {
                        throw Exception("Insufficient stock for product: ${item.productName}")
                    }
                }

                // 2. Insert Order Master
                val orderId = orderMasterDao.insert(order)

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
