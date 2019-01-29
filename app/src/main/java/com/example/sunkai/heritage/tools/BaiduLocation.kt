package com.example.sunkai.heritage.tools

import com.example.sunkai.heritage.connectWebService.BaseSetting
import com.example.sunkai.heritage.entity.BaiduLoacationResponse
import com.example.sunkai.heritage.value.BaiduIPLocationUrl
import com.example.sunkai.heritage.value.ERROR
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit


object BaiduLocation {
    var location: String = ""

    init {
        ThreadPool.execute {
            location = GetIPAddress()
        }
    }

    fun IPLocation(): String {
        val request = Request.Builder()
                .url(BaiduIPLocationUrl)
                .build()
        //设置1秒超时，防止因为百度api出现问题而造成无法上传帖子等问题
        val client = OkHttpClient.Builder().connectTimeout(1, TimeUnit.SECONDS).build()
        val response = client.newCall(request).execute()
        return if (response.isSuccessful) response.body()?.string()!! else return ERROR
    }

    fun GetIPAddress(): String {
        val result = IPLocation()
        try {
            val locationData = Gson().fromJson<BaiduLoacationResponse>(result, BaiduLoacationResponse::class.java)
            if (locationData.status != 0) {
                return ""
            }
            return locationData.content?.address ?: ""
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    fun getLocateAdressInfo():BaiduLoacationResponse?{
        val result= IPLocation()
        return try{
            BaseSetting.gsonInstance.fromJson<BaiduLoacationResponse>(result,BaiduLoacationResponse::class.java)
        }catch (e:Exception){
            e.printStackTrace()
            null
        }
    }
}