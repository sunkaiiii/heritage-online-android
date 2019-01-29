package com.example.sunkai.heritage.entity

import java.io.Serializable

/**
 * Created by sunkai on 2017/12/15.
 * 此类用于存放民间页的活动信息
 * 实现了Serializable可以传入至bundle中
 */
class FolkData(val id:Int,
               val time:String,
               val divide:String,
               val category:String,
               val location:String,
               val apply_location:String,
               val title:String,
               val content:String,
               val number:String,
               val img:String) : Serializable
