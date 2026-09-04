package com.veeransh.aifashion.enterprise.data.local.dao

import androidx.room.*
import com.veeransh.aifashion.enterprise.data.local.entity.AiDrapeResultEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AiDrapeResultDao {
    @Query("SELECT * FROM ai_drape_results ORDER BY createdAt DESC")
    fun getAllResults(): Flow<List<AiDrapeResultEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(result: AiDrapeResultEntity): Long

    @Update
    suspend fun update(result: AiDrapeResultEntity)

    @Query("DELETE FROM ai_drape_results WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM ai_drape_results WHERE id IN (:ids)")
    suspend fun getResultsByIds(ids: List<Long>): List<AiDrapeResultEntity>
}
