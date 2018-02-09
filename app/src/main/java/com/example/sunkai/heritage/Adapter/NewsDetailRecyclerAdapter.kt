package com.example.sunkai.heritage.Adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.sunkai.heritage.ConnectWebService.BaseSettingNew
import com.example.sunkai.heritage.Data.NewsDetail
import com.example.sunkai.heritage.R

/**
 * 展示新闻详情
 * Created by sunkai on 2018/2/9.
 */
class NewsDetailRecyclerAdapter(val context: Context,val datas:List<NewsDetail>) :RecyclerView.Adapter<NewsDetailRecyclerAdapter.ViewHolder>() {

    class ViewHolder(view:View):RecyclerView.ViewHolder(view){
        val textview:TextView
        val imageView:ImageView
        init {
            textview=view.findViewById(R.id.news_detail_item_text)
            imageView=view.findViewById(R.id.news_detail_item_img)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view=LayoutInflater.from(context).inflate(R.layout.news_detail_item,parent,false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.let{
            val data=datas[position]
            if(data.type=="text"){
                holder.imageView.visibility=View.GONE
                holder.textview.visibility=View.VISIBLE
                holder.textview.text=data.info
            }else{
                holder.textview.visibility=View.GONE
                holder.imageView.visibility=View.VISIBLE
                Glide.with(context).load(BaseSettingNew.URL+data.info).into(holder.imageView)
            }
        }
    }

    override fun getItemCount(): Int {
        return datas.size
    }
}