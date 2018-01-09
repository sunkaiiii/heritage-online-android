package com.example.sunkai.heritage.ConnectWebService


import com.example.sunkai.heritage.Data.FolkData
import com.example.sunkai.heritage.Data.OrderData
import org.json.JSONException
import org.json.JSONObject
import org.kobjects.base64.Base64
import org.ksoap2.serialization.SoapObject
import org.ksoap2.transport.HttpTransportSE
import org.xmlpull.v1.XmlPullParserException

import java.io.IOException
import java.util.ArrayList

/**
 * Created by sunkai on 2017-1-9.
 * 此类封装了有关民间页相关的功能的服务器请求的方法
 */

object HandleFolk : BaseSetting() {
    private fun Json_To_FolkList(json: String?): List<FolkData>? {
        if (BaseSetting.error == json || json == null)
            return null
        try {
            val MainActivity = JSONObject(json)
            val activities = MainActivity.getJSONArray("folk_information")
            val folkInformations = ArrayList<FolkData>()
            for (i in 0 until activities.length()) {
                val data = FolkData()
                val activity = activities.get(i) as JSONObject
                data.id = Integer.valueOf(activity.get("id") as String)
                data.title = activity.get("title") as String
                data.content = activity.get("content") as String
                data.location = activity.get("location") as String
                data.divide = activity.get("divide") as String
                data.teacher = activity.get("teacher") as String
                //                data.techTime=(String)activity.get("tech-time");
                folkInformations.add(data)
            }
            return folkInformations
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return null
    }

    private fun Json_To_OrderList(json: String): List<OrderData>? {
        try {
            val MainActivity = JSONObject(json)
            val activities = MainActivity.getJSONArray("UserOrderInfo")
            val folkInformations = ArrayList<OrderData>()
            for (i in 0 until activities.length()) {
                val data = OrderData()
                val activity = activities.get(i) as JSONObject
                data.id = activity.get("id") as Int
                data.userID = activity.get("userID") as Int
                data.orderID = activity.get("orderID") as Int
                folkInformations.add(data)
            }
            return folkInformations
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return null
    }

    private fun Json_To_SingleFolkData(json: String?): FolkData? {
        try {
            val folkData = JSONObject(json)
            val data = FolkData()
            data.id = Integer.valueOf(folkData.get("id") as String)
            data.title = folkData.get("title") as String
            data.content = folkData.get("content") as String
            data.location = folkData.get("location") as String
            return data
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return null
    }


    fun GetFolkInforMation(): List<FolkData>? {
        BaseSetting.methodName = "Get_Folk_Information"
        BaseSetting.soapAction = WebServiceSetting.namespace + "/" + BaseSetting.methodName
        val transport = HttpTransportSE(WebServiceSetting.url)
        transport.debug = true
        val soapObject = SoapObject(WebServiceSetting.namespace, BaseSetting.methodName)
        val result = BaseSetting.Get_Post(soapObject)
        return Json_To_FolkList(result)
    }

    fun GetFolkImage(id: Int): ByteArray? {
        BaseSetting.methodName = "Get_Folk_Image"
        BaseSetting.soapAction = WebServiceSetting.namespace + "/" + BaseSetting.methodName
        val transport = HttpTransportSE(WebServiceSetting.url)
        transport.debug = true
        val soapObject = SoapObject(WebServiceSetting.namespace, BaseSetting.methodName)
        soapObject.addProperty("id", id)
        val envelope = BaseSetting.pre_processSoap(soapObject)
        try {
            transport.call(null, envelope)
            val `object` = envelope.bodyIn as SoapObject
            val imgCode = `object`.getProperty(0).toString()
//            System.out.println(imgCode);
            return Base64.decode(imgCode)
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: XmlPullParserException) {
            e.printStackTrace()
        }

        return null
    }

    fun Search_Folk_Info(searchInfo: String): List<FolkData>? {
        BaseSetting.methodName = "Search_Folk_Info"
        BaseSetting.soapAction = WebServiceSetting.namespace + "/" + BaseSetting.methodName
        val soapObject = SoapObject(WebServiceSetting.namespace, BaseSetting.methodName)
        soapObject.addProperty("searhInfo", searchInfo)
        val result = BaseSetting.Get_Post(soapObject)
        return Json_To_FolkList(result)
    }

    fun Add_User_Order(userID: Int, orderID: Int): Boolean {
        BaseSetting.methodName = "Add_User_Order"
        BaseSetting.soapAction = WebServiceSetting.namespace + "/" + BaseSetting.methodName
        val soapObject = SoapObject(WebServiceSetting.namespace, BaseSetting.methodName)
        soapObject.addProperty("userID", userID)
        soapObject.addProperty("orderID", orderID)
        val result = BaseSetting.Get_Post(soapObject) ?: return false
//           System.out.println(result);
        return BaseSetting.success == result
    }

    fun Cancel_User_Order(userID: Int, orderID: Int): Boolean {
        BaseSetting.methodName = "Cancel_User_Order"
        BaseSetting.soapAction = WebServiceSetting.namespace + "/" + BaseSetting.methodName
        val soapObject = SoapObject(WebServiceSetting.namespace, BaseSetting.methodName)
        soapObject.addProperty("userID", userID)
        soapObject.addProperty("orderID", orderID)
        val result = BaseSetting.Get_Post(soapObject) ?: return false
        return BaseSetting.success == result
    }

    fun Check_User_Order(userID: Int, orderID: Int): Int {
        BaseSetting.methodName = "Check_Is_Order"
        BaseSetting.soapAction = WebServiceSetting.namespace + "/" + BaseSetting.methodName
        val soapObject = SoapObject(WebServiceSetting.namespace, BaseSetting.methodName)
        soapObject.addProperty("userID", userID)
        soapObject.addProperty("orderID", orderID)
        val result = BaseSetting.Get_Post(soapObject) ?: return -1
        return Integer.parseInt(result)
    }

    private fun Get_User_Orders_ID(userID: Int): List<OrderData>? {
        BaseSetting.methodName = "Get_User_Order"
        BaseSetting.soapAction = WebServiceSetting.namespace + "/" + BaseSetting.methodName
        val soapObject = SoapObject(WebServiceSetting.namespace, BaseSetting.methodName)
        soapObject.addProperty("userID", userID)
        val result = BaseSetting.Get_Post(soapObject) ?: return null
        return Json_To_OrderList(result)
    }

    fun Get_User_Order_Information(id: Int): FolkData? {
        BaseSetting.methodName = "Get_Folk_Single_Information"
        BaseSetting.soapAction = WebServiceSetting.namespace + "/" + BaseSetting.methodName
        val soapObject = SoapObject(WebServiceSetting.namespace, BaseSetting.methodName)
        soapObject.addProperty("id", id)
        val result = BaseSetting.Get_Post(soapObject)
        return Json_To_SingleFolkData(result)
    }

    fun Get_User_Orders(userID: Int): List<FolkData>? {
        val datas = Get_User_Orders_ID(userID) ?: return null
        val Orders = ArrayList<FolkData>()
        for (i in datas.indices) {
            val data=Get_User_Order_Information(datas[i].orderID)
            data?.let{
                Orders.add(data)
            }
        }
        return Orders
    }
}
