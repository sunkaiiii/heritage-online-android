package com.example.sunkai.heritage.tools.createMyCollectAdapterUtils

import android.content.Context
import com.bumptech.glide.RequestManager
import com.example.sunkai.heritage.activity.loginActivity.LoginActivity
import com.example.sunkai.heritage.adapter.baseAdapter.BaseRecyclerAdapter
import com.example.sunkai.heritage.adapter.FindFragmentRecyclerViewAdapter
import com.example.sunkai.heritage.connectWebService.HandlePerson
import com.example.sunkai.heritage.value.MY_FOCUS_COMMENT

class CreateCommentCollectaAdapter(private val context: Context, private val glide: RequestManager, private val typeName: String) : ICreateMyCollectAdapter {
    override fun createCorrespondingMyCollectAdapter(): BaseRecyclerAdapter<*, *> {
        val data = HandlePerson.GetFindCollection(LoginActivity.userID, typeName)
        return FindFragmentRecyclerViewAdapter(context, data, MY_FOCUS_COMMENT, glide)
    }
}