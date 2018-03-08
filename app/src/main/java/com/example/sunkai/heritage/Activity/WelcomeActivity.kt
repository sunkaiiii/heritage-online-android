package com.example.sunkai.heritage.Activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Window
import android.view.WindowManager
import androidx.content.edit
import com.example.sunkai.heritage.Activity.LoginActivity.LoginActivity
import com.example.sunkai.heritage.Data.GlobalContext
import com.example.sunkai.heritage.Dialog.PushDialog
import com.example.sunkai.heritage.Interface.OnDialogDismiss
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.ThreadPool

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

        val sharedPreferences = getSharedPreferences("setting", Context.MODE_PRIVATE)
        var startCount = sharedPreferences.getInt("startCount", 0)
        if (startCount == 0) {
            startCount++
            getSharedPreferences("setting", Context.MODE_PRIVATE).edit {
                putInt("startCount", startCount)
                putBoolean("pushSwitch", false)
            }
            showDialog()

        } else {
            goToLogin(gotoLogin)
        }
    }

    private fun showDialog() {
        val dialog = PushDialog()
        dialog.setOnDialogMissListner(object :OnDialogDismiss{
            override fun onDialogDismiss() {
                goToLogin(gotoLogin)
            }
        })
        dialog.show(supportFragmentManager, "开启推送？")
    }

    private fun goToLogin(what: Int) {
        ThreadPool.execute {
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
        }
    }

    companion object {
        private const val gotoLogin = 0
    }
}
