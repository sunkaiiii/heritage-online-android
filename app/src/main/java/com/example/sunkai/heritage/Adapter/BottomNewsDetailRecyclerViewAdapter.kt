package com.example.sunkai.heritage.Adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.RequestManager
import com.example.sunkai.heritage.Adapter.BaseAdapter.BaseRecyclerAdapter
import com.example.sunkai.heritage.ConnectWebService.BaseSetting
import com.example.sunkai.heritage.Data.BottomNewsDetail
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.value.TYPE_TEXT

/**
 * 底部新闻的详情页
 * Created by sunkai on 2018/2/15.
 */
class BottomNewsDetailRecyclerViewAdapter(val context: Context, datas: List<BottomNewsDetail>,glide: RequestManager) : BaseRecyclerAdapter<BottomNewsDetailRecyclerViewAdapter.ViewHolder, BottomNewsDetail>(datas,glide) {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView
        val imageView: ImageView

        init {
            textView = view.findViewById(R.id.bottom_news_detail_item_text)
            imageView = view.findViewById(R.id.bottom_news_detail_item_img)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.bottom_news_detail_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position)
        if (data.type == TYPE_TEXT) {
            setData(holder, data)
        } else {
            setData(holder, data, false)
        }
    }

    private fun setData(holder: ViewHolder, data: BottomNewsDetail, isInfoText: Boolean = true) {
        if (isInfoText) {
            holder.textView.visibility = View.VISIBLE
            holder.imageView.visibility = View.GONE
            holder.textView.text = data.info
        } else {
            holder.textView.visibility = View.GONE
            holder.imageView.visibility = View.VISIBLE
            glide.load(BaseSetting.URL + data.info).into(holder.imageView)
        }
    }

}