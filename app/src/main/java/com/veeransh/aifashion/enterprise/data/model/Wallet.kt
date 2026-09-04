package com.veeransh.aifashion.enterprise.types

data class WalletTransaction(
    val orderId: String,
    val amount: Double,
    val type: String = "credit_pending",
    val status: String, // pending/available
    val createdAt: Long = System.currentTimeMillis(),
    val availableAt: Long = createdAt + 7 * 24 * 60 * 60 * 1000
)

data class StylePartnerWallet(
    val pending: Double,
    val available: Double,
    val transactions: List<WalletTransaction>
)
