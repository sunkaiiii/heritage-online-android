package com.example.sunkai.heritage.tools.CreateMyCollectAdapterUtils

import com.example.sunkai.heritage.Adapter.BaseAdapter.BaseRecyclerAdapter

interface ICreateMyCollectAdapter {
    fun createCorrespondingMyCollectAdapter(): BaseRecyclerAdapter<*, *>
}