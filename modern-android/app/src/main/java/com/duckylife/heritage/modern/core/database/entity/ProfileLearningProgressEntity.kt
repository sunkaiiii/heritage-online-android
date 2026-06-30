package com.duckylife.heritage.modern.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.duckylife.heritage.modern.core.profile.ProfileSyncStatus

/**
 * 每条 route 的完成步骤、当前步骤、百分比、更新时间。
 */
@Entity(
    tableName = "profile_learning_progress",
    indices = [
        Index(
            value = ["profileId", "routeId"],
            unique = true,
        ),
        Index(value = ["updatedAt"]),
    ],
)
data class ProfileLearningProgressEntity(
    @PrimaryKey val id: String,
    val profileId: String,
    val routeId: String,
    val routeTitle: String? = null,
    val completedStepIds: List<String> = emptyList(),
    val currentStepId: String? = null,
    val percent: Int = 0,
    val startedAt: String? = null,
    val updatedAt: String? = null,
    val completedAt: String? = null,
    val syncStatus: ProfileSyncStatus = ProfileSyncStatus.Synced,
)
