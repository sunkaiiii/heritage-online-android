package com.example.sunkai.heritage.tools.createSearchActivityAdapterUtils

import android.content.Context
import com.bumptech.glide.RequestManager
import com.example.sunkai.heritage.activity.loginActivity.LoginActivity
import com.example.sunkai.heritage.adapter.baseAdapter.BaseRecyclerAdapter
import com.example.sunkai.heritage.adapter.SearchUserRecclerAdapter
import com.example.sunkai.heritage.connectWebService.HandlePerson

class CreateSearchUserAdapter(private val context: Context, private val glide: RequestManager):ICreateCorrespondingSearchAdapter  {
    override fun createCorrespondingApapter(searchInfo: String): BaseRecyclerAdapter<*, *> {
        val data = HandlePerson.GetSearchUserInfo(searchInfo, LoginActivity.userID)
        return SearchUserRecclerAdapter(context, data, glide)
    }
}