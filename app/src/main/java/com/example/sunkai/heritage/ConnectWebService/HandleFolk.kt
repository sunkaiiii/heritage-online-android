package com.example.sunkai.heritage.ConnectWebService


import com.example.sunkai.heritage.Data.FolkDataLite
import com.google.gson.GsonBuilder
import org.ksoap2.serialization.SoapObject
import org.ksoap2.transport.HttpTransportSE

/**
 * Created by sunkai on 2017-1-9.
 * 此类封装了有关民间页相关的功能的服务器请求的方法
 */

object HandleFolk : BaseSetting() {


    fun GetFolkInforMation(): List<FolkDataLite>? {
        BaseSetting.methodName = "Get_Folk_Information_New"
        BaseSetting.soapAction = BaseSetting.namespace + "/" + BaseSetting.methodName
        val transport = HttpTransportSE(BaseSetting.url)
        transport.debug = true
        val soapObject = SoapObject(BaseSetting.namespace, BaseSetting.methodName)
        val result = BaseSetting.Get_Post(soapObject)
        if(error == result||result==null)
            return null
        try {
            return GsonBuilder().create().fromJsonToList(result, Array<FolkDataLite>::class.java)
        }catch (e:Exception){
            e.printStackTrace()
        }
        return null
    }

    fun Search_Folk_Info(searchInfo: String): List<FolkDataLite>? {
        BaseSetting.methodName = "Search_Folk_Info_New"
        BaseSetting.soapAction = BaseSetting.namespace + "/" + BaseSetting.methodName
        val soapObject = SoapObject(BaseSetting.namespace, BaseSetting.methodName)
        soapObject.addProperty("searhInfo", searchInfo)
        val result = BaseSetting.Get_Post(soapObject)
        if(error == result||result==null)
            return null
        try {
            return GsonBuilder().create().fromJsonToList(result,Array<FolkDataLite>::class.java)
        }catch (e:Exception){
            e.printStackTrace()
        }
        return null
    }
}
