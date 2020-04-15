package com.example.sunkai.heritage.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class NewsDetailContent(
        @PrimaryKey(autoGenerate = true) val id:Int?,
        @ColumnInfo val type:String,
        @ColumnInfo val content:String,
        @ColumnInfo val compressImg:String?,
        @ColumnInfo val newsLink:String
)