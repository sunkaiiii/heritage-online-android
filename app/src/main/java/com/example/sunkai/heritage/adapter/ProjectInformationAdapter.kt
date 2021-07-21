package com.example.sunkai.heritage.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.activity.ProjectDetailActivity
import com.example.sunkai.heritage.entity.response.ProjectListInformation
import com.example.sunkai.heritage.value.DATA

class ProjectInformationAdapter : PagingDataAdapter<ProjectListInformation,ProjectInformationAdapter.Holder>(
    COMARATOR) {
    companion object{
        private val COMARATOR = object: DiffUtil.ItemCallback<ProjectListInformation>(){
            override fun areItemsTheSame(
                oldItem: ProjectListInformation,
                newItem: ProjectListInformation
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ProjectListInformation,
                newItem: ProjectListInformation
            ): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val number:TextView=view.findViewById(R.id.project_number)
        val title: TextView = view.findViewById(R.id.project_title)
        val type:TextView=view.findViewById(R.id.project_type)
        val category:TextView=view.findViewById(R.id.project_category)
        val location:TextView=view.findViewById(R.id.project_location)
        val time:TextView=view.findViewById(R.id.project_time)
    }


    override fun onBindViewHolder(holder: Holder, position: Int) {
        val data=getItem(position) ?:return
        holder.itemView.setOnClickListener {
            onItemClick(it,data)
        }
        holder.number.text=data.project_num
        holder.title.text=data.title
        holder.category.text=data.cate
        holder.type.text=data.type
        holder.time.text=data.rx_time
        holder.location.text=data.province
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_project_list_item, parent, false)
        return Holder(view)
    }

    private fun onItemClick(itemView: View, item: ProjectListInformation) {
        val intent=Intent(itemView.context,ProjectDetailActivity::class.java)
        intent.putExtra(DATA,item.link)
        itemView.context.startActivity(intent)
    }
}