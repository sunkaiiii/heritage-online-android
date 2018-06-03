package com.example.sunkai.heritage.Adapter.BaseAdapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager

/**
 * 可以加载更多的adapter
 * Created by sunkai on 2018/2/15.
 */
abstract class BaseLoadMoreRecyclerAdapter<T: androidx.recyclerview.widget.RecyclerView.ViewHolder,W>(context:Context, datas:List<W>, glide: RequestManager):BaseRecyclerAdapter<T,W>(context,datas,glide){
    abstract fun addNewData(datas:List<W>)
}