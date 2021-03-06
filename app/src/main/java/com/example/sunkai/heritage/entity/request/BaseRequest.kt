package com.example.sunkai.heritage.entity.request

import android.util.Log
import com.example.sunkai.heritage.interfaces.NetworkRequest
import com.example.sunkai.heritage.tools.BaiduLocation
import com.example.sunkai.heritage.value.VERSION_NAME
import com.google.gson.Gson
import java.io.Serializable
import java.util.*
import kotlin.collections.HashMap

abstract class BaseRequest : Serializable, NetworkRequest {
    val baseinfo = BaseInfo()
    override fun getJsonParameter(): String {
        return Gson().toJson(baseinfo)
    }

    override fun getNormalParameter(): Map<String, String> {
        val result = HashMap<String, String>()
        this.javaClass.declaredFields.forEach {
            try {
                it.isAccessible = true
                result[it.name] = it.get(this)!!.toString()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return result
    }

    override fun toMap(): Map<String, String> {
        val result = HashMap<String, String>()
        result[this.javaClass.simpleName] = getJsonParameter()
        Log.e(this.javaClass.name, result.toString())
        return result
    }

    override fun getName(): String = "request"

    class BaseInfo {
        val androidVersion: String = android.os.Build.VERSION.RELEASE
        val language: String = Locale.getDefault().language
        val modelName: String = android.os.Build.MODEL
        val brandName: String = android.os.Build.BRAND
        val version: String = VERSION_NAME()
        val from = "android"
        val locationInfo = BaiduLocation.contentBean
    }
}