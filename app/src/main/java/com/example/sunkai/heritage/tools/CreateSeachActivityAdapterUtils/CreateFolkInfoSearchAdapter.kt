package com.example.sunkai.heritage.tools.CreateSeachActivityAdapterUtils

import android.content.Context
import com.bumptech.glide.RequestManager
import com.example.sunkai.heritage.Adapter.BaseAdapter.BaseRecyclerAdapter
import com.example.sunkai.heritage.Adapter.FolkRecyclerViewAdapter
import com.example.sunkai.heritage.ConnectWebService.HandleFolk

class CreateFolkInfoSearchAdapter(private val context: Context, private val glide: RequestManager):ICreateCorrespondingSearchAdapter  {
    override fun createCorrespondingApapter(searchInfo: String): BaseRecyclerAdapter<*, *> {
        val data = HandleFolk.Search_Folk_Info(searchInfo)
        return FolkRecyclerViewAdapter(context, data, glide)
    }
}