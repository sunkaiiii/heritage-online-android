package com.example.sunkai.heritage.tools.CreateMyCollectAdapterUtils

import android.content.Context
import com.bumptech.glide.RequestManager
import com.example.sunkai.heritage.Adapter.BaseAdapter.BaseRecyclerAdapter
import com.example.sunkai.heritage.value.TYPE_FIND
import com.example.sunkai.heritage.value.TYPE_FOCUS_HERITAGE
import com.example.sunkai.heritage.value.TYPE_FOLK
import com.example.sunkai.heritage.value.TYPE_MAIN

object CreateMyCollectAdapterFactory {

    fun createCorrespondingAdapter(context: Context,glide:RequestManager,typeName:String):BaseRecyclerAdapter<*,*>?{
        val adapterCreater=when(typeName){
            TYPE_MAIN -> CreateAllNewsCollectAdapter(context,glide,typeName)
            TYPE_FOCUS_HERITAGE -> CreateBottomNewsCollectAdapter(context,glide,typeName)
            TYPE_FOLK -> CreateFolkCollectionAdapter(context,glide,typeName)
            TYPE_FIND -> CreateCommentCollectaAdapter(context,glide,typeName)
            else->null
        }
        return adapterCreater?.createCorrespondingMyCollectAdapter()
    }
}