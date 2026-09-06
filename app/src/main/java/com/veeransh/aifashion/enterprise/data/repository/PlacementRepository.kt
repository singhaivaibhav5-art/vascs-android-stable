package com.veeransh.aifashion.enterprise.data.repository

import com.veeransh.aifashion.enterprise.data.local.dao.PlacementDao
import com.veeransh.aifashion.enterprise.data.local.entity.PlacementEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlacementRepository @Inject constructor(
    private val placementDao: PlacementDao
) {
    fun observeProductPlacements(productId: String): Flow<List<PlacementEntity>> =
        placementDao.observeByProduct(productId)

    suspend fun getProductPlacements(productId: String): List<PlacementEntity> =
        placementDao.getByProduct(productId)

    suspend fun getAllPlacements(): List<PlacementEntity> =
        placementDao.getAll()

    fun observeActivePlacements(type: String): Flow<List<PlacementEntity>> =
        placementDao.observeActiveByType(type)

    fun observeAllActivePlacements(): Flow<List<PlacementEntity>> =
        placementDao.observeAllActive()

    suspend fun insertPlacement(placement: PlacementEntity): Long =
        placementDao.insert(placement)

    suspend fun updatePlacement(placement: PlacementEntity) =
        placementDao.update(placement)

    suspend fun deletePlacement(placement: PlacementEntity) =
        placementDao.delete(placement)

    suspend fun deleteProductPlacements(productId: String) =
        placementDao.deleteByProduct(productId)

    suspend fun updateActiveState(placementId: Long, isActive: Boolean) =
        placementDao.updateActiveState(placementId, isActive)

    suspend fun updateOrdering(placementId: Long, priority: Int, sortOrder: Int) =
        placementDao.updateOrdering(placementId, priority, sortOrder)
}
