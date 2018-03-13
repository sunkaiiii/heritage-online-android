package com.example.sunkai.heritage.Dialog.Base

import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.example.sunkai.heritage.Interface.OnDialogDismiss

/**Dialog的基类
 * Created by sunkai on 2018/3/5.
 */
abstract class BaseDialogFragment:DialogFragment() {
    private var listner:OnDialogDismiss?=null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return inflater.inflate(getLayoutID(),container)
    }

    abstract fun getLayoutID():Int
    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        listner?.onDialogDismiss()
    }

    fun setOnDialogMissListner(listener: OnDialogDismiss){
        this.listner=listener
    }
}