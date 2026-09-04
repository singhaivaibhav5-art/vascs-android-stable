package com.veeransh.aifashion.enterprise.data.repository

import com.veeransh.aifashion.enterprise.data.local.dao.FinishedGoodsDao
import com.veeransh.aifashion.enterprise.data.local.entity.FinishedGoodsEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InventoryRepository @Inject constructor(
    private val finishedGoodsDao: FinishedGoodsDao
) {
    val allFinishedGoods: Flow<List<FinishedGoodsEntity>> = finishedGoodsDao.getAll()

    suspend fun saveFinishedGoods(goods: FinishedGoodsEntity) = finishedGoodsDao.insert(goods)

    suspend fun updateFinishedGoods(goods: FinishedGoodsEntity) = finishedGoodsDao.update(goods)

    suspend fun deleteFinishedGoods(id: Long) = finishedGoodsDao.delete(id)
}
