package com.example.sunkai.heritage.ConnectWebService

import com.google.gson.Gson
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

/*
 * Created by sunkai on 2018/1/30.
 */

abstract class BaseSetting {
    companion object {
        const val SUCCESS = "SUCCESS"
        const val ERROR = "ERROR"
        const val URL = "http://btbudinner.win:8080"
//        const val URL="http://10.20.254.64:8080"
    }
    //定义扩展方法，简单化Gson的使用
    inline fun <reified T:Any> Gson.fromJsonToList(s: String, clazz: Class<Array<T>>): List<T> {
        val arr = Gson().fromJson(s, clazz)
        return arr.toList()
    }

    fun PutGet(url:String):String{
        val request=Request.Builder().url(url).build()
        val client=OkHttpClient()
        try {
            val response = client.newCall(request).execute()
            return response.body()?.string() ?: ERROR
        }catch (e:IOException){
            e.printStackTrace()
        }
        return ERROR
    }

    fun PutPost(url:String,form:FormBody):String{
        val request=Request.Builder().url(url).post(form).build()
        val client=OkHttpClient()
        try {
            val response = client.newCall(request).execute()
            return response?.body()?.string() ?: ERROR
        }catch (e:IOException){
            e.printStackTrace()
        }
        return ERROR
    }
}
