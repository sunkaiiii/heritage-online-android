package com.example.sunkai.heritage.tools

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

/**
 * Created by sunkai on 2018/2/9.
 */
abstract class OnSrollHelper:RecyclerView.OnScrollListener() {
    private var notOnLoad=true
    override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if (notOnLoad) {
            val layoutManager = recyclerView?.layoutManager
            if ((layoutManager is LinearLayoutManager) && notOnLoad) {
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                if (lastVisibleItemPosition + 3 > recyclerView.adapter.itemCount) {
                    loadMoreData(recyclerView)
                }
            }
        }
    }
    fun setPageOnLoad(){
        this.notOnLoad=false
    }
    fun setPageLoaded(){
        this.notOnLoad=true
    }
    abstract fun loadMoreData(recyclerView: RecyclerView)
}