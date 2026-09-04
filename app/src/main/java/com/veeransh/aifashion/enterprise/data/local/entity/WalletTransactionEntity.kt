package com.veeransh.aifashion.enterprise.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wallet_transactions")
data class WalletTransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dealerId: String,
    val orderId: String,
    val productId: String,
    val type: String, // CREDIT, DEBIT, REDEEM
    val amount: Double,
    val status: String, // PENDING, AVAILABLE
    val pendingTill: Long,
    val timestamp: Long = System.currentTimeMillis()
)
