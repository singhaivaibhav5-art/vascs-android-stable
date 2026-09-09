package com.veeransh.aifashion.enterprise.di

import android.content.Context
import androidx.room.Room
import com.veeransh.aifashion.enterprise.data.local.AppDatabase
import com.veeransh.aifashion.enterprise.data.local.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "veeransh_enterprise.db"
        ).addMigrations(
            AppDatabase.MIGRATION_6_7,
            AppDatabase.MIGRATION_7_8,
            AppDatabase.MIGRATION_8_9,
            AppDatabase.MIGRATION_9_10,
            AppDatabase.MIGRATION_10_11,
            AppDatabase.MIGRATION_11_12,
            AppDatabase.MIGRATION_12_13,
            AppDatabase.MIGRATION_13_14,
            AppDatabase.MIGRATION_14_15,
            AppDatabase.MIGRATION_15_16,
            AppDatabase.MIGRATION_16_17,
            AppDatabase.MIGRATION_17_18
        ).build()
    }

    @Provides
    fun provideProductDao(db: AppDatabase): ProductDao = db.productDao()

    @Provides
    fun provideOrderMasterDao(db: AppDatabase): OrderMasterDao = db.orderMasterDao()

    @Provides
    fun provideOrderItemDao(db: AppDatabase): OrderItemDao = db.orderItemDao()

    @Provides
    fun provideDealerWalletDao(db: AppDatabase): DealerWalletDao = db.dealerWalletDao()

    @Provides
    fun provideUserDao(db: AppDatabase): UserDao = db.userDao()

    @Provides
    fun provideFinishedGoodsDao(db: AppDatabase): FinishedGoodsDao = db.finishedGoodsDao()

    @Provides
    fun provideAdminConfigDao(db: AppDatabase): AdminConfigDao = db.adminConfigDao()

    @Provides
    fun provideAiDrapeResultDao(db: AppDatabase): AiDrapeResultDao = db.aiDrapeResultDao()

    @Provides
    fun providePlacementDao(db: AppDatabase): PlacementDao = db.placementDao()

    @Provides
    fun provideStockTransactionDao(db: AppDatabase): StockTransactionDao = db.stockTransactionDao()

    @Provides
    fun provideStockBalanceDao(db: AppDatabase): StockBalanceDao = db.stockBalanceDao()

    @Provides
    fun provideRequirementDao(db: AppDatabase): RequirementDao = db.requirementDao()
}
