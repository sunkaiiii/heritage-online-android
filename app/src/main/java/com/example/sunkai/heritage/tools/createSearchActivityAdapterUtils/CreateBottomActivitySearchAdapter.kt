package com.example.sunkai.heritage.tools.createSearchActivityAdapterUtils

import android.content.Context
import com.bumptech.glide.RequestManager
import com.example.sunkai.heritage.adapter.baseAdapter.BaseRecyclerAdapter
import com.example.sunkai.heritage.adapter.BottomFolkNewsRecyclerviewAdapter
import com.example.sunkai.heritage.connectWebService.HandleMainFragment

class CreateBottomActivitySearchAdapter(private val context: Context, private val glide:RequestManager):ICreateCorrespondingSearchAdapter {
    override fun createCorrespondingApapter(searchInfo: String): BaseRecyclerAdapter<*, *> {
        val data = HandleMainFragment.SearchBottomNewsInfo(searchInfo)
        return BottomFolkNewsRecyclerviewAdapter(context, data, glide)
    }
}