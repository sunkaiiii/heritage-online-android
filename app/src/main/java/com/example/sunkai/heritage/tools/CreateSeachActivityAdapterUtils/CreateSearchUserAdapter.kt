package com.example.sunkai.heritage.tools.CreateSeachActivityAdapterUtils

import android.content.Context
import com.bumptech.glide.RequestManager
import com.example.sunkai.heritage.Activity.LoginActivity.LoginActivity
import com.example.sunkai.heritage.Adapter.BaseAdapter.BaseRecyclerAdapter
import com.example.sunkai.heritage.Adapter.SearchUserRecclerAdapter
import com.example.sunkai.heritage.ConnectWebService.HandlePerson

class CreateSearchUserAdapter(private val context: Context, private val glide: RequestManager):ICreateCorrespondingSearchAdapter  {
    override fun createCorrespondingApapter(searchInfo: String): BaseRecyclerAdapter<*, *> {
        val data = HandlePerson.GetSearchUserInfo(searchInfo, LoginActivity.userID)
        return SearchUserRecclerAdapter(context, data, glide)
    }
}