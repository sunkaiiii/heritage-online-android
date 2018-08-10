package com.example.sunkai.heritage.Service

import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.sunkai.heritage.Activity.LoginActivity.LoginActivity
import com.example.sunkai.heritage.Activity.MyMessageActivity
import com.example.sunkai.heritage.Activity.UserCommentDetailActivity
import com.example.sunkai.heritage.ConnectWebService.HandlePush
import com.example.sunkai.heritage.Data.PushMessageData
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.ThreadPool
import com.example.sunkai.heritage.value.HOST_IP
import com.example.sunkai.heritage.value.PUSH_PORT
import com.google.gson.Gson
import java.io.BufferedInputStream
import java.io.IOException
import java.lang.ref.WeakReference
import java.net.ConnectException
import java.net.Socket


class PushService : Service() {
    private lateinit var mNM: NotificationManager
    private val NOTIFICATION = R.string.app_name
    private val mBinder = LocalBinder()
    private var pushChannelID = -1
    private var socketRef: WeakReference<Socket>? = null
    private var bufferInputStream: BufferedInputStream? = null

    inner class LocalBinder : Binder() {
        internal val service: PushService
            get() = this@PushService
    }

    override fun onCreate() {
        mNM = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createAndroidONotificationChannel(mNM)
        }
        //第一次启动服务的时候，获取所有未读取的推送
        getPushMessage()
        ThreadPool.execute {
            try {
                initSocket()
            } catch (e: ConnectException) {
                e.printStackTrace()
                return@execute
            }
            handleSocketPushChannel(socketRef?.get())
        }
    }

    @Throws(ConnectException::class)
    private fun initSocket() {
        val socket = Socket(HOST_IP, PUSH_PORT)
        socketRef = WeakReference(socket)
    }

    private fun getPushMessage() {
        val userID = LoginActivity.userID
        if (userID == 0) return
        ThreadPool.execute {
            val resultList = HandlePush.GetPush(userID)
            if (resultList.isNotEmpty()) {
                val notifiCationStringList = ArrayList<String>()
                resultList.forEach {
                    notifiCationStringList.add(String.format("%s:%s\n", it.userName, it.replyContent))
                }
                val pendingIntent = PendingIntent.getActivity(this, 0, Intent(this, MyMessageActivity::class.java), 0)
                Log.d("notification", notifiCationStringList.toString())
                showNotification(notifiCationStringList.toTypedArray(), pendingIntent)
            }
        }
    }

    private fun handleSocketPushChannel(socket: Socket?) {
        socket ?: return
        if (LoginActivity.userID == 0) {
            socket.close()
            return
        }
        if (socket.isConnected) {
            sendUserIdToServer(socket)
        }
        socket.keepAlive = true
        var emtyTime = 0
        bufferInputStream = BufferedInputStream(socket.getInputStream())
        val buf = ByteArray(1024)
        while (socket.isConnected && emtyTime < 100) {
            try {
                val count = bufferInputStream?.read(buf)
                if (count ?: continue < 0) {
                    continue
                }
                val content = String(buf, 0, count)
                Log.d("content", content)
                if (content.isEmpty()) {
                    emtyTime++
                    continue
                }
                handlePushData(content)
            } catch (e: IOException) {
                e.printStackTrace()
                emtyTime++
            }
        }
    }


    private fun sendUserIdToServer(socket: Socket) {
        val bufferedReader = socket.getInputStream().bufferedReader()
        val getSocketID = bufferedReader.readLine()
        pushChannelID = getSocketID.toInt()
        val writer = socket.getOutputStream().bufferedWriter()
        writer.write(LoginActivity.userID.toString())
        writer.flush()
    }

    private fun handlePushData(content: String) {
        val pushMessageData = try {
            Gson().fromJson<PushMessageData>(content, PushMessageData::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            return
        }
        val intent = Intent(this@PushService, UserCommentDetailActivity::class.java)
        intent.putExtra("id", pushMessageData.replyCommentID)
        //这个flag非常重要，不添加的话将不会传递intent的信息
        val pendingIntent = PendingIntent.getActivity(this@PushService, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        showNotification(arrayOf(String.format("%s:%s", pushMessageData.userName, pushMessageData.replyContent)), pendingIntent)
    }

    private fun showNotification(content: Array<String>, contentIntent: PendingIntent) {
        val text = getString(R.string.new_reply)
        val inboxStyle = NotificationCompat.InboxStyle()
        inboxStyle.setBigContentTitle(text)
        content.forEach { inboxStyle.addLine(it) }
        val notificaiton = NotificationCompat.Builder(this, getString(R.string.push_channel))
                .setSmallIcon(R.mipmap.app_logo_image)
                .setTicker(text)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setStyle(inboxStyle)
                .build()

        mNM.notify(NOTIFICATION, notificaiton)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }


    override fun onBind(intent: Intent): IBinder? {
        return mBinder
    }

    private fun clearAllConnect() {
        try {
            if (socketRef?.get()?.isConnected == true) {
                socketRef?.get()?.close()
            }
            bufferInputStream?.close()
        }catch (e:Exception){
            e.printStackTrace()
        }
        bufferInputStream = null
        mNM.cancel(NOTIFICATION)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        clearAllConnect()
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        clearAllConnect()
        super.onDestroy()
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun createAndroidONotificationChannel(mNM: NotificationManager) {
        val channel = NotificationChannel(getString(R.string.push_channel), getString(R.string.push_channel_name), NotificationManager.IMPORTANCE_DEFAULT)
        channel.enableLights(true)
        channel.lightColor = getColor(R.color.colorPrimary)
        channel.setShowBadge(true) //是否显示通知角标
        mNM.createNotificationChannel(channel)
    }

}
