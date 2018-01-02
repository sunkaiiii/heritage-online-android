package com.example.sunkai.heritage.Adapter.BaseAdapter

import android.support.v7.widget.RecyclerView
import android.view.View
import com.example.sunkai.heritage.Interface.OnItemClickListener
import com.example.sunkai.heritage.Interface.OnItemLongClickListener

/**
 * Created by sunkai on 2018/1/2.
 * 给RecyclerAdapter封装了一些点击的操作
 */
abstract class BaseRecyclerAdapter :RecyclerView.Adapter<RecyclerView.ViewHolder>(),View.OnClickListener,View.OnLongClickListener{
    protected var mOnItemClickListener: OnItemClickListener? = null
    protected var mOnItemLongClickListener: OnItemLongClickListener? = null

    override fun onClick(v: View) {
        mOnItemClickListener?.onItemClick(v,v.tag as Int)
    }

    override fun onLongClick(v: View): Boolean {
        mOnItemLongClickListener?.let {
            mOnItemLongClickListener!!.onItemlongClick(v, v.tag as Int)
            return true
        }
        return false
    }


    fun setOnItemClickListen(listenr: OnItemClickListener) {
        this.mOnItemClickListener = listenr
    }
    fun setOnItemLongClickListener(listener: OnItemLongClickListener) {
        this.mOnItemLongClickListener = listener
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        holder?.itemView?.tag = position
    }
}