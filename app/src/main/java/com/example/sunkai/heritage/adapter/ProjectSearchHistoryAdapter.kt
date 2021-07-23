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

class ProjectSearchHistoryAdapter(
    context: Context,
    data: List<SearchHistory>,
    glide: RequestManager,
    private var projectSearchItemClickListener: OnProjectSearchItemClickListener? = null
) : BaseRecyclerAdapter<ProjectSearchHistoryAdapter.Holder, SearchHistory>(context, data, glide) {
    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val clearIcon: ImageView
        val searchHistoryTextView: TextView

        init {
            clearIcon = view.findViewById(R.id.searchHistoryClearIcon)
            searchHistoryTextView = view.findViewById(R.id.searchHistoryTextView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            LayoutInflater.from(context)
                .inflate(R.layout.fragment_search_history_item_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        super.onBindViewHolder(holder, position)
        val data = getItem(position)
        holder.searchHistoryTextView.text = data.title
        holder.clearIcon.setOnClickListener {
            projectSearchItemClickListener?.onDelete(it, data)
        }
    }

    fun setOnProjectSearchItemClickListener(action: ((View, SearchHistory) -> Unit)) {
        this.projectSearchItemClickListener = object : OnProjectSearchItemClickListener {
            override fun onDelete(itemView: View, searchHistory: SearchHistory) {
                action(itemView, searchHistory)
            }

        }
    }

    interface OnProjectSearchItemClickListener {
        fun onDelete(itemView: View, searchHistory: SearchHistory)
    }

}