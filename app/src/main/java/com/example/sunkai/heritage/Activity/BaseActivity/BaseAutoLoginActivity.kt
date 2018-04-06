package com.example.sunkai.heritage.Activity.BaseActivity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.sunkai.heritage.Activity.RegistActivity
import com.example.sunkai.heritage.Dialog.NormalWarningDialog
import com.example.sunkai.heritage.tools.MakeToast.toast
import com.example.sunkai.heritage.tools.ThreadPool
import com.example.sunkai.heritage.tools.backGroundLogin
import com.example.sunkai.heritage.tools.checkLogin
import com.example.sunkai.heritage.tools.gotoLogin

abstract class BaseAutoLoginActivity : AppCompatActivity(), onAutoLogin {
    var dialog: NormalWarningDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //容错，防止注册页无法进入的问题（注册页继承了BaseTakeCameraActivity)
        if(this is RegistActivity) return
        if (!checkLogin()) {
            showProcess()
            ThreadPool.execute {
                runOnUiThread {
                    if (backGroundLogin()) {
                        dismissProgress()
                        getInformation()
                    } else {
                        toast("没有登录，请重新登陆")
                        finish()
                        gotoLogin()
                    }
                }
            }
        }
    }

    private fun showProcess() {
        dialog = NormalWarningDialog()
                .setDisableTouchDismiss()
                .setSubmitVisible(false)
                .setCancelVisible(false)
                .setProgressVisibility(true)
                .setContent("载入中...")
        dialog?.show(supportFragmentManager, "loadUserInfoLoading")
    }

    private fun dismissProgress() {
        dialog?.dismiss()
    }

}

private interface onAutoLogin {
    fun getInformation() {}
}