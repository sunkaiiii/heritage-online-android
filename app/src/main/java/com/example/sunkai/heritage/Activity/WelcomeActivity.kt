package com.example.sunkai.heritage.Activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.bumptech.glide.Glide

import com.example.sunkai.heritage.Data.GlobalContext
import com.example.sunkai.heritage.R

/**
 * 此页面是欢迎界面的类
 */
class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_welcome)
        val sharedPreferences = getSharedPreferences("setting", Context.MODE_PRIVATE)
        var startCount = sharedPreferences.getInt("startCount", 0)
        if (startCount == 0) {
            startCount++
            val editor = getSharedPreferences("setting", Context.MODE_PRIVATE).edit()
            editor.putInt("startCount", startCount)
            editor.putBoolean("pushSwitch", false)
            editor.apply()
            val view = View.inflate(this, R.layout.push_warining_layout, null)
            val ad = AlertDialog.Builder(this).setTitle("是否开启推送？")
                    .setView(view)
                    .setPositiveButton("开启") { _, _ ->
                        editor.putBoolean("pushSwitch", true)
                        editor.apply()
                        GlobalContext.instance.registMipush()
                    }
                    .setNegativeButton("关闭") { _, _ -> }
                    .create()
            ad.setOnDismissListener { goToLogin(gotoLogin) }
            ad.show()
        } else {
            goToLogin(gotoLogin)
        }
    }

    private fun goToLogin(what: Int) {
        Thread {
            try {
                Thread.sleep(1000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            runOnUiThread {
                val sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE)
                LoginActivity.userID = sharedPreferences.getInt("user_id", 0)
                LoginActivity.userName = sharedPreferences.getString("user_name", null)
                if (what == gotoLogin) {
                    val intent: Intent
                    when (LoginActivity.userID) {
                        0 -> {
                            intent = Intent(this@WelcomeActivity, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        else -> {
                            intent = Intent(this@WelcomeActivity, MainActivity::class.java)
                            GlobalContext.instance.registUser()
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            }
        }.start()
    }
    companion object {
        private val gotoLogin = 0
    }
}
