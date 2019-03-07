package com.example.sunkai.heritage.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.sunkai.heritage.connectWebService.BaseSetting
import com.example.sunkai.heritage.entity.NewsDetail
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.ViewImageUtils
import com.example.sunkai.heritage.value.TYPE_TEXT

/**
 * 展示新闻详情
 * Created by sunkai on 2018/2/9.
 */
class NewsDetailRecyclerAdapter(val context: Context, val datas: List<NewsDetail>, val glide: RequestManager) : RecyclerView.Adapter<NewsDetailRecyclerAdapter.ViewHolder>() {

    private val images: Array<String>

    init {
        val images = arrayListOf<String>()
        datas.forEach {
            if (!isTextLine(it)) {
                images.add(it.info)
            }
        }
        this.images = images.toTypedArray()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textview: TextView
        val imageView: ImageView

        init {
            textview = view.findViewById(R.id.news_detail_item_text)
            imageView = view.findViewById(R.id.news_detail_item_img)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.news_detail_item, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = datas[position]
        if (isTextLine(data)) {
            holder.imageView.visibility = View.GONE
            holder.textview.visibility = View.VISIBLE
            holder.textview.text = data.info
        } else {
            holder.textview.visibility = View.GONE
            holder.imageView.visibility = View.VISIBLE
            glide.load(BaseSetting.URL + data.info).into(holder.imageView)
            holder.imageView.setOnClickListener {
                ViewImageUtils.setViewImageClick(context, holder.imageView, images, images.indexOf(data.info))
            }
        }
    }

    private fun isTextLine(data: NewsDetail) = data.type == TYPE_TEXT

    override fun getItemCount(): Int {
        return datas.size
    }
}