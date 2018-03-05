package com.example.sunkai.heritage.Dialog

import android.app.Activity
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.example.sunkai.heritage.Activity.LoginActivity
import com.example.sunkai.heritage.ConnectWebService.HandleUser
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.MakeToast
import com.example.sunkai.heritage.tools.MakeToast.toast
import com.example.sunkai.heritage.tools.ThreadPool
import com.example.sunkai.heritage.tools.infoToRSA

/**
 * 修改密码的dialog
 * Created by sunkai on 2018/3/2.
 */
class ChangePasswordDialog(val context: Activity) {
    private val ad:AlertDialog
    init {
        val builder= AlertDialog.Builder(context).setTitle("修改密码").setView(R.layout.change_password)
        ad = builder.create()
    }

    fun show(){
        ad.show()
        val holder=Holder(ad)
        holder.userName?.setText(LoginActivity.userName)
        holder.cancel?.setOnClickListener { ad.dismiss() }
        holder.submit?.setOnClickListener(View.OnClickListener {
            if (TextUtils.isEmpty(holder.password?.text) || TextUtils.isEmpty(holder.insure?.text)) {
                toast("密码不能为空")
                return@OnClickListener
            }
            if (holder.password?.text.toString() != holder.insure?.text.toString()) {
                toast("密码输入不一致")
                return@OnClickListener
            }
            holder.submit.isEnabled = false
            changePassword(holder)
        })
    }

    private class Holder(ad: AlertDialog){
        val userName: EditText?
        val password: EditText?
        val insure: EditText?
        val submit: Button?
        val cancel: Button?
        init {
            userName= ad.findViewById(R.id.change_password_name)
            password= ad.findViewById(R.id.change_password_password)
            insure= ad.findViewById(R.id.change_password_insure)
            submit= ad.findViewById(R.id.change_password_queding)
            cancel= ad.findViewById(R.id.change_password_cancel)
        }
    }

    private fun changePassword(holder: Holder){
        ThreadPool.execute {
            val encryPassword = infoToRSA(holder.password?.text.toString()) ?: return@execute
            val name = LoginActivity.userName ?: return@execute
            val result = HandleUser.Change_Password(name, encryPassword)
            context.runOnUiThread({
                if (result) {
                    MakeToast.MakeText("修改成功")
                    if (ad.isShowing) {
                        ad.dismiss()
                    } else {
                        MakeToast.MakeText("修改失败，请稍后再试")
                        holder.submit?.isEnabled = true
                    }
                }
            })
        }
    }
}