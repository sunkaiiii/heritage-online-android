package com.example.sunkai.heritage.entity

import java.io.Serializable

/**
 * 民间页数据类
 * Created by sunkai on 2018/1/17.
 */
class ClassifyDivideData(var id: Int,
                         var time: String,
                         var category: String,
                         var location: String,
                         var divide:String,
                         var apply_location: String,
                         var title:String,
                         var content: String,
                         var number: String,
                         var img: String):Serializable{
    constructor(folkDataLite: FolkDataLite) : this(
            folkDataLite.id,
            "",
            folkDataLite.category,
            "",
            folkDataLite.divide,
            folkDataLite.apply_location,
            folkDataLite.title,
            "",
            "",
            folkDataLite.img
    )
    constructor(folkData: FolkData):this(
            folkData.id,
            folkData.time,
            folkData.category,
            folkData.location,
            folkData.divide,
            folkData.apply_location,
            folkData.title,
            folkData.content,
            folkData.number,
            folkData.img
    )
}