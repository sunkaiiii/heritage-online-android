package com.example.sunkai.heritage.Service

import android.util.Log
import com.example.sunkai.heritage.Activity.LoginActivity.LoginActivity
import com.example.sunkai.heritage.ConnectWebService.HandleUser
import com.example.sunkai.heritage.tools.MakeToast.toast
import com.example.sunkai.heritage.tools.ThreadPool
import com.example.sunkai.heritage.tools.runOnUiThread
import com.google.firebase.messaging.FirebaseMessagingService

class FCMMessageService : FirebaseMessagingService() {
    companion object {
        const val TAG = "FCM_MessageService"
    }

    override fun onNewToken(token: String?) {
        Log.d(TAG, "Refreshed token:$token")
        val userName = LoginActivity.userName ?: return
        token ?: return
        ThreadPool.execute {
            val result = HandleUser.SendFCMToken(userName, token)
        }
    }
}