package com.example.sunkai.heritage.tools.createSearchActivityAdapterUtils

import android.content.Context
import com.bumptech.glide.RequestManager
import com.example.sunkai.heritage.adapter.baseAdapter.BaseRecyclerAdapter
import com.example.sunkai.heritage.adapter.FolkRecyclerViewAdapter
import com.example.sunkai.heritage.connectWebService.HandleFolk

class CreateFolkInfoSearchAdapter(private val context: Context, private val glide: RequestManager):ICreateCorrespondingSearchAdapter  {
    override fun createCorrespondingApapter(searchInfo: String): BaseRecyclerAdapter<*, *> {
        val data = HandleFolk.Search_Folk_Info(searchInfo)
        return FolkRecyclerViewAdapter(context, data, glide)
    }
}