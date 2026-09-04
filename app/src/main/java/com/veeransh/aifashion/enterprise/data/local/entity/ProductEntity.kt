package com.veeransh.aifashion.enterprise.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val name: String,
    val sku: String,
    val barcode: String,
    val category: String,
    val subCategory: String = "",
    val brand: String = "",
    val fabric: String = "",
    val colour: String = "",
    val size: String = "",
    val hsn: String = "",
    val gst: Double = 0.0,
    val purchasePrice: Double = 0.0,
    val wholesalePrice: Double = 0.0,
    val retailPrice: Double = 0.0,
    val dealerPrice: Double = 0.0,
    val partnerPrice: Double = 0.0,
    val mrp: Double = 0.0,
    val discount: Double = 0.0,
    val stock: Int = 0,
    val moq: Int = 1,
    val lowStockAlert: Int = 5,
    val weight: String = "",
    val location: String = "",
    val image: String = "",
    val imagesJson: String = "[]",
    val supplierName: String = "",
    val description: String = "",
    val tags: String = "",
    val createdAt: String = ""
)
