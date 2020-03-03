package com.example.sunkai.heritage.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.activity.ProjectDetailActivity
import com.example.sunkai.heritage.adapter.baseAdapter.BaseLoadMoreRecyclerAdapter
import com.example.sunkai.heritage.entity.response.ProjectListInformation

class ProjectInformationAdapter(context: Context, data: List<ProjectListInformation>, glide: RequestManager) : BaseLoadMoreRecyclerAdapter<ProjectInformationAdapter.Holder, ProjectListInformation>(context, data, glide) {
    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val number:TextView=view.findViewById(R.id.project_number)
        val title: TextView = view.findViewById(R.id.project_title)
        val type:TextView=view.findViewById(R.id.project_type)
        val category:TextView=view.findViewById(R.id.project_category)
        val location:TextView=view.findViewById(R.id.project_location)
        val time:TextView=view.findViewById(R.id.project_time)
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
        holder.number.text=data.project_num
        holder.title.text=data.title
        holder.category.text=data.cate
        holder.type.text=data.type
        holder.time.text=data.rx_time
        holder.location.text=data.province
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.fragment_project_list_item, parent, false)
        return Holder(view)
    }

    override fun setItemClick(itemView: View, item: ProjectListInformation) {
        val intent=Intent(context,ProjectDetailActivity::class.java)
        intent.putExtra("data",item)
        context.startActivity(intent)
    }
}