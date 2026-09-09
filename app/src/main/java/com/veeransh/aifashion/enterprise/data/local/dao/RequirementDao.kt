package com.veeransh.aifashion.enterprise.data.local.dao

import androidx.room.*
import com.veeransh.aifashion.enterprise.data.local.entity.CustomerRequirementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RequirementDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(requirement: CustomerRequirementEntity)

    @Query("SELECT * FROM customer_requirements ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<CustomerRequirementEntity>>

    @Query("SELECT * FROM customer_requirements WHERE userId = :userId ORDER BY createdAt DESC")
    fun observeByUserId(userId: String): Flow<List<CustomerRequirementEntity>>

    @Query("SELECT * FROM customer_requirements WHERE requirementId = :requirementId")
    suspend fun getById(requirementId: String): CustomerRequirementEntity?

    @Query("UPDATE customer_requirements SET status = :status, updatedAt = :updatedAt WHERE requirementId = :requirementId")
    suspend fun updateStatus(requirementId: String, status: String, updatedAt: Long)

    @Query("UPDATE customer_requirements SET adminNotes = :notes, updatedAt = :updatedAt WHERE requirementId = :requirementId")
    suspend fun updateAdminNotes(requirementId: String, notes: String, updatedAt: Long)
}
