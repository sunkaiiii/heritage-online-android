package com.example.sunkai.heritage.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.sunkai.heritage.adapter.baseAdapter.BaseRecyclerAdapter
import com.example.sunkai.heritage.connectWebService.BaseSetting
import com.example.sunkai.heritage.entity.BottomNewsDetail
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.ViewImageUtils
import com.example.sunkai.heritage.value.TYPE_TEXT

/**
 * 底部新闻的详情页
 * Created by sunkai on 2018/2/15.
 */
class BottomNewsDetailRecyclerViewAdapter(context: Context, datas: List<BottomNewsDetail>, glide: RequestManager) : BaseRecyclerAdapter<BottomNewsDetailRecyclerViewAdapter.ViewHolder, BottomNewsDetail>(context, datas, glide) {

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
        val textView: TextView = view.findViewById(R.id.bottom_news_detail_item_text)
        val imageView: ImageView = view.findViewById(R.id.bottom_news_detail_item_img)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.bottom_news_detail_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position)
        if (isTextLine(data)) {
            setData(holder, data)
        } else {
            setData(holder, data, false)
        }
    }

    private fun isTextLine(data: BottomNewsDetail) =
            data.type == TYPE_TEXT

    private fun setData(holder: ViewHolder, data: BottomNewsDetail, isInfoText: Boolean = true) {
        if (isInfoText) {
            holder.textView.visibility = View.VISIBLE
            holder.imageView.visibility = View.GONE
            holder.textView.text = data.info
        } else {
            holder.textView.visibility = View.GONE
            holder.imageView.visibility = View.VISIBLE
            glide.load(BaseSetting.URL + data.info).into(holder.imageView)
            holder.imageView.setOnClickListener {
                ViewImageUtils.setViewImageClick(context, holder.imageView, images, images.indexOf(data.info))
            }
        }
    }

}