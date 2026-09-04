package com.veeransh.aifashion.enterprise.types

enum class CouponType { PERCENT, FIXED }
enum class CouponScope { PRODUCT, CART }

data class Coupon(
    val code: String,
    val type: CouponType,
    val value: Double,
    val scope: CouponScope,
    val minCart: Double,
    val maxDisc: Double,
    val priority: Int,
    val active: Boolean
)
