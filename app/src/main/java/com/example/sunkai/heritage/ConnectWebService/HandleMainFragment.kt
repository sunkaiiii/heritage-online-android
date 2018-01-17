package com.example.sunkai.heritage.ConnectWebService

import com.example.sunkai.heritage.Data.ActivityData
import com.example.sunkai.heritage.Data.MainActivityData
import com.example.sunkai.heritage.Data.ClassifyActiviyData
import com.example.sunkai.heritage.Data.ClassifyDivideData
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.json.JSONArray

import org.json.JSONException
import org.json.JSONObject
import org.kobjects.base64.Base64
import org.ksoap2.serialization.SoapObject
import java.util.*

import java.util.Arrays.asList



/**
 * Created by sunkai on 2017/1/9.
 * 此类封装了有关首页相关的各类服务器请求的方法
 */

object HandleMainFragment : BaseSetting() {


    //定义扩展方法，简单化Gson的使用
    inline fun <reified T:Any> Gson.fromJsonToList(s: String, clazz: Class<Array<T>>): List<T> {
        val arr = Gson().fromJson(s, clazz)
        return arr.toList()
    }

    fun Get_Main_Divide_Activity_Image_Url():List<ActivityData>?{
        methodName="Get_Main_Divide_Activity_Image_Url"
        soapAction= namespace+"/"+ methodName
        val soapObject=SoapObject(namespace, methodName)
        val result= Get_Post(soapObject)
        try{
            return if(error == result||null==result)
                null
            else{
                val gson=GsonBuilder().create()
                gson.fromJsonToList(result,Array<ActivityData>::class.java)
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
        return null
    }

    fun GetChannelInformation(channel: String): List<ClassifyDivideData>? {
        methodName = "Get_Channel_Information_New"
        soapAction = namespace + "/" + methodName
        val soapObject = SoapObject(namespace, methodName)
        soapObject.addProperty("channel", channel)
        val result = BaseSetting.Get_Post(soapObject)
        try {
            if (result == null || error == result)
                return null
            val gson=Gson()
            return gson.fromJsonToList(result, Array<ClassifyDivideData>::class.java)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return null
    }

    fun GetChannelImage(id: Int): ByteArray? {
        BaseSetting.methodName = "Get_Channel_Image"
        BaseSetting.soapAction = BaseSetting.namespace + "/" + BaseSetting.methodName
        val soapObject = SoapObject(BaseSetting.namespace, BaseSetting.methodName)
        soapObject.addProperty("id", id)
        val imgCode = BaseSetting.Get_Post(soapObject)
        return if (null == imgCode || BaseSetting.error == imgCode) {
            null
        } else Base64.decode(imgCode)
    }
}
