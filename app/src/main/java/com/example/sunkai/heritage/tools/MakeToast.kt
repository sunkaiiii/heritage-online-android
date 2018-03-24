package com.example.sunkai.heritage.tools

import android.widget.Toast

/**全局可调用的toast工具类
 * Created by sunkai on 2017/12/29.
 */

object MakeToast {
    fun MakeText(toastText: String) {
        Toast.makeText(GlobalContext.instance, toastText, Toast.LENGTH_SHORT).show()
    }

    fun toast(toastText: String)= MakeText(toastText)
}
