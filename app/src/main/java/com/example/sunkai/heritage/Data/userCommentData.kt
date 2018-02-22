package com.example.sunkai.heritage.Data

import com.example.sunkai.heritage.ConnectWebService.BaseSettingNew.Companion.SUCCESS
import java.io.Serializable

/**
 * Created by sunkai on 2018/2/21.
 */
class UserCommentData(val id:Int,
                      val commentTime:String,
                      val userName:String,
                      var commentTitle:String,
                      var commentContent:String,
                      val userID:Int,
                      var isLike:String,
                      var isFollow:String,
                      var likeNum:Int,
                      var replyNum:Int,
                      var imageUrl:String):Serializable{
    fun isLike():Boolean{
        return SUCCESS==isLike
    }

    fun isFollow():Boolean{
        return SUCCESS==isFollow
    }
}