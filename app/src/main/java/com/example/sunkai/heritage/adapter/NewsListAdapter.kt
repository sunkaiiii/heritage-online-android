package com.example.sunkai.heritage.adapter

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
import com.example.sunkai.heritage.databinding.BottomFolkNewsLayoutBinding
import com.example.sunkai.heritage.entity.response.NewsListResponse
import com.example.sunkai.heritage.tools.ViewImageUtils
import com.example.sunkai.heritage.tools.getThemeColor
import com.example.sunkai.heritage.tools.loadImageFromServer

/**
 * 首页新闻的RecyclerView的Adapter
 * Created by sunkai on 2018/2/12.
 */
class NewsListAdapter(private val glide: RequestManager,private var listner:OnNewsListItemClickListener?=null) : PagingDataAdapter<NewsListResponse,NewsListAdapter.Holder>(
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


    class Holder(binding:BottomFolkNewsLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        val title: TextView = binding.bottomViewTitle
        val time: TextView = binding.bottomViewTime
        val image: ImageView = binding.bottomViewImage
        val readMark: View = binding.isReadMark
        val newsAuthor = binding.newsAuthor
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(BottomFolkNewsLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val data = getItem(position) ?:return
        setData(holder, data)
        holder.itemView.setOnClickListener {
            onItemClick(holder.itemView,data)
        }
    }

    private fun setData(holder: Holder, data: NewsListResponse) {
        holder.image.visibility = View.GONE
        holder.image.setImageDrawable(null)
        holder.title.text = data.title
        holder.time.text = data.date
        holder.newsAuthor.text = "作者："
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


    private fun onItemClick(itemView: View, item: NewsListResponse) {
        item.isRead = true
        itemView.findViewById<View>(R.id.isReadMark).visibility=View.VISIBLE
        this.listner?.onItemClick(item)
    }

    fun setOnNewsItemClickListener(action:((NewsListResponse)->Unit)){
        this.listner = object:OnNewsListItemClickListener{
            override fun onItemClick(newsData: NewsListResponse) {
                action(newsData)
            }

        }
    }


    interface OnNewsListItemClickListener{
        fun onItemClick(newsData:NewsListResponse)
    }
}