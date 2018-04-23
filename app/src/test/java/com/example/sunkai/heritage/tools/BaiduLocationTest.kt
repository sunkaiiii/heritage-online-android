package com.example.sunkai.heritage.tools

import com.example.sunkai.heritage.Data.BaiduLoacationResponse
import com.example.sunkai.heritage.value.ERROR
import com.google.gson.Gson
import org.junit.Test

import org.junit.Assert.*

class BaiduLocationTest {

    @Test
    fun IPLocation() {
        val result=BaiduLocation.IPLocation()
        println(result)
        assertNotNull(result)
        assertTrue(result!= ERROR)
        val obj=Gson().fromJson<BaiduLoacationResponse>(result,BaiduLoacationResponse::class.java)
        assertNotNull(obj)
        println(obj.content?.address)
        println(obj.content?.address_detail?.city)
        println(obj.content?.address_detail?.province)
        println(obj.content?.address_detail?.district)
    }
}