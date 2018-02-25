package com.example.sunkai.heritage.Adapter

import android.app.Activity
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import at.markushi.ui.CircleButton
import com.example.sunkai.heritage.Activity.LoginActivity
import com.example.sunkai.heritage.Activity.UserCommentDetailActivity
import com.example.sunkai.heritage.Adapter.BaseAdapter.BaseBottomDialog
import com.example.sunkai.heritage.ConnectWebService.HandleFindNew
import com.example.sunkai.heritage.Data.CommentReplyInformation
import com.example.sunkai.heritage.Data.GlobalContext
import com.example.sunkai.heritage.Interface.AddUserReplyDialog
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.MakeToast.toast
import com.example.sunkai.heritage.value.ERROR

/**
 * 回帖的dialog
 * Created by sunkai on 2018/2/25.
 */
class AddUserCommentBottomDialog(val context:Activity,val commentID:Int): BaseBottomDialog(context),AddUserReplyDialog {
    init {
        setContentView(R.layout.add_usercomment_reply_dialog)
        initViews()
    }
    private var onAddUserReplyListener:AddUserReplyDialog?=null
    class Holder(view:View){
        val replyContent:EditText
        val btnSend:CircleButton
        init {
            replyContent=view.findViewById(R.id.add_reply_content)
            btnSend=view.findViewById(R.id.add_comment_btn_send)
        }
    }
    private fun initViews(){
        val view=view
        if(view!=null){
            val holder=Holder(view)
            holder.btnSend.setOnClickListener{
                if(checkUserLogin() and checkTextIsLegal(holder)) {
                    setViewState(holder,false)
                    addReply(holder)
                }
            }
        }
    }

    private fun setViewState(holder: Holder,state:Boolean){
        holder.btnSend.isEnabled=state
        holder.replyContent.isEnabled=state
    }

    private fun checkTextIsLegal(holder: Holder):Boolean{
        val text=holder.replyContent.text.toString()
        if(TextUtils.isEmpty(text.trim())){
            toast("回复内容不能为空")
            return false
        }
        return true
    }
    private fun addReply(holder: Holder){
        val replyContent=holder.replyContent.text.toString()
        val uriString=generateInentString()
        Thread{
            val result=HandleFindNew.AddUserCommentReply(LoginActivity.userID,commentID,replyContent,uriString)
            context.runOnUiThread {
                if(result!= ERROR){
                    val replyID=result.toInt()
                    val userName= LoginActivity.userName ?: ""
                    val data=CommentReplyInformation(replyID,"",commentID,LoginActivity.userID,userName,replyContent)
                    toast("回复成功")
                    cancel()
                    onAddUserReplySuccess(data)
                }else{
                    toast("出现错误")
                }
                setViewState(holder,true)
            }
        }.start()
    }

    override fun onAddUserReplySuccess(data: CommentReplyInformation) {
        onAddUserReplyListener?.onAddUserReplySuccess(data)
    }

    fun setOnAddUserReplyListener(listner:AddUserReplyDialog){
        this.onAddUserReplyListener=listner
    }

    private fun generateInentString():String{
        val intent = Intent(GlobalContext.instance, UserCommentDetailActivity::class.java)
        intent.putExtra("id", commentID)
        val uriString = intent.toUri(Intent.URI_INTENT_SCHEME)
        Log.d("addCommentDialog", "uriString: " + uriString)
        return uriString
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