package com.example.sunkai.heritage.Service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import com.example.sunkai.heritage.Activity.LoginActivity.LoginActivity
import com.example.sunkai.heritage.Activity.UserOwnTieziActivity
import com.example.sunkai.heritage.ConnectWebService.HandlePush
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.ThreadPool
import com.example.sunkai.heritage.tools.runOnUiThread


class PushService : Service() {
    private lateinit var mNM: NotificationManager
    private val NOTIFICATION = R.string.app_name
    private val mBinder = LocalBinder()

    inner class LocalBinder : Binder() {
        internal val service: PushService
            get() = this@PushService
    }

    override fun onCreate() {
        mNM = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createAndroidONotificationChannel(mNM)
        }
        getPushMessage()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createAndroidONotificationChannel(mNM: NotificationManager) {
        val channel = NotificationChannel(getString(R.string.push_channel), getString(R.string.push_channel_name), NotificationManager.IMPORTANCE_DEFAULT)
        channel.enableLights(true)
        channel.lightColor = getColor(R.color.colorPrimary)
        channel.setShowBadge(true)
        mNM.createNotificationChannel(channel)
    }

    private fun setGetMessageTimer() {
        Handler().postDelayed({
            getPushMessage()
        }, getTime())
    }

    private fun getTime(): Long = getSharedPreferences("setting", Context.MODE_PRIVATE).getLong("push_time", 500000)

    private fun getPushMessage() {
        val userID=LoginActivity.userID
        if(userID==0)return
        ThreadPool.execute {
            val resultList=HandlePush.GetPush(userID)
            if (resultList.isNotEmpty()){
                runOnUiThread(Runnable {
                    showNotification(resultList[0].replyContent)
                })
            }
        }
        setGetMessageTimer()
    }

    private fun showNotification(content:String) {
        val text = getString(R.string.new_reply)

        val contentIntent = PendingIntent.getActivity(this, 0, Intent(this, UserOwnTieziActivity::class.java), 0)

        val notificaiton = NotificationCompat.Builder(this,getString(R.string.push_channel))
                .setSmallIcon(R.mipmap.app_logo_image)
                .setTicker(text)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(content)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setContentText(content)
                .build()

        mNM.notify(NOTIFICATION, notificaiton)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        mNM.cancel(NOTIFICATION)
    }

    override fun onBind(intent: Intent): IBinder? {
        return mBinder
    }
}
