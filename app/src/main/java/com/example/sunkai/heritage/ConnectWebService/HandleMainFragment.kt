package com.example.sunkai.heritage.ConnectWebService

import com.example.sunkai.heritage.Data.MainActivityData
import com.example.sunkai.heritage.Data.ClassifyActiviyData

import org.json.JSONException
import org.json.JSONObject
import org.kobjects.base64.Base64
import org.ksoap2.serialization.SoapObject

import java.util.ArrayList

/**
 * Created by sunkai on 2017/1/9.
 * 此类封装了有关首页相关的各类服务器请求的方法
 */

object HandleMainFragment : BaseSetting() {
    fun ReadMainActivity(): List<MainActivityData>? {
        BaseSetting.methodName = "Read_Main_Activity"
        BaseSetting.soapAction = BaseSetting.namespace + "/" + BaseSetting.methodName
        val soapObject = SoapObject(BaseSetting.namespace, BaseSetting.methodName)
        val result = BaseSetting.Get_Post(soapObject)
        if (BaseSetting.error != result && result != null) {
            try {
                val MainActivity = JSONObject(result)
                val activities = MainActivity.getJSONArray("main_Activity")
                val activityDatas = ArrayList<MainActivityData>()
                for (i in 0 until activities.length()) {
                    val data = MainActivityData()
                    val activity = activities.get(i) as JSONObject
                    data.id = Integer.valueOf(activity.get("id") as String)
                    data.activityTitle = activity.get("activity_title") as String
                    data.activityContent = activity.get("activity_content") as String
                    val imgCode = activity.get("activity_image") as String
                    data.activityImage = Base64.decode(imgCode)
                    activityDatas.add(data)
                }
                return activityDatas
            } catch (e: JSONException) {
                e.printStackTrace()
            }

        }
        return null
    }

    fun GetChannelInformation(channel: String): List<ClassifyActiviyData>? {
        BaseSetting.methodName = "Get_Channel_Information"
        BaseSetting.soapAction = BaseSetting.namespace + "/" + BaseSetting.methodName
        val soapObject = SoapObject(BaseSetting.namespace, BaseSetting.methodName)
        soapObject.addProperty("channel", channel)
        val result = BaseSetting.Get_Post(soapObject)
        try {
            if (result == null || BaseSetting.error == result)
                return null
            val MainActivity = JSONObject(result)
            val activities = MainActivity.getJSONArray("classify_activity")
            val activityDatas = ArrayList<ClassifyActiviyData>()
            for (i in 0 until activities.length()) {
                val data = ClassifyActiviyData()
                val activity = activities.get(i) as JSONObject
                data.id = Integer.valueOf(activity.get("id") as String)
                data.activityTitle = activity.get("activity_title") as String
                data.activityContent = activity.get("activity_content") as String
                data.activityChannel = activity.get("activity_channel") as String
                activityDatas.add(data)
            }
            return activityDatas
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
