package com.example.sunkai.heritage.Dialog

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
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
    private var progressVisibility=false
    private var submitVisibility=true
    private var cancelVIsibility=true
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
        val progressBar:ProgressBar

        init {
            title = view.findViewById(R.id.warning_title)
            content = view.findViewById(R.id.warning_content)
            submit = view.findViewById(R.id.warning_submit)
            cancel = view.findViewById(R.id.warning_cancel)
            progressBar=view.findViewById(R.id.warning_progress)
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
        holder.progressBar.visibility=progressVisibility.toViewVisible()
        holder.submit.visibility=submitVisibility.toViewVisible()
        holder.cancel.visibility=cancelVIsibility.toViewVisible()
        holder.submit.setOnClickListener { submitListener.onSubmit(holder.submit,this) }
        holder.cancel.setOnClickListener { cancelListener.onCanceled(holder.cancel,this) }
    }

    private fun visibilityHandler(visible:Boolean):Int{
        return if(visible)View.VISIBLE else View.GONE
    }

    private fun Boolean.toViewVisible():Int{
        return visibilityHandler(this)
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

    fun setProgressVisibility(boolean: Boolean):NormalWarningDialog{
        this.progressVisibility=boolean
        return this
    }

    fun setSubmitVisible(visible: Boolean):NormalWarningDialog{
        this.submitVisibility=visible
        return this
    }

    fun setCancelVisible(visible: Boolean):NormalWarningDialog{
        this.cancelVIsibility=visible
        return this
    }

    fun setDisableTouchDismiss():NormalWarningDialog{
        this.isCancelable=false
        return this
    }

    interface onSubmitClickListener {
        fun onSubmit(view: View,dialog: NormalWarningDialog)
    }

    interface onCancelClickListener {
        fun onCanceled(view: View,dialog: NormalWarningDialog)
    }
}