package com.example.sunkai.heritage.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SearchHistory(@PrimaryKey var id: Int?,
                         @ColumnInfo var num: String?,
                         @ColumnInfo var title: String?,
                         @ColumnInfo var type: String?,
                         @ColumnInfo var rx_time: String?,
                         @ColumnInfo var cate: String?,
                         @ColumnInfo var province: String?,
                         @ColumnInfo var unit: String?) {


    constructor() : this(null, null, null, null, null, null, null, null)
}