package com.example.sunkai.heritage.connectWebService

import android.util.Log
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.entity.request.BaseRequest
import com.example.sunkai.heritage.tools.GlobalContext
import com.example.sunkai.heritage.value.HOST
import com.google.gson.Gson
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSession

/*
 * Created by sunkai on 2018/1/30.
 */

abstract class BaseSetting {
    companion object {
        const val SUCCESS = "SUCCESS"
        const val ERROR = "ERROR"
        private const val VERSION_UNKNOW = "unknow"
        const val URL = HOST
        const val NEW_HOST = "http://118.138.80.153:5000"
        val gsonInstance = Gson()
        private val baseParaMeter=BaseParamsInterceptor.Builder().addParam("from","android")
                .addQueryParam("version",GlobalContext.instance.getString(R.string.verson_code))
                .addParamsObj(BaseRequest()).build()
        private val client = OkHttpClient.Builder().build()
    }

    //定义泛型方法，简单化Gson的使用
    fun <T> fromJsonToList(s: String, clazz: Class<Array<T>>): List<T> {
        val arr = gsonInstance.fromJson(s, clazz)
        return arr.toList()
    }

    private fun formatUrlWithVersionCode(url: String): String {
        //TODO 网络请求这块需要重新做
        return url
    }

    fun PutGet(url: String): String {
        val formatUrl = formatUrlWithVersionCode(url)
        val baseParaMeter=BaseParamsInterceptor.Builder().addParam("from","android")
                .addQueryParam("version",GlobalContext.instance.getString(R.string.verson_code))
                .addQueryParamsMap(BaseRequest().toMap())
                .addParamsObj(BaseRequest()).build()
        val client = OkHttpClient.Builder().addInterceptor(baseParaMeter).hostnameVerifier { _, _ -> true }.build()
        val request = Request.Builder().url(formatUrlWithVersionCode(url)).build()
        try {
            Log.e("PutGet",request.toString())
            val response = client.newCall(request).execute()
            val result = response.body()?.string() ?: ERROR
            Log.e("PutGet", formatUrl + "\n" + result)
            return result
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return ERROR
    }

    fun PutNewGet(url:String):String{
        return PutGet(NEW_HOST+url)
    }

    fun PutPost(url: String, form: FormBody): String {
        val formatUrl = formatUrlWithVersionCode(url)
        val request = Request.Builder().url(formatUrl).post(form).build()
        try {
            val response = client.newCall(request).execute()
            val result = response.body()?.string() ?: ERROR
            Log.e("PutPost", formatUrl + "\n" + result)
            return result
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return ERROR
    }
}
