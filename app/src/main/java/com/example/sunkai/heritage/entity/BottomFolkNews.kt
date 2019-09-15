package com.example.sunkai.heritage.entity

/**
 * 底部新闻的类
 * Created by sunkai on 2018/2/15.
 */
class BottomFolkNews(val link: String,
                     val title: String,
                     val subTitle:List<String>,
                     val time: String,
                     var content:List<BottomFolkNewsContent>,
                     var relativeNews:List<BottomFolkNewsRelativeNews>)
class BottomFolkNewsContent(val type:String,val content:String)
class BottomFolkNewsRelativeNews(val link:String,val date:String,val title:String)