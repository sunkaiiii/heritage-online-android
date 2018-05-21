package com.example.sunkai.heritage.tools.CreateSeachActivityAdapterUtils

import com.example.sunkai.heritage.Adapter.BaseAdapter.BaseRecyclerAdapter

interface ICreateCorrespondingSearchAdapter {
    fun createCorrespondingApapter(searchInfo:String):BaseRecyclerAdapter<*,*>
}