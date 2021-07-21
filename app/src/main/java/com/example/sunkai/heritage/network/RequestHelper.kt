package com.example.sunkai.heritage.network

import com.example.sunkai.heritage.interfaces.IRequest
import com.example.sunkai.heritage.interfaces.NetworkRequest

class RequestHelper(private val api: EHeritageApi, private val bean: NetworkRequest) : IRequest{

    override fun getRequestApi(): EHeritageApi {
        return api
    }

    override fun getRequestBean(): NetworkRequest {
        return bean
    }


}