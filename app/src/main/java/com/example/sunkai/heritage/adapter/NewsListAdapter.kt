package com.example.sunkai.heritage.adapter

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.activity.NewsDetailActivity
import com.example.sunkai.heritage.adapter.baseAdapter.BaseLoadMoreRecyclerAdapter
import com.example.sunkai.heritage.entity.response.NewsListResponse
import com.example.sunkai.heritage.fragment.MainFragment
import com.example.sunkai.heritage.tools.ViewImageUtils
import com.example.sunkai.heritage.tools.getThemeColor
import com.example.sunkai.heritage.tools.loadImageFromServer
import com.example.sunkai.heritage.value.API
import com.example.sunkai.heritage.value.DATA

/**
 * 首页新闻的RecyclerView的Adapter
 * Created by sunkai on 2018/2/12.
 */
class NewsListAdapter(private val glide: RequestManager, private val requestDetailApi: MainFragment.NewsPages) : PagingDataAdapter<NewsListResponse,NewsListAdapter.Holder>(
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
        val title: TextView
        val time: TextView
        val briefly: TextView
        val image: ImageView
        val readMark: View

        init {
            title = view.findViewById(R.id.bottom_view_title)
            time = view.findViewById(R.id.bottom_view_time)
            briefly = view.findViewById(R.id.bottom_view_briefly)
            image = view.findViewById(R.id.bottom_view_image)
            readMark = view.findViewById(R.id.isReadMark)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.bottom_folk_news_layout, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val data = getItem(position) ?:return
        setData(holder, data)
    }

    private fun setData(holder: Holder, data: NewsListResponse) {
        holder.image.visibility = View.GONE
        holder.image.setImageDrawable(null)
        holder.title.text = data.title
        holder.time.text = data.date
        holder.briefly.text = data.content
        holder.readMark.setBackgroundColor(getThemeColor())
        holder.readMark.visibility = if (data.isRead) View.VISIBLE else View.GONE
        if (!data.img.isNullOrEmpty()) {
            holder.image.visibility = View.VISIBLE
            glide.loadImageFromServer(data.compressImg ?: data.img
            ?: "").listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    holder.image.visibility = View.GONE
                    data.compressImg = null
                    data.img = null
                    return false
                }

                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    return false
                }

            }).into(holder.image)
            holder.image.setOnClickListener {
                ViewImageUtils.setViewImageClick(holder.itemView.context, holder.image, data.img!!, data.compressImg
                        ?: data.img)
            }
        }
    }


    private fun setItemClick(itemView: View, item: NewsListResponse) {
        item.isRead = true
        itemView.findViewById<View>(R.id.isReadMark).visibility=View.VISIBLE
        val intent = Intent(itemView.context, NewsDetailActivity::class.java)
        intent.putExtra(DATA, item)
        intent.putExtra(API, requestDetailApi)
        itemView.context.startActivity(intent)
    }

}