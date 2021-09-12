package com.example.sunkai.heritage.adapter

import android.content.Context
import android.graphics.Typeface
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.adapter.baseAdapter.BaseRecyclerAdapter
import com.example.sunkai.heritage.entity.response.NewsDetailContent
import com.example.sunkai.heritage.entity.response.NewsDetailRelativeNews
import com.example.sunkai.heritage.tools.ViewImageUtils
import com.example.sunkai.heritage.tools.loadImageFromServerToList
import com.example.sunkai.heritage.value.TYPE_TEXT

/**
 * 底部新闻的详情页
 * Created by sunkai on 2018/2/15.
 */
class NewsDetailRecyclerViewAdapter(data: List<NewsDetailContent>, glide: RequestManager, private val relevantNews: List<NewsDetailRelativeNews>) : BaseRecyclerAdapter<NewsDetailRecyclerViewAdapter.ViewHolder, NewsDetailContent>(data, glide) {

    private val images: Array<String>
    private val compressImages: Array<String?>
    private var initFooter = false
    private var onRelevantNewsClickListner: onRelevantNewsClick? = null

    init {
        val images = arrayListOf<String>()
        val compressImages = arrayListOf<String?>()
        data.forEach {
            if (!isTextLine(it)) {
                images.add(it.content)
                compressImages.add(it.compressImg)
            }
        }

        this.images = images.toTypedArray()
        this.compressImages = compressImages.toTypedArray()
    }

    class ViewHolder(view: View, type: Int) : RecyclerView.ViewHolder(view) {
        val textView: TextView?
        val imageView: ImageView?


        init {
            if (type == ViewType.NORMAL) {
                textView = view.findViewById(R.id.bottom_news_detail_item_text)
                imageView = view.findViewById(R.id.bottom_news_detail_item_img)
            } else {
                textView = null
                imageView = null
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = if (viewType == ViewType.NORMAL) {
            LayoutInflater.from(parent.context).inflate(R.layout.news_detail_item, parent, false)
        } else {
            LayoutInflater.from(parent.context).inflate(R.layout.news_detail_footer_view, parent, false)
        }
        return ViewHolder(view, viewType)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position < itemCount - 1) {
            val data = getItem(position)
            if (isTextLine(data)) {
                val isLastImage = position - 1 >= 0 && !isTextLine(getItem(position - 1))
                setData(holder, data, isLastOneImage = isLastImage)
            } else {
                setData(holder, data, false)
            }
        } else {
            setRelevantNews(holder)
        }

    }

    private fun setRelevantNews(holder: ViewHolder) {
        if (relevantNews.isEmpty()) {
            holder.itemView.visibility = View.GONE
        } else {
            holder.itemView.visibility = View.VISIBLE
        }
        if (!initFooter) {
            val layoutView = holder.itemView.findViewById<LinearLayout>(R.id.relevant_item_layout)
            relevantNews.forEach { relativeNews ->
                val view = LayoutInflater.from(holder.itemView.context).inflate(R.layout.news_detail_relevant_news_item, layoutView, false)
                val title = view.findViewById<TextView>(R.id.relevant_news_title)
                val date = view.findViewById<TextView>(R.id.relevant_news_date)
                title.text = relativeNews.title
                date.text = relativeNews.date
                view.setOnClickListener {
                    onRelevantNewsClickListner?.onClick(it, relativeNews)
                }
                layoutView.addView(view)
            }
            initFooter = true
        }
    }

    fun setOnRelevantNewsClickListner(listner: onRelevantNewsClick) {
        this.onRelevantNewsClickListner = listner
    }

    private fun isTextLine(data: NewsDetailContent) =
            data.type == TYPE_TEXT

    private fun setData(holder: ViewHolder, data: NewsDetailContent, isInfoText: Boolean = true, isLastOneImage: Boolean = false) {
        val typedValue = TypedValue()
        val theme = holder.itemView.context.theme
        if (isInfoText) {
            if (isLastOneImage) {
                holder.textView?.typeface = Typeface.DEFAULT_BOLD
                theme.resolveAttribute(android.R.attr.textColorPrimary, typedValue, true)
            } else {
                theme.resolveAttribute(android.R.attr.textColorSecondary, typedValue, true)
                holder.textView?.typeface = Typeface.DEFAULT
            }
            holder.textView?.visibility = View.VISIBLE
            holder.imageView?.visibility = View.GONE
            holder.textView?.text = data.content.replace("\r", "").replace("\n", "").replace("\t", "")
        } else {
            holder.textView?.visibility = View.GONE
            holder.imageView?.visibility = View.VISIBLE
            glide.loadImageFromServerToList(data.compressImg ?: data.content, holder.imageView
                    ?: return)
            holder.imageView.setOnClickListener {
                ViewImageUtils.setViewImageClick(holder.itemView.context, holder.imageView, images, compressImages, images.indexOf(data.content))
            }
        }
    }

    override fun getItemCount(): Int {
        return datas.size + 1
    }

    private object ViewType {
        const val NORMAL = 1
        const val FOOTER = 2
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < datas.size) return ViewType.NORMAL else ViewType.FOOTER
    }

    interface onRelevantNewsClick {
        fun onClick(v: View, news: NewsDetailRelativeNews)
    }

}