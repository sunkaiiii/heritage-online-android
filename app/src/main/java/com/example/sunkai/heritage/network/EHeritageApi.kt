package com.example.sunkai.heritage.network

import com.example.sunkai.heritage.entity.MainPageBanner
import com.example.sunkai.heritage.entity.response.*
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

const val Banner = "/api/banner"
const val NewsListUrl = "/api/NewsList/{page}"
const val NewsDetail = "/api/NewsDetail"
const val ForumsList = "/api/Forums/ForumsList/{page}"
const val ForumsDetail = "/api/Forums/GetForumsDetail"
const val SpecialTopic = "/api/SpecialTopic/GetSpecialTopicList/{page}"
const val SpecialTopicDetail = "/api/SpecialTopic/GetSpecialTopicDetail"
const val PeopleMainPage = "/api/People/GetPeopleMainPage"
const val PeopleList = "/api/People/PeopleList/{page}"
const val PeopleDetail = "/api/People/GetPeopleDetail"
const val ProjectList = "/api/HeritageProject/GetHeritageProjectList/{page}"
const val ProjectBasicInformation = "/api/HeritageProject/GetMainPage"
const val ProjectDetail = "/api/HeritageProject/GetHeritageDetail"
const val InheritanceDetail = "/api/HeritageProject/GetInheritatePeople"
const val SearchCategory = "/api/HeritageProject/GetSearchCategories"
const val SearchProject = "/api/HeritageProject/SearchHeritageProject"
const val ProjectStatistics = "/api/HeritageProject/GetProjectStatisticInformation"
const val SearchNews = "/api/NewsList/SearchNews/{pages}"
const val GetAllProjectType = "/api/HeritageProject/GetAllProjectType"
const val IMAGE_HOST = "https://www.duckylife.net/img/"

private const val HOST = "www.duckylife.net"
private const val SCHEME = "https"
private const val PORT = 443
private val interceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
private val client = OkHttpClient.Builder().addInterceptor(interceptor).build()
private val httpUrl = HttpUrl.Builder().scheme(SCHEME).host(HOST).port(PORT).build()
private val retrofit = Retrofit.Builder()
    .baseUrl(httpUrl)
    .client(client)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

private fun <T> create(serviceClass:Class<T>):T = retrofit.create(serviceClass)

private inline fun <reified T> create():T= create(T::class.java)

val EHeritageApi = create<EHeritageApiRetrofit>()

interface EHeritageApiRetrofit{
    @GET(Banner)
    fun getBanner():Call<List<MainPageBanner>>

    @GET(NewsListUrl)
    fun getNewsList(@Path("page") page:Int):Call<List<NewsListResponse>>

    @GET(NewsDetail)
    fun getNewsDetail(@Query("link")link:String):Call<NewsDetail>

    @GET(ForumsList)
    fun getForumsList(@Path("page") page:Int):Call<List<NewsListResponse>>

    @GET(ForumsDetail)
    fun getForumsDetail(@Query("link") link:String):Call<NewsDetail>

    @GET(SpecialTopic)
    fun getSpecialTopicList(@Path("page") page:Int):Call<List<NewsListResponse>>

    @GET(SpecialTopicDetail)
    fun getSpecialTopicDetail(@Query("link")link:String):Call<NewsDetail>

    @GET(PeopleMainPage)
    fun getPeopleTopBanner():Call<PeopleMainPageResponse>

    @GET(PeopleList)
    fun getPeopleList(@Path("page")page:Int):Call<List<NewsListResponse>>

    @GET(PeopleDetail)
    fun getPeopleDetail(@Query("link")link:String):Call<NewsDetail>

    @GET(ProjectList)
    fun getProjecrList(@Path("page")page:Int,@Query("keywords")keywords: String? = null,@Query("year")year:Int? = null,@Query("type")type: String? = null):Call<List<ProjectListInformation>>

    @GET(ProjectBasicInformation)
    fun getProjectBasicInformation():Call<ProjectBasicInformation>

    @GET(ProjectDetail)
    fun getProjectDetail(@Query("link")link:String):Call<ProjectDetailResponse>

    @GET(InheritanceDetail)
    fun getInheritanceDetail(@Query("link")link:String):Call<InheritateDetailResponse>

    @GET(SearchCategory)
    fun getSearchCategory():Call<SearchCategoryResponse>

    @GET(SearchProject)
    fun getSearchProjectResult(@Query("num")num:String?,@Query("title")title:String?,@Query("type")type:String?,@Query("rx_time")rx_time:String?,@Query("cate")cate:String?,@Query("province")province:String?,@Query("unit")unit:String?,@Path("page")page:Int):Call<List<ProjectListInformation>>

    @GET(ProjectStatistics)
    fun getProjectStatistics():Call<HeritageProjectStatisticsResponse>

    @GET(SearchNews)
    fun searchNews(@Path("pages")page:Int,@Query("keywords")keywords:String,@Query("year")year:String?):Call<List<NewsListResponse>>

    @GET(GetAllProjectType)
    fun getAllProjectType():Call<SearchProjectTypeResponse>
}

suspend fun <T> Call<T>.await():T{
    return suspendCoroutine { continuation ->
        enqueue(object:Callback<T>{
            override fun onResponse(call: Call<T>, response: Response<T>) {
                val body = response.body()
                if(body!=null){
                    continuation.resume(body)
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                t.printStackTrace()
//                continuation.resumeWithException(t)
            }

        })
    }
}