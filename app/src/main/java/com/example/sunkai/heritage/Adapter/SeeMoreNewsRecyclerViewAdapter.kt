package com.example.sunkai.heritage.Adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.sunkai.heritage.Adapter.BaseAdapter.BaseLoadMoreRecyclerAdapter
import com.example.sunkai.heritage.ConnectWebService.BaseSettingNew
import com.example.sunkai.heritage.Data.FolkNewsLite
import com.example.sunkai.heritage.R

/**
 * 点击更多之后，展示全部内容的adapter
 * Created by sunkai on 2018/2/9.
 */
class SeeMoreNewsRecyclerViewAdapter(val context:Context,datas:List<FolkNewsLite>):BaseLoadMoreRecyclerAdapter<SeeMoreNewsRecyclerViewAdapter.ViewHolder,FolkNewsLite>(datas) {

    class ViewHolder(view:View):RecyclerView.ViewHolder(view){
        val textview:TextView
        val imageview:ImageView
        init {
            textview=view.findViewById(R.id.news_item_title)
            imageview=view.findViewById(R.id.news_item_image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view=LayoutInflater.from(context).inflate(R.layout.main_news_item,parent,false)
        view.setOnClickListener(this)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        super.onBindViewHolder(holder, position)
        holder?.let{
            setDatas(holder,position)
        }
    }

    private fun setDatas(holder:ViewHolder,position: Int){
        val data=datas[position]
        holder.textview.text=data.title
        //防止复用的时候不显示图片的问题
        holder.imageview.visibility=View.VISIBLE
        if(TextUtils.isEmpty(data.img)){
            holder.imageview.visibility=View.GONE
        }else {
            Glide.with(context).load(BaseSettingNew.URL + data.img).into(SimpleTaget(holder.imageview))
        }
    }

    private class SimpleTaget(val imageView: ImageView):SimpleTarget<Drawable>(){
        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
            imageView.setImageDrawable(resource)
        }

        override fun onLoadFailed(errorDrawable: Drawable?) {
            imageView.visibility=View.GONE
        }
    }
    override fun addNewData(datas: List<FolkNewsLite>) {
        val mutableDatas=this.datas.toMutableList()
        if(mutableDatas.addAll(datas)){
            this.datas=mutableDatas
            notifyDataSetChanged()
        }
    }

}