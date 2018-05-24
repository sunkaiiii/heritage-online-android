package com.example.sunkai.heritage.tools.CreateMyCollectAdapterUtils

import android.content.Context
import com.bumptech.glide.RequestManager
import com.example.sunkai.heritage.Adapter.BaseAdapter.BaseRecyclerAdapter
import com.example.sunkai.heritage.value.TYPE_FIND
import com.example.sunkai.heritage.value.TYPE_FOCUS_HERITAGE
import com.example.sunkai.heritage.value.TYPE_FOLK
import com.example.sunkai.heritage.value.TYPE_MAIN

object CreateMyCollectAdapterFactory {

    fun createCorrespondingAdapter(context: Context,glide:RequestManager,typeName:String,className:String):BaseRecyclerAdapter<*,*>?{
        try {
            return (Class.forName(className)
                    .getConstructor(Context::class.java,RequestManager::class.java,String::class.java)
                    .newInstance(context,glide,typeName) as ICreateMyCollectAdapter)
                    .createCorrespondingMyCollectAdapter()
        }catch (e:Exception){
            e.printStackTrace()
        }
        return null
    }
}