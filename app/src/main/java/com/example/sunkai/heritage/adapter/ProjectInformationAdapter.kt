package com.example.sunkai.heritage.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.sunkai.heritage.databinding.FragmentProjectListItemBinding
import com.example.sunkai.heritage.entity.response.ProjectListInformation

class ProjectInformationAdapter(private var listener: ProjectListItemClick? = null) : PagingDataAdapter<ProjectListInformation, ProjectInformationAdapter.Holder>(
        COMARATOR) {
    private var currentProjectYear = ""

    companion object {
        private val COMARATOR = object : DiffUtil.ItemCallback<ProjectListInformation>() {
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

    class Holder(binding: FragmentProjectListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val number = binding.projectNumber
        val title = binding.projectTitle
        val category = binding.projectCategory
        val location = binding.projectLocation
        val time = binding.projectTime
        val projectYearTitle = binding.projectYearTitle
    }


    override fun onBindViewHolder(holder: Holder, position: Int) {
        val data = getItem(position) ?: return
        val year = data.rx_time.substring(0,4)
        if (this.currentProjectYear != year || position == 0) {
            this.currentProjectYear = year
            holder.projectYearTitle.visibility = View.VISIBLE
            holder.projectYearTitle.text = year
        }else{
            holder.projectYearTitle.visibility = View.GONE
        }
        holder.itemView.setOnClickListener {
            this.listener?.onItemClick(holder.itemView, data)
        }
        holder.number.text = data.num
        holder.title.text = data.title
        holder.category.text = data.cate
        holder.time.text = data.rx_time
        holder.location.text = data.province
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(FragmentProjectListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }


    fun setProjectItemClickListener(action: ((View, ProjectListInformation) -> Unit)) {
        setProjectItemClickListener(object : ProjectListItemClick {
            override fun onItemClick(itemView: View, item: ProjectListInformation) {
                action(itemView, item)
            }

        })
    }

    fun setProjectItemClickListener(listener: ProjectListItemClick) {
        this.listener = listener
    }

    interface ProjectListItemClick {
        fun onItemClick(itemView: View, item: ProjectListInformation)
    }
}