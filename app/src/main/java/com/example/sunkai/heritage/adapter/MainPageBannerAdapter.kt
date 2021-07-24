package com.example.sunkai.heritage.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.sunkai.heritage.activity.WebViewActivity
import com.example.sunkai.heritage.databinding.MainPageSlideItemBinding
import com.example.sunkai.heritage.entity.MainPageBanner
import com.example.sunkai.heritage.tools.loadImageFromServer
import com.example.sunkai.heritage.value.IHCHINA
import com.example.sunkai.heritage.value.URL

/**
 * 显示更多新闻顶部滑动条的adapter
 * Created by sunkai on 2018/2/18.
 */
class MainPageBannerAdapter(
    val context: Context,
    val data: List<MainPageBanner>,
    val glide: RequestManager
) : RecyclerView.Adapter<MainPageBannerAdapter.Holder>() {
    class Holder(binding: MainPageSlideItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val image = binding.mainPageSlideImage
    }


    private fun getIntent(data: MainPageBanner): Intent {
        val intent = Intent(context, WebViewActivity::class.java)
        intent.putExtra(URL, IHCHINA + data.link)
        return intent
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding =
            MainPageSlideItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        instantiateItem(holder, position)
    }


    fun instantiateItem(holder: Holder, position: Int) {
        val image = holder.image
        val data = data[position % data.size]
        glide.loadImageFromServer(data.compressImg ?: data.img).into(image)
        holder.image.setOnClickListener {
            val intent = getIntent(data)
            context.startActivity(intent)
        }
    }


    override fun getItemCount(): Int = Int.MAX_VALUE

}