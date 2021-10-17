package com.example.sunkai.heritage.database.entities

import androidx.room.*
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.getString
import java.util.*

@Entity
@TypeConverters(Collection.CollectionTypeConverters::class)
data class Collection(

    @ColumnInfo val collectionType: CollectionType,
    @ColumnInfo val key: String,
    @ColumnInfo val content:String,
    @ColumnInfo val imageLink:String?,
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    @ColumnInfo val collectDate: String = Calendar.getInstance().toString()
) {
    enum class CollectionType(val value: Int,private val text:String) {
        NewsDetail(0, getString(R.string.news)),
        ForumsDetail(1, getString(R.string.forums)),
        SpeicialListDetail(2, getString(R.string.special_topic)),
        PeopleDetail(3, getString(R.string.people)),
        ProjectDetail(4, getString(R.string.project_page)),
        InheritatePeopleDetail(5, getString(R.string.inheritors));

        fun getName() = text
    }

    class CollectionTypeConverters {
        @TypeConverter
        fun toCollectionType(value: Int) = enumValues<CollectionType>()[value]

        @TypeConverter
        fun fromCollectionType(value: CollectionType) = value.value
    }
}
