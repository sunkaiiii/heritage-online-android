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

object HandleMainFragment : BaseSettingNew() {


    fun Get_Main_Divide_Activity_Image_Url():List<ActivityData>?{
        val methodName=URL+"/GetMainDivideActivityImageUrl"
        val result=PutGet(methodName)
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
        val result=PutGet(methodName)
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
