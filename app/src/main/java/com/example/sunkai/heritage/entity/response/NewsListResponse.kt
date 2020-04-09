package com.example.sunkai.heritage.entity.response

import java.io.Serializable

/**
 * 聚焦非遗的数据类
 * Created by sunkai on 2018/2/12.
 */
class NewsListResponse(val link:String,
                       val title:String,
                       val date:String,
                       val content:String,
                       var img:String?,
                       var compressImg:String?):Serializable