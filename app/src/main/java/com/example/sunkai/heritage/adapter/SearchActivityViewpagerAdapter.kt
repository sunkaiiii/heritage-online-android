package com.example.sunkai.heritage.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter

class SearchActivityViewpagerAdapter(val context:Context,val divide:Array<String>,val searchType:Array<String>): PagerAdapter() {
    val views:MutableList<View>
    init {
        views= arrayListOf()
        divide.forEach {
            val recyclerView= RecyclerView(context)
            recyclerView.layoutManager= LinearLayoutManager(context)
            val layoutParams= RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT)
            layoutParams.height=ViewGroup.LayoutParams.MATCH_PARENT
            layoutParams.width=ViewGroup.LayoutParams.MATCH_PARENT
            recyclerView.layoutParams=layoutParams
            views.add(recyclerView)
        }
    }
    var isDivideSetData= Array(divide.size,{false})
    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return `object`==view
    }


    override fun getCount(): Int {
        return divide.size
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
        isDivideSetData[position]=false
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view=views[position]
        container.addView(view)
        return view
    }
}