package com.example.sunkai.heritage.database.entities

import androidx.room.Embedded
import androidx.room.Relation

data class NewsWithNewsDetailContent(
        @Embedded val newsDetail:NewsDetail,
        @Relation(
                parentColumn = "link",
                entityColumn = "newsLink"
        )
        val newsContent:List<NewsDetailContent>
)