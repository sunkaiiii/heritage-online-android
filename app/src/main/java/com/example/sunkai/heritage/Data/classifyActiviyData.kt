package com.example.sunkai.heritage.Data

import java.io.Serializable

/**
 * Created by sunkai on 2017/12/15.
 * 首页非遗分类的listview显示数据
 */
class classifyActiviyData(var id:Int=0
                          ,var activityTitle:String?=null
                          ,var activityContent:String?=null
                          ,var activityChannel:String?=null
                          ,var activityImage:ByteArray?=null) : Serializable
