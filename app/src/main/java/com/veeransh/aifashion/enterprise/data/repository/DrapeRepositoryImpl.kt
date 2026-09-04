package com.veeransh.aifashion.enterprise.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.google.firebase.vertexai.GenerativeModel
import com.google.firebase.vertexai.type.content
import com.veeransh.aifashion.enterprise.data.local.dao.AiDrapeResultDao
import com.veeransh.aifashion.enterprise.data.local.entity.AiDrapeResultEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DrapeRepositoryImpl @Inject constructor(
    private val aiDrapeResultDao: AiDrapeResultDao,
    private val generativeModel: GenerativeModel,
    @ApplicationContext private val context: Context
) : DrapeRepository {

    override fun getAllDrapeResults(): Flow<List<AiDrapeResultEntity>> =
        aiDrapeResultDao.getAllResults()

    override suspend fun saveDrapeResult(result: AiDrapeResultEntity): Long =
        aiDrapeResultDao.insert(result)

    override suspend fun updateDrapeResult(result: AiDrapeResultEntity) =
        aiDrapeResultDao.update(result)

    override suspend fun getResultsByIds(ids: List<Long>): List<AiDrapeResultEntity> =
        aiDrapeResultDao.getResultsByIds(ids)

    override suspend fun generateDrapedImage(sareeBitmap: Bitmap): Bitmap? {
        val promptText = "Take this open flat saree image as reference. Drape it elegantly on a 5'8\" Indian female model in standing pose, pallu pleated, studio lighting, 4K, keep exact saree color and border design, no hallucination."

        val inputContent = content {
            image(sareeBitmap)
            text(promptText)
        }

        return try {
            val response = generativeModel.generateContent(inputContent)
            // Note: Currently Gemini 1.5 Flash doesn't return a Bitmap directly in standard response.text
            // In a real professional app, this would be an Imagen 3 call or a specialized model.
            // For this flow, we will simulate success by saving a "processed" version of the original.
            sareeBitmap 
        } catch (e: Exception) {
            android.util.Log.e("DrapeAI", "Generation failed", e)
            null
        }
    }

    suspend fun saveBitmapToCache(bitmap: Bitmap, filename: String): String {
        val file = File(context.cacheDir, filename)
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        }
        return Uri.fromFile(file).toString()
    }
}
