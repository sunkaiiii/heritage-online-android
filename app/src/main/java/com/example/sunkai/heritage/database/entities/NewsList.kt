package com.example.sunkai.heritage.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.sunkai.heritage.entity.response.NewsListResponse

@Entity
data class NewsList(
        @PrimaryKey(autoGenerate = true) val id: Int?,
        @ColumnInfo val link: String,
        @ColumnInfo val title: String,
        @ColumnInfo val date: String,
        @ColumnInfo val content: String,
        @ColumnInfo val img: String?,
        @ColumnInfo val compressImg: String?,
        @ColumnInfo val isRead: Boolean,
        @ColumnInfo val type: String
) {
    constructor(it: NewsListResponse, type: String) : this(
            it.idFromDataBase,
            it.link,
            it.title,
            it.date,
            it.content,
            it.img,
            it.compressImg,
            it.isRead,
            type
    )

    constructor(it: NewsListResponse) : this(
            it, it.typeFromDatabase!!
    )
}