package com.example.sunkai.heritage.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.RequestManager
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.activity.NewsDetailActivity
import com.example.sunkai.heritage.connectWebService.EHeritageApi
import com.example.sunkai.heritage.entity.response.PeopleMainPageResponse
import com.example.sunkai.heritage.tools.loadImageFromServer
import com.example.sunkai.heritage.views.tools.RectangleImageView
import com.example.sunkai.heritage.value.API
import com.example.sunkai.heritage.value.DATA

class FragmentPeopleBannerAdapter(private val context: Context, private val data:List<PeopleMainPageResponse.PeopleBannerTableContent>,private val glide:RequestManager):PagerAdapter() {
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
        val layout=LayoutInflater.from(context).inflate(R.layout.fragment_people_banner_layout,container,false)
        val imageView=layout.findViewById<RectangleImageView>(R.id.bannerImageView)
        val textView=layout.findViewById<TextView>(R.id.bannerIamgeDescTextView)
        val data=data[position%data.size]
        glide.loadImageFromServer(data.img).into(imageView)
        textView.text=data.desc
        layout.setOnClickListener {
            val intent=Intent(context, NewsDetailActivity::class.java)
            intent.putExtra(DATA,data.link)
            intent.putExtra(API,EHeritageApi.GetPeopleDetail)
            context.startActivity(intent)
        }
        container.addView(layout)
        return layout
    }
}