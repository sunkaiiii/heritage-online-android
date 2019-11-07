package com.example.sunkai.heritage.interfaces

import com.example.sunkai.heritage.connectWebService.EHeritageApi

interface IRequest<T> {
    fun getRequestApi(): EHeritageApi
    fun getReturnType(): Class<T>
}