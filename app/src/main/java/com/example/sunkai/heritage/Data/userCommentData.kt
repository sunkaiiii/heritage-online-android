package com.example.sunkai.heritage.Data

import java.io.Serializable

/**
 * Created by sunkai on 2017/12/15.
 * 此类用于存储用户他发帖的信息
 * 实现了Serializable可以传入至bundle中
 */
class UserCommentData(var id: Int = 0
                      , var inListPosition: Int = 0
                      , var user_id: Int = 0
                      , var commentTime: String? = null
                      , var commentTitle: String? = null
                      , var commentContent: String? = null
                      , var commentLikeNum: String? = null
                      , var commentReplyNum: String? = null
                      , var userName: String? = null
                      , var userImage: ByteArray? = null
                      , var isUserLike:Boolean = false
                      , var isUserFocusUser:Boolean = false) : Serializable{
    fun getUserLike():Boolean=isUserLike
    fun getUserFocusUser():Boolean=isUserFocusUser
}