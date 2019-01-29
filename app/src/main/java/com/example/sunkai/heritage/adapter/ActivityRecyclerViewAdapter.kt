package com.example.sunkai.heritage.adapter


import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.sunkai.heritage.adapter.baseAdapter.BaseRecyclerAdapter
import com.example.sunkai.heritage.entity.ClassifyDivideData
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.HandleAdapterItemClickClickUtils
import com.example.sunkai.heritage.value.HOST

/*
 * Created by sunkai on 2017/12/22.
 */

class ActivityRecyclerViewAdapter(context: Context,datas:List<ClassifyDivideData>,glide:RequestManager) : BaseRecyclerAdapter<ActivityRecyclerViewAdapter.ViewHolder, ClassifyDivideData>(context,datas,glide) {

    private var imageAnimation: Animation//图片出现动画


    class ViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        val img: ImageView
        val title: TextView
        val time: TextView
        val number: TextView
        val location: TextView
        val content: TextView

        init {
            img = view.findViewById(R.id.activity_layout_img)
            title = view.findViewById(R.id.activity_layout_title)
            time = view.findViewById(R.id.activity_layout_time)
            number = view.findViewById(R.id.activity_layout_number)
            location = view.findViewById(R.id.activity_layout_location)
            content = view.findViewById(R.id.activity_layout_content)
        }
    }

    init {
        imageAnimation = AnimationUtils.loadAnimation(context, R.anim.image_apear)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.activity_layout, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val data = datas[position]
        holder.title.text = data.title
        holder.location.text = "地区:" + data.location
        holder.time.text = "时间:" + data.time
        holder.number.text = "编号:" + data.number
        holder.content.text = data.content
        holder.img.setImageResource(R.drawable.empty_background)
        glide.load(HOST + data.img).into(holder.img)
    }

    override fun setItemClick() {
        HandleAdapterItemClickClickUtils.handleActivityRecyclerViewItemClick(context,this)
    }

}