package com.example.sunkai.heritage.Adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.RequestManager
import com.example.sunkai.heritage.Adapter.BaseAdapter.BaseRecyclerAdapter
import com.example.sunkai.heritage.ConnectWebService.HandlePerson
import com.example.sunkai.heritage.Data.PushMessageData
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.ThreadPool
import com.example.sunkai.heritage.tools.runOnUiThread
import com.example.sunkai.heritage.value.ERROR

class MyMessageRecyclerAdapter(context:Context,data:List<PushMessageData>,glide: RequestManager):BaseRecyclerAdapter<MyMessageRecyclerAdapter.Holder,PushMessageData>(context,data,glide) {
    class Holder(view:View):RecyclerView.ViewHolder(view){
        val userImage:ImageView
        val userName:TextView
        val replyContent:TextView
        val originalReplyContent:TextView
        init {
            userImage=view.findViewById(R.id.my_message_user_image)
            userName=view.findViewById(R.id.my_message_user_name)
            replyContent=view.findViewById(R.id.my_message_reply_content)
            originalReplyContent=view.findViewById(R.id.my_message_original_content_name)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view=LayoutInflater.from(context).inflate(R.layout.my_message_layout,parent,false)
        view.setOnClickListener(this)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        super.onBindViewHolder(holder, position)
        val data=getItem(position)
        setData(holder,data)
    }

    private fun setData(holder: MyMessageRecyclerAdapter.Holder, data: PushMessageData) {
        holder.userName.text=data.userName
        holder.replyContent.text=data.replyContent
        holder.originalReplyContent.text=data.originalReplyContent
        getUserImage(holder,data.userID)
    }

    private fun getUserImage(holder: Holder,userID: Int) {
        ThreadPool.execute {
            val userImagUrl=HandlePerson.GetUserImageURL(userID)
            if(userImagUrl!= ERROR||!userImagUrl.isNullOrEmpty()){
                runOnUiThread(Runnable {
                    glide.load(userImagUrl).into(holder.userImage)
                })
            }
        }
    }
}