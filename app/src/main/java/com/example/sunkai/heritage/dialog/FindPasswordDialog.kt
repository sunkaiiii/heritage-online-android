package com.example.sunkai.heritage.dialog

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.example.sunkai.heritage.connectWebService.HandleUser
import com.example.sunkai.heritage.dialog.base.BaseDialogFragment
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.MakeToast.toast
import com.example.sunkai.heritage.tools.infoToRSA
import com.example.sunkai.heritage.value.NO_USER
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * 找回密码的dialog
 * Created by sunkai on 2018/3/5.
 */
class FindPasswordDialog : BaseDialogFragment() {

    class Holder(view: View) {
        val userName: EditText
        val passwordQuestion: EditText
        val passwordAnswer: EditText
        val submit: Button
        val cancel: Button

        init {
            userName = view.findViewById(R.id.find_password_username)
            passwordQuestion = view.findViewById(R.id.find_password_question)
            passwordAnswer = view.findViewById(R.id.find_password_answer)
            submit = view.findViewById(R.id.find_password_queding)
            cancel = view.findViewById(R.id.find_password_cancel)
        }
    }
    override fun getLayoutID(): Int {
        return R.layout.find_password
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val holder = Holder(view)
        holder.cancel.setOnClickListener { dismiss() }
        holder.submit.setOnClickListener {
            if (holder.passwordQuestion.visibility == View.GONE) {
                if (checkInputIsIlligle(holder)) {
                    val username = holder.userName.text.toString()
                    getFindPasswordQuestion(holder, username)
                }
            } else {
                if (checkAnswerIsIlligle(holder)) {
                    checkUserAnswer(holder)
                }
            }
        }
    }


    private fun checkInputIsIlligle(holder: Holder): Boolean {
        if (TextUtils.isEmpty(holder.userName.text)) {
            toast("用户名不能为空")
            return false
        }
        return true
    }

    private fun getFindPasswordQuestion(holder: Holder, userName: String) {
        GlobalScope.launch {
            val result = HandleUser.Find_Password_Question(userName) ?: return@launch
            val activity = activity ?: return@launch
            activity.runOnUiThread {
                if (result != NO_USER) {
                    showFindPasswordQuestion(holder, userName, result)
                } else {
                    toast("发生错误")
                }
            }
        }
    }

    private fun showFindPasswordQuestion(holder: Holder, userName: String, question: String) {
        holder.passwordQuestion.visibility = View.VISIBLE
        holder.passwordAnswer.visibility = View.VISIBLE
        holder.userName.setText(userName)
        holder.passwordQuestion.setText(question)
        holder.userName.isEnabled = false
    }

    private fun checkAnswerIsIlligle(holder: Holder): Boolean {
        if (TextUtils.isEmpty(holder.passwordAnswer.text)) {
            toast("答案不能为空")
            return false
        }
        return true
    }

    private fun checkUserAnswer(holder: Holder) {
        GlobalScope.launch {
            val activity = activity ?: return@launch
            val encrtAnswer = infoToRSA(holder.passwordAnswer.text.toString()) ?: return@launch
            val userName = holder.userName.text.toString()
            val result = HandleUser.Check_Question_Answer(userName, encrtAnswer)
            activity.runOnUiThread {
                if (result) {
                    dismiss()
                    val changePasswordDialog = ChangePasswordDialog()
                    val args = Bundle()
                    args.putString("username", userName)
                    changePasswordDialog.arguments = args
                    changePasswordDialog.show(fragmentManager!!, "修改密码")
                } else {
                    toast("找回密码答案有误")
                }
            }
        }
    }
}