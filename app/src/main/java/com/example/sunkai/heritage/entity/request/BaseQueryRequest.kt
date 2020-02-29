package com.example.sunkai.heritage.entity.request

import android.util.Log
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.interfaces.NetworkRequest
import com.example.sunkai.heritage.tools.BaiduLocation
import com.example.sunkai.heritage.tools.GlobalContext
import com.google.gson.Gson
import java.io.Serializable
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

open class BaseQueryRequest : BaseRequest() {
    override fun getPathParamerater(): List<String> = listOf()
}

