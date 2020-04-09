package com.example.sunkai.heritage.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.adapter.baseAdapter.BaseRecyclerAdapter
import com.example.sunkai.heritage.entity.BottomFolkNewsContent
import com.example.sunkai.heritage.tools.ViewImageUtils
import com.example.sunkai.heritage.tools.loadImageFromServerToList
import com.example.sunkai.heritage.value.TYPE_TEXT

/**
 * 底部新闻的详情页
 * Created by sunkai on 2018/2/15.
 */
class NewsDetailRecyclerViewAdapter(context: Context, datas: List<BottomFolkNewsContent>, glide: RequestManager) : BaseRecyclerAdapter<NewsDetailRecyclerViewAdapter.ViewHolder, BottomFolkNewsContent>(context, datas, glide) {

    private val images: Array<String>
    private val compressImages:Array<String?>

    init {
        val images = arrayListOf<String>()
        val compressImages= arrayListOf<String?>()
        datas.forEach {
            if (!isTextLine(it)) {
                images.add(it.content)
                compressImages.add(it.compressImg)
            }
        }
        this.images = images.toTypedArray()
        this.compressImages=compressImages.toTypedArray()
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
            val isLastImage = position-1>=0 && !isTextLine(getItem(position-1))
            setData(holder, data,isLastOneImage = isLastImage)
        } else {
            setData(holder, data, false)
        }
    }

    private fun isTextLine(data: BottomFolkNewsContent) =
            data.type == TYPE_TEXT

    private fun setData(holder: ViewHolder, data: BottomFolkNewsContent, isInfoText: Boolean = true, isLastOneImage:Boolean = false) {
        if (isInfoText) {
            if (isLastOneImage) {
                holder.textView.typeface = Typeface.DEFAULT_BOLD
                holder.textView.setTextColor(Color.BLACK)
            } else {
                holder.textView.typeface = Typeface.DEFAULT
                holder.textView.setTextColor(Color.GRAY)
            }
            holder.textView.visibility = View.VISIBLE
            holder.imageView.visibility = View.GONE
            holder.textView.text = data.content.replace("\r", "").replace("\n", "").replace("\t", "")
        } else {
            holder.textView.visibility = View.GONE
            holder.imageView.visibility = View.VISIBLE
            glide.loadImageFromServerToList(data.compressImg ?: data.content,holder.imageView)
            holder.imageView.setOnClickListener {
                ViewImageUtils.setViewImageClick(context, holder.imageView, images,compressImages, images.indexOf(data.content))
            }
        }
    }

}