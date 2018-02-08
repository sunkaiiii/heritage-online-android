package com.example.sunkai.heritage.Adapter

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.sunkai.heritage.Activity.NewsDetailActivity
import com.example.sunkai.heritage.Activity.SeeMoreNewsActivity
import com.example.sunkai.heritage.Adapter.BaseAdapter.BaseRecyclerAdapter
import com.example.sunkai.heritage.ConnectWebService.BaseSettingNew
import com.example.sunkai.heritage.Data.FolkNewsLite
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.value.CATEGORIES

/**
 * 首页新闻卡片的adapter
 * Created by sunkai on 2018/2/8.
 */
class MainNewsAdapter(private val context: Context, private val news: List<List<FolkNewsLite>>) : BaseRecyclerAdapter() {

    class NewsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView:ImageView
        val firstText:TextView
        val linearLayout:LinearLayout
        val textView:TextView
        var isSetItem=false
        init {
            imageView=view.findViewById(R.id.main_news_image)
            firstText=view.findViewById(R.id.main_page_image_first_news_text)
            linearLayout=view.findViewById(R.id.main_news_other_news)
            textView=view.findViewById(R.id.see_more)
        }
    }

    override fun getItem(position: Int): Any {
        return news[position]
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.main_news_card, parent, false)
        view.setOnClickListener(this)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        super.onBindViewHolder(holder, position)
        if(holder is NewsViewHolder){
            setClick(position,holder)
            setDatas(position,holder)
        }
    }

    private fun setClick(position: Int,holder: NewsViewHolder){
        val category= CATEGORIES[position]
        holder.textView.setOnClickListener {
            val intent= Intent(context,SeeMoreNewsActivity::class.java)
            intent.putExtra("category",category)
            context.startActivity(intent)
        }
    }

    private fun setDatas(position: Int,holder: NewsViewHolder){
        val data=news[position]
        for ((i,item) in data.withIndex()) {
            if(i==0){
                setFirstNews(item,holder)
            }else{
                if(!holder.isSetItem)
                    setScondlyNews(item, holder)
            }
        }
        holder.isSetItem=true
    }

    private fun setFirstNews(item:FolkNewsLite,holder: NewsViewHolder){
        holder.firstText.text = item.title
        if(TextUtils.isEmpty(item.img)){
            holder.imageView.visibility=View.GONE
        }else {
            Glide.with(context).load(BaseSettingNew.URL + item.img).into(holder.imageView)
        }
    }

    private fun setScondlyNews(item:FolkNewsLite,holder:NewsViewHolder){
        val itemView=LayoutInflater.from(context).inflate(R.layout.main_news_item,holder.linearLayout,false)
        val itemHolder=secondlyView(itemView)
        itemView.setOnClickListener {
            val intent=Intent(context,NewsDetailActivity::class.java)
            intent.putExtra("id",item.id)
            context.startActivity(intent)
        }
        itemHolder.title.text=item.title
        if(TextUtils.isEmpty(item.img)){
            itemHolder.image.visibility=View.GONE
        }else {
            Glide.with(context).load(BaseSettingNew.URL + item.img).into(simpleTarget(itemHolder))
        }
        holder.linearLayout.addView(itemView)
    }

    private class secondlyView(view:View){
        val image:ImageView
        val title:TextView
        init {
            image=view.findViewById(R.id.news_item_image)
            title=view.findViewById(R.id.news_item_title)
        }
    }

    private class simpleTarget(private val holder: secondlyView):SimpleTarget<Drawable>(){
        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
            holder.image.setImageDrawable(resource)
        }

        override fun onLoadFailed(errorDrawable: Drawable?) {
            holder.image.visibility=View.GONE
        }

    }
    override fun getItemCount(): Int {
        return news.size
    }
}