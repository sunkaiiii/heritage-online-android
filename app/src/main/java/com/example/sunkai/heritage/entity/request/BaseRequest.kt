package com.example.sunkai.heritage.entity.request

import android.util.Log
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.interfaces.NetworkRequest
import com.example.sunkai.heritage.tools.BaiduLocation
import com.example.sunkai.heritage.tools.GlobalContext
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Serializable
import java.util.*
import kotlin.collections.HashMap

open class BaseRequest : Serializable, NetworkRequest {

    val baseinfo = BaseInfo()
    override fun toJson(): String {
        return Gson().toJson(this)
    }

    override fun toMap(): Map<String, String> {
        val result = HashMap<String, String>()
        result[this.javaClass.simpleName] = toJson()
        Log.e(this.javaClass.name, result.toString())
        return result
    }

    override fun getName(): String = this.javaClass.simpleName

    class BaseInfo {
        val androidVersion = android.os.Build.VERSION.RELEASE
        val language = Locale.getDefault().language
        val modelName = android.os.Build.MODEL
        val brandName = android.os.Build.BRAND
        val version = GlobalContext.instance.getString(R.string.verson_code)
        val from = "android"
        val locationInfo=BaiduLocation.contentBean
    }

}

