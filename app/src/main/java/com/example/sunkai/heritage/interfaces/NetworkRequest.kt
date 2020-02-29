package com.example.sunkai.heritage.interfaces

interface NetworkRequest {
    fun getJsonParameter(): String
    fun getNormalParameter():Map<String,String>
    fun toMap(): Map<String, String>
    fun getName(): String
    fun getPathParamerater():List<String>
}