package com.example.sunkai.heritage.entity

import java.io.Serializable

/**
 * 帖子回复的类
 * Created by sunkai on 2018/2/21.
 */
class CommentReplyInformation(val id:Int,
                              val replyTime:String,
                              val commentID:Int,
                              val userID:Int,
                              val userName:String,
                              val replyContent:String):Serializable