package com.example.sunkai.heritage.tools

import com.example.sunkai.heritage.value.BaiduIPLocationUrl
import com.example.sunkai.heritage.value.ERROR
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * Created by sunkai on 2018/1/9.
 */
object BaiduLocation {
    fun IPLocation():String{
        val request=Request.Builder()
                .url(BaiduIPLocationUrl)
                .build()
        val response=OkHttpClient().newCall(request).execute()
        return if (response.isSuccessful) response.body()?.string()!! else return ERROR
    }
}