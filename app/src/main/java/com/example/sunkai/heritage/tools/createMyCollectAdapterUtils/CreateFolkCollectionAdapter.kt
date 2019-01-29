package com.example.sunkai.heritage.tools.createMyCollectAdapterUtils

import android.content.Context
import com.bumptech.glide.RequestManager
import com.example.sunkai.heritage.activity.loginActivity.LoginActivity
import com.example.sunkai.heritage.adapter.ActivityRecyclerViewAdapter
import com.example.sunkai.heritage.adapter.baseAdapter.BaseRecyclerAdapter
import com.example.sunkai.heritage.connectWebService.HandlePerson

class CreateFolkCollectionAdapter(private val context: Context, private val glide: RequestManager, private val typeName: String) : ICreateMyCollectAdapter {
    override fun createCorrespondingMyCollectAdapter(): BaseRecyclerAdapter<*, *> {
        val data = HandlePerson.GetFolkColelction(LoginActivity.userID, typeName)
        return ActivityRecyclerViewAdapter(context, data, glide)
    }
}