package com.example.sunkai.heritage.Data

import java.io.Serializable

/**
 * 搜索用户的info
 * Created by sunkai on 2018/2/22.
 */
class SearchUserInfo(val id:Int,
                     val userName:String,
                     var checked:Boolean,
                     var imageUrl:String?=null):Serializable