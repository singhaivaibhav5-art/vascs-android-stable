package com.veeransh.aifashion.enterprise.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "finished_goods",
    indices = [
        Index(value = ["batchId"]),
        Index(value = ["sku"]),
        Index(value = ["qrNumber"])
    ]
)
data class FinishedGoodsEntity(
    @PrimaryKey(autoGenerate = true) val finishedId: Long = 0,
    val batchId: Long,
    val productId: String,
    val sku: String,
    val qrNumber: String,
    val finishedQty: Int,
    val availableQty: Int,
    val category: String = "",
    val status: String = "Available"
)
