package com.example.sunkai.heritage.interfaces

interface NetworkRequest {
    fun toJson(): String
    fun toMap(): Map<String, String>
    fun getName(): String
}