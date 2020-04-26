package com.example.sunkai.heritage.connectWebService

import android.text.TextUtils
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException


class BaseParamsInterceptor : Interceptor {
    val queryParamsMap = HashMap<String, String>()
    val paramsMap = HashMap<String, String>()
    val headerParamsMap = HashMap<String, String>()
    val headerLinesList = ArrayList<String>()


    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val requestBuilder = request.newBuilder()

        val headerBuilder = request.headers.newBuilder()

        //添加消息头
        if (headerParamsMap.size > 0) {
            headerParamsMap.forEach {
                headerBuilder.add(it.key, it.value)
            }
            requestBuilder.headers((headerBuilder.build()))
        }
        if (headerLinesList.size > 0) {
            headerLinesList.forEach { headerBuilder.add(it) }
        }

        //处理query的部分
        if (queryParamsMap.size > 0) {
            injectParamsIntoUrl(request.url.newBuilder(), requestBuilder, queryParamsMap)
        }

        if (paramsMap.size > 0) {
            if (canInjectIntoBody(request)) {
                val formBodyBuilder = FormBody.Builder()
                paramsMap.forEach { formBodyBuilder.add(it.key, it.value) }
                val formBody = formBodyBuilder.build()
                val bodyString = bodyToString(request.body)
                val postBodyString = bodyString + (if (bodyString.isNotEmpty()) "&" else "") + bodyToString(formBody)
                requestBuilder.post(postBodyString.toRequestBody("application/x-www-form-urlencoded;charset=UTF-8".toMediaTypeOrNull()))
            }
        }
        request = requestBuilder.build()
        return chain.proceed(request)
    }

    //确认是否发送Post请求
    private fun canInjectIntoBody(request: Request): Boolean {
        if (!TextUtils.equals(request.method, "POST")) {
            return false
        }
        val body = request.body ?: return false
        val mediaType = body.contentType() ?: return false
        if (!TextUtils.equals(mediaType.subtype, "x-www-form-urlencoded")) {
            return false
        }
        return true
    }

    private fun injectParamsIntoUrl(httpUrlBuilder: HttpUrl.Builder, requestBuilder: Request.Builder, paramsMap: Map<String, String>) {
        if (paramsMap.isNotEmpty()) {
            paramsMap.forEach {
                httpUrlBuilder.addQueryParameter(it.key, it.value)
            }
            requestBuilder.url(httpUrlBuilder.build())
        }
    }

    private fun bodyToString(request: RequestBody?): String {
        try {
            val copy = request ?: return ""
            val buffer = okio.Buffer()
            copy.writeTo(buffer)
            return buffer.readUtf8()
        } catch (e: IOException) {
            return "did not work"
        }
    }

    class Builder {

        private val interceptor: BaseParamsInterceptor

        init {
            interceptor = BaseParamsInterceptor()
        }

        // 添加公共参数到 post 消息体
        fun addParam(key: String, value: String): Builder {
            interceptor.paramsMap[key] = value
            return this
        }


        // 添加公共参数到 post 消息体
        fun addParamsMap(paramsMap: Map<String, String>): Builder {
            interceptor.paramsMap.putAll(paramsMap)
            return this
        }

        // 添加公共参数到消息头
        fun addHeaderParam(key: String, value: String): Builder {
            interceptor.headerParamsMap[key] = value
            return this
        }

        // 添加公共参数到消息头
        fun addHeaderParamsMap(headerParamsMap: Map<String, String>): Builder {
            interceptor.headerParamsMap.putAll(headerParamsMap)
            return this
        }

        // 添加公共参数到消息头
        fun addHeaderLine(headerLine: String): Builder {
            val index = headerLine.indexOf(":")
            require(index != -1) { "Unexpected header: $headerLine" }
            interceptor.headerLinesList.add(headerLine)
            return this
        }

        // 添加公共参数到消息头
        fun addHeaderLinesList(headerLinesList: List<String>): Builder {
            for (headerLine in headerLinesList) {
                val index = headerLine.indexOf(":")
                require(index != -1) { "Unexpected header: $headerLine" }
                interceptor.headerLinesList.add(headerLine)
            }
            return this
        }

        // 添加公共参数到 URL
        fun addQueryParam(key: String, value: String): Builder {
            interceptor.queryParamsMap[key] = value
            return this
        }

        // 添加公共参数到 URL
        fun addQueryParamsMap(queryParamsMap: Map<String, String>): Builder {
            interceptor.queryParamsMap.putAll(queryParamsMap)
            return this
        }



        fun build(): BaseParamsInterceptor {
            return interceptor
        }
    }
}