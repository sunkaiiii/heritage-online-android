package com.example.sunkai.heritage.tools

import android.widget.Toast

import com.example.sunkai.heritage.Data.GlobalContext

/**
 * Created by sunkai on 2017/12/29.
 */

object MakeToast {
    fun MakeText(toastText: String) {
        Toast.makeText(GlobalContext.instance, toastText, Toast.LENGTH_SHORT).show()
    }
}
