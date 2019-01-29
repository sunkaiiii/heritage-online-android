package com.example.sunkai.heritage.service

import android.util.Log
import com.example.sunkai.heritage.activity.loginActivity.LoginActivity
import com.example.sunkai.heritage.connectWebService.HandleUser
import com.google.firebase.messaging.FirebaseMessagingService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FCMMessageService : FirebaseMessagingService() {
    companion object {
        const val TAG = "FCM_MessageService"
    }

    override fun onNewToken(token: String?) {
        Log.d(TAG, "Refreshed token:$token")
        val userName = LoginActivity.userName ?: return
        token ?: return
        GlobalScope.launch {
            val result = HandleUser.SendFCMToken(userName, token)
        }
    }
}