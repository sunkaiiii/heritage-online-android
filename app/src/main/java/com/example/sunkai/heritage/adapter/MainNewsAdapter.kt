package com.example.sunkai.heritage.adapter

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.sunkai.heritage.activity.NewsDetailActivity
import com.example.sunkai.heritage.activity.SeeMoreNewsActivity
import com.example.sunkai.heritage.adapter.baseAdapter.BaseRecyclerAdapter
import com.example.sunkai.heritage.connectWebService.BaseSetting
import com.example.sunkai.heritage.entity.FolkNewsLite
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.GlobalContext
import com.example.sunkai.heritage.tools.ViewImageUtils
import com.example.sunkai.heritage.tools.getThemeColor
import com.example.sunkai.heritage.tools.tintTextView
import com.example.sunkai.heritage.value.CATEGORIES
import com.example.sunkai.heritage.value.HOST
import com.example.sunkai.heritage.value.MAIN_PAGE_CATEGORY_NEWS_IMAGE

class MainNewsAdapter(context: Context, datas: List<List<FolkNewsLite>>, glide: RequestManager) : BaseRecyclerAdapter<MainNewsAdapter.Holder, List<FolkNewsLite>>(context, datas, glide) {
    private val secondViewHolders = mutableMapOf<Int, MutableList<SecondlyView>>()

    class Holder(view: View, context: Context) : RecyclerView.ViewHolder(view) {
        val seeMoreTextview: TextView = view.findViewById(R.id.see_more)
        val linearLayout: LinearLayout = view.findViewById(R.id.main_news_other_news)
        val categoryTextView: TextView = view.findViewById(R.id.main_news_card_category)
        val categoryBackGroundImageView: ImageView = view.findViewById(R.id.main_news_card_category_image)
        val itemViews: MutableList<SecondlyView> = mutableListOf()

        init {
            for (i in 0..2) {
                val secondView = LayoutInflater.from(context).inflate(R.layout.main_news_item, linearLayout, false)
                linearLayout.addView(secondView)
                itemViews.add(SecondlyView(secondView))
            }
        }
    }

    class SecondlyView(view: View) {
        val image: ImageView = view.findViewById(R.id.news_item_image)
        val title: TextView = view.findViewById(R.id.news_item_title)
        val itemView: View = view
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.main_news_card, parent, false)
        return Holder(view, context)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.categoryTextView.text = CATEGORIES[position]
        val secondlyView = secondViewHolders[position]
        secondlyView?.forEach {
            it.image.setImageBitmap(null)
        }
        setThemeColor(holder)
        getCategoryImage(position, holder.categoryBackGroundImageView)
        setData(holder, position)
        setCardClick(position, holder)
    }

    private fun getCategoryImage(position: Int, imageView: ImageView) {
        glide.load(HOST + "/img/main_news_divide_img/" + MAIN_PAGE_CATEGORY_NEWS_IMAGE[position]).into(CategoryImageSimpleTarget(imageView))
    }

    private fun setThemeColor(holder: Holder) {
        val themeColor = getThemeColor()
        holder.seeMoreTextview.setTextColor(themeColor)
        tintTextView(holder.seeMoreTextview)
    }


    private fun setData(holder: Holder, position: Int) {
        val data = getItem(position)
        holder.itemViews.withIndex().forEach { secondView ->
            if (secondView.index == data.size) {
                return
            }
            val item = data[secondView.index]
            val itemView = secondView.value
            setItemClick(itemView, item)
            itemView.title.text = item.title
            if (item.img.isEmpty()) {
                itemView.image.visibility = View.INVISIBLE
            } else {
                glide.load(BaseSetting.URL + item.img).into(ItemsimpleTarget(itemView))
                itemView.image.setOnClickListener {
                    ViewImageUtils.setViewImageClick(context, it, item.img)
                }
            }
        }
    }

    private fun setCardClick(position: Int, holder: Holder) {
        val category = CATEGORIES[position]
        holder.seeMoreTextview.setOnClickListener {
            loadSeemoreNewsActivity(category)
        }
        holder.categoryBackGroundImageView.setOnClickListener {
            loadSeemoreNewsActivity(category)
        }
    }

    private fun setItemClick(views: SecondlyView, data: FolkNewsLite) {
        val title = views.title
        views.itemView.setOnClickListener {
            val intent = Intent(context, NewsDetailActivity::class.java)
            intent.putExtra("data", data)
            intent.putExtra("category", data.category)
            if (context is Activity) {
                context.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(context, title, context.getString(R.string.news_detail_share_title)).toBundle())
            } else {
                context.startActivity(intent)
            }
        }
    }

    private fun loadSeemoreNewsActivity(category: String) {
        val intent = Intent(context, SeeMoreNewsActivity::class.java)
        intent.putExtra("category", category)
        context.startActivity(intent)
    }

    private class ItemsimpleTarget(private val holder: SecondlyView) : SimpleTarget<Drawable>() {
        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
            holder.image.setImageDrawable(resource)
        }

        override fun onLoadFailed(errorDrawable: Drawable?) {
            holder.image.visibility = View.INVISIBLE
        }

    }

    private class CategoryImageSimpleTarget(val imageView: ImageView) : SimpleTarget<Drawable>() {
        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
            val animation = AnimationUtils.loadAnimation(GlobalContext.instance, R.anim.fade_in_quick)
            imageView.setImageDrawable(resource)
            imageView.startAnimation(animation)
        }
    }
}