package com.example.sunkai.heritage.Dialog

import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.example.sunkai.heritage.Dialog.Base.BaseDialogFragment
import com.example.sunkai.heritage.R

/**
 * 通用的提示框，用于替代AlertDialog
 * Created by sunkai on 2018/3/22.
 */
class NormalWarningDialog : BaseDialogFragment() {
    private var title = ""
    private var content = ""
    private var submitListener: onSubmitClickListener = object : onSubmitClickListener {
        override fun onSubmit(view: View, dialog: NormalWarningDialog) {
            dismiss()
        }

    }
    private var cancelListener: onCancelClickListener = object : onCancelClickListener {
        override fun onCanceled(view: View, dialog: NormalWarningDialog) {
            dismiss()
        }

    }

    class Holder(view: View) {
        val title: TextView
        val content: TextView
        val submit: TextView
        val cancel: TextView

        init {
            title = view.findViewById(R.id.warning_title)
            content = view.findViewById(R.id.warning_content)
            submit = view.findViewById(R.id.warning_submit)
            cancel = view.findViewById(R.id.warning_cancel)
        }
    }

    override fun getLayoutID(): Int {
        return R.layout.normal_warning_dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val holder = Holder(view)
        if(title.isEmpty()){
            holder.title.visibility=View.GONE
        }
        if(content.isEmpty()){
            holder.content.visibility=View.GONE
        }
        holder.title.text = title
        holder.content.text = content
        holder.submit.setOnClickListener { submitListener.onSubmit(holder.submit,this) }
        holder.cancel.setOnClickListener { cancelListener.onCanceled(holder.cancel,this) }
    }

    fun setOnSubmitClickListener(listener: onSubmitClickListener): NormalWarningDialog {
        this.submitListener = listener
        return this
    }

    fun setOnCancelClickListener(listener: onCancelClickListener): NormalWarningDialog {
        this.cancelListener = listener
        return this
    }

    fun setTitle(title: String): NormalWarningDialog {
        this.title = title
        return this
    }

    fun setContent(content: String): NormalWarningDialog {
        this.content = content
        return this
    }

    interface onSubmitClickListener {
        fun onSubmit(view: View,dialog: NormalWarningDialog)
    }

    interface onCancelClickListener {
        fun onCanceled(view: View,dialog: NormalWarningDialog)
    }
}