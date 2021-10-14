package com.example.sunkai.heritage.database.entities

import androidx.room.*
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
    enum class CollectionType(val value: Int) {
        NewsDetail(0),
        ForumsDetail(1),
        SpeicialListDetail(2),
        PeopleDetail(3),
        ProjectDetail(4),
        InheritatePeopleDetail(5)
    }

    class CollectionTypeConverters {
        @TypeConverter
        fun toCollectionType(value: Int) = enumValues<CollectionType>()[value]

        @TypeConverter
        fun fromCollectionType(value: CollectionType) = value.value
    }
}
