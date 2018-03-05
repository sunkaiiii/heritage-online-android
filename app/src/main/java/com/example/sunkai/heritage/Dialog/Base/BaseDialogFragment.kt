package com.example.sunkai.heritage.Dialog.Base

import android.app.DialogFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window

/**Dialog的基类
 * Created by sunkai on 2018/3/5.
 */
abstract class BaseDialogFragment:android.support.v4.app.DialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return inflater.inflate(getLayoutID(),container)
    }

    abstract fun getLayoutID():Int
}