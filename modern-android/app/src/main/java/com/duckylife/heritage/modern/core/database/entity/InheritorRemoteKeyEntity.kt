package com.duckylife.heritage.modern.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "inheritor_remote_keys")
data class InheritorRemoteKeyEntity(
    @PrimaryKey val queryKey: String,
    val nextPage: Int?,
    val hasMore: Boolean,
)
