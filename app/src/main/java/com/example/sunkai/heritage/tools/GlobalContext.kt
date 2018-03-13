package com.example.sunkai.heritage.tools

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.util.Log
import com.xiaomi.channel.commonutils.logger.LoggerInterface
import com.xiaomi.mipush.sdk.Logger
import com.xiaomi.mipush.sdk.MiPushClient



/**
 * Created by sunkai on 2017/12/13.
 */
class GlobalContext : Application() {
    private val APP_ID:String="2882303761517683469"
    private val APP_KEY:String="5391768355469"
    private val TAG:String="GlobalContext"


    override fun onCreate() {
        super.onCreate()
        instance = this

        val sharePrefrence=getSharedPreferences("setting",Context.MODE_PRIVATE)
        if(sharePrefrence.getBoolean("pushSwitch",false)) {
            registMipush() //注册mipush
        }
    }
    fun unregistMipush(){
        MiPushClient.unregisterPush(instance)
    }
    fun registMipush() {
        if (shouldInit()) {
            MiPushClient.registerPush(instance, APP_ID, APP_KEY)
        }
        //打开Log
        val newLogger = object : LoggerInterface {

            override fun setTag(tag: String) {
                // ignore
            }

            override fun log(content: String, t: Throwable) {
                Log.d(TAG, content, t)
            }

            override fun log(content: String) {
                Log.d(TAG, content)
            }
        }
        Logger.setLogger(this, newLogger)
    }

    fun registUser(){
        val userName:String?=getSharedPreferences("data",Context.MODE_PRIVATE).getString("user_name",null)
        userName?.let {
            MiPushClient.setUserAccount(instance,userName,null)
        }
    }

    fun unregistUser(){
        val userName:String?=getSharedPreferences("data",Context.MODE_PRIVATE).getString("user_name",null)
        userName?.let {
            MiPushClient.unsetUserAccount(instance,userName,null)
        }
    }

    fun pauseMiPush(){
        MiPushClient.pausePush(instance,null)
    }

    fun resumeMiPush(){
        MiPushClient.resumePush(instance,null)
    }

    private fun shouldInit(): Boolean {
        val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val processInfos = am.runningAppProcesses
        val mainProcessName = packageName
        val myPid = android.os.Process.myPid()
        for (info in processInfos) {
            if (info.pid == myPid && mainProcessName == info.processName) {
                return true
            }
        }
        return false
    }

    companion object {
        lateinit var instance: GlobalContext
            private set
    }
}