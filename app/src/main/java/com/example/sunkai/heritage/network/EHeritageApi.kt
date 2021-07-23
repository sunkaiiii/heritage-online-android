package com.example.sunkai.heritage.network

import com.example.sunkai.heritage.interfaces.MyEHeritageApi
import java.io.Serializable

enum class EHeritageApi constructor(val _name: String, val _url: String, val _type: RequestType) : MyEHeritageApi, Serializable {
    GetPeopleDetail("peopleDetail", "/api/People/GetPeopleDetail", RequestType.GET);

    override fun getRequestName(): String {
        return _name
    }

    override fun getUrl(): String {
        return _url
    }

    override fun getRequestType(): RequestType {
        return _type
    }

}
