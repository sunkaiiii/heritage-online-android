package com.example.sunkai.heritage.tools.CreateSeachActivityAdapterUtils

import android.content.Context
import com.bumptech.glide.RequestManager
import com.example.sunkai.heritage.Adapter.BaseAdapter.BaseRecyclerAdapter
import com.example.sunkai.heritage.value.*

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