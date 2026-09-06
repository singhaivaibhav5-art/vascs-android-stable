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
        AiDrapeResultEntity::class,
        PlacementEntity::class
    ],
    version = 11,
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
    abstract fun placementDao(): PlacementDao

    companion object {
        val MIGRATION_6_7 = object : androidx.room.migration.Migration(6, 7) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                // 1. users table: Add lastLoginTimestamp if missing
                val usersCursor = db.query("PRAGMA table_info(users)")
                var hasLastLogin = false
                while (usersCursor.moveToNext()) {
                    val nameIndex = usersCursor.getColumnIndex("name")
                    if (nameIndex != -1 && usersCursor.getString(nameIndex) == "lastLoginTimestamp") {
                        hasLastLogin = true
                    }
                }
                usersCursor.close()
                if (!hasLastLogin) {
                    db.execSQL("ALTER TABLE users ADD COLUMN lastLoginTimestamp INTEGER NOT NULL DEFAULT 0")
                }

                // 2. finished_goods table: Robust Schema Synchronization with Null Safety
                db.execSQL("DROP TABLE IF EXISTS `finished_goods_new`")
                db.execSQL("CREATE TABLE `finished_goods_new` (`finishedId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `batchId` INTEGER NOT NULL, `productId` TEXT NOT NULL, `sku` TEXT NOT NULL, `qrNumber` TEXT NOT NULL, `finishedQty` INTEGER NOT NULL, `availableQty` INTEGER NOT NULL, `category` TEXT NOT NULL, `status` TEXT NOT NULL)")
                
                val tableCheckCursor = db.query("SELECT name FROM sqlite_master WHERE type='table' AND name='finished_goods'")
                val tableExists = tableCheckCursor.count > 0
                tableCheckCursor.close()

                if (tableExists) {
                    val columnsCursor = db.query("PRAGMA table_info(finished_goods)")
                    val existingColsMap = mutableMapOf<String, Boolean>()
                    while (columnsCursor.moveToNext()) {
                        val nameIndex = columnsCursor.getColumnIndex("name")
                        val notNullIndex = columnsCursor.getColumnIndex("notnull")
                        if (nameIndex != -1 && notNullIndex != -1) {
                            existingColsMap[columnsCursor.getString(nameIndex)] = columnsCursor.getInt(notNullIndex) == 1
                        }
                    }
                    columnsCursor.close()

                    val targetCols = listOf("finishedId", "batchId", "productId", "sku", "qrNumber", "finishedQty", "availableQty", "category", "status")
                    val selectList = targetCols.map { col ->
                        if (existingColsMap.containsKey(col)) {
                            val isNotNullInOld = existingColsMap[col] == true
                            if (isNotNullInOld) col else {
                                val dflt = when (col) {
                                    "batchId", "finishedQty", "availableQty" -> "0"
                                    "status" -> "'Available'"
                                    else -> "''"
                                }
                                "COALESCE($col, $dflt)"
                            }
                        } else {
                            when (col) {
                                "category" -> "''"
                                "status" -> "'Available'"
                                else -> throw Exception("Unexpected missing mandatory column: $col")
                            }
                        }
                    }

                    db.execSQL("INSERT INTO finished_goods_new (${targetCols.joinToString(", ")}) SELECT ${selectList.joinToString(", ")} FROM finished_goods")
                    db.execSQL("DROP TABLE finished_goods")
                }
                db.execSQL("ALTER TABLE finished_goods_new RENAME TO finished_goods")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_finished_goods_batchId` ON `finished_goods` (`batchId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_finished_goods_sku` ON `finished_goods` (`sku`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_finished_goods_qrNumber` ON `finished_goods` (`qrNumber`)")

                db.execSQL("CREATE TABLE IF NOT EXISTS `admin_config` (`id` INTEGER NOT NULL, `enableCod` INTEGER NOT NULL, `minCod` INTEGER NOT NULL, `maxCod` INTEGER NOT NULL, `codCharge` INTEGER NOT NULL, `moveToDisplay` INTEGER NOT NULL, `fullDetail` INTEGER NOT NULL, `showOutOfStock` INTEGER NOT NULL, `productCouponEnabled` INTEGER NOT NULL, `productCouponOrig` INTEGER NOT NULL, `productCouponOffer` INTEGER NOT NULL, `cartCouponMin` INTEGER NOT NULL, `cartCouponDiscount` INTEGER NOT NULL, `pincodesJson` TEXT NOT NULL, `premiumDealsJson` TEXT NOT NULL, `otpOnly` INTEGER NOT NULL, `noReturn` INTEGER NOT NULL, `bananaAiEnabled` INTEGER NOT NULL, `razorpayKey` TEXT NOT NULL, `razorpaySecret` TEXT NOT NULL, `adminPin` TEXT NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`id`))")
                db.execSQL("CREATE TABLE IF NOT EXISTS `ai_drape_results` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `originalUri` TEXT NOT NULL, `drapedUri` TEXT NOT NULL, `status` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `isSelected` INTEGER NOT NULL)")
            }
        }

        val MIGRATION_7_8 = object : androidx.room.migration.Migration(7, 8) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `product_placements` (`placementId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `productId` TEXT NOT NULL, `placementType` TEXT NOT NULL, `title` TEXT NOT NULL, `imageUri` TEXT NOT NULL, `cropMode` TEXT NOT NULL, `aspectRatio` TEXT NOT NULL, `targetWidthPx` INTEGER NOT NULL, `targetHeightPx` INTEGER NOT NULL, `priority` INTEGER NOT NULL, `sortOrder` INTEGER NOT NULL, `status` TEXT NOT NULL, `isActive` INTEGER NOT NULL, `startAt` INTEGER, `endAt` INTEGER, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_product_placements_productId` ON `product_placements` (`productId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_product_placements_placementType` ON `product_placements` (`placementType`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_product_placements_isActive` ON `product_placements` (`isActive`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_product_placements_priority` ON `product_placements` (`priority`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_product_placements_sortOrder` ON `product_placements` (`sortOrder`)")
            }
        }

        val MIGRATION_8_9 = object : androidx.room.migration.Migration(8, 9) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                val defaults = com.veeransh.aifashion.enterprise.types.DefaultTemplates.getSerializedDefaults()
                db.execSQL("ALTER TABLE admin_config ADD COLUMN templatesJson TEXT NOT NULL DEFAULT '$defaults'")
            }
        }

        val MIGRATION_9_10 = object : androidx.room.migration.Migration(9, 10) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE product_placements ADD COLUMN cropScale REAL NOT NULL DEFAULT 1.0")
                db.execSQL("ALTER TABLE product_placements ADD COLUMN cropOffsetX REAL NOT NULL DEFAULT 0.0")
                db.execSQL("ALTER TABLE product_placements ADD COLUMN cropOffsetY REAL NOT NULL DEFAULT 0.0")
            }
        }

        val MIGRATION_10_11 = object : androidx.room.migration.Migration(10, 11) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                // Add sourceUri column for Phase 2.7.3
                db.execSQL("ALTER TABLE product_placements ADD COLUMN sourceUri TEXT NOT NULL DEFAULT ''")
                // Initial data copy: previous imageUri becomes sourceUri if it wasn't a processed asset
                db.execSQL("UPDATE product_placements SET sourceUri = imageUri")
            }
        }
    }
}
