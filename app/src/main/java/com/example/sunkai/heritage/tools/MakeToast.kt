package com.example.sunkai.heritage.tools

import android.app.Activity
import android.content.Context
import android.widget.Toast

/**全局可调用的toast工具类
 * Created by sunkai on 2017/12/29.
 */

object MakeToast {
    fun MakeText(toastText: String) {
        Toast.makeText(GlobalContext.instance, toastText, Toast.LENGTH_SHORT).show()
    }

    fun Activity.toast(toastText: String)=MakeText(toastText)

    fun Context.toast(toastText: String)= MakeText(toastText)

    fun Any.toast(toastText: String)= MakeText(toastText)
}
