package com.example.sunkai.heritage.entity.response

import com.example.sunkai.heritage.database.entities.NewsWithNewsDetailContent

/**
 * 底部新闻的类
 * Created by sunkai on 2018/2/15.
 */
class NewsDetail(val link: String,
                 val title: String,
                 val subtitle:List<String>?,
                 val time: String?,
                 val author:String,
                 var content:List<NewsDetailContent>,
                 var relativeNews:List<NewsDetailRelativeNews>) {
    constructor(databaseData: NewsWithNewsDetailContent) : this(
            databaseData.newsDetail.link,
            databaseData.newsDetail.title,
            databaseData.newsDetail.subtitle.split(","),
            databaseData.newsDetail.time,
            databaseData.newsDetail.author,
            arrayListOf(),
            arrayListOf()
    ){
        val databaseNewsContent = databaseData.newsContent
        val list = mutableListOf<NewsDetailContent>()
        databaseNewsContent.forEach {
            val newsDetailContent=NewsDetailContent(it.type,it.content,it.compressImg)
            list.add(newsDetailContent)
        }
        this.content=list
    }
}

class NewsDetailContent(val type:String, val content:String, val compressImg:String?)
class NewsDetailRelativeNews(val link:String, val date:String, val title:String)