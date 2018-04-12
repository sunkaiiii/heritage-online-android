package com.example.sunkai.heritage.ConnectWebService

import org.junit.Assert

open class BaseTestClass {
    fun <T> assertData(data:List<T>){
        Assert.assertNotNull(data)
        Assert.assertTrue(data.isNotEmpty())
    }

    fun <T> assertEmptyData(data:List<T>){
        Assert.assertNotNull(data)
        Assert.assertTrue(data.size==1 || data.isEmpty())
    }
}