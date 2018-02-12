package com.example.sunkai.heritage.Adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.sunkai.heritage.Adapter.BaseAdapter.BaseRecyclerAdapter
import com.example.sunkai.heritage.ConnectWebService.BaseSettingNew
import com.example.sunkai.heritage.Data.BottomFolkNewsLite
import com.example.sunkai.heritage.R
import kotlinx.android.synthetic.main.bottom_folk_news_layout.view.*

/**
 * Created by sunkai on 2018/2/12.
 */
class BottomFolkNewsRecyclerviewAdapter(val context: Context,datas:List<BottomFolkNewsLite>):BaseRecyclerAdapter<BottomFolkNewsRecyclerviewAdapter.Holder,BottomFolkNewsLite>(datas){

    class Holder(view: View):RecyclerView.ViewHolder(view){
        val title:TextView
        val time:TextView
        val briefly:TextView
        val image:ImageView
        init {
            title=view.findViewById(R.id.bottom_view_title)
            time=view.findViewById(R.id.bottom_view_time)
            briefly=view.findViewById(R.id.bottom_view_briefly)
            image=view.findViewById(R.id.bottom_view_image)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): Holder {
        val view=LayoutInflater.from(context).inflate(R.layout.bottom_folk_news_layout,parent,false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder?, position: Int) {
        super.onBindViewHolder(holder, position)
        holder?.let {
            val data = getItem(position)
            setData(holder, data)
        }
    }

    private fun setData(holder:Holder,data:BottomFolkNewsLite){
        holder.title.text=data.title
        holder.time.text=data.time
        holder.briefly.text=data.briefly
        //TODO 数据库图片url不对，待修复
        if(data.img.isEmpty()){
            holder.image.visibility=View.GONE
        }else {
            holder.image.visibility=View.VISIBLE
            Glide.with(context).load(BaseSettingNew.URL + "/img" + data.img).into(holder.image)
        }
    }
}