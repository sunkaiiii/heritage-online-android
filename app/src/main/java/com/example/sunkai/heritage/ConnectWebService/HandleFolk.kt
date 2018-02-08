package com.example.sunkai.heritage.ConnectWebService


import com.example.sunkai.heritage.ConnectWebService.HandleMainFragment.fromJsonToList
import com.example.sunkai.heritage.Data.ActivityData
import com.example.sunkai.heritage.Data.ClassifyDivideData
import com.example.sunkai.heritage.Data.FolkDataLite
import com.google.gson.Gson


/**
 * Created by sunkai on 2017-1-9.
 * 此类封装了有关民间页相关的功能的服务器请求的方法
 */

object HandleFolk : BaseSettingNew() {


    fun GetFolkInforMation(): List<FolkDataLite>? {
        val getUrl= URL+"/GetChannelFolkInformation"
        val result=PutGet(getUrl)
        return if(ERROR==result){
            null
        }else{
            Gson().fromJsonToList(result,Array<FolkDataLite>::class.java)
        }
    }

    fun Search_Folk_Info(searchInfo: String): List<FolkDataLite>? {
        val getUrl= URL+"/SearchChannelForkInfo?searchInfo="+searchInfo
        val result=PutGet(getUrl)
        return if(ERROR==result){
            null
        }else{
            Gson().fromJsonToList(result,Array<FolkDataLite>::class.java)
        }

    }

    fun Get_Main_Divide_Activity_Image_Url():List<ActivityData>?{
        val methodName=URL+"/GetMainDivideActivityImageUrl"
        val result= HandleMainFragment.PutGet(methodName)
        if(ERROR==result){
            return null
        }else{
            try{
                return Gson().fromJsonToList(result,Array<ActivityData>::class.java)
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
        return null
    }

    fun GetChannelInformation(channel: String): List<ClassifyDivideData>? {
        val methodName=URL+"/GetChannelInformation?divide="+channel
        val result= HandleMainFragment.PutGet(methodName)
        if(ERROR==result){
            return null
        }else{
            try{
                return Gson().fromJsonToList(result,Array<ClassifyDivideData>::class.java)
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
        return null
    }
}
