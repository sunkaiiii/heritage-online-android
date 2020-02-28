package com.example.sunkai.heritage.adapter

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.RequestManager
import com.example.sunkai.heritage.activity.NewsDetailActivity
import com.example.sunkai.heritage.entity.MainPageBanner
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.loadImageFromServer

/**
 * 显示更多新闻顶部滑动条的adapter
 * Created by sunkai on 2018/2/18.
 */
class MainPageBannerAdapter(val context:Context, val data:List<MainPageBanner>, val glide: RequestManager): PagerAdapter() {
    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view==`object`
    }

    override fun getCount(): Int {
        return Int.MAX_VALUE
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view=LayoutInflater.from(context).inflate(R.layout.main_page_slide_item,container,false)
        val image=view.findViewById<ImageView>(R.id.main_page_slide_image)
        val data=data[position%data.size]
        glide.loadImageFromServer(data.img).into(image)
        container.addView(view)
        view.setOnClickListener {
            val intent=setViewClick(data)
            if(context is Activity) {
                context.startActivity(intent,ActivityOptions.makeSceneTransitionAnimation(context).toBundle())
                return@setOnClickListener
            }
            context.startActivity(intent)
        }
        return view
    }

    private fun setViewClick(data:MainPageBanner):Intent{
        val intent= Intent(context,NewsDetailActivity::class.java)
        intent.putExtra("category","要闻")
        intent.putExtra("data",data)
        return intent
    }
}