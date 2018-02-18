package com.example.sunkai.heritage.Adapter

import android.content.Context
import android.content.Intent
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.sunkai.heritage.Activity.NewsDetailActivity
import com.example.sunkai.heritage.ConnectWebService.BaseSettingNew
import com.example.sunkai.heritage.Data.MainPageSlideNews
import com.example.sunkai.heritage.R

/**
 * 显示更多新闻顶部滑动条的adapter
 * Created by sunkai on 2018/2/18.
 */
class MainPageSlideAdapter(val context:Context,val datas:List<MainPageSlideNews>):PagerAdapter() {
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
        val title=view.findViewById<TextView>(R.id.main_page_slide_title)
        val data=datas[position%datas.size]
        title.text=data.content
        Glide.with(context).load(BaseSettingNew.URL+data.img).into(image)
        container.addView(view)
        view.setOnClickListener({
            val intent=setViewClick(data)
            context.startActivity(intent)
        })
        return view
    }

    private fun setViewClick(data:MainPageSlideNews):Intent{
        val intent= Intent(context,NewsDetailActivity::class.java)
        intent.putExtra("data",data)
        return intent
    }
}