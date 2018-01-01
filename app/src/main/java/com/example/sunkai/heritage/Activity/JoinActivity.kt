package com.example.sunkai.heritage.Activity

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

import com.example.sunkai.heritage.ConnectWebService.HandleFolk
import com.example.sunkai.heritage.Data.FolkData
import com.example.sunkai.heritage.R

/**
 * 此类用来处理预约界面
 */

class JoinActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var join_activity_img: ImageView
    private lateinit var join_activity_title: TextView
    private lateinit var join_activity_content: TextView
    private lateinit var join_activity_btn: Button
    private var folkActiviyData: FolkData? = null
    internal var accentColor: Int = 0
    internal var isOrderd: Boolean = false//判断是否已经预约了

    internal var CheckUserOrderThread: Runnable = Runnable {
        val result = HandleFolk.Check_User_Order(LoginActivity.userID, folkActiviyData!!.id!!)
        CheckUserOrderHandler.sendEmptyMessage(result)
    }

    internal var AddUserOrderThread: Runnable = Runnable {
        val isSuccess= HandleFolk.Add_User_Order(LoginActivity.userID, folkActiviyData!!.id!!)
        val msg = Message()
        if (isSuccess) {
            msg.what = 1
            AddUserOrderHandler.sendMessage(msg)
        } else {
            msg.what = 0
            AddUserOrderHandler.sendMessage(msg)
        }
    }

    internal var CancelOrderThred: Runnable = Runnable {
        val isSuccess = HandleFolk.Cancel_User_Order(LoginActivity.userID, folkActiviyData!!.id!!)
        if (isSuccess) {
            CancelOrderHandler.sendEmptyMessage(1)
        } else {
            CancelOrderHandler.sendEmptyMessage(0)
        }
    }

    internal var CheckUserOrderHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            join_activity_btn.isEnabled = true
            if (msg.what == 1) {
                isOrderd = false
                changeButton()
            } else {
                isOrderd = true
                changeButton()
            }
        }
    }

    internal var AddUserOrderHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            if (msg.what == 1) {
                Toast.makeText(this@JoinActivity, "预约成功", Toast.LENGTH_SHORT).show()
                isOrderd = false
                changeButton()
            } else {
                Toast.makeText(this@JoinActivity, "预约失败", Toast.LENGTH_SHORT).show()
            }
        }
    }

    internal var CancelOrderHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            if (msg.what == 1) {
                Toast.makeText(this@JoinActivity, "取消成功", Toast.LENGTH_SHORT).show()
                isOrderd = true
                changeButton()
                val intent = Intent("android.intent.action.cancelOrderBroadCast")
                intent.putExtra("message", "changed")
                sendBroadcast(intent)
            } else {
                Toast.makeText(this@JoinActivity, "取消失败", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)
        initView()
        folkActiviyData = intent.getSerializableExtra("activity") as FolkData
        folkActiviyData?.image?.let {
            val bitmap = BitmapFactory.decodeByteArray(folkActiviyData!!.image, 0, folkActiviyData!!.image!!.size)
            join_activity_img.setImageBitmap(bitmap)
        }
        join_activity_title.text = folkActiviyData!!.title
        join_activity_content.text = folkActiviyData!!.content
        /**
         * 在页面显示的时候判断此用户是否已经预约此活动
         */
        join_activity_btn.isEnabled = false
        Thread(CheckUserOrderThread).start()
    }

    private fun initView() {
        join_activity_img = findViewById(R.id.join_activity_img)
        join_activity_title = findViewById(R.id.join_activity_title)
        join_activity_content = findViewById(R.id.join_activity_content)
        join_activity_btn = findViewById(R.id.join_activity_btn)
        accentColor = join_activity_btn.currentTextColor
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        join_activity_btn.setOnClickListener(this)

        join_activity_img.scaleType = ImageView.ScaleType.FIT_XY


    }

    override fun onClick(v: View) {
        when (v.id) {
        /**
         * 根据用户是否已经预约，执行取消预约、预约
         */
            R.id.join_activity_btn -> {
                if (LoginActivity.userID == 0) {
                    Toast.makeText(this@JoinActivity, "没有登录", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@JoinActivity, LoginActivity::class.java)
                    intent.putExtra("isInto", 1)
                    startActivity(intent)
                    return
                }
                if (isOrderd) {
                    Thread(AddUserOrderThread).start()
                } else {
                    Thread(CancelOrderThred).start()
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun changeButton() {
        if (isOrderd) {
            join_activity_btn.setTextColor(accentColor)
            join_activity_btn.text = "立即预约"
        } else {
            join_activity_btn.setTextColor(Color.GRAY)
            join_activity_btn.text = "已预约,点击取消"
        }
    }

    override fun onPostResume() {
        super.onPostResume()
        join_activity_btn.isEnabled = false
        Thread(CheckUserOrderThread).start()
    }
}
