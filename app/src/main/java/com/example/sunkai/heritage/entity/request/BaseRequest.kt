package com.example.sunkai.heritage.entity.request

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Serializable
import java.util.*
import kotlin.collections.HashMap

class BaseRequest : Serializable,NetWorkRequest {


    //TODO 静态成员
    val androidVersion = android.os.Build.VERSION.RELEASE
    val language = Locale.getDefault().language
    val modelName = android.os.Build.MODEL
    val brandName = android.os.Build.BRAND


    override fun toJson(): String {
        return Gson().toJson(this)
    }

    override fun toMap(): Map<String, String> {
        val result=HashMap<String,String>()
        this.javaClass.declaredFields.forEach { itField ->
            val itValue=itField.get(this)
                itValue?.let {
                result[itField.name]=it.toString()
            }
        }
        println(result)
        return result
    }

}

interface NetWorkRequest{
    fun toJson():String
    fun toMap():Map<String,String>
}