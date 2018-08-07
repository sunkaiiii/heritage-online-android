package com.example.sunkai.heritage.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sunkai.heritage.tools.Views.FollowThemeEdgeRecyclerView
import com.bumptech.glide.RequestManager
import com.example.sunkai.heritage.Adapter.BaseAdapter.BaseRecyclerAdapter
import com.example.sunkai.heritage.Data.SearchHistoryData
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.tintDrawable
import com.example.sunkai.heritage.value.SEARCH_SHAREPREF_NAME

//搜索历史的adapter
class SearchActivitySearchHistoryAdapter(context: Context, datas: List<SearchHistoryData>, glide: RequestManager) : BaseRecyclerAdapter<SearchActivitySearchHistoryAdapter.Holder, SearchHistoryData>(context, datas, glide) {
    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val searchHistoryTextView: TextView = view.findViewById(R.id.search_history_textview)
        val deleteHistoryImageView: ImageView = view.findViewById(R.id.search_history_delete_history)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.search_history_layout, parent, false)
        view.setOnClickListener(this)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        super.onBindViewHolder(holder, position)
        val data = getItem(position)
        holder.searchHistoryTextView.text = data.searchString
        holder.deleteHistoryImageView.setOnClickListener {
            deleteThisHistory(data)
        }
        holder.deleteHistoryImageView.setImageDrawable(tintDrawable(R.drawable.ic_clear_black_24dp))
    }

    private fun deleteThisHistory(data: SearchHistoryData) {
        val sharePref = context.getSharedPreferences(SEARCH_SHAREPREF_NAME, Context.MODE_PRIVATE)
        val historySet = sharePref.getStringSet(data.searchType, mutableSetOf()) ?: mutableSetOf()
        historySet.remove(data.searchString)
        val datas = datas.toMutableList()
        datas.remove(data)
        this.datas = datas
        notifyDataSetChanged()
    }
}