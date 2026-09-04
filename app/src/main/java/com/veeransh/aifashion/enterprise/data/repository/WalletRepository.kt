package com.veeransh.aifashion.enterprise.data.repository

import com.veeransh.aifashion.enterprise.data.local.dao.DealerWalletDao
import com.veeransh.aifashion.enterprise.data.local.entity.DealerWalletEntity
import com.veeransh.aifashion.enterprise.data.local.entity.WalletTransactionEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WalletRepository @Inject constructor(
    private val walletDao: DealerWalletDao
) {
    val wallet: Flow<DealerWalletEntity?> = walletDao.getWallet()
    val transactions: Flow<List<WalletTransactionEntity>> = walletDao.getTransactions()

    suspend fun insertWallet(wallet: DealerWalletEntity) = walletDao.insertWallet(wallet)

    suspend fun addTransaction(transaction: WalletTransactionEntity) = walletDao.insertTransaction(transaction)
}
