package com.example.sunkai.heritage.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.sunkai.heritage.Adapter.BaseAdapter.BaseRecyclerAdapter
import com.example.sunkai.heritage.Data.CommentReplyInformation
import com.example.sunkai.heritage.R
import java.util.*

/**
 * 帖子详情页RecyclerView的adapter
 * Created by sunkai on 2018/2/24.
 */
class UserCommentReplyRecyclerAdapter(val context: Context, datas: List<CommentReplyInformation>) : BaseRecyclerAdapter<UserCommentReplyRecyclerAdapter.ViewHodler, CommentReplyInformation>(datas) {
    class ViewHodler(view: View) : RecyclerView.ViewHolder(view) {
        val userName: TextView
        val replyContent: TextView

        init {
            userName = view.findViewById(R.id.reply_name)
            replyContent = view.findViewById(R.id.reply_content)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHodler {
        val view = LayoutInflater.from(context).inflate(R.layout.user_comment_reply_information, parent, false)
        view.setOnClickListener(this)
        return ViewHodler(view)
    }

    override fun onBindViewHolder(holder: ViewHodler, position: Int) {
        super.onBindViewHolder(holder, position)
        val data = getItem(position)
        setData(holder, data)
    }

    @SuppressLint("SetTextI18n")
    private fun setData(holder: ViewHodler, data: CommentReplyInformation) {
        holder.userName.text = data.userName + ":"
        holder.replyContent.text = data.replyContent
    }

    fun reverseData() {
        Collections.reverse(datas)
        notifyDataSetChanged()
    }

    fun addData(data: CommentReplyInformation) {
        val mutabledata = datas.toMutableList()
        mutabledata.add(data)
        this.datas = mutabledata
        notifyDataSetChanged()
    }
}