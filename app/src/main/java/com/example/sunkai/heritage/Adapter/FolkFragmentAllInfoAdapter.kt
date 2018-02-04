package com.example.sunkai.heritage.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.sunkai.heritage.Data.FolkDataLite
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.value.HOST

/*
 * Created by sunkai on 2018/2/4.
 */
class FolkFragmentAllInfoAdapter(val datas:List<FolkDataLite>,val context:Context):PagerAdapter(){
    override fun isViewFromObject(view: View, `object`: Any): Boolean {
       return view==`object`
    }

    override fun getCount(): Int {
        return datas.size
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View )
    }

    @SuppressLint("InflateParams")
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view=LayoutInflater.from(context).inflate(R.layout.folk_listview_layout,null)
        val listImage: ImageView=view.findViewById(R.id.list_img)
        val listTitle: TextView=view.findViewById(R.id.list_title)
        val listLocation: TextView=view.findViewById(R.id.list_location)
        val listDivide: TextView=view.findViewById(R.id.list_divide)
        val data= datas[position]
        listTitle.text=data.title
        listDivide.text=data.divide
        listLocation.text=data.category
        Glide.with(context).load(HOST +data.img).into(listImage)
        container.addView(view)
        return view
    }

}