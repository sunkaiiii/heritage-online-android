package com.example.sunkai.heritage.tools

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

/**
 * 操作键盘的工具类
 * Created by sunkai on 2018/1/10.
 */
object SoftInputTools{
    fun Context.hideKeyboard(view:View) {
        view.let {
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    //显示键盘
    fun Context.showKeyboard(view: View) {
        view.let {
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(view, 1)
        }
    }
}