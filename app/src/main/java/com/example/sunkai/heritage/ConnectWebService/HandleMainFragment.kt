package com.example.sunkai.heritage.ConnectWebService

import android.util.Log
import com.example.sunkai.heritage.Data.*
import com.example.sunkai.heritage.value.CATEGORIES
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.json.JSONArray

import org.json.JSONException
import org.json.JSONObject
import org.kobjects.base64.Base64
import org.ksoap2.serialization.SoapObject
import java.util.*

import java.util.Arrays.asList
import kotlin.collections.ArrayList


/**
 * Created by sunkai on 2017/1/9.
 * 此类封装了有关首页相关的各类服务器请求的方法
 */

object HandleMainFragment : BaseSettingNew() {

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
        Log.d("GetFolkNewsList",result)
        if(ERROR==result){
            return arrayListOf()
        }
        try{
            return Gson().fromJsonToList(result,Array<FolkNewsLite>::class.java)
        }catch (e:Exception){
            e.printStackTrace()
        }
        return arrayListOf()
    }
}
