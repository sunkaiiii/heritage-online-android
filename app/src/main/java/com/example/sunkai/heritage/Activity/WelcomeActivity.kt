package com.example.sunkai.heritage.Activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Window
import android.view.WindowManager
import androidx.core.content.edit
import com.example.sunkai.heritage.Activity.LoginActivity.LoginActivity
import com.example.sunkai.heritage.Dialog.PushDialog
import com.example.sunkai.heritage.Interface.OnDialogDismiss
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.MakeToast.toast
import com.example.sunkai.heritage.tools.ThreadPool
import com.example.sunkai.heritage.tools.attempToLogin
import com.example.sunkai.heritage.value.FIRST_OPEN
import com.example.sunkai.heritage.value.NOT_FIRST_OPEN
import com.example.sunkai.heritage.value.NOT_LOGIN

/**
 * 此页面是欢迎界面的类
 */
class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setBackgroundDrawable(null)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_welcome)
        //判断是否是第一次启动，如果是第一次启动则显示是否打开推送的弹窗
        if (getSharedPreferences("setting", Context.MODE_PRIVATE).getInt("startCount", FIRST_OPEN) == FIRST_OPEN) {
            getSharedPreferences("setting", Context.MODE_PRIVATE).edit {
                putInt("startCount", NOT_FIRST_OPEN)
                putBoolean("pushSwitch", false)
            }
            showDialog()

        } else {
            goToLogin()
        }
    }

    private fun showDialog() {
        val dialog = PushDialog()
        dialog.setOnDialogMissListner(object : OnDialogDismiss {
            override fun onDialogDismiss() {
                goToLogin()
            }
        })
        dialog.show(supportFragmentManager, "开启推送？")
    }

    private fun goToLogin() {
        ThreadPool.execute {
            //首先，获取自动登录的信息
            val username = getSharedPreferences("data", Context.MODE_PRIVATE).getString("user_name", null)
            val userPassword = getSharedPreferences("data", Context.MODE_PRIVATE).getString("user_password", null)
            //尝试尝试自动登录
            val result = attempToLogin(username, userPassword)
            runOnUiThread {
                handleLogin(result, username)
            }
        }
    }

    private fun handleLogin(autoLoginSuccess: Boolean, username: String?) {
        val intent: Intent
        if (autoLoginSuccess) {
            LoginActivity.userName = username
            intent = Intent(this@WelcomeActivity, MainActivity::class.java)
        } else {
            //如果自动登录失败，但存有自动登录用户信息，则提示密码过期
            if (username != null) {
                toast("您的密码已过期，请重新登陆")
            }
            LoginActivity.userID = NOT_LOGIN
            intent = Intent(this@WelcomeActivity, LoginActivity::class.java)
        }
        startActivity(intent)
        finish()
    }
}


