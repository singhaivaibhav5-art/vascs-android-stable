package com.veeransh.aifashion.enterprise.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "stock_balances",
    indices = [
        Index(value = ["productId", "locationId"], unique = true)
    ]
)
data class StockBalanceEntity(
    @PrimaryKey(autoGenerate = true)
    val balanceId: Long = 0,
    val productId: String,
    val locationId: String = "",
    val totalStock: Int = 0,
    val updatedAt: Long = System.currentTimeMillis()
)
