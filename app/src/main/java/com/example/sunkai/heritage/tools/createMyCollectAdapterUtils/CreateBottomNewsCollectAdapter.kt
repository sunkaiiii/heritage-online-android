package com.example.sunkai.heritage.tools.createMyCollectAdapterUtils

import android.content.Context
import com.bumptech.glide.RequestManager
import com.example.sunkai.heritage.activity.loginActivity.LoginActivity
import com.example.sunkai.heritage.adapter.baseAdapter.BaseRecyclerAdapter
import com.example.sunkai.heritage.adapter.BottomFolkNewsRecyclerviewAdapter
import com.example.sunkai.heritage.connectWebService.HandlePerson

class CreateBottomNewsCollectAdapter(private val context: Context, private val glide: RequestManager, private val typeName: String) : ICreateMyCollectAdapter {
    override fun createCorrespondingMyCollectAdapter(): BaseRecyclerAdapter<*, *> {
        val data = HandlePerson.GetFocusOnHeritageCollection(LoginActivity.userID, typeName)
        return BottomFolkNewsRecyclerviewAdapter(context, data, glide)
    }
}