package com.example.sunkai.heritage.entity

import java.io.Serializable

/**
 * 首页卡片新闻的数据
 * Created by sunkai on 2018/2/8.
 */
class FolkNewsLite(val id: Int
                   , val title: String
                   , val time: String
                   , val content: String
                   , val category: String
                   , val img: String):Serializable