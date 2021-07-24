package com.example.sunkai.heritage.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.sunkai.heritage.databinding.FragmentPeopleBannerLayoutBinding
import com.example.sunkai.heritage.entity.response.PeopleMainPageResponse
import com.example.sunkai.heritage.tools.loadImageFromServer

class FragmentPeopleBannerAdapter(
    private val data: List<PeopleMainPageResponse.PeopleBannerTableContent>,
    private val glide: RequestManager,
    private var listner: OnPeopleBannerClickListener? = null
) : RecyclerView.Adapter<FragmentPeopleBannerAdapter.PeopleBannerHolder>() {

    class PeopleBannerHolder(binding:FragmentPeopleBannerLayoutBinding):RecyclerView.ViewHolder(binding.root){
        val text = binding.bannerIamgeDescTextView
        val image = binding.bannerImageView
    }


    fun setBannerClickListener(action: ((View, PeopleMainPageResponse.PeopleBannerTableContent) -> Unit)) {
        this.listner = object : OnPeopleBannerClickListener {
            override fun onItemClick(
                itemView: View,
                data: PeopleMainPageResponse.PeopleBannerTableContent
            ) {
                action(itemView, data)
            }

        }
    }

    interface OnPeopleBannerClickListener {
        fun onItemClick(itemView: View, data: PeopleMainPageResponse.PeopleBannerTableContent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeopleBannerHolder {
        return PeopleBannerHolder(FragmentPeopleBannerLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }


    override fun onBindViewHolder(holder: PeopleBannerHolder, position: Int) {
        val data = data[position % data.size]
        glide.loadImageFromServer(data.img).into(holder.image)
        holder.text.text = data.desc
        holder.itemView.setOnClickListener {
            this.listner?.onItemClick(it,data)
        }
    }

    override fun getItemCount(): Int = Int.MAX_VALUE
}