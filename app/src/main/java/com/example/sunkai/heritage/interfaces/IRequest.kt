package com.example.sunkai.heritage.interfaces

import com.example.sunkai.heritage.network.EHeritageApi

interface IRequest {
    fun getRequestApi(): EHeritageApi
    fun getRequestBean():NetworkRequest
}