package com.example.sunkai.heritage.ConnectWebService


import com.example.sunkai.heritage.Data.ActivityData
import com.example.sunkai.heritage.Data.ClassifyDivideData
import com.example.sunkai.heritage.Data.FolkData
import com.example.sunkai.heritage.Data.FolkDataLite
import com.google.gson.Gson


/**
 * Created by sunkai on 2018-1-9.
 * 此类封装了有关民间页相关的功能的服务器请求的方法
 */

object HandleFolk : BaseSetting() {


    fun GetFolkInforMation(): List<FolkDataLite>? {
        val getUrl= "$URL/GetChannelFolkInformation"
        val result=PutGet(getUrl)
        return if(ERROR==result){
            null
        }else{
            fromJsonToList(result, Array<FolkDataLite>::class.java)
        }
    }

    fun Search_Folk_Info(searchInfo: String): List<FolkDataLite> {
        val getUrl= "$URL/SearchChannelForkInfo?searchInfo=$searchInfo"
        val result=PutGet(getUrl)
        if(ERROR==result){
            return arrayListOf()
        }else{
            try {
                return fromJsonToList(result, Array<FolkDataLite>::class.java)
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
        return arrayListOf()
    }

    fun Get_Channel_Folk_Single_Information(id:Int):FolkData?{
        val getUrl="$URL/GetChannelFolkSingleInformation?id=$id"
        val result=PutGet(getUrl)
        if(result!= ERROR){
            return gsonInstance.fromJson(result,FolkData::class.java)
        }
        return null
    }

    fun Get_Main_Divide_Activity_Image_Url():List<ActivityData>?{
        val methodName= "$URL/GetMainDivideActivityImageUrl"
        val result= HandleMainFragment.PutGet(methodName)
        if(ERROR==result){
            return null
        }else{
            try{
                return fromJsonToList(result, Array<ActivityData>::class.java)
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
        return null
    }

    fun GetChannelInformation(channel: String): List<ClassifyDivideData> {
        val methodName= "$URL/GetChannelInformation?divide=$channel"
        val result= HandleMainFragment.PutGet(methodName)
        if(ERROR==result){
            return arrayListOf()
        }else{
            try{
                return fromJsonToList(result, Array<ClassifyDivideData>::class.java)
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
        return arrayListOf()
    }
}
