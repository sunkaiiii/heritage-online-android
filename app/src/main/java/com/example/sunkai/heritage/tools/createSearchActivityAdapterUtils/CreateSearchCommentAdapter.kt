package com.example.sunkai.heritage.tools.createSearchActivityAdapterUtils

import android.content.Context
import com.bumptech.glide.RequestManager
import com.example.sunkai.heritage.adapter.baseAdapter.BaseRecyclerAdapter
import com.example.sunkai.heritage.adapter.FindFragmentRecyclerViewAdapter
import com.example.sunkai.heritage.connectWebService.HandleFind
import com.example.sunkai.heritage.value.ALL_COMMENT

class CreateSearchCommentAdapter(private val context: Context, private val glide: RequestManager):ICreateCorrespondingSearchAdapter  {
    override fun createCorrespondingApapter(searchInfo: String): BaseRecyclerAdapter<*, *> {
        val data = HandleFind.SearchUserCommentInfo(searchInfo)
        return FindFragmentRecyclerViewAdapter(context, data, ALL_COMMENT, glide)
    }
}