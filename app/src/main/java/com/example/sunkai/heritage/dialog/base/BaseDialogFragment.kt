package com.example.sunkai.heritage.dialog.base

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.example.sunkai.heritage.interfaces.OnDialogDismiss
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.forEachAndTintViews

/**Dialog的基类
 * Created by sunkai on 2018/3/5.
 */
abstract class BaseDialogFragment : DialogFragment() {
    private var listner: OnDialogDismiss? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_AppCompat_Light_Dialog_Alert)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return inflater.inflate(getLayoutID(), container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (view is ViewGroup) {
            forEachAndTintViews(view)
        }
    }

    abstract fun getLayoutID(): Int
    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        listner?.onDialogDismiss(this)
    }

    fun setOnDialogMissListner(listener: OnDialogDismiss) {
        this.listner = listener
    }
}