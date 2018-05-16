package com.example.sunkai.heritage.Interface

import android.content.Context
import com.example.sunkai.heritage.Adapter.BaseAdapter.BaseRecyclerAdapter

interface IHandleAdapterItemClick {
    fun handleAdapterItemClick(context:Context,adapter:BaseRecyclerAdapter<*,*>)
}