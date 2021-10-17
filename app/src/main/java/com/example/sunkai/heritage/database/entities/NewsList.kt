package com.example.sunkai.heritage.database.entities

import androidx.room.*
import com.example.sunkai.heritage.entity.response.NewsListResponse

@Entity
@TypeConverters(NewsList.NewsListTypeConverters::class)
data class NewsList(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    @ColumnInfo val link: String,
    @ColumnInfo val title: String,
    @ColumnInfo val date: String,
    @ColumnInfo val content: String,
    @ColumnInfo val img: String?,
    @ColumnInfo val compressImg: String?,
    @ColumnInfo val isRead: Boolean,
    @ColumnInfo val type: NewsType
) {
    constructor(it: NewsListResponse, type: NewsType) : this(
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
        it, it.typeFromDatabase ?: NewsType.NewsList
    )

    enum class NewsType(val value: Int) {
        NewsList(0),
        ForumList(1),
        SpecialTopic(2)
    }

    class NewsListTypeConverters {
        @TypeConverter
        fun toNewsType(value: Int) = enumValues<NewsType>()[value]

        @TypeConverter
        fun fromNewsType(value: NewsType) = value.value
    }
}