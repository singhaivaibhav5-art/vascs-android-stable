package com.veeransh.aifashion.enterprise.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customer_requirements")
data class CustomerRequirementEntity(
    @PrimaryKey val requirementId: String,
    val userId: String,
    val userName: String,
    val userPhone: String,
    val userEmail: String,
    val productId: String,
    val productName: String,
    val productSku: String,
    val quantity: Int,
    val description: String,
    val imageUrisJson: String = "[]",
    val supportMediaUrisJson: String = "[]",
    val status: String = "PENDING",
    val adminNotes: String = "",
    val createdAt: Long,
    val updatedAt: Long
)
