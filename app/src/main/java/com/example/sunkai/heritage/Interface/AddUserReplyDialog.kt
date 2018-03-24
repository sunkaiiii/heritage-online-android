package com.example.sunkai.heritage.Interface

import com.example.sunkai.heritage.Data.CommentReplyInformation

/**
 * 添加回复成功后的回调接口
 * Created by sunkai on 2018/2/25.
 */
interface AddUserReplyDialog {
    fun onAddUserReplySuccess(data:CommentReplyInformation)
}