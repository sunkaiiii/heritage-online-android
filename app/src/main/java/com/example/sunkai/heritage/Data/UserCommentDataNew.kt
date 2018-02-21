package com.example.sunkai.heritage.Data

import java.io.Serializable

/**
 * Created by sunkai on 2018/2/21.
 */
class UserCommentDataNew(val id:Int,
                         val commentTime:String,
                         val userName:String,
                         val commentTitle:String,
                         val commentContent:String,
                         val userID:Int,
                         val like:Boolean,
                         val follow:Boolean,
                         val likeNum:Int,
                         val replyNum:Int,
                         val image:String):Serializable