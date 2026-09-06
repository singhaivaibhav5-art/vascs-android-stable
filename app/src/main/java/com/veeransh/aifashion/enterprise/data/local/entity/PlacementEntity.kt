package com.veeransh.aifashion.enterprise.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "product_placements",
    indices = [
        Index(value = ["productId"]),
        Index(value = ["placementType"]),
        Index(value = ["isActive"]),
        Index(value = ["priority"]),
        Index(value = ["sortOrder"])
    ]
)
data class PlacementEntity(
    @PrimaryKey(autoGenerate = true)
    val placementId: Long = 0,
    val productId: String,
    val placementType: String, // homeHero, homeBanner, productGrid, category, featured, newArrivals, bestSellers, adBanner
    val title: String = "",
    val imageUri: String = "", // Stores the PROCESSED asset URI
    val sourceUri: String = "", // Stores the ORIGINAL high-res source URI (Phase 2.7.3)
    val cropMode: String = "CenterCrop",
    val aspectRatio: String = "Original",
    val targetWidthPx: Int = 0,
    val targetHeightPx: Int = 0,
    val priority: Int = 0,
    val sortOrder: Int = 0,
    val status: String = "Draft",
    val isActive: Boolean = false,
    val startAt: Long? = null,
    val endAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    
    // Phase 2.7.2 Crop Fields
    val cropScale: Float = 1.0f,
    val cropOffsetX: Float = 0.0f,
    val cropOffsetY: Float = 0.0f
)
