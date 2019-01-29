package com.example.sunkai.heritage.dialog

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.example.sunkai.heritage.activity.loginActivity.LoginActivity
import com.example.sunkai.heritage.connectWebService.HandleUser
import com.example.sunkai.heritage.dialog.base.BaseDialogFragment
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.MakeToast.toast
import com.example.sunkai.heritage.tools.infoToRSA
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * 修改密码的dialog(2018/3/5添加，根据阿里巴巴Android开发手册推荐，改为DialogFragment管理dialog）
 * Created by sunkai on 2018/3/2.
 */
class ChangePasswordDialog: BaseDialogFragment() {
    private class Holder(view: View){
        val userName: EditText
        val password: EditText
        val insure: EditText
        val submit: Button
        val cancel: Button
        init {
            userName= view.findViewById(R.id.change_password_name)
            password= view.findViewById(R.id.change_password_password)
            insure= view.findViewById(R.id.change_password_insure)
            submit= view.findViewById(R.id.change_password_queding)
            cancel= view.findViewById(R.id.change_password_cancel)
        }
    }

    override fun getLayoutID(): Int {
        return R.layout.change_password
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val holder=Holder(view)
        val username=arguments?.getString("username")?: LoginActivity.userName?:return
        holder.userName.setText(username)
        holder.cancel.setOnClickListener { dialog.dismiss() }
        holder.submit.setOnClickListener{
            if(username == "test"){
                toast("测试账号不允许修改密码")
                return@setOnClickListener
            }
            if(checkInputIsIlligle(holder)){
                holder.submit.isEnabled=false
                changePassword(holder,username)
            }
        }
    }

    private fun checkInputIsIlligle(holder: Holder):Boolean{
        if (TextUtils.isEmpty(holder.password.text) || TextUtils.isEmpty(holder.insure.text)) {
            toast("密码不能为空")
            return false
        }
        if (holder.password.text.toString() != holder.insure.text.toString()) {
            toast("密码输入不一致")
            return false
        }
        return true
    }


    private fun changePassword(holder: Holder,userName:String){
        GlobalScope.launch {
            val encryPassword = infoToRSA(holder.password.text.toString())
            if(encryPassword==null){
                holder.submit.isEnabled = true
                return@launch
            }
            val result = HandleUser.Change_Password(userName, encryPassword)
            activity?.runOnUiThread({
                if (result) {
                    toast("修改成功")
                    if (dialog.isShowing) {
                        dialog.dismiss()
                    } else {
                        toast("修改失败，请稍后再试")
                        holder.submit.isEnabled = true
                    }
                }
            })
        }
    }
}