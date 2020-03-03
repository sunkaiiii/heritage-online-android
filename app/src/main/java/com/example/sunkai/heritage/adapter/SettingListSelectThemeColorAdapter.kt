package com.example.sunkai.heritage.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.sunkai.heritage.activity.base.BaseGlideActivity
import com.example.sunkai.heritage.activity.MainActivity
import com.example.sunkai.heritage.adapter.baseAdapter.BaseRecyclerAdapter
import com.example.sunkai.heritage.tools.*

class SettingListSelectThemeColorAdapter(context: Context, datas: List<String>, glide: RequestManager) : BaseRecyclerAdapter<SettingListSelectThemeColorAdapter.Holder, String>(context, datas, glide) {

    companion object {
        val IMAGE_VIEW_SIZE = Utils.dip2px(48)
    }

    private val tintGradientDrawableMap: Map<String, Int>

    init {
        val tempMap = HashMap<String, Int>()
        datas.forEach {
            tempMap[it] = getSelectGradientDrawableColor(it)
        }
        tintGradientDrawableMap = HashMap(tempMap)
    }

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val colorImageView = view
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(ImageView(context))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        super.onBindViewHolder(holder, position)
        val colorString = getItem(position)
        initImageView(holder.colorImageView as ImageView, colorString)
    }

    private fun initImageView(view: ImageView, colorString: String) {
        view.setImageDrawable(null)
        val color = Color.parseColor(colorString)
        val layputParames = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        layputParames.height = IMAGE_VIEW_SIZE
        layputParames.width = IMAGE_VIEW_SIZE
        view.layoutParams = layputParames
        view.setBackgroundColor(color)
        if (color == getThemeColor()) {
            val drawable = GradientDrawable()
            drawable.setStroke(Utils.dip2px(4), tintGradientDrawableMap[colorString] ?: return)
            view.setImageDrawable(drawable)
        }
    }

    override fun setItemClick(itemView: View, item: String) {
        setOnItemClickListener { view, position ->
            val color = Color.parseColor(getItem(position))
            setThemeColor(color)
            notifyDataSetChanged()
            if (context is BaseGlideActivity) {
                context.changeWidgeTheme()
            }
            //因为viewpager使用的反射方式修改的阴影
            //在主题颜色修改之后，需要重新设置阴影
            resetMainActivityViewPager()
        }
    }

    private fun resetMainActivityViewPager() {
        val viewPager = MainActivity.mainViewPagerRef?.get() ?: return
        viewPager.initEdge()
    }
}