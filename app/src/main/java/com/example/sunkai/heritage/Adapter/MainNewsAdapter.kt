package com.example.sunkai.heritage.Adapter

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.sunkai.heritage.Activity.NewsDetailActivity
import com.example.sunkai.heritage.Activity.SeeMoreNewsActivity
import com.example.sunkai.heritage.Adapter.BaseAdapter.BaseRecyclerAdapter
import com.example.sunkai.heritage.ConnectWebService.BaseSetting
import com.example.sunkai.heritage.Data.FolkNewsLite
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.GlobalContext
import com.example.sunkai.heritage.tools.ViewImageUtils
import com.example.sunkai.heritage.tools.generateDarkColor
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
        holder.categoryBackGroundImageView.setImageBitmap(null)
        holder.categoryTextView.text = CATEGORIES[position]
        val secondlyView = secondViewHolders[position]
        secondlyView?.forEach {
            it.image.setImageBitmap(null)
        }
        getCategoryImage(position, holder.categoryBackGroundImageView, holder.linearLayout, holder.seeMoreTextview)
        setData(holder, position)
        setCardClick(position, holder)
    }

    private fun getCategoryImage(position: Int, imageView: ImageView, linearLayout: LinearLayout, seeMoreTextview: TextView) {
        glide.load(HOST + "/img/main_news_divide_img/" + MAIN_PAGE_CATEGORY_NEWS_IMAGE[position]).into(CategoryImageSimpleTarget(imageView, linearLayout, seeMoreTextview))
    }


    private fun setData(holder: Holder, position: Int) {
        val data = getItem(position)
        holder.itemViews.withIndex().forEach {
            if (it.index == data.size) {
                return
            }
            val item = data[it.index]
            val itemView = it.value
            setItemClick(itemView, item)
            itemView.title.text = item.title
            if (item.img.isEmpty()) {
                itemView.image.visibility = View.INVISIBLE
            } else {
                glide.load(BaseSetting.URL + item.img).into(ItemsimpleTarget(itemView))
                itemView.image.setOnClickListener {
                    ViewImageUtils.setViewImageClick(context, it as ImageView, item.img)
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
            if (context is Activity && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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

    private class CategoryImageSimpleTarget(val imageView: ImageView, val linearLayout: LinearLayout, val seeMoreTextView: TextView) : SimpleTarget<Drawable>() {
        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
            val darkColor = resource.generateDarkColor()
            val animation = AnimationUtils.loadAnimation(GlobalContext.instance, R.anim.fade_in_quick)
            imageView.setImageDrawable(resource)
            imageView.startAnimation(animation)
            linearLayout.children.forEach { it.findViewById<TextView>(R.id.news_item_title)?.setTextColor(darkColor) }
            seeMoreTextView.setTextColor(darkColor)
        }
    }
}