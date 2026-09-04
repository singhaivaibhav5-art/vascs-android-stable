package com.veeransh.aifashion.enterprise.ui.shop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.veeransh.aifashion.enterprise.types.StylePartnerWallet
import com.veeransh.aifashion.enterprise.types.WalletTransaction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommissionViewModel @Inject constructor() : ViewModel() {

    private val SEVEN_DAYS = 7 * 24 * 60 * 60 * 1000L

    private val _wallet = MutableStateFlow(
        StylePartnerWallet(
            pending = 2350.0,
            available = 12450.50,
            transactions = listOf(
                WalletTransaction("ORD-901", 1250.0, status = "AVAILABLE", createdAt = System.currentTimeMillis() - SEVEN_DAYS - 1000),
                WalletTransaction("ORD-905", 850.0, status = "PENDING", createdAt = System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000L),
                WalletTransaction("ORD-909", 1500.0, status = "PENDING", createdAt = System.currentTimeMillis() - 10 * 60 * 1000L)
            )
        )
    )
    val wallet = _wallet.asStateFlow()

    fun creditCommission(orderId: String, amount: Double) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val newTransaction = WalletTransaction(
                orderId = orderId,
                amount = amount,
                status = "PENDING",
                createdAt = now,
                availableAt = now + SEVEN_DAYS
            )
            
            val currentWallet = _wallet.value
            _wallet.value = currentWallet.copy(
                pending = currentWallet.pending + amount,
                transactions = listOf(newTransaction) + currentWallet.transactions
            )
        }
    }

    fun releasePendingCommissions() {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val currentWallet = _wallet.value
            
            val toRelease = currentWallet.transactions.filter { 
                it.status == "PENDING" && it.availableAt <= now 
            }
            
            if (toRelease.isNotEmpty()) {
                val releaseAmount = toRelease.sumOf { it.amount }
                val updatedTransactions = currentWallet.transactions.map { txn ->
                    if (toRelease.contains(txn)) txn.copy(status = "AVAILABLE") else txn
                }
                
                _wallet.value = currentWallet.copy(
                    pending = currentWallet.pending - releaseAmount,
                    available = currentWallet.available + releaseAmount,
                    transactions = updatedTransactions
                )
            }
        }
    }

    fun canRedeem(): Boolean {
        // available >= 500 && bankVerified (mocked bankVerified as true)
        return _wallet.value.available >= 500.0
    }
}
