package com.example.sunkai.heritage.tools.createSearchActivityAdapterUtils

import com.example.sunkai.heritage.adapter.baseAdapter.BaseRecyclerAdapter

interface ICreateCorrespondingSearchAdapter {
    fun createCorrespondingApapter(searchInfo:String):BaseRecyclerAdapter<*,*>
}