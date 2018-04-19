package com.example.sunkai.heritage.Adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.RequestManager
import com.example.sunkai.heritage.Adapter.BaseAdapter.BaseLoadMoreRecyclerAdapter
import com.example.sunkai.heritage.ConnectWebService.BaseSetting
import com.example.sunkai.heritage.Data.BottomFolkNewsLite
import com.example.sunkai.heritage.R
import com.google.gson.Gson

/**
 * 首页底部聚焦非遗的adapter
 * Created by sunkai on 2018/2/12.
 */
class BottomFolkNewsRecyclerviewAdapter(val context: Context, datas: List<BottomFolkNewsLite>,glide: RequestManager) : BaseLoadMoreRecyclerAdapter<BottomFolkNewsRecyclerviewAdapter.Holder, BottomFolkNewsLite>(datas,glide) {

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView
        val time: TextView
        val briefly: TextView
        val image: ImageView
        val bottomLinear: LinearLayout
        private val bottomImage1: ImageView
        private val bottomImage2: ImageView
        private val bottomImage3: ImageView
        val imageViews: Array<ImageView>

        init {
            title = view.findViewById(R.id.bottom_view_title)
            time = view.findViewById(R.id.bottom_view_time)
            briefly = view.findViewById(R.id.bottom_view_briefly)
            image = view.findViewById(R.id.bottom_view_image)
            bottomLinear = view.findViewById(R.id.bottom_view_bottom_images_linear)
            bottomImage1 = view.findViewById(R.id.bottom_view_bottom_image1)
            bottomImage2 = view.findViewById(R.id.bottom_view_bottom_image2)
            bottomImage3 = view.findViewById(R.id.bottom_view_bottom_image3)
            imageViews = arrayOf(bottomImage1, bottomImage2, bottomImage3)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.bottom_folk_news_layout, parent, false)
        view.setOnClickListener(this)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        super.onBindViewHolder(holder, position)
        val data = getItem(position)
        setData(holder, data)
    }

    private fun setData(holder: Holder, data: BottomFolkNewsLite) {
        holder.title.text = data.title
        holder.time.text = data.time
        holder.briefly.text = data.briefly
        try {
            val imgArray = Gson().fromJson(data.img, Array<String>::class.java)
            //判断有几张图片，根据图片的数目不同item展示不同的样式
            when {
                imgArray.isEmpty() -> {
                    holder.image.visibility = View.GONE
                    holder.bottomLinear.visibility = View.GONE
                    holder.briefly.visibility = View.VISIBLE
                }
                imgArray.size < 3 -> {
                    holder.image.visibility = View.VISIBLE
                    glide.load(BaseSetting.URL + imgArray[0]).into(holder.image)
                    holder.bottomLinear.visibility = View.GONE
                    holder.briefly.visibility = View.GONE
                }
                else -> {
                    holder.image.visibility = View.GONE
                    holder.bottomLinear.visibility = View.VISIBLE
                    holder.briefly.visibility = View.VISIBLE
                    for ((position, imageview) in holder.imageViews.withIndex()) {
                        glide.load(BaseSetting.URL + imgArray[position]).into(imageview)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return
        }
    }

    override fun addNewData(datas: List<BottomFolkNewsLite>) {
        val extendData = this.datas.toMutableList()
        extendData.addAll(datas)
        this.datas = extendData
        notifyDataSetChanged()
    }
}