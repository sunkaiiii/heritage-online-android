package com.example.sunkai.heritage.ConnectWebService

import org.json.JSONException
import org.json.JSONObject
import org.kobjects.base64.Base64
import org.ksoap2.serialization.SoapObject

/**
 * Created by sunkai on 2018-1-9.
 * 此类封装了发现页相关服务器请求的方法
 */

object HandleFind : BaseSetting() {

    private fun Json_To_FindComment_ID(json: String?): List<Int>? {
        return try {
            val js = JSONObject(json)
            val jsonArray = js.getJSONArray("members")
            val ids = (0 until jsonArray.length()).map { jsonArray.getInt(it) }
            ids
        } catch (e: JSONException) {
            e.printStackTrace()
            null
        }

    }

    fun Delete_User_Comment_By_ID(commentID: Int): Boolean {
        BaseSetting.methodName = "Delete_User_Comment_By_ID"
        BaseSetting.soapAction = BaseSetting.namespace + "/" + BaseSetting.methodName
        val soapObject = SoapObject(BaseSetting.namespace, BaseSetting.methodName)
        soapObject.addProperty("commentID", commentID)
        val result = BaseSetting.Get_Post(soapObject)
        return BaseSetting.success == result
    }


    fun Get_User_Comment_ID_By_User(userID: Int): List<Int>? {
        BaseSetting.methodName = "Get_User_Comment_ID_By_User"
        BaseSetting.soapAction = BaseSetting.namespace + "/" + BaseSetting.methodName
        val soapObject = SoapObject(BaseSetting.namespace, BaseSetting.methodName)
        soapObject.addProperty("userID", userID)
        val result = BaseSetting.Get_Post(soapObject)
        return if (BaseSetting.error == result) null else Json_To_FindComment_ID(result)
    }


    fun Get_User_Comment_Image(id: Int): ByteArray? {
        BaseSetting.methodName = "Get_User_Comment_Image"
        BaseSetting.soapAction = BaseSetting.namespace + "/" + BaseSetting.methodName
        val soapObject = SoapObject(BaseSetting.namespace, BaseSetting.methodName)
        soapObject.addProperty("id", id)
        val result = BaseSetting.Get_Post(soapObject)
        return if (null == result || BaseSetting.error == result) {
            null
        } else Base64.decode(result)
    }


}
