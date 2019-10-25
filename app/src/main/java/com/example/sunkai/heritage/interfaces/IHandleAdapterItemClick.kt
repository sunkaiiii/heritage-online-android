package com.example.sunkai.heritage.interfaces

import android.content.Context
import com.example.sunkai.heritage.adapter.*

interface IHandleAdapterItemClick {
    fun handleBottomNewsAdapterItemClick(context: Context, adapter: BottomFolkNewsRecyclerviewAdapter)
    fun handleAllFolkNewsAdapterItemClick(context: Context, adapter: SeeMoreNewsRecyclerViewAdapter)
    fun handleFolkHeritageAdapterItemCLick(context: Context, adapter: FolkRecyclerViewAdapter)
    fun handleActivityRecyclerViewItemClick(context: Context, adapter: ActivityRecyclerViewAdapter)
}