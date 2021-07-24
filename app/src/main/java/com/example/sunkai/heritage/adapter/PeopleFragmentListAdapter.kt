package com.example.sunkai.heritage.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.databinding.FragmentPeopleListItemBinding
import com.example.sunkai.heritage.entity.response.NewsListResponse
import com.example.sunkai.heritage.tools.loadImageFromServer
import com.example.sunkai.heritage.views.tools.RectangleImageView

class PeopleFragmentListAdapter(private val glide: RequestManager,private var listener: PeopleClickListner?=null) : PagingDataAdapter<NewsListResponse,PeopleFragmentListAdapter.Holder>(
    COMPARATOR) {
    companion object{
        private val COMPARATOR = object : DiffUtil.ItemCallback<NewsListResponse>() {
            override fun areItemsTheSame(oldItem: NewsListResponse, newItem: NewsListResponse): Boolean {
                return oldItem.link == newItem.link
            }

            override fun areContentsTheSame(oldItem: NewsListResponse, newItem: NewsListResponse): Boolean {
                return oldItem == newItem
            }
        }
    }

    class Holder(binding: FragmentPeopleListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val peopleContent = binding.peopleContengLayout
        val peopleImageLayout = binding.peopleImageLayout
        val peopleImage = binding.peopleImage
        val peopleTitleTextInsideImage = binding.peopleTitleTextInsideImage
        val peopleTitleOutSideImage = binding.peopleTitleOutsideImage
        val peopleDesc = binding.peopleDesc
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(FragmentPeopleListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val data = getItem(position) ?:return
        holder.itemView.setOnClickListener {
            this.listener?.onItemClick(it,data)
        }
        if (data.img != null) {
            val imgUrl = data.compressImg ?: data.img
            holder.peopleContent.visibility = View.GONE
            holder.peopleImageLayout.visibility = View.VISIBLE
            glide.loadImageFromServer(imgUrl!!).into(holder.peopleImage)
            holder.peopleTitleTextInsideImage.text = data.title
        } else {
            holder.peopleContent.visibility = View.VISIBLE
            holder.peopleImageLayout.visibility = View.GONE
            holder.peopleTitleOutSideImage.text = data.title
            holder.peopleDesc.text = data.content
        }
    }

    fun setOnItemClickListener(listener:PeopleClickListner){
        this.listener = listener
    }


    interface PeopleClickListner{
        fun onItemClick(itemView: View,item:NewsListResponse)
    }
}