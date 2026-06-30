package com.duckylife.heritage.modern.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.duckylife.heritage.modern.core.profile.ProfileSyncStatus

/**
 * 服务端收藏的轻量快照与同步状态。
 */
@Entity(
    tableName = "profile_favorites",
    indices = [
        Index(
            value = ["profileId", "targetType", "targetId"],
            unique = true,
        ),
        Index(value = ["updatedAt"]),
    ],
)
data class ProfileFavoriteEntity(
    @PrimaryKey val id: String,
    val profileId: String,
    val targetType: String,
    val targetId: String,
    val titleSnapshot: String? = null,
    val coverImageUrlSnapshot: String? = null,
    val tags: List<String> = emptyList(),
    val note: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val syncStatus: ProfileSyncStatus = ProfileSyncStatus.Synced,
)
