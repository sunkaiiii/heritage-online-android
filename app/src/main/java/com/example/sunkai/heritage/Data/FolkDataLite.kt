package com.example.sunkai.heritage.Data

import java.io.Serializable

/**
 * 民间页全部非遗信息的类
 * Created by sunkai on 2018/1/18.
 */
class FolkDataLite(val id:Int=-1,
                   val divide:String="",
                   val title:String="",
                   val img:String="",
                   val apply_location:String="",
                   val category:String=""):Serializable
