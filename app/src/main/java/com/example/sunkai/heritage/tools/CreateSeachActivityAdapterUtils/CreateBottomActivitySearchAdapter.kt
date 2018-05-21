package com.example.sunkai.heritage.tools.CreateSeachActivityAdapterUtils

import android.content.Context
import com.bumptech.glide.RequestManager
import com.example.sunkai.heritage.Adapter.BaseAdapter.BaseRecyclerAdapter
import com.example.sunkai.heritage.Adapter.BottomFolkNewsRecyclerviewAdapter
import com.example.sunkai.heritage.ConnectWebService.HandleMainFragment

class CreateBottomActivitySearchAdapter(private val context: Context, private val glide:RequestManager):ICreateCorrespondingSearchAdapter {
    override fun createCorrespondingApapter(searchInfo: String): BaseRecyclerAdapter<*, *> {
        val data = HandleMainFragment.SearchBottomNewsInfo(searchInfo)
        return BottomFolkNewsRecyclerviewAdapter(context, data, glide)
    }
}