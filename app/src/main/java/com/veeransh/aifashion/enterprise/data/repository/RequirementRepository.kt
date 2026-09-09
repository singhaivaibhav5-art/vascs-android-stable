package com.veeransh.aifashion.enterprise.data.repository

import com.veeransh.aifashion.enterprise.data.local.dao.RequirementDao
import com.veeransh.aifashion.enterprise.data.local.entity.CustomerRequirementEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RequirementRepository @Inject constructor(
    private val requirementDao: RequirementDao
) {
    suspend fun create(requirement: CustomerRequirementEntity) = 
        requirementDao.insert(requirement)

    fun observeAll(): Flow<List<CustomerRequirementEntity>> = 
        requirementDao.observeAll()

    fun observeByUserId(userId: String): Flow<List<CustomerRequirementEntity>> = 
        requirementDao.observeByUserId(userId)

    suspend fun getById(requirementId: String): CustomerRequirementEntity? = 
        requirementDao.getById(requirementId)

    suspend fun updateStatus(requirementId: String, status: String) = 
        requirementDao.updateStatus(requirementId, status, System.currentTimeMillis())

    suspend fun updateAdminNotes(requirementId: String, notes: String) = 
        requirementDao.updateAdminNotes(requirementId, notes, System.currentTimeMillis())
}
