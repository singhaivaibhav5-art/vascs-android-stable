package com.veeransh.aifashion.enterprise.data.local.dao

import androidx.room.*
import com.veeransh.aifashion.enterprise.data.local.entity.AdminConfigEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AdminConfigDao {
    @Query("SELECT * FROM admin_config WHERE id = 1")
    fun getConfig(): Flow<AdminConfigEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(config: AdminConfigEntity)

    @Update
    suspend fun update(config: AdminConfigEntity)
}
