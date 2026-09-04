package com.veeransh.aifashion.enterprise.data.repository

import android.graphics.Bitmap
import com.veeransh.aifashion.enterprise.data.local.entity.AiDrapeResultEntity
import kotlinx.coroutines.flow.Flow

interface DrapeRepository {
    fun getAllDrapeResults(): Flow<List<AiDrapeResultEntity>>
    suspend fun saveDrapeResult(result: AiDrapeResultEntity): Long
    suspend fun updateDrapeResult(result: AiDrapeResultEntity)
    suspend fun getResultsByIds(ids: List<Long>): List<AiDrapeResultEntity>
    suspend fun generateDrapedImage(sareeBitmap: Bitmap): Bitmap?
}
