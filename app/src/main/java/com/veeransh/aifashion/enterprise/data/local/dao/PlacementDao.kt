package com.veeransh.aifashion.enterprise.data.local.dao

import androidx.room.*
import com.veeransh.aifashion.enterprise.data.local.entity.PlacementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlacementDao {
    @Query("SELECT * FROM product_placements WHERE productId = :productId")
    fun observeByProduct(productId: String): Flow<List<PlacementEntity>>

    @Query("SELECT * FROM product_placements WHERE productId = :productId")
    suspend fun getByProduct(productId: String): List<PlacementEntity>

    @Query("SELECT * FROM product_placements")
    suspend fun getAll(): List<PlacementEntity>

    @Query("SELECT * FROM product_placements WHERE placementType = :type AND isActive = 1 ORDER BY sortOrder ASC, priority DESC")
    fun observeActiveByType(type: String): Flow<List<PlacementEntity>>

    @Query("SELECT * FROM product_placements WHERE isActive = 1 ORDER BY sortOrder ASC, priority DESC")
    fun observeAllActive(): Flow<List<PlacementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(placement: PlacementEntity): Long

    @Update
    suspend fun update(placement: PlacementEntity)

    @Delete
    suspend fun delete(placement: PlacementEntity)

    @Query("DELETE FROM product_placements WHERE productId = :productId")
    suspend fun deleteByProduct(productId: String)

    @Query("UPDATE product_placements SET isActive = :isActive, updatedAt = :timestamp WHERE placementId = :placementId")
    suspend fun updateActiveState(placementId: Long, isActive: Boolean, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE product_placements SET priority = :priority, sortOrder = :sortOrder, updatedAt = :timestamp WHERE placementId = :placementId")
    suspend fun updateOrdering(placementId: Long, priority: Int, sortOrder: Int, timestamp: Long = System.currentTimeMillis())
}
