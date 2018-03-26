package com.example.sunkai.heritage.ConnectWebService

import com.example.sunkai.heritage.Data.PushMessageData

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
}