package com.example.sunkai.heritage.tools

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

/**
 * 用于自动加载更多的OnScrollListener的辅助类
 * Created by sunkai on 2018/2/9.
 */
abstract class OnSrollHelper:RecyclerView.OnScrollListener() {
    private var notOnLoad=true
    override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if (notOnLoad) {
            val layoutManager = recyclerView?.layoutManager
            //gridLayoutManager继承自LinearLayoutManger，所以此类也适用于网格视图
            if ((layoutManager is LinearLayoutManager) && notOnLoad) {
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                if (lastVisibleItemPosition + 3 > recyclerView.adapter.itemCount) {
                    setPageOnLoad()
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