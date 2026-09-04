package com.veeransh.aifashion.enterprise.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "order_master",
    indices = [
        Index(value = ["orderNumber"]),
        Index(value = ["dealerId"]),
        Index(value = ["status"])
    ]
)
data class OrderMasterEntity(
    @PrimaryKey(autoGenerate = true)
    val orderId: Long = 0,
    val orderNumber: String,
    val dealerId: String,
    val dealerName: String,
    val mobile: String,
    val whatsapp: String,
    val orderDate: String,
    val totalItems: Int,
    val totalQty: Int,
    val totalAmount: Double,
    val gstAmount: Double,
    val netAmount: Double,
    val status: String = "PENDING", // PENDING, APPROVED, PACKING, PACKED, DISPATCHED, DELIVERED, CANCELLED
    val remarks: String = "",
    val createdDate: String,
    val updatedDate: String
)
