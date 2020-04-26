package com.example.sunkai.heritage.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.adapter.baseAdapter.BaseRecyclerAdapter
import com.example.sunkai.heritage.database.entities.SearchHistory
import com.example.sunkai.heritage.tools.GlobalContext
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ProjectSearchHistoryAdapter(context: Context, data: List<SearchHistory>, glide: RequestManager) : BaseRecyclerAdapter<ProjectSearchHistoryAdapter.Holder, SearchHistory>(context, data, glide) {
    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val clearIcon: ImageView
        val searchHistoryTextView: TextView

        init {
            clearIcon = view.findViewById(R.id.searchHistoryClearIcon)
            searchHistoryTextView = view.findViewById(R.id.searchHistoryTextView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(context).inflate(R.layout.activity_search_history_item_layout, parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        super.onBindViewHolder(holder, position)
        val data = getItem(position)
        holder.searchHistoryTextView.text = data.title
        holder.clearIcon.setOnClickListener {
            GlobalScope.launch {
                GlobalContext.newsDetailDatabase.searchHistoryDao().delete(data)
            }
            datas.remove(data)
            notifyDataSetChanged()
        }
    }

}