package com.example.sunkai.heritage.entity.response

import com.example.sunkai.heritage.database.entities.NewsList
import java.io.Serializable

/**
 * 首页的数据类
 * Created by sunkai on 2018/2/12.
 */
data class NewsListResponse(val link: String,
                       val title: String,
                       val date: String,
                       val content: String,
                       var img: String?,
                       var compressImg: String?,
                       var isRead: Boolean = false,
                       var idFromDataBase: Int?,
                       var typeFromDatabase: String?) : Serializable {
    constructor(it: NewsList) : this(
            it.link,
            it.title,
            it.date,
            it.content,
            it.img,
            it.compressImg,
            it.isRead,
            it.id,
            it.type
    )

    constructor(relativeNews: NewsDetailRelativeNews) : this(relativeNews.link,relativeNews.title,relativeNews.date,"","",null,false,null,null)


}