package com.veeransh.aifashion.enterprise.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.veeransh.aifashion.enterprise.data.repository.WalletRepository
import com.veeransh.aifashion.enterprise.data.local.entity.DealerWalletEntity
import com.veeransh.aifashion.enterprise.data.local.entity.WalletTransactionEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(
    private val walletRepository: WalletRepository
) : ViewModel() {

    val wallet: StateFlow<DealerWalletEntity?> = walletRepository.wallet.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val transactions: StateFlow<List<WalletTransactionEntity>> = walletRepository.transactions.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun addTransaction(transaction: WalletTransactionEntity) {
        viewModelScope.launch {
            walletRepository.addTransaction(transaction)
        }
    }
}
