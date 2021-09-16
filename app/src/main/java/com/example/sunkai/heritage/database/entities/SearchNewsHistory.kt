package com.example.sunkai.heritage.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class SearchNewsHistory(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    @ColumnInfo val searchValue: String,
    @ColumnInfo val searchHappenedTime: Long
) {
    constructor(searchValue: String) : this(null, searchValue, Date().time)
}