package com.example.sunkai.heritage.interfaces

import android.os.Handler
import com.example.sunkai.heritage.connectWebService.RequestHelper

interface RequestAction {
    fun <T> onRequestError(api: RequestHelper<T>, action:RequestAction, ex:Exception)
    fun <T> onTaskReturned(api:RequestHelper<T>,action: RequestAction,response:String)
    fun <T> getUIThread(): Handler
}