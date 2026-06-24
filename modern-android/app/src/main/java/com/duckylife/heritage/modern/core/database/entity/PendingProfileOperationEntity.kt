package com.duckylife.heritage.modern.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.duckylife.heritage.modern.core.profile.PendingOperationKind

/**
 * 离线待发送操作。
 */
@Entity(
    tableName = "pending_profile_operations",
    indices = [
        Index(value = ["deduplicationKey"], unique = true),
        Index(value = ["createdAt"]),
    ],
)
data class PendingProfileOperationEntity(
    @PrimaryKey val operationId: String,
    val kind: PendingOperationKind,
    val deduplicationKey: String,
    val payloadJson: String,
    val createdAt: Long,
    val attemptCount: Int = 0,
    val lastError: String? = null,
)
