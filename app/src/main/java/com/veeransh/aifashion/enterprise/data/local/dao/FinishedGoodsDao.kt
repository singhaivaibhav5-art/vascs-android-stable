package com.veeransh.aifashion.enterprise.data.local.dao

import androidx.room.*
import com.veeransh.aifashion.enterprise.data.local.entity.FinishedGoodsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FinishedGoodsDao {
    @Query("SELECT * FROM finished_goods")
    fun getAll(): Flow<List<FinishedGoodsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(goods: FinishedGoodsEntity)

    @Update
    suspend fun update(goods: FinishedGoodsEntity)

    @Query("DELETE FROM finished_goods WHERE finishedId = :id")
    suspend fun delete(id: Long)
}
