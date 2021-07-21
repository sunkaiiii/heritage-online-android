package com.example.sunkai.heritage.interfaces

import android.os.Handler
import com.example.sunkai.heritage.network.RequestHelper
import kotlinx.coroutines.Job

interface RequestAction {
    fun beforeReuqestStart(request: RequestHelper)
    fun onRequestError(api: RequestHelper, action: RequestAction, ex: Exception)
    fun onTaskReturned(api: RequestHelper, action: RequestAction, response: String)
    fun getUIThread(): Handler
    fun onRequestEnd(request: RequestHelper)
    fun getRunningMap():Map<NetworkRequest,Job>
}