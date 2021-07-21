package com.example.sunkai.heritage.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.activity.NewsDetailActivity
import com.example.sunkai.heritage.network.EHeritageApi
import com.example.sunkai.heritage.entity.response.NewsListResponse
import com.example.sunkai.heritage.tools.loadImageFromServer
import com.example.sunkai.heritage.value.API
import com.example.sunkai.heritage.value.DATA
import com.example.sunkai.heritage.views.tools.RectangleImageView

class PeopleFragmentListAdapter(private val glide: RequestManager) : PagingDataAdapter<NewsListResponse,PeopleFragmentListAdapter.Holder>(
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

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val peopleContent: View
        val peopleImageLayout: View
        val peopleImage: RectangleImageView
        val peopleTitleTextInsideImage: TextView
        val peopleTitleOutSideImage: TextView
        val peopleDesc: TextView

        init {
            peopleContent = view.findViewById(R.id.peopleContengLayout)
            peopleImageLayout = view.findViewById(R.id.peopleImageLayout)
            peopleImage = view.findViewById(R.id.peopleImage)
            peopleTitleTextInsideImage = view.findViewById(R.id.peopleTitleTextInsideImage)
            peopleTitleOutSideImage = view.findViewById(R.id.peopleTitleOutsideImage)
            peopleDesc = view.findViewById(R.id.peopleDesc)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_people_list_item, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val data = getItem(position) ?:return
        holder.itemView.setOnClickListener {
            onItemClick(it,data)
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

    fun onItemClick(itemView: View, item: NewsListResponse) {
        val intent = Intent(itemView.context, NewsDetailActivity::class.java)
        intent.putExtra(DATA, item.link)
        intent.putExtra(API, EHeritageApi.GetPeopleDetail)
        itemView.context.startActivity(intent)
    }
}