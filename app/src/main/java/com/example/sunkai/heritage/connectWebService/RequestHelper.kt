package com.example.sunkai.heritage.connectWebService

import com.example.sunkai.heritage.interfaces.IRequest

class RequestHelper(private val api: EHeritageApi) : IRequest{

    override fun getRequestApi(): EHeritageApi {
        return api
    }

}