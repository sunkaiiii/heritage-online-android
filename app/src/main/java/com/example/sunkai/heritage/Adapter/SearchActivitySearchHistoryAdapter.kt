package com.example.sunkai.heritage.Adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.RequestManager
import com.example.sunkai.heritage.Adapter.BaseAdapter.BaseRecyclerAdapter
import com.example.sunkai.heritage.Data.SearchHistoryData
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.value.SEARCH_SHAREPREF_NAME

//搜索历史的adapter
class SearchActivitySearchHistoryAdapter(context:Context,datas:List<SearchHistoryData>,glide:RequestManager):BaseRecyclerAdapter<SearchActivitySearchHistoryAdapter.Holder,SearchHistoryData>(context,datas,glide) {
    class Holder(view:View):RecyclerView.ViewHolder(view){
        val searchHistoryTextView:TextView
        val deleteHistoryImageView:ImageView
        init {
            searchHistoryTextView=view.findViewById(R.id.search_history_textview)
            deleteHistoryImageView=view.findViewById(R.id.search_history_delete_history)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view=LayoutInflater.from(context).inflate(R.layout.search_history_layout,parent,false)
        view.setOnClickListener(this)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        super.onBindViewHolder(holder, position)
        val data=getItem(position)
        holder.searchHistoryTextView.text=data.searchString
        holder.deleteHistoryImageView.setOnClickListener {
            deleteThisHistory(data)
        }
    }

    private fun deleteThisHistory(data: SearchHistoryData) {
        val sharePref=context.getSharedPreferences(SEARCH_SHAREPREF_NAME,Context.MODE_PRIVATE)
        val historySet=sharePref.getStringSet(data.searchType, mutableSetOf())
        historySet.remove(data.searchString)
        val datas=datas.toMutableList()
        datas.remove(data)
        this.datas=datas
        notifyDataSetChanged()
    }
}