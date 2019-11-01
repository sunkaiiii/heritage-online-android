package com.example.sunkai.heritage.interfaces

import java.lang.Exception

interface RequestAction {
    fun onRequestError(api:MyEHeritageApi,action:RequestAction,ex:Exception)
    fun onTaskReturned(api:MyEHeritageApi,action: RequestAction,response:String)
}