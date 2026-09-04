package com.veeransh.aifashion.enterprise.data.repository

import com.veeransh.aifashion.enterprise.data.local.dao.OrderItemDao
import com.veeransh.aifashion.enterprise.data.local.dao.OrderMasterDao
import com.veeransh.aifashion.enterprise.data.local.entity.OrderItemEntity
import com.veeransh.aifashion.enterprise.data.local.entity.OrderMasterEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepository @Inject constructor(
    private val orderMasterDao: OrderMasterDao,
    private val orderItemDao: OrderItemDao
) {
    val allOrders: Flow<List<OrderMasterEntity>> = orderMasterDao.getAll()

    suspend fun getOrderById(id: Long): OrderMasterEntity? = orderMasterDao.getById(id)

    fun getItemsForOrder(orderId: Long): Flow<List<OrderItemEntity>> = orderItemDao.getItemsForOrder(orderId)

    suspend fun createOrder(order: OrderMasterEntity, items: List<OrderItemEntity>) {
        val orderId = orderMasterDao.insert(order)
        val itemsWithId = items.map { it.copy(orderId = orderId) }
        orderItemDao.insertAll(itemsWithId)
    }

    suspend fun updateOrderStatus(order: OrderMasterEntity) = orderMasterDao.update(order)
}
