package com.example.sunkai.heritage.tools.CreateSeachActivityAdapterUtils

import android.content.Context
import com.bumptech.glide.RequestManager
import com.example.sunkai.heritage.Adapter.BaseAdapter.BaseRecyclerAdapter
import com.example.sunkai.heritage.Adapter.SeeMoreNewsRecyclerViewAdapter
import com.example.sunkai.heritage.ConnectWebService.HandleMainFragment

class CreateSearchAllNewsSearchAdapter(private val context: Context, private val glide: RequestManager):ICreateCorrespondingSearchAdapter {
    override fun createCorrespondingApapter(searchInfo: String): BaseRecyclerAdapter<*, *> {
        val data = HandleMainFragment.SearchAllNewsInfo(searchInfo)
        return SeeMoreNewsRecyclerViewAdapter(context, data, glide)
    }
}