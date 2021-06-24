package com.example.sunkai.heritage.connectWebService

import com.example.sunkai.heritage.entity.response.NewsDetail
import com.example.sunkai.heritage.entity.response.NewsListResponse
import okhttp3.HttpUrl
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

const val NewsListUrl = "/api/NewsList/{page}"
const val NewsDetail = "/api/NewsDetail"

object EHeritageApiRetrofitServiceCreator {
    private const val HOST = "sunkai.xyz"
    private const val SCHEME = "https"
    private const val PORT = 5001
    private val httpUrl = HttpUrl.Builder().scheme(SCHEME).host(HOST).port(PORT).build()
    private val retrofit = Retrofit.Builder()
        .baseUrl(httpUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private fun <T> create(serviceClass:Class<T>):T = retrofit.create(serviceClass)

    private inline fun <reified T> create():T= create(T::class.java)

    val EhritageService = create<EHeritageApiRetrofit>()
}

interface EHeritageApiRetrofit{
    @GET(NewsListUrl)
    fun getNewsList(@Path("page") page:Int):Call<List<NewsListResponse>>

    @GET(NewsDetail)
    fun getNewsDetail(@Query("link")link:String):Call<NewsDetail>
}

suspend fun <T> Call<T>.await():T{
    return suspendCoroutine { continuation ->
        enqueue(object:Callback<T>{
            override fun onResponse(call: Call<T>, response: Response<T>) {
                val body = response.body()
                if(body!=null){
                    continuation.resume(body)
                }else{
                    continuation.resumeWithException(RuntimeException("response body is null"))
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                continuation.resumeWithException(t)
            }

        })
    }
}