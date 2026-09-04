package com.veeransh.aifashion.enterprise.types

import com.veeransh.aifashion.enterprise.data.local.entity.ProductEntity

data class CartItem(
    val product: ProductEntity,
    val qty: Int
)
