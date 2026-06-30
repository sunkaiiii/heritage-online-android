package com.duckylife.heritage.modern.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 单行 profile 元数据、最近服务端同步时间、同步错误摘要。
 */
@Entity(tableName = "local_profile_state")
data class LocalProfileStateEntity(
    @PrimaryKey val profileId: String,
    val displayName: String = "",
    val favoriteCount: Long = 0L,
    val historyCount: Long = 0L,
    val learningRouteCount: Long = 0L,
    val generatedAt: String? = null,
    val lastSyncAt: Long? = null,
    val lastSyncError: String? = null,
)
