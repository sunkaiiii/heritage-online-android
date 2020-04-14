package com.example.sunkai.heritage.entity.response

/**
 * 底部新闻的类
 * Created by sunkai on 2018/2/15.
 */
class NewsDetail(val link: String,
                 val title: String,
                 val subtitle:List<String>?,
                 val time: String,
                 val author:String,
                 var content:List<NewsDetailContent>,
                 var relativeNews:List<NewsDetailRelativeNews>)
class NewsDetailContent(val type:String, val content:String, val compressImg:String?)
class NewsDetailRelativeNews(val link:String, val date:String, val title:String)