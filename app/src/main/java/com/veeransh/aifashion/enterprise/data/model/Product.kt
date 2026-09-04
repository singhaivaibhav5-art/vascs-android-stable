package com.veeransh.aifashion.enterprise.types

data class Product(
    val id: String,
    val sku: String,
    val productId: String,
    val name: String,
    val category: String,
    val fabric: String,
    val work: String,
    val stock: Int,
    val size: String,
    val ratio: String,
    val fit: String,
    val displayLocations: List<String>,
    val couponCodes: List<String>,
    val images: List<String>,
    val qr: String,
    val isHidden: Boolean
)

data class Banner(
    val id: String,
    val title: String,
    val subtitle: String,
    val image: String,
    val action: String
)
