package com.veeransh.aifashion.enterprise.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.veeransh.aifashion.enterprise.data.local.dao.*
import com.veeransh.aifashion.enterprise.data.local.entity.*

@Database(
    entities = [
        ProductEntity::class,
        OrderMasterEntity::class,
        OrderItemEntity::class,
        DealerWalletEntity::class,
        WalletTransactionEntity::class,
        UserEntity::class,
        FinishedGoodsEntity::class,
        AdminConfigEntity::class,
        AiDrapeResultEntity::class
    ],
    version = 7,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun orderMasterDao(): OrderMasterDao
    abstract fun orderItemDao(): OrderItemDao
    abstract fun dealerWalletDao(): DealerWalletDao
    abstract fun userDao(): UserDao
    abstract fun finishedGoodsDao(): FinishedGoodsDao
    abstract fun adminConfigDao(): AdminConfigDao
    abstract fun aiDrapeResultDao(): AiDrapeResultDao

    companion object {
        val MIGRATION_6_7 = object : androidx.room.migration.Migration(6, 7) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                // Add new column to users table
                db.execSQL("ALTER TABLE users ADD COLUMN lastLoginTimestamp INTEGER NOT NULL DEFAULT 0")
                
                // Ensure tables added in 'dirty' version 6 are created if missing
                db.execSQL("CREATE TABLE IF NOT EXISTS `admin_config` (`id` INTEGER NOT NULL, `enableCod` INTEGER NOT NULL, `minCod` INTEGER NOT NULL, `maxCod` INTEGER NOT NULL, `codCharge` INTEGER NOT NULL, `moveToDisplay` INTEGER NOT NULL, `fullDetail` INTEGER NOT NULL, `showOutOfStock` INTEGER NOT NULL, `productCouponEnabled` INTEGER NOT NULL, `productCouponOrig` INTEGER NOT NULL, `productCouponOffer` INTEGER NOT NULL, `cartCouponMin` INTEGER NOT NULL, `cartCouponDiscount` INTEGER NOT NULL, `pincodesJson` TEXT NOT NULL, `premiumDealsJson` TEXT NOT NULL, `otpOnly` INTEGER NOT NULL, `noReturn` INTEGER NOT NULL, `bananaAiEnabled` INTEGER NOT NULL, `razorpayKey` TEXT NOT NULL, `razorpaySecret` TEXT NOT NULL, `adminPin` TEXT NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`id`))")
                db.execSQL("CREATE TABLE IF NOT EXISTS `ai_drape_results` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `originalUri` TEXT NOT NULL, `drapedUri` TEXT NOT NULL, `status` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `isSelected` INTEGER NOT NULL)")
            }
        }
    }
}
