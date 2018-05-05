package com.example.sunkai.heritage.Interface

import android.content.Context
import com.example.sunkai.heritage.Adapter.BaseAdapter.BaseRecyclerAdapter

interface IHandleSearchAdapter {
    fun handleAdapterItemClick(context:Context,adapter:BaseRecyclerAdapter<*,*>)
}