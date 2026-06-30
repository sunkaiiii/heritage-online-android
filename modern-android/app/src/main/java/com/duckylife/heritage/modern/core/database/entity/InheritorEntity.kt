package com.duckylife.heritage.modern.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "inheritors")
data class InheritorEntity(
    @PrimaryKey val id: String,
    val queryKey: String,
    val name: String?,
    val gender: String?,
    val birthDateText: String?,
    val ethnicity: String?,
    val category: String?,
    val projectCode: String?,
    val projectName: String?,
    val region: String?,
    val batch: String?,
    val description: String?,
    val coverImageJson: String?,
    val sourceUrl: String?,
    val page: Int,
    val positionInPage: Int,
)
