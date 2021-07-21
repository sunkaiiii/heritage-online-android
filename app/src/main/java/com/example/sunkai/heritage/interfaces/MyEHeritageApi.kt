package com.example.sunkai.heritage.interfaces

import com.example.sunkai.heritage.network.RequestType

interface MyEHeritageApi {
    fun getRequestName():String
    fun getUrl():String
    fun getRequestType():RequestType
}