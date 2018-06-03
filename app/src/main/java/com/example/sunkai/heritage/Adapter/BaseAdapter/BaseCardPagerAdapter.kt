package com.example.sunkai.heritage.Adapter.BaseAdapter

import android.content.Context
import androidx.cardview.widget.CardView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sunkai.heritage.R

/**CardViewPager的基类
 * 来源于开源项目 https://github.com/rubensousa/ViewPagerCards
 * Created by sunkai on 2018/2/9.
 */
abstract class BaseCardPagerAdapter(private val views:MutableList<androidx.cardview.widget.CardView>):CardAdapter() {
    private var baseElevation:Float=0F
    override fun getBaseElevation(): Float {
        return baseElevation
    }

    override fun getCardViewAt(position: Int): androidx.cardview.widget.CardView {
        return views[position]
    }

    override fun getCount(): Int {
        return views.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view==`object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view=LayoutInflater.from(container.context).inflate(R.layout.main_news_card,container,false)
        container.addView(view)
        val cardView=view.findViewById<androidx.cardview.widget.CardView>(R.id.main_news_cardview)
        if(baseElevation==0F){
            baseElevation=cardView.cardElevation
        }
        cardView.maxCardElevation=baseElevation*MAX_ELEVATION_FACTOR
        views[position] = cardView
        setDataInView(view, position, container.context)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    abstract fun setDataInView(view:View,position: Int,context:Context)

}