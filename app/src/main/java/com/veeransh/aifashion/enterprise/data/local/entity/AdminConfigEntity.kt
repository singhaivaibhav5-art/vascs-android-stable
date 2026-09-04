package com.veeransh.aifashion.enterprise.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "admin_config")
data class AdminConfigEntity(
    @PrimaryKey val id: Int = 1,
    val enableCod: Boolean = true,
    val minCod: Int = 0,
    val maxCod: Int = 5000,
    val codCharge: Int = 50,
    val moveToDisplay: Boolean = true,
    val fullDetail: Boolean = true,
    val showOutOfStock: Boolean = true,
    val productCouponEnabled: Boolean = false,
    val productCouponOrig: Int = 1000,
    val productCouponOffer: Int = 950,
    val cartCouponMin: Int = 999,
    val cartCouponDiscount: Int = 10,
    val pincodesJson: String = "[]",
    val premiumDealsJson: String = "[]",
    val otpOnly: Boolean = true,
    val noReturn: Boolean = true,
    val bananaAiEnabled: Boolean = true,
    val razorpayKey: String = "",
    val razorpaySecret: String = "",
    val adminPin: String = "2026",
    val updatedAt: Long = System.currentTimeMillis()
)
