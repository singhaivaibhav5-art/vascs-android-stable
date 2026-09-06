package com.veeransh.aifashion.enterprise.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.net.Uri
import com.veeransh.aifashion.enterprise.data.local.entity.PlacementEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object PlacementImageProcessor {

    suspend fun processImage(
        context: Context,
        sourceUri: String,
        placement: PlacementEntity
    ): String = withContext(Dispatchers.IO) {
        if (sourceUri.isBlank()) return@withContext ""
        
        val targetWidth = placement.targetWidthPx.coerceAtLeast(1)
        val targetHeight = placement.targetHeightPx.coerceAtLeast(1)
        
        val uri = Uri.parse(sourceUri)
        
        // 1. Decode Source Bitmap (Safely)
        val sourceBitmap = decodeBitmap(context, uri, targetWidth * 2, targetHeight * 2) 
            ?: throw Exception("Failed to decode source image")
            
        try {
            // 2. Create Target Canvas
            val outputBitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(outputBitmap)
            val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
            
            // 3. Calculate Transformation Matrix
            val matrix = calculateMatrix(
                srcWidth = sourceBitmap.width.toFloat(),
                srcHeight = sourceBitmap.height.toFloat(),
                dstWidth = targetWidth.toFloat(),
                dstHeight = targetHeight.toFloat(),
                placement = placement
            )
            
            // 4. Draw
            canvas.drawBitmap(sourceBitmap, matrix, paint)
            
            // 5. Save to Persistent Storage
            val fingerprint = generateFingerprint(placement, sourceUri)
            val fileName = "placement_${placement.placementId}_fp_${fingerprint}.jpg"
            val dir = File(context.filesDir, "placements")
            if (!dir.exists()) dir.mkdirs()
            
            val file = File(dir, fileName)
            FileOutputStream(file).use { out ->
                outputBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }
            
            outputBitmap.recycle()
            Uri.fromFile(file).toString()
        } finally {
            sourceBitmap.recycle()
        }
    }

    private fun decodeBitmap(context: Context, uri: Uri, maxWidth: Int, maxHeight: Int): Bitmap? {
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        
        context.contentResolver.openInputStream(uri)?.use { 
            BitmapFactory.decodeStream(it, null, options)
        }
        
        var sampleSize = 1
        if (options.outWidth > maxWidth || options.outHeight > maxHeight) {
            val halfWidth = options.outWidth / 2
            val halfHeight = options.outHeight / 2
            while (halfWidth / sampleSize >= maxWidth && halfHeight / sampleSize >= maxHeight) {
                sampleSize *= 2
            }
        }
        
        return context.contentResolver.openInputStream(uri)?.use { 
            BitmapFactory.decodeStream(it, null, BitmapFactory.Options().apply {
                inSampleSize = sampleSize
            })
        }
    }

    private fun calculateMatrix(
        srcWidth: Float,
        srcHeight: Float,
        dstWidth: Float,
        dstHeight: Float,
        placement: PlacementEntity
    ): Matrix {
        val matrix = Matrix()
        
        when (placement.cropMode) {
            "CenterCrop" -> {
                // Initial fill scale
                val scaleX = dstWidth / srcWidth
                val scaleY = dstHeight / srcHeight
                val initialScale = Math.max(scaleX, scaleY)
                
                // Centering translation
                val dx = (dstWidth - srcWidth * initialScale) / 2f
                val dy = (dstHeight - srcHeight * initialScale) / 2f
                
                matrix.postScale(initialScale, initialScale)
                matrix.postTranslate(dx, dy)
                
                // Apply manual crop state (normalized to dstWidth)
                val manualScale = placement.cropScale
                val manualOffsetX = placement.cropOffsetX * dstWidth
                val manualOffsetY = placement.cropOffsetY * dstWidth
                
                // Scale around center of target
                matrix.postScale(manualScale, manualScale, dstWidth / 2f, dstHeight / 2f)
                // Panning
                matrix.postTranslate(manualOffsetX, manualOffsetY)
            }
            "Fit" -> {
                val scaleX = dstWidth / srcWidth
                val scaleY = dstHeight / srcHeight
                val scale = Math.min(scaleX, scaleY)
                val dx = (dstWidth - srcWidth * scale) / 2f
                val dy = (dstHeight - srcHeight * scale) / 2f
                matrix.postScale(scale, scale)
                matrix.postTranslate(dx, dy)
            }
            "FillBounds" -> {
                matrix.postScale(dstWidth / srcWidth, dstHeight / srcHeight)
            }
            else -> {
                // Original / Center
                val dx = (dstWidth - srcWidth) / 2f
                val dy = (dstHeight - srcHeight) / 2f
                matrix.postTranslate(dx, dy)
            }
        }
        
        return matrix
    }

    fun generateFingerprint(placement: PlacementEntity, sourceUri: String): String {
        val raw = "${sourceUri}|${placement.cropScale}|${placement.cropOffsetX}|${placement.cropOffsetY}|" +
                "${placement.aspectRatio}|${placement.cropMode}|${placement.targetWidthPx}|${placement.targetHeightPx}"
        // Simple hash for filename safety
        return raw.hashCode().toString(16)
    }

    fun isStale(placement: PlacementEntity, sourceUri: String): Boolean {
        if (placement.imageUri.isBlank()) return true
        val currentFingerprint = generateFingerprint(placement, sourceUri)
        // If imageUri contains the current fingerprint, it's NOT stale
        return !placement.imageUri.contains("_fp_$currentFingerprint")
    }

    fun cleanupOldAsset(context: Context, oldUri: String, currentUri: String = "") {
        if (oldUri.isBlank() || oldUri == currentUri || !oldUri.startsWith("file://")) return
        try {
            val path = oldUri.removePrefix("file://")
            val file = File(path)
            if (file.exists() && file.parentFile?.name == "placements") {
                file.delete()
            }
        } catch (e: Exception) {
            // Ignore cleanup failures
        }
    }
}
