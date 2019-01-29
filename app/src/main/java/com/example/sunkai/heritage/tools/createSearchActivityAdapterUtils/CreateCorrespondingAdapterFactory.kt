package com.example.sunkai.heritage.tools.createSearchActivityAdapterUtils

import android.content.Context
import com.bumptech.glide.RequestManager
import com.example.sunkai.heritage.adapter.baseAdapter.BaseRecyclerAdapter

object CreateCorrespondingAdapterFactory {
    fun create(context: Context,glide:RequestManager,searchType:String,searchInfo:String):BaseRecyclerAdapter<*,*>?{
        try {
            return (Class.forName(searchType)
                    .getConstructor(Context::class.java,RequestManager::class.java)
                    .newInstance(context,glide)
                    as ICreateCorrespondingSearchAdapter).createCorrespondingApapter(searchInfo)
        }catch (e:Exception){
            e.printStackTrace()
        }
        return null
    }
}