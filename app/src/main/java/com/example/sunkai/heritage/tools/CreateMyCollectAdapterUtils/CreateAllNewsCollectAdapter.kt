package com.example.sunkai.heritage.tools.CreateMyCollectAdapterUtils

import android.content.Context
import com.bumptech.glide.RequestManager
import com.example.sunkai.heritage.Activity.LoginActivity.LoginActivity
import com.example.sunkai.heritage.Adapter.BaseAdapter.BaseRecyclerAdapter
import com.example.sunkai.heritage.Adapter.SeeMoreNewsRecyclerViewAdapter
import com.example.sunkai.heritage.ConnectWebService.HandlePerson

class CreateAllNewsCollectAdapter(private val context: Context, private val glide: RequestManager, private val typeName: String) : ICreateMyCollectAdapter {
    override fun createCorrespondingMyCollectAdapter(): BaseRecyclerAdapter<*, *> {
        val data = HandlePerson.GetMainCollection(LoginActivity.userID, typeName)
        return SeeMoreNewsRecyclerViewAdapter(context, data, glide)
    }

}