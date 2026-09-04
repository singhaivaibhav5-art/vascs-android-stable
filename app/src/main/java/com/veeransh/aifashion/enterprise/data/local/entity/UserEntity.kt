package com.veeransh.aifashion.enterprise.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val uid: String,
    val phone: String,
    val email: String,
    val name: String,
    val passwordHash: String = "",
    val role: String = "customer", // customer, stylePartner, salesManager, admin
    val is2FAEnabled: Boolean = false,
    val twoFAMethod: String = "NONE", // NONE, EMAIL, AUTHENTICATOR
    val backupCodes: String = "", // JSON string of encrypted codes
    val alternatePhone: String = "",
    val status: String = "active", // active, suspended, blocked
    val suspendedTill: Long = 0,
    val kycStatus: String = "NONE", // NONE, PENDING, VERIFIED, REJECTED
    val createdDate: Long = System.currentTimeMillis(),
    val lastLoginTimestamp: Long = 0
)
