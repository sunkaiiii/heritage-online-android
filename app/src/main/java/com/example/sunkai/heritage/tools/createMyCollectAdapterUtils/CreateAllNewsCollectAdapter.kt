package com.example.sunkai.heritage.tools.createMyCollectAdapterUtils

import android.content.Context
import com.bumptech.glide.RequestManager
import com.example.sunkai.heritage.activity.loginActivity.LoginActivity
import com.example.sunkai.heritage.adapter.baseAdapter.BaseRecyclerAdapter
import com.example.sunkai.heritage.adapter.SeeMoreNewsRecyclerViewAdapter
import com.example.sunkai.heritage.connectWebService.HandlePerson

class CreateAllNewsCollectAdapter(private val context: Context, private val glide: RequestManager, private val typeName: String) : ICreateMyCollectAdapter {
    override fun createCorrespondingMyCollectAdapter(): BaseRecyclerAdapter<*, *> {
        val data = HandlePerson.GetMainCollection(LoginActivity.userID, typeName)
        return SeeMoreNewsRecyclerViewAdapter(context, data, glide)
    }
}