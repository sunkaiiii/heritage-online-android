package com.example.sunkai.heritage.tools.CreateSeachActivityAdapterUtils

import android.content.Context
import com.bumptech.glide.RequestManager
import com.example.sunkai.heritage.Adapter.BaseAdapter.BaseRecyclerAdapter
import com.example.sunkai.heritage.value.*

object CreateCorrespondingAdapterFactory {
    fun create(context: Context,glide:RequestManager,searchType:String,searchInfo:String):BaseRecyclerAdapter<*,*>?{
        val adapterCreater=when(searchType){
            TYPE_BOTTOM_NEWS -> CreateBottomActivitySearchAdapter(context,glide)
            TYPE_NEWS -> CreateSearchAllNewsSearchAdapter(context,glide)
            TYPE_FOLK_HERITAGE -> CreateFolkInfoSearchAdapter(context,glide)
            TYPE_COMMENT -> CreateSearchCommentAdapter(context,glide)
            TYPE_USER -> CreateSearchUserAdapter(context,glide)
            else->null
        }
        return adapterCreater?.createCorrespondingApapter(searchInfo)
    }
}