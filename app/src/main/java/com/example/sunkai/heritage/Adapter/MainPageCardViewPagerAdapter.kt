package com.example.sunkai.heritage.Adapter

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.support.v7.widget.CardView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.sunkai.heritage.Activity.NewsDetailActivity
import com.example.sunkai.heritage.Activity.SeeMoreNewsActivity
import com.example.sunkai.heritage.Adapter.BaseAdapter.BaseCardPagerAdapter
import com.example.sunkai.heritage.ConnectWebService.BaseSettingNew
import com.example.sunkai.heritage.Data.FolkNewsLite
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.value.CATEGORIES

/**
 * 首页viewapger卡片的adapter
 * Created by sunkai on 2018/2/9.
 */
class MainPageCardViewPagerAdapter(views:MutableList<CardView>,val datas:List<List<FolkNewsLite>>):BaseCardPagerAdapter(views) {
    override fun setDataInView(view: View, position: Int, context: Context) {
        val textView=view.findViewById<TextView>(R.id.see_more)
        val linearLayout=view.findViewById<LinearLayout>(R.id.main_news_other_news)
        val categoryTextView=view.findViewById<TextView>(R.id.main_news_card_category)
        categoryTextView.text= CATEGORIES[position]
        setClick(position,textView,context)
        val postionList=datas[position]
        for(data in postionList){
            setScondlyNews(data,linearLayout,context)
        }
    }
    private fun setClick(position: Int, textView: TextView,context: Context) {
        val category = CATEGORIES[position]
        textView.setOnClickListener {
            val intent = Intent(context, SeeMoreNewsActivity::class.java)
            intent.putExtra("category", category)
            context.startActivity(intent)
        }
    }
    private fun setScondlyNews(item: FolkNewsLite, linearLayout: LinearLayout,context: Context) {
        val itemView = LayoutInflater.from(context).inflate(R.layout.main_news_item, linearLayout, false)
        val itemHolder = secondlyView(itemView)
        itemView.setOnClickListener {
            val intent = Intent(context, NewsDetailActivity::class.java)
            intent.putExtra("id", item.id)
            context.startActivity(intent)
        }
        itemHolder.title.text = item.title
        if (TextUtils.isEmpty(item.img)) {
            itemHolder.image.visibility = View.GONE
        } else {
            Glide.with(context).load(BaseSettingNew.URL + item.img).into(simpleTarget(itemHolder))
        }
        linearLayout.addView(itemView)
    }

    private class secondlyView(view: View) {
        val image: ImageView
        val title: TextView
        init {
            image = view.findViewById(R.id.news_item_image)
            title = view.findViewById(R.id.news_item_title)
        }
    }

    private class simpleTarget(private val holder: secondlyView) : SimpleTarget<Drawable>() {
        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
            holder.image.setImageDrawable(resource)
        }

        override fun onLoadFailed(errorDrawable: Drawable?) {
            holder.image.visibility = View.GONE
        }

    }
}