package com.example.sunkai.heritage.dialog

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.content.edit
import com.example.sunkai.heritage.activity.loginActivity.LoginActivity
import com.example.sunkai.heritage.connectWebService.HandleFind
import com.example.sunkai.heritage.entity.CommentReplyInformation
import com.example.sunkai.heritage.entity.UserCommentData
import com.example.sunkai.heritage.dialog.base.BaseBottomDialog
import com.example.sunkai.heritage.interfaces.AddUserReplyDialog
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.MakeToast.toast
import com.example.sunkai.heritage.value.ADD_COMMENT_REPLY
import com.example.sunkai.heritage.value.ERROR
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * 回帖的dialog
 * Created by sunkai on 2018/2/25.
 */
class AddUserCommentBottomDialog(val context: Activity, private val commentID: Int, private val data: UserCommentData,private val themeColor:Int) : BaseBottomDialog(context), AddUserReplyDialog {

    private var onAddUserReplyListener: AddUserReplyDialog? = null
    private var isSuccessUpload = false

    init {
        setContentView(R.layout.add_usercomment_reply_dialog)
        initViews()
        this.setOnCancelListener {
            if (!isSuccessUpload) {
                saveReplyContent()
            }
        }
    }

    class Holder(view: View) {
        val replyContent: EditText = view.findViewById(R.id.add_reply_content)
        val btnSend: ImageButton = view.findViewById(R.id.add_comment_btn_send)
    }

    private fun initViews() {
        val view = view
        if (view != null) {
            val holder = Holder(view)
            val shape = OvalShape()
            val shapeDrawable = ShapeDrawable(shape)
            shapeDrawable.paint.color = themeColor
            holder.btnSend.background = shapeDrawable
            holder.btnSend.setOnClickListener {
                if (checkUserLogin() and checkTextIsLegal(holder)) {
                    setViewState(holder, false)
                    addReply(holder)
                }
            }
            holder.replyContent.setText(context.getSharedPreferences(ADD_COMMENT_REPLY, Context.MODE_PRIVATE).getString(ADD_COMMENT_REPLY, ""))
            holder.replyContent.setSelection(holder.replyContent.text.length)
        }
    }

    private fun setViewState(holder: Holder, state: Boolean) {
        holder.btnSend.isEnabled = state
        holder.replyContent.isEnabled = state
    }

    private fun checkTextIsLegal(holder: Holder): Boolean {
        val text = holder.replyContent.text.toString()
        if (TextUtils.isEmpty(text.trim())) {
            toast("回复内容不能为空")
            return false
        }
        return true
    }

    private fun addReply(holder: Holder) {
        val replyContent = holder.replyContent.text.toString()
        GlobalScope.launch {
            val result = HandleFind.AddUserCommentReply(LoginActivity.userID, commentID, replyContent, data.userID, data.userName, data.commentContent)
            context.runOnUiThread {
                if (result != ERROR) {
                    val replyID = result.toInt()
                    val userName = LoginActivity.userName ?: ""
                    val data = CommentReplyInformation(replyID, "", commentID, LoginActivity.userID, userName, replyContent)
                    toast("回复成功")
                    isSuccessUpload = true
                    cancel()
                    context.getSharedPreferences(ADD_COMMENT_REPLY, Context.MODE_PRIVATE).edit().clear().apply()
                    onAddUserReplySuccess(data)
                } else {
                    toast("出现错误")
                }
                setViewState(holder, true)
            }
        }
    }

    private fun saveReplyContent() {
        val replyContent = findViewById<EditText>(R.id.add_reply_content)
        if (replyContent != null) {
            context.getSharedPreferences(ADD_COMMENT_REPLY, Context.MODE_PRIVATE).edit {
                putString(ADD_COMMENT_REPLY, replyContent.text.toString())
            }
        }
    }

    override fun onAddUserReplySuccess(data: CommentReplyInformation) {
        onAddUserReplyListener?.onAddUserReplySuccess(data)
    }

    fun setOnAddUserReplyListener(listner: AddUserReplyDialog) {
        this.onAddUserReplyListener = listner
    }

    private fun checkUserLogin(): Boolean {
        if (LoginActivity.userID == 0) {
            Toast.makeText(context, "没有登录", Toast.LENGTH_SHORT).show()
            val intent = Intent(context, LoginActivity::class.java)
            intent.putExtra("isInto", 1)
            context.startActivity(intent)
            return false
        }
        return true
    }

}