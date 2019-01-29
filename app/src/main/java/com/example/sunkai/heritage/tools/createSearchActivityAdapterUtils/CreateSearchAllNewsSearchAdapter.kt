package com.example.sunkai.heritage.tools.createSearchActivityAdapterUtils

import android.content.Context
import com.bumptech.glide.RequestManager
import com.example.sunkai.heritage.adapter.baseAdapter.BaseRecyclerAdapter
import com.example.sunkai.heritage.adapter.SeeMoreNewsRecyclerViewAdapter
import com.example.sunkai.heritage.connectWebService.HandleMainFragment

class CreateSearchAllNewsSearchAdapter(private val context: Context, private val glide: RequestManager):ICreateCorrespondingSearchAdapter {
    override fun createCorrespondingApapter(searchInfo: String): BaseRecyclerAdapter<*, *> {
        val data = HandleMainFragment.SearchAllNewsInfo(searchInfo)
        return SeeMoreNewsRecyclerViewAdapter(context, data, glide)
    }
}