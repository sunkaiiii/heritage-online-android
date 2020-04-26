package com.example.sunkai.heritage.connectWebService

import android.util.Log
import com.example.sunkai.heritage.interfaces.RequestAction
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

/*
 * Created by sunkai on 2018/1/30.
 */

open class BaseSetting {

    companion object {
        const val IMAGE_HOST = "https://sunkai.xyz:5001/img/"
        val gsonInstance = Gson()
        private val client = OkHttpClient.Builder().build()

        fun requestNetwork(request: RequestHelper, action: RequestAction, coroutineScope: CoroutineScope) {
            action.getUIThread().post { action.beforeReuqestStart(request) }
            val httpQueryUrl = HttpUrl.Builder().scheme("https").host("sunkai.xyz").port(5001)
            request.getRequestApi()._url.split("/").forEach {
                httpQueryUrl.addPathSegment(it)
            }
            val requestBean = request.getRequestBean()
            val runnableMap = action.getRunningMap()
            val pathParameter = requestBean.getPathParamerater()
            pathParameter.forEach {
                httpQueryUrl.addPathSegment(it)
            }
            val parameters = requestBean.getNormalParameter()
            parameters.forEach {
                httpQueryUrl.addQueryParameter(it.key, it.value)
            }
            val r = when (request.getRequestApi().getRequestType()) {
                RequestType.GET, RequestType.POST, RequestType.PUT -> Request.Builder().url(httpQueryUrl.build()).addHeader(requestBean.getName(), requestBean.getJsonParameter()).build()
            }
            Log.e("Network Request", request.getRequestApi().getRequestName())
            try {
                val response = client.newCall(r).execute()
                Log.e("Network Requst", request.getRequestApi().getRequestName() + ": " + response)
                val result = response.body?.string()

                if (runnableMap[requestBean] == null || runnableMap[requestBean]?.isCancelled == true)
                    return
                action.getUIThread().post {
                    if (result.isNullOrEmpty()) {
                        action.onRequestError(request, action, IOException("new result returned"))
                        return@post
                    }
                    action.onTaskReturned(request, action, result)
                }
            } catch (e: IOException) {
                action.getUIThread().post {
                    action.onRequestError(request, action, e)
                }
            } finally {
                if (runnableMap[requestBean] == null || runnableMap[requestBean]?.isCancelled == true)
                    return
                action.getUIThread().post { action.onRequestEnd(request) }
            }
        }
    }


}
