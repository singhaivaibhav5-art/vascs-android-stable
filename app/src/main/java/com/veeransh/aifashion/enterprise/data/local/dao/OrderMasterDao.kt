package com.veeransh.aifashion.enterprise.data.local.dao

import androidx.room.*
import com.veeransh.aifashion.enterprise.data.local.entity.OrderMasterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderMasterDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(order: OrderMasterEntity): Long

    @Update
    suspend fun update(order: OrderMasterEntity)

    @Query("SELECT * FROM order_master ORDER BY orderId DESC")
    fun getAll(): Flow<List<OrderMasterEntity>>

    @Query("SELECT * FROM order_master WHERE orderId = :id")
    suspend fun getById(id: Long): OrderMasterEntity?

    @Query("SELECT * FROM order_master WHERE status = :status")
    fun getByStatus(status: String): Flow<List<OrderMasterEntity>>
}
