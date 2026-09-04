package com.veeransh.aifashion.enterprise.types

import com.veeransh.aifashion.enterprise.data.local.entity.ProductEntity

data class UserPDPCouponItem(
    val code: String,
    val discount: String,
    val type: String,
    val value: Double
)

data class CartItem(
    val product: ProductEntity,
    val qty: Int,
    val appliedCoupon: UserPDPCouponItem? = null
)
