package com.veeransh.aifashion.enterprise.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "stock_transactions",
    indices = [
        Index(value = ["productId"]),
        Index(value = ["transactionType"]),
        Index(value = ["referenceType", "referenceId"]),
        Index(value = ["createdAt"])
    ]
)
data class StockTransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val transactionId: Long = 0,
    val productId: String,
    val transactionType: String, // PURCHASE, SALE, RETURN_IN, ADJUSTMENT_ADD, ADJUSTMENT_SUB
    val quantity: Int, // Always stored as positive
    val referenceType: String, // ORDER, PURCHASE, ADJUSTMENT, RETURN
    val referenceId: String,
    val batchId: Long? = null,
    val locationId: String = "",
    val unitCost: Double,
    val balanceAfter: Int,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
