package com.example.sunkai.heritage.ConnectWebService

import com.example.sunkai.heritage.Data.FocusData
import com.example.sunkai.heritage.Activity.LoginActivity
import com.example.sunkai.heritage.Data.OtherPersonData
import com.example.sunkai.heritage.value.*

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.ksoap2.serialization.SoapObject

import java.util.ArrayList

/**
 * Created by sunkai on 2018-1-9.
 * 此类封装了个人中心页服务器请求的方法
 */


object HandlePerson : BaseSetting() {

    private fun Json_To_focusData(json: String?): List<FocusData>? {
        try {
            val jsonObject = JSONObject(json)
            val info = jsonObject.get("Follow_Information") as JSONArray
            val datas = ArrayList<FocusData>()
            for (i in 0 until info.length()) {
                val data = FocusData()
                val jsondata = info.get(i) as JSONObject
                data.focusUserid = Integer.parseInt(jsondata.getString("focus_focusID"))
                data.focusFansID = Integer.parseInt(jsondata.getString("focus_fansID"))
                data.name = jsondata.getString("USER_NAME")
                datas.add(data)
            }
            return datas
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return null
    }

    private fun Json_To_SearchData(json: String?): List<FocusData>? {
        try {
            val jsonObject = JSONObject(json)
            val searInfo = jsonObject.get("searchInfo") as JSONArray
            val datas = ArrayList<FocusData>()
            for (i in 0 until searInfo.length()) {
                val jsonData = searInfo.get(i) as JSONObject
                val data = FocusData()
                data.followeachother = false
                data.isCheck = false
                data.name = jsonData.getString("user_name")
                data.focusFansID = Integer.parseInt(jsonData.getString("id"))
                data.focusUserid = LoginActivity.userID
                datas.add(data)
            }
            return datas
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return null
    }

    private fun Json_To_OtherPersonData(userID: Int, json: String?): OtherPersonData? {
        if (json == null)
            return null
        try {
            val jsonObject = JSONObject(json)
            val userName = jsonObject.getString("userName")
            val focusNumber = jsonObject.getInt("focusNumber")
            val fansNumber = jsonObject.getInt("fansNumber")
            val permisison = jsonObject.getInt("permission")
            val userFollowAndFansPermission = jsonObject.getInt("focusAndFansPermission")
            return OtherPersonData(userID, focusNumber, fansNumber, userName, permisison, userFollowAndFansPermission)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return null
    }

    fun Update_User_Image(userID: Int, image: String): Boolean {
        BaseSetting.methodName = "Update_User_Image"
        BaseSetting.soapAction = WebServiceSetting.namespace + "/" + BaseSetting.methodName
        val soapObject = SoapObject(WebServiceSetting.namespace, BaseSetting.methodName)
        soapObject.addProperty("userID", userID)
        soapObject.addProperty("image", image)
        val result = BaseSetting.Get_Post(soapObject)
        return BaseSetting.success == result
    }

    fun Get_User_Image(userID: Int): String? {
        BaseSetting.methodName = "Get_User_Image"
        BaseSetting.soapAction = WebServiceSetting.namespace + "/" + BaseSetting.methodName
        val soapObject = SoapObject(WebServiceSetting.namespace, BaseSetting.methodName)
        soapObject.addProperty("userID", userID)
        val result = BaseSetting.Get_Post(soapObject)
        return if (BaseSetting.error == result) null else result
    }

    fun Get_Follow_Number(userID: Int): Int {
        BaseSetting.methodName = "Get_Follow_Number"
        BaseSetting.soapAction = WebServiceSetting.namespace + "/" + BaseSetting.methodName
        val soapObject = SoapObject(WebServiceSetting.namespace, BaseSetting.methodName)
        soapObject.addProperty("userID", userID)
        val result = BaseSetting.Get_Post(soapObject)
        return if (BaseSetting.error == result || result == null) {
            0
        } else Integer.parseInt(result)
    }

    fun Get_Fans_Number(userID: Int): Int {
        BaseSetting.methodName = "Get_Fans_Number"
        BaseSetting.soapAction = WebServiceSetting.namespace + "/" + BaseSetting.methodName
        val soapObject = SoapObject(WebServiceSetting.namespace, BaseSetting.methodName)
        soapObject.addProperty("userID", userID)
        val result = BaseSetting.Get_Post(soapObject)
        return if (BaseSetting.error == result || result == null) {
            0
        } else Integer.parseInt(result)
    }

    fun Get_Follow_Information(userID: Int): List<FocusData>? {
        BaseSetting.methodName = "Get_Follow_Information"
        BaseSetting.soapAction = WebServiceSetting.namespace + "/" + BaseSetting.methodName
        val soapObject = SoapObject(WebServiceSetting.namespace, BaseSetting.methodName)
        soapObject.addProperty("userID", userID)
        val result = BaseSetting.Get_Post(soapObject)
        return Json_To_focusData(result)
    }

    fun Get_Fans_Information(userID: Int): List<FocusData>? {
        BaseSetting.methodName = "Get_Fans_Information"
        BaseSetting.soapAction = WebServiceSetting.namespace + "/" + BaseSetting.methodName
        val soapObject = SoapObject(WebServiceSetting.namespace, BaseSetting.methodName)
        soapObject.addProperty("userID", userID)
        val result = BaseSetting.Get_Post(soapObject)
        return Json_To_focusData(result)
    }

    fun Get_User_All_Info(userID: Int): OtherPersonData? {
        BaseSetting.methodName = "Get_User_All_Info"
        BaseSetting.soapAction = WebServiceSetting.namespace + "/" + BaseSetting.methodName
        val soapObject = SoapObject(WebServiceSetting.namespace, BaseSetting.methodName)
        soapObject.addProperty("userID", userID)
        val result = BaseSetting.Get_Post(soapObject)
        return Json_To_OtherPersonData(userID, result)
    }

    fun Add_Focus(userID: Int, focusID: Int): Boolean {
        BaseSetting.methodName = "Add_Focus"
        BaseSetting.soapAction = WebServiceSetting.namespace + "/" + BaseSetting.methodName
        val soapObject = SoapObject(WebServiceSetting.namespace, BaseSetting.methodName)
        soapObject.addProperty("userID", userID)
        soapObject.addProperty("focusID", focusID)
        val result = BaseSetting.Get_Post(soapObject)
        return BaseSetting.success == result
    }

    fun Cancel_Focus(userID: Int, focusID: Int): Boolean {
        BaseSetting.methodName = "Cancel_Focus"
        BaseSetting.soapAction = WebServiceSetting.namespace + "/" + BaseSetting.methodName
        val soapObject = SoapObject(WebServiceSetting.namespace, BaseSetting.methodName)
        soapObject.addProperty("userID", userID)
        soapObject.addProperty("focusID", focusID)
        val result = BaseSetting.Get_Post(soapObject)
        return BaseSetting.success == result
    }

    fun Check_Follow_Eachohter(userID: Int, focusID: Int): Boolean {
        BaseSetting.methodName = "Check_Follow_Eachohter"
        BaseSetting.soapAction = WebServiceSetting.namespace + "/" + BaseSetting.methodName
        val soapObject = SoapObject(WebServiceSetting.namespace, BaseSetting.methodName)
        soapObject.addProperty("userID", userID)
        soapObject.addProperty("focusID", focusID)
        val result = BaseSetting.Get_Post(soapObject)
        return BaseSetting.success == result
    }

    fun Get_Search_UserInfo(name: String): List<FocusData>? {
        BaseSetting.methodName = "Get_Search_UserInfo"
        BaseSetting.soapAction = WebServiceSetting.namespace + "/" + BaseSetting.methodName
        val soapObject = SoapObject(WebServiceSetting.namespace, BaseSetting.methodName)
        soapObject.addProperty("name", name)
        val result = BaseSetting.Get_Post(soapObject)
        return Json_To_SearchData(result)
    }

    fun is_User_Follow(userID: Int, fansID: Int): Boolean {
        BaseSetting.methodName = "is_User_Follow"
        BaseSetting.soapAction = WebServiceSetting.namespace + "/" + BaseSetting.methodName
        val soapObject = SoapObject(WebServiceSetting.namespace, BaseSetting.methodName)
        soapObject.addProperty("userName", userID)
        soapObject.addProperty("fansName", fansID)
        val result = BaseSetting.Get_Post(soapObject)
        return BaseSetting.success == result
    }

    fun Get_User_Update_Time(userID: Int): String? {
        BaseSetting.methodName = "Get_User_Update_Time"
        BaseSetting.soapAction = WebServiceSetting.namespace + "/" + BaseSetting.methodName
        val soapObject = SoapObject(WebServiceSetting.namespace, BaseSetting.methodName)
        soapObject.addProperty("userID", userID)
        return Get_Post(soapObject)
    }

    fun Get_User_Permission(userID: Int): Int {
        BaseSetting.methodName = "Get_User_Permission"
        BaseSetting.soapAction = WebServiceSetting.namespace + "/" + BaseSetting.methodName
        val soapObject = SoapObject(WebServiceSetting.namespace, BaseSetting.methodName)
        soapObject.addProperty("userID", userID)
        val result = BaseSetting.Get_Post(soapObject) ?: return DENIALD
        return Integer.parseInt(result)
    }

    fun Get_User_Focus_And_Fans_View_Permission(userID: Int): Int {
        BaseSetting.methodName = "Get_User_Focus_And_Fans_View_Permission"
        BaseSetting.soapAction = WebServiceSetting.namespace + "/" + BaseSetting.methodName
        val soapObject = SoapObject(WebServiceSetting.namespace, BaseSetting.methodName)
        soapObject.addProperty("userID", userID)
        val result = BaseSetting.Get_Post(soapObject) ?: return DENIALD
        return Integer.parseInt(result)
    }

    fun Set_User_Permission(userID: Int, permission: Int): Boolean {
        BaseSetting.methodName = "Set_User_Permission"
        BaseSetting.soapAction = WebServiceSetting.namespace + "/" + BaseSetting.methodName
        val soapObject = SoapObject(WebServiceSetting.namespace, BaseSetting.methodName)
        soapObject.addProperty("userID", userID)
        soapObject.addProperty("permission", permission)
        val result = BaseSetting.Get_Post(soapObject)
        return BaseSetting.success == result
    }

    fun Set_User_Focus_And_Fans_View_Permission(userID: Int, permission: Int): Boolean {
        BaseSetting.methodName = "Set_User_Focus_And_Fans_View_Permission"
        BaseSetting.soapAction = WebServiceSetting.namespace + "/" + BaseSetting.methodName
        val soapObject = SoapObject(WebServiceSetting.namespace, BaseSetting.methodName)
        soapObject.addProperty("userID", userID)
        soapObject.addProperty("permission", permission)
        val result = BaseSetting.Get_Post(soapObject)
        return BaseSetting.success == result
    }
}
