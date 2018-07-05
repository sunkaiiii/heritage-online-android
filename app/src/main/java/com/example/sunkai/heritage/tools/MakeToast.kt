package com.example.sunkai.heritage.tools

import android.annotation.SuppressLint
import android.app.Application
import android.widget.Toast

/**全局可调用的toast工具类
 * Created by sunkai on 2017/12/29.
 */

object MakeToast {
    private var toastInstance: Toast? = null

    @Synchronized
    fun toast(toastText: String) {
        toastInstance?.setText(toastText)
        toastInstance?.show()
    }

    @Synchronized
    fun toast(resID: Int) {
        if (resID < 0) {
            return
        }
        toastInstance?.setText(resID)
        toastInstance?.show()
    }


    @SuppressLint("ShowToast")
    fun initToast(application: Application) {
        toastInstance = Toast.makeText(application.applicationContext, "", Toast.LENGTH_SHORT)
    }

}
