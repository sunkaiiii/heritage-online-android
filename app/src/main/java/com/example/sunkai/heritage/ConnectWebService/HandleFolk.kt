package com.example.sunkai.heritage.ConnectWebService


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
}
