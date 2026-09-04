package com.veeransh.aifashion.enterprise.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ai_drape_results")
data class AiDrapeResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val originalUri: String,
    val drapedUri: String,
    val status: String, // DRAPED, FAILED, PENDING
    val createdAt: Long = System.currentTimeMillis(),
    val isSelected: Boolean = false
)
