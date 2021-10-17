package com.example.sunkai.heritage.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.sunkai.heritage.entity.response.NewsDetail

@Entity
data class NewsDetail(
    @PrimaryKey val link: String,
    @ColumnInfo val title: String,
    @ColumnInfo val subtitle: String,
    @ColumnInfo val time: String?,
    @ColumnInfo val author: String,
    @ColumnInfo val img: String?,
    @ColumnInfo val compressImg: String?,
    @ColumnInfo val newsType: NewsList.NewsType
) {
    constructor(newsResponse: NewsDetail) : this(
        newsResponse.link,
        newsResponse.title,
        newsResponse.subtitle?.toString() ?: "",
        newsResponse.time,
        newsResponse.author,
        newsResponse.img,
        newsResponse.compressImg,
        newsResponse.newsType ?: NewsList.NewsType.NewsList
    )
}