package com.example.sunkai.heritage.tools.createMyCollectAdapterUtils

import android.content.Context
import com.bumptech.glide.RequestManager
import com.example.sunkai.heritage.adapter.baseAdapter.BaseRecyclerAdapter

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