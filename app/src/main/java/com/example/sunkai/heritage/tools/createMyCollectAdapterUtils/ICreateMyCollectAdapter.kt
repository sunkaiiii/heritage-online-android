package com.example.sunkai.heritage.tools.createMyCollectAdapterUtils

import com.example.sunkai.heritage.adapter.baseAdapter.BaseRecyclerAdapter

interface ICreateMyCollectAdapter {
    fun createCorrespondingMyCollectAdapter(): BaseRecyclerAdapter<*, *>
}