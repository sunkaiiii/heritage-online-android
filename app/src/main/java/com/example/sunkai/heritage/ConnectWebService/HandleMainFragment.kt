package com.example.sunkai.heritage.ConnectWebService

import com.example.sunkai.heritage.Data.*
import com.example.sunkai.heritage.value.CATEGORIES
import com.google.gson.Gson


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

    fun GetBottomNewsLiteInformation(start: Int=0,end: Int=20):List<BottomFolkNewsLite>{
        val url="$URL/GetBottomNewsLiteInformation?start=$start&end=$end"
        val result=PutGet(url)
        if(ERROR==result){
            return arrayListOf()
        }
        try{
            return fromJsonToList(result, Array<BottomFolkNewsLite>::class.java)
        }catch (e:Exception){
            e.printStackTrace()
        }
        return arrayListOf()
    }

    fun GetBottomNewsInformationByID(id:Int):BottomFolkNews?{
        val url="$URL/GetBottomNewsInformationByID?id=$id"
        val result=PutGet(url)
        if(ERROR==result){
            return null
        }
        try{
            return Gson().fromJson(result,BottomFolkNews::class.java)
        }catch (e:Exception){
            e.printStackTrace()
        }
        return null
    }

    fun GetBottomNewsDetailInfo(content:String):List<BottomNewsDetail>{
        try{
            return fromJsonToList(content, Array<BottomNewsDetail>::class.java)
        }catch (e:Exception){
            e.printStackTrace()
        }
        return arrayListOf()
    }

    //会返回null,针对空值和非空值做不同的反应
    fun GetMainPageSlideNewsInfo():List<MainPageSlideNews>?{
        val url="$URL/GetMainPageSlideNewsInformation"
        val result=PutGet(url)
        if(result== ERROR){
            return null
        }
        try{
            return fromJsonToList(result, Array<MainPageSlideNews>::class.java)
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

    fun SearchBottomNewsInfo(searchInfo:String):List<BottomFolkNewsLite>{
        val url="$URL/SearchBottomNewsInformation?searchInfo=$searchInfo"
        val result=PutGet(url)
        if(result== ERROR){
            return arrayListOf()
        }
        try{
            return fromJsonToList(result,Array<BottomFolkNewsLite>::class.java)
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
