package com.example.sunkai.heritage.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.sunkai.heritage.adapter.baseAdapter.BaseRecyclerAdapter
import com.example.sunkai.heritage.entity.CommentReplyInformation
import com.example.sunkai.heritage.R
import java.lang.StringBuilder

/**
 * 帖子详情页RecyclerView的adapter
 * Created by sunkai on 2018/2/24.
 */
class UserCommentReplyRecyclerAdapter(context: Context, datas: List<CommentReplyInformation>, glide: RequestManager) : BaseRecyclerAdapter<UserCommentReplyRecyclerAdapter.ViewHodler, CommentReplyInformation>(context, datas, glide) {
    class ViewHodler(view: View) : RecyclerView.ViewHolder(view) {
        val userName: TextView = view.findViewById(R.id.reply_name)
        val replyContent: TextView = view.findViewById(R.id.reply_content)
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

    private fun setData(holder: ViewHodler, data: CommentReplyInformation) {
        holder.userName.text = StringBuilder().append(data.userName).append(":").toString()
        holder.replyContent.text = data.replyContent
    }

    fun reverseData() {
        datas = datas.asReversed()
        notifyDataSetChanged()
    }

    fun addData(data: CommentReplyInformation) {
        val mutabledata = datas.toMutableList()
        mutabledata.add(data)
        this.datas = mutabledata
        notifyDataSetChanged()
    }
}