package com.example.sunkai.heritage.entity

import com.example.sunkai.heritage.connectWebService.BaseSetting.Companion.SUCCESS
import com.example.sunkai.heritage.value.ERROR
import java.io.Serializable

/**
 * 用户帖子的信息
 * Created by sunkai on 2018/2/21.
 */
class UserCommentData(val id:Int,
                      val commentTime:String,
                      val userName:String,
                      var commentTitle:String,
                      var commentContent:String,
                      val userID:Int,
                      var isLike:String= ERROR,
                      var isFollow:String= ERROR,
                      var likeNum:Int,
                      var replyNum:Int,
                      var isCollect:String= ERROR,
                      val location:String,
                      var miniReplys:List<CommentReplyInformation>,
                      var imageUrl:String):Serializable {
    fun isLike():Boolean{
        return SUCCESS==isLike
    }

    fun isFollow():Boolean{
        return SUCCESS==isFollow
    }

    fun isCollect():Boolean{
        return SUCCESS==isCollect
    }
}