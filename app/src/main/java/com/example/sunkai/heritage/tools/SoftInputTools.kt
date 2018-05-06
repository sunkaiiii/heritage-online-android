package com.example.sunkai.heritage.tools

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager

/**
 * 操作键盘的工具类
 * Created by sunkai on 2018/1/10.
 */
object SoftInputTools {
    fun hideKeyboard(activity: Activity) {
        val inputMethodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (inputMethodManager.isActive) {
            inputMethodManager.hideSoftInputFromWindow(activity.currentFocus.windowToken, 0)
        }
    }

//    //显示键盘
//    fun showKeyboard(context: Context) {
//        view?.let {
//            val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//            inputMethodManager.showSoftInput(view, 1)
//        }
//    }
}