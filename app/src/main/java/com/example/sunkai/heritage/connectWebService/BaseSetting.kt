package com.example.sunkai.heritage.connectWebService

import android.util.Log
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.entity.request.BaseRequest
import com.example.sunkai.heritage.entity.request.BaseRequestBean
import com.example.sunkai.heritage.interfaces.MyEHeritageApi
import com.example.sunkai.heritage.interfaces.NetworkRequest
import com.example.sunkai.heritage.interfaces.RequestAction
import com.example.sunkai.heritage.tools.GlobalContext
import com.example.sunkai.heritage.value.HOST
import com.google.gson.Gson
import okhttp3.FormBody
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.lang.IllegalArgumentException
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSession

/*
 * Created by sunkai on 2018/1/30.
 */

open class BaseSetting {

    companion object {
        val SUCCESS = "SUCCESS"
        val ERROR = "ERROR"
        private val VERSION_UNKNOW = "unknow"
        val URL = HOST
        val IMAGE_HOST = "https://sunkai.xyz:5001/img/"
        val NEW_HOST = "https://sunkai.xyz:5001"
        val gsonInstance = Gson()
        private val client = OkHttpClient.Builder().build()

        fun <T> requestNetwork(api: RequestHelper<T>, requestBean: NetworkRequest, action: RequestAction) {
            val httpQueryUrl = HttpUrl.Builder().scheme("https").host("sunkai.xyz").port(5001)
            api.getRequestApi()._url.split("/").forEach {
                httpQueryUrl.addPathSegment(it)
            }
            httpQueryUrl.addQueryParameter(requestBean.getName(), requestBean.toJson())
            val request = when (api.getRequestApi().getRequestType()) {
                RequestType.GET, RequestType.POST, RequestType.PUT -> Request.Builder().url(httpQueryUrl.build()).build()
            }
            Log.e("Network Request", api.getRequestApi().getRequestName())
            try {
                val response = client.newCall(request).execute()
                Log.e("Network Requst", api.getRequestApi().getRequestName() + ": " + response)
                val result = response.body()?.string()
                action.getUIThread<T>().post {
                    if (result == null) {

                        action.onRequestError(api, action, IOException("new result returned"))
                        return@post
                    }
                    action.onTaskReturned(api, action, result)
                }
            } catch (e: IOException) {
                action.getUIThread<T>().post {
                    action.onRequestError(api, action, e)
                }

            }
        }
    }


    //定义泛型方法，简单化Gson的使用
    fun <T> fromJsonToList(s: String, clazz: Class<Array<T>>): List<T> {
        val arr = gsonInstance.fromJson(s, clazz)
        return arr.toList()
    }

    fun PutGet(url: String): String {
        val request = Request.Builder().url(url).build()
        try {
            Log.e("PutGet", request.toString())
            val response = client.newCall(request).execute()
            val result = response.body()?.string() ?: ERROR
            Log.e("PutGet", url + "\n" + result)
            return result
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return ERROR
    }

    fun PutNewGet(url: String): String {
        return PutGet(NEW_HOST + url)
    }


    fun PutPost(url: String, form: FormBody): String {
        val request = Request.Builder().url(url).post(form).build()
        try {
            val response = client.newCall(request).execute()
            val result = response.body()?.string() ?: ERROR
            Log.e("PutPost", url + "\n" + result)
            return result
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return ERROR
    }
}
