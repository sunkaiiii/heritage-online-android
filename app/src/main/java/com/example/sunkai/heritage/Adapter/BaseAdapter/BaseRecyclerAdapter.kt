package com.example.sunkai.heritage.Adapter.BaseAdapter

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.sunkai.heritage.tools.Views.FollowThemeEdgeRecyclerView
import com.bumptech.glide.RequestManager
import com.example.sunkai.heritage.Interface.OnItemClickListener
import com.example.sunkai.heritage.Interface.OnItemLongClickListener
import com.example.sunkai.heritage.Interface.OnPageLoaded

/**
 * Created by sunkai on 2018/1/2.
 * 给RecyclerAdapter封装了一些点击的操作
 */
abstract class BaseRecyclerAdapter<T: RecyclerView.ViewHolder, W>(protected val context: Context, datas:List<W>, val glide: RequestManager) : RecyclerView.Adapter<T>(),View.OnClickListener,View.OnLongClickListener, OnPageLoaded {
    private var mOnItemClickListener: OnItemClickListener? = null
    private var mOnItemLongClickListener: OnItemLongClickListener? = null
    protected var mOnPagedListener:OnPageLoaded?=null
    protected var datas:MutableList<W>
    init {
        this.datas=datas.toMutableList()
    }

    override fun onClick(v: View) {
        mOnItemClickListener?.onItemClick(v,v.tag as Int)
    }

    override fun onLongClick(v: View): Boolean {
        val longClickListner=this.mOnItemLongClickListener
        longClickListner?.let {
            longClickListner.onItemlongClick(v, v.tag as Int)
            return true
        }
        return false
    }

    override fun getItemCount(): Int {
        return datas.size
    }


    fun setOnItemClickListen(listenr: OnItemClickListener) {
        this.mOnItemClickListener = listenr
    }
    fun setOnItemLongClickListener(listener: OnItemLongClickListener) {
        this.mOnItemLongClickListener = listener
    }

    override fun onBindViewHolder(holder: T, position: Int) {
        holder.itemView.setOnClickListener(this)
        holder.itemView.tag = position
        setItemClick()
    }

    override fun onPreLoad() {
        mOnPagedListener?.onPreLoad()
    }

    override fun onPostLoad() {
        mOnPagedListener?.onPostLoad()
    }

    fun getItem(position: Int):W{
        return datas[position]
    }

    fun getAdapterDatas():MutableList<W>{
        return ArrayList(datas)
    }
    protected open fun setItemClick(){}
}