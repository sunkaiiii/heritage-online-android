package com.example.sunkai.heritage.Interface

import android.content.Context
import com.example.sunkai.heritage.Adapter.*
import com.example.sunkai.heritage.Adapter.BaseAdapter.BaseRecyclerAdapter

abstract class AbsHandleAdapter : IHandleSearchAdapter {
    override fun handleAdapterItemClick(context: Context, adapter: BaseRecyclerAdapter<*, *>) {
        when (adapter::class.java.name) {
            BottomFolkNewsRecyclerviewAdapter::class.java.name -> handleBottomNewsAdapterItemClick(context, adapter as BottomFolkNewsRecyclerviewAdapter)
            SeeMoreNewsRecyclerViewAdapter::class.java.name -> handleAllFolkNewsAdapterItemClick(context, adapter as SeeMoreNewsRecyclerViewAdapter)
            FolkRecyclerViewAdapter::class.java.name -> handleFolkHeritageAdapterItemCLick(context, adapter as FolkRecyclerViewAdapter)
            FindFragmentRecyclerViewAdapter::class.java.name -> handleFindUserCommentAdapterItemClick(context, adapter as FindFragmentRecyclerViewAdapter)
            SearchUserRecclerAdapter::class.java.name -> handlePersonAdapterItemClick(context, adapter as SearchUserRecclerAdapter)
        }
    }

    protected abstract fun handleBottomNewsAdapterItemClick(context: Context, adapter: BottomFolkNewsRecyclerviewAdapter)
    protected abstract fun handleAllFolkNewsAdapterItemClick(context: Context, adapter: SeeMoreNewsRecyclerViewAdapter)
    protected abstract fun handleFolkHeritageAdapterItemCLick(context: Context, adapter: FolkRecyclerViewAdapter)
    protected abstract fun handleFindUserCommentAdapterItemClick(context: Context, adapter: FindFragmentRecyclerViewAdapter)
    protected abstract fun handlePersonAdapterItemClick(context: Context, adapter: SearchUserRecclerAdapter)
}