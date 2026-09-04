package com.veeransh.aifashion.enterprise.data.local.dao

import androidx.room.*
import com.veeransh.aifashion.enterprise.data.local.entity.DealerWalletEntity
import com.veeransh.aifashion.enterprise.data.local.entity.WalletTransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DealerWalletDao {
    @Query("SELECT * FROM dealer_wallets LIMIT 1")
    fun getWallet(): Flow<DealerWalletEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWallet(wallet: DealerWalletEntity)

    @Query("SELECT * FROM wallet_transactions ORDER BY timestamp DESC")
    fun getTransactions(): Flow<List<WalletTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: WalletTransactionEntity)
}
