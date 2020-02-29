package com.example.sunkai.heritage.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.adapter.baseAdapter.BaseLoadMoreRecyclerAdapter
import com.example.sunkai.heritage.entity.response.ProjectListInformation

class ProjectInformationAdapter(context: Context, data: List<ProjectListInformation>, glide: RequestManager) : BaseLoadMoreRecyclerAdapter<ProjectInformationAdapter.Holder, ProjectListInformation>(context, data, glide) {
    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.project_title)
    }

    override fun addNewData(datas: List<ProjectListInformation>) {
        val extendedData = this.datas.toMutableList()
        extendedData.addAll(datas)
        this.datas = extendedData
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        super.onBindViewHolder(holder, position)
        val data=getItem(position)
        holder.title.text=data.title
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.fragment_project_list_item, parent, false)
        return Holder(view)
    }
}