package com.example.sunkai.heritage.Data

import java.io.Serializable

/**
 * Created by sunkai on 2017/12/15.
 * 此类用于存放民间页的活动信息
 * 实现了Serializable可以传入至bundle中
 */
class folkData(var id: Int? = null
               , var title: String? = null
               , var content: String? = null
               , var location: String? = null
               , var divide: String? = null
               , var teacher: String? = null
               , var techTime: String? = null
               , var image: ByteArray? = null) : Serializable
