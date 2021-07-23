package com.example.sunkai.heritage.adapter.baseAdapter

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.sunkai.heritage.interfaces.OnItemClickListener
import com.example.sunkai.heritage.interfaces.OnItemLongClickListener

/**
 * Created by sunkai on 2018/1/2.
 * 给RecyclerAdapter封装了一些点击的操作
 */
abstract class BaseRecyclerAdapter<T : RecyclerView.ViewHolder, W>(protected val context: Context, datas: List<W>, val glide: RequestManager) : RecyclerView.Adapter<T>(), View.OnClickListener, View.OnLongClickListener {
    private var mOnItemClickListener: OnItemClickListener = object : OnItemClickListener {
        override fun onItemClick(view: View, position: Int) {
            setItemClick(view, getItem(position))
        }

    }
    private var mOnItemLongClickListener: OnItemLongClickListener? = null
    protected var datas: MutableList<W> = datas.toMutableList()
    override fun onClick(v: View) {
        mOnItemClickListener.onItemClick(v, v.tag as Int)
    }

    override fun onLongClick(v: View): Boolean {
        val longClickListner = this.mOnItemLongClickListener
        longClickListner?.let {
            longClickListner.onItemlongClick(v, v.tag as Int)
            return true
        }
        return false
    }

    override fun getItemCount(): Int {
        return datas.size
    }


    fun setOnItemClickListener(listenr: OnItemClickListener) {
        this.mOnItemClickListener = listenr
    }

    fun setOnItemClickListener(listener: (view: View, position: Int) -> Unit) {
        this.mOnItemClickListener = object : OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                listener.invoke(view, position)
            }

        }
    }

    fun setOnItemLongClickListener(listener: OnItemLongClickListener) {
        this.mOnItemLongClickListener = listener
    }

    override fun onBindViewHolder(holder: T, position: Int) {
        holder.itemView.tag = position
        holder.itemView.setOnClickListener {
            mOnItemClickListener.onItemClick(holder.itemView, position)
        }

    }

    fun getItem(position: Int): W {
        return datas[position]
    }

    fun getAdapterDatas(): MutableList<W> {
        return ArrayList(datas)
    }

    protected open fun setItemClick(itemView: View, item: W) {}
}