package com.example.sunkai.heritage.connectWebService

import com.example.sunkai.heritage.interfaces.IRequest

class RequestHelper<T>(private val api: EHeritageApi,
                       private val type: Class<T>) : IRequest<T> {


    override fun getRequestApi(): EHeritageApi {
        return api
    }

    override fun getReturnType(): Class<T> {
        return type
    }


}