package com.veeransh.aifashion.enterprise.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "order_items",
    indices = [
        Index(value = ["orderId"]),
        Index(value = ["productId"]),
        Index(value = ["sku"])
    ]
)
data class OrderItemEntity(
    @PrimaryKey(autoGenerate = true)
    val orderItemId: Long = 0,
    val orderId: Long,
    val productId: String,
    val sku: String,
    val qrNumber: String = "",
    val productName: String,
    val qty: Int,
    val rate: Double,
    val amount: Double,
    val gst: Double,
    val netAmount: Double
)
