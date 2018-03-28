package com.example.sunkai.heritage.ConnectWebService

import com.example.sunkai.heritage.Data.PushMessageData
import okhttp3.FormBody

/**
 * 推送相关的网络请求
 * Created by sunkai on 2018/3/26.
 */
object HandlePush:BaseSetting() {


    fun GetPush(userID:Int):List<PushMessageData>{
        val url="$URL/GetPushMessage?userID=$userID"
        val result=PutGet(url)
        if(ERROR==result)return arrayListOf()
        try{
            return fromJsonToList(result,Array<PushMessageData>::class.java)
        }catch (e:Exception){
            e.printStackTrace()
        }
        return arrayListOf()
    }

    fun AddPushMessage(userID: Int,replyCommentID:Int,replyToUserID:Int,userName:String,replyContent:String,replyToUserName:String,replyTime:String,originalReplyContent:String){
        val url="$URL/AddPushMessage"
        val formBody=FormBody.Builder()
                .add("userID",userID.toString())
                .add("replyCommentID",replyCommentID.toString())
                .add("replyToUserID",replyToUserID.toString())
                .add("userName",userName)
                .add("replyContent",replyContent)
                .add("replyToUserName",replyToUserName)
                .add("replyTime",replyTime)
                .add("originalReplyContent",originalReplyContent)
                .build()
        PutPost(url,formBody)
    }
}