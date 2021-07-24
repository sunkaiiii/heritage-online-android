package com.example.sunkai.heritage.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.sunkai.heritage.adapter.baseAdapter.BaseRecyclerAdapter
import com.example.sunkai.heritage.database.entities.SearchHistory
import com.example.sunkai.heritage.databinding.FragmentSearchHistoryItemLayoutBinding

class ProjectSearchHistoryAdapter(
    context: Context,
    data: List<SearchHistory>,
    glide: RequestManager,
    private var projectSearchItemClickListener: OnProjectSearchItemClickListener? = null
) : BaseRecyclerAdapter<ProjectSearchHistoryAdapter.Holder, SearchHistory>(context, data, glide) {
    class Holder(binding: FragmentSearchHistoryItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        val clearIcon = binding.searchHistoryClearIcon
        val searchHistoryTextView = binding.searchHistoryTextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(FragmentSearchHistoryItemLayoutBinding.inflate(LayoutInflater.from(context),parent,false))
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