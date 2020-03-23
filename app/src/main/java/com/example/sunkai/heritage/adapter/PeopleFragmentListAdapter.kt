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
import com.example.sunkai.heritage.activity.BottomNewsDetailActivity
import com.example.sunkai.heritage.activity.NewsDetailActivity
import com.example.sunkai.heritage.adapter.baseAdapter.BaseLoadMoreRecyclerAdapter
import com.example.sunkai.heritage.connectWebService.EHeritageApi
import com.example.sunkai.heritage.entity.NewsListResponse
import com.example.sunkai.heritage.tools.loadImageFromServer
import com.example.sunkai.heritage.tools.views.RectangleImageView
import com.example.sunkai.heritage.value.API
import com.example.sunkai.heritage.value.DATA
import kotlinx.android.synthetic.main.fragment_people_list_item.view.*

class PeopleFragmentListAdapter(context: Context, data: List<NewsListResponse>, glide: RequestManager) : BaseLoadMoreRecyclerAdapter<PeopleFragmentListAdapter.Holder, NewsListResponse>(context, data, glide) {
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

    override fun addNewData(datas: List<NewsListResponse>) {
        val extendData = this.datas.toMutableList()
        extendData.addAll(datas)
        this.datas = extendData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.fragment_people_list_item, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        super.onBindViewHolder(holder, position)
        val data = datas[position]
        if (data.img != null) {
            val imgUrl = data.compressImg ?: data.img
            holder.peopleContent.visibility = View.GONE
            holder.peopleImageLayout.visibility = View.VISIBLE
            glide.loadImageFromServer(imgUrl).into(holder.peopleImage)
            holder.peopleTitleTextInsideImage.text = data.title
        } else {
            holder.peopleContent.visibility = View.VISIBLE
            holder.peopleImageLayout.visibility = View.GONE
            holder.peopleTitleOutSideImage.text = data.title
            holder.peopleDesc.text = data.content
        }
    }

    override fun setItemClick(itemView: View, item: NewsListResponse) {
        val intent = Intent(context, BottomNewsDetailActivity::class.java)
        intent.putExtra(DATA, item.link)
        intent.putExtra(API, EHeritageApi.GetPeopleDetail)
        context.startActivity(intent)
    }
}