package com.example.sunkai.heritage.Interface

import android.content.Context
import com.example.sunkai.heritage.Adapter.*
import com.example.sunkai.heritage.Adapter.BaseAdapter.BaseRecyclerAdapter

abstract class AbsHandleAdapterItemClick : IHandleAdapterItemClick {
    override fun handleAdapterItemClick(context: Context, adapter: BaseRecyclerAdapter<*, *>) {
        when (adapter::class.java.name) {
            BottomFolkNewsRecyclerviewAdapter::class.java.name -> handleBottomNewsAdapterItemClick(context, adapter as BottomFolkNewsRecyclerviewAdapter)
            SeeMoreNewsRecyclerViewAdapter::class.java.name -> handleAllFolkNewsAdapterItemClick(context, adapter as SeeMoreNewsRecyclerViewAdapter)
            FolkRecyclerViewAdapter::class.java.name -> handleFolkHeritageAdapterItemCLick(context, adapter as FolkRecyclerViewAdapter)
            FindFragmentRecyclerViewAdapter::class.java.name -> handleFindUserCommentAdapterItemClick(context, adapter as FindFragmentRecyclerViewAdapter)
            SearchUserRecclerAdapter::class.java.name -> handlePersonAdapterItemClick(context, adapter as SearchUserRecclerAdapter)
            ActivityRecyclerViewAdapter::class.java.name->handleActivityRecyclerViewItemClick(context,adapter as ActivityRecyclerViewAdapter)
            BottomFolkNewsRecyclerviewAdapter::class.java.name->handleBottomFolkNewsRecyclerItemClick(context,adapter as BottomFolkNewsRecyclerviewAdapter)
            FocusListviewAdapter::class.java.name->handleFocusListviewItemClick(context,adapter as FocusListviewAdapter)
            MyLikeCommentRecyclerAdapter::class.java.name->handleMyLikeCommentRecyclerviewItemClick(context,adapter as MyLikeCommentRecyclerAdapter)
        }
    }

    protected abstract fun handleBottomNewsAdapterItemClick(context: Context, adapter: BottomFolkNewsRecyclerviewAdapter)
    protected abstract fun handleAllFolkNewsAdapterItemClick(context: Context, adapter: SeeMoreNewsRecyclerViewAdapter)
    protected abstract fun handleFolkHeritageAdapterItemCLick(context: Context, adapter: FolkRecyclerViewAdapter)
    protected abstract fun handleFindUserCommentAdapterItemClick(context: Context, adapter: FindFragmentRecyclerViewAdapter)
    protected abstract fun handlePersonAdapterItemClick(context: Context, adapter: SearchUserRecclerAdapter)
    protected abstract fun handleActivityRecyclerViewItemClick(context: Context,adapter:ActivityRecyclerViewAdapter)
    protected abstract fun handleBottomFolkNewsRecyclerItemClick(context: Context,adapter: BottomFolkNewsRecyclerviewAdapter)
    protected abstract fun handleFocusListviewItemClick(context: Context,adapter: FocusListviewAdapter)
    protected abstract fun handleMyLikeCommentRecyclerviewItemClick(context: Context,adapter:MyLikeCommentRecyclerAdapter)
}