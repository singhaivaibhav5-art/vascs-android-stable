package com.veeransh.aifashion.enterprise.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dealer_wallets")
data class DealerWalletEntity(
    @PrimaryKey
    val dealerId: String,
    val dealerCode: String,
    val balanceAvailable: Double = 0.0,
    val balancePending: Double = 0.0,
    val totalEarned: Double = 0.0,
    val lastUpdated: Long = System.currentTimeMillis()
)
