package com.duckylife.heritage.modern.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "home_banners")
data class HomeBannerEntity(
    @PrimaryKey val id: String,
    val sortOrder: Int,
    val targetUrl: String?,
    val displayImageJson: String?,
    val mobileImageJson: String?,
    val desktopImageJson: String?,
    val updatedAtEpochMillis: Long,
)
