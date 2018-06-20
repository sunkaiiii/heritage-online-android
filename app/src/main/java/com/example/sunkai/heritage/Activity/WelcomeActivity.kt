package com.example.sunkai.heritage.Activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.core.content.edit
import com.example.sunkai.heritage.Activity.BaseActivity.BaseGlideActivity
import com.example.sunkai.heritage.Activity.LoginActivity.LoginActivity
import com.example.sunkai.heritage.Dialog.PushDialog
import com.example.sunkai.heritage.Interface.OnDialogDismiss
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.MakeToast.toast
import com.example.sunkai.heritage.tools.WindowHelper
import com.example.sunkai.heritage.tools.attempToLogin
import com.example.sunkai.heritage.value.*

/**
 * 此页面是欢迎界面的类
 */
class WelcomeActivity : BaseGlideActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setBackgroundDrawable(null)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_welcome)
        WindowHelper.setWindowFullScreen(this)
        //判断是否是第一次启动，如果是第一次启动则显示是否打开推送的弹窗
        if (getSharedPreferences(SETTING, Context.MODE_PRIVATE).getInt(START_COUNT, FIRST_OPEN) == FIRST_OPEN) {
            getSharedPreferences(SETTING, Context.MODE_PRIVATE).edit {
                putInt(START_COUNT, NOT_FIRST_OPEN)
                putBoolean(PUSH_SWITCH, false)
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
        requestHttp {
            //首先，获取自动登录的信息
            val username = getSharedPreferences(DATA, Context.MODE_PRIVATE).getString(SHARE_PREFRENCE_USERNAME, null)
            val userPassword = getSharedPreferences(DATA, Context.MODE_PRIVATE).getString(SHARE_PREFRENCE_PASSWORD, null)
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


