package com.example.sunkai.heritage.connectWebService

import com.example.sunkai.heritage.entity.*
import com.example.sunkai.heritage.value.CATEGORIES


/**
 * Created by sunkai on 2017/1/9.
 * 此类封装了有关首页相关的各类服务器请求的方法
 */

object HandleMainFragment : BaseSetting() {

    fun ReadMainNews():List<List<FolkNewsLite>>{
        val resultList=ArrayList<List<FolkNewsLite>>()
        CATEGORIES.forEach {
            resultList.add(GetFolkNewsList(it))
        }
        return resultList
    }

    fun GetFolkNewsList(category:String,start:Int=0,end:Int=3):List<FolkNewsLite>{
        val url= "$URL/GetFolkNewsList?divide=$category&start=$start&end=$end"
        val result=PutGet(url)
        if(ERROR==result){
            return arrayListOf()
        }
        try{
            return fromJsonToList(result, Array<FolkNewsLite>::class.java)
        }catch (e:Exception){
            e.printStackTrace()
        }
        return arrayListOf()
    }

    fun GetFolkNewsInformation(id:Int):List<NewsDetail>{
        val url="$URL/GetFolkNewsInformation?id=$id"
        val result=PutGet(url)
        if(ERROR==result){
            return arrayListOf()
        }
        try{
            return fromJsonToList(result, Array<NewsDetail>::class.java)
        }catch (e:Exception){
            e.printStackTrace()
        }
        return arrayListOf()
    }

    fun GetBottomNewsLiteInformation(page:Int):List<NewsListResponse>{
        val url="/api/NewsList/$page"
        val result=PutNewGet(url)
        if(ERROR==result){
            return arrayListOf()
        }
        try{
            return fromJsonToList(result, Array<NewsListResponse>::class.java)
        }catch (e:Exception){
            e.printStackTrace()
        }
        return arrayListOf()
    }

    fun GetBottomNewsInformationByLink(link:String):BottomFolkNews?{
        val url="/api/NewsDetail?link=$link"
        val result=PutNewGet(url)
        if(ERROR==result){
            return null
        }
        try{
            return gsonInstance.fromJson(result,BottomFolkNews::class.java)
        }catch (e:Exception){
            e.printStackTrace()
        }
        return null
    }


    //会返回null,针对空值和非空值做不同的反应
    fun GetMainPageSlideNewsInfo():List<MainPageBanner>?{
        val url="$URL/GetMainPageSlideNewsInformation"
        val result=PutGet(url)
        if(result== ERROR){
            return null
        }
        try{
            return fromJsonToList(result, Array<MainPageBanner>::class.java)
        }catch (e:Exception){
            e.printStackTrace()
        }
        return null
    }

    fun GetMainPageSlideDetailInfo(content:String):List<NewsDetail>{
        try{
            return fromJsonToList(content, Array<NewsDetail>::class.java)
        }catch (e:Exception){
            e.printStackTrace()
        }
        return arrayListOf()
    }

    fun SearchBottomNewsInfo(searchInfo:String):List<NewsListResponse>{
        val url="$URL/SearchBottomNewsInformation?searchInfo=$searchInfo"
        val result=PutGet(url)
        if(result== ERROR){
            return arrayListOf()
        }
        try{
            return fromJsonToList(result,Array<NewsListResponse>::class.java)
        }catch (e:Exception){
            e.printStackTrace()
        }
        return arrayListOf()
    }

    fun SearchAllNewsInfo(searchInfo: String):List<FolkNewsLite>{
        val url="$URL/SearchFolkNewsInformaiton?searchInfo=$searchInfo"
        val result=PutGet(url)
        if(result== ERROR){
            return arrayListOf()
        }
        try{
            return fromJsonToList(result,Array<FolkNewsLite>::class.java)
        }catch (e:Exception){
            e.printStackTrace()
        }
        return arrayListOf()
    }
}
