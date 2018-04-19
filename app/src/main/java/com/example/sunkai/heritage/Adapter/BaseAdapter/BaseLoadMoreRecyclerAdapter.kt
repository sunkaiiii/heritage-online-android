package com.example.sunkai.heritage.Adapter.BaseAdapter

import android.support.v7.widget.RecyclerView
import com.bumptech.glide.RequestManager

/**
 * 可以加载更多的adapter
 * Created by sunkai on 2018/2/15.
 */
abstract class BaseLoadMoreRecyclerAdapter<T:RecyclerView.ViewHolder,W>(datas:List<W>,glide: RequestManager):BaseRecyclerAdapter<T,W>(datas,glide){
    abstract fun addNewData(datas:List<W>)
}