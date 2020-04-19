package com.example.sunkai.heritage.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class NewsDetailRelevantContent(
        @PrimaryKey(autoGenerate = true) val id: Int?,
        @ColumnInfo val link: String,
        @ColumnInfo val title: String,
        @ColumnInfo val date: String,
        @ColumnInfo val newsLink: String
)