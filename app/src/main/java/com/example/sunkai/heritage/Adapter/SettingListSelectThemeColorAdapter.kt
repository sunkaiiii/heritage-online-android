package com.example.sunkai.heritage.Adapter

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.sunkai.heritage.Adapter.BaseAdapter.BaseRecyclerAdapter
import com.example.sunkai.heritage.tools.Utils
import com.example.sunkai.heritage.tools.getSelectGradientDrawableColor

class SettingListSelectThemeColorAdapter(context: Context, datas: List<String>, glide: RequestManager) : BaseRecyclerAdapter<SettingListSelectThemeColorAdapter.Holder, String>(context, datas, glide) {

    companion object {
        val IMAGE_VIEW_SIZE = Utils.dip2px(48)
    }

    private val tintGradientDrawableMap:Map<String,Int>
    init {
        val tempMap=HashMap<String,Int>()
        datas.forEach {
            tempMap[it]= getSelectGradientDrawableColor(it)
        }
        tintGradientDrawableMap=HashMap(tempMap)
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
        initImageView(holder.colorImageView, colorString)
    }

    private fun initImageView(view: View, colorString: String) {
        val color = Color.parseColor(colorString)
        val layputParames = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        layputParames.height = IMAGE_VIEW_SIZE
        layputParames.width = IMAGE_VIEW_SIZE
        view.layoutParams = layputParames
        view.setBackgroundColor(color)
        if(view is ImageView) {
            val drawable=GradientDrawable()
            drawable.setStroke(Utils.dip2px(4),tintGradientDrawableMap[colorString]?:return)
            view.setImageDrawable(drawable)
        }
    }

}