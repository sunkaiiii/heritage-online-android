package com.duckylife.heritage.modern.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.duckylife.heritage.modern.core.profile.ProfileSyncStatus

/**
 * 服务端历史快照、浏览次数、最后位置。
 */
@Entity(
    tableName = "profile_history",
    indices = [
        Index(
            value = ["profileId", "targetType", "targetId"],
            unique = true,
        ),
        Index(value = ["viewedAt"]),
    ],
)
data class ProfileHistoryEntity(
    @PrimaryKey val id: String,
    val profileId: String,
    val targetType: String,
    val targetId: String,
    val titleSnapshot: String? = null,
    val viewedAt: String? = null,
    val viewCount: Int = 1,
    val lastPosition: String? = null,
    val syncStatus: ProfileSyncStatus = ProfileSyncStatus.Synced,
)
