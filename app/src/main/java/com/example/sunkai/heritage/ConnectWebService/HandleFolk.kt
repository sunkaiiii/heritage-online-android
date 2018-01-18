package com.example.sunkai.heritage.ConnectWebService


import com.example.sunkai.heritage.Data.FolkData
import com.example.sunkai.heritage.Data.FolkDataLite
import com.example.sunkai.heritage.Data.OrderData
import com.google.gson.GsonBuilder
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

    fun Add_User_Order(userID: Int, orderID: Int): Boolean {
        BaseSetting.methodName = "Add_User_Order"
        BaseSetting.soapAction = BaseSetting.namespace + "/" + BaseSetting.methodName
        val soapObject = SoapObject(BaseSetting.namespace, BaseSetting.methodName)
        soapObject.addProperty("userID", userID)
        soapObject.addProperty("orderID", orderID)
        val result = BaseSetting.Get_Post(soapObject) ?: return false
//           System.out.println(result);
        return BaseSetting.success == result
    }

    fun Cancel_User_Order(userID: Int, orderID: Int): Boolean {
        BaseSetting.methodName = "Cancel_User_Order"
        BaseSetting.soapAction = BaseSetting.namespace + "/" + BaseSetting.methodName
        val soapObject = SoapObject(BaseSetting.namespace, BaseSetting.methodName)
        soapObject.addProperty("userID", userID)
        soapObject.addProperty("orderID", orderID)
        val result = BaseSetting.Get_Post(soapObject) ?: return false
        return BaseSetting.success == result
    }

    fun Check_User_Order(userID: Int, orderID: Int): Int {
        BaseSetting.methodName = "Check_Is_Order"
        BaseSetting.soapAction = BaseSetting.namespace + "/" + BaseSetting.methodName
        val soapObject = SoapObject(BaseSetting.namespace, BaseSetting.methodName)
        soapObject.addProperty("userID", userID)
        soapObject.addProperty("orderID", orderID)
        val result = BaseSetting.Get_Post(soapObject) ?: return -1
        return Integer.parseInt(result)
    }

    private fun Get_User_Orders_ID(userID: Int): List<OrderData>? {
        BaseSetting.methodName = "Get_User_Order"
        BaseSetting.soapAction = BaseSetting.namespace + "/" + BaseSetting.methodName
        val soapObject = SoapObject(BaseSetting.namespace, BaseSetting.methodName)
        soapObject.addProperty("userID", userID)
        val result = BaseSetting.Get_Post(soapObject) ?: return null
        return Json_To_OrderList(result)
    }

    fun Get_User_Order_Information(id: Int): FolkData? {
        BaseSetting.methodName = "Get_Folk_Single_Information_New"
        BaseSetting.soapAction = BaseSetting.namespace + "/" + BaseSetting.methodName
        val soapObject = SoapObject(BaseSetting.namespace, BaseSetting.methodName)
        soapObject.addProperty("id", id)
        val result = BaseSetting.Get_Post(soapObject)
        if(error==result||result==null)
            return null
        try{
            return GsonBuilder().create().fromJson(result,FolkData::class.java)
        }catch (e:Exception){
            e.printStackTrace()
        }
        return null
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
