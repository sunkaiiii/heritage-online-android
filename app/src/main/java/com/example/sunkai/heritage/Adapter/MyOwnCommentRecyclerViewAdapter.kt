package com.example.sunkai.heritage.Adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.support.v7.graphics.Palette
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.LruCache
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.graphics.drawable.toBitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.sunkai.heritage.Adapter.BaseAdapter.BaseRecyclerAdapter
import com.example.sunkai.heritage.ConnectWebService.HandleFind
import com.example.sunkai.heritage.Data.UserCommentData
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.BaseAsyncTask
import com.example.sunkai.heritage.value.ERROR

/**
 * 我的帖子的recyclerView的adapter
 * Created by sunkai on 2018/1/15.
 */
class MyOwnCommentRecyclerViewAdapter(context: Context, datas: List<UserCommentData>) : BaseRecyclerAdapter<MyOwnCommentRecyclerViewAdapter.ViewHolder, UserCommentData>(datas) {

    private val lruCache: LruCache<Int, Bitmap>
    private var recyclerView: RecyclerView? = null
    private val context: Context

    init {
        this.datas = datas
        this.context = context
        val avilableMemory = Runtime.getRuntime().maxMemory().toInt() / 8
        val cacheSize = avilableMemory / 4
        lruCache = object : LruCache<Int, Bitmap>(cacheSize) {
            override fun sizeOf(key: Int, value: Bitmap): Int {
                return value.byteCount
            }
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mycomment_item_image: ImageView
        val mycomment_item_title: TextView
        val mycomment_item_content: TextView
        val view: View

        init {
            this.view = view
            mycomment_item_image = view.findViewById(R.id.mycomment_item_image)
            mycomment_item_title = view.findViewById(R.id.mycomment_item_title)
            mycomment_item_content = view.findViewById(R.id.mycomment_item_content)

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (recyclerView == null)
            recyclerView = parent as RecyclerView
        val view = LayoutInflater.from(context).inflate(R.layout.mycomment_layout_item, parent, false)
        view.visibility = View.GONE
        val holder = ViewHolder(view)
        view.setOnClickListener(this)
        view.setOnLongClickListener(this)
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        GetCommentImage(holder, datas[position])
    }

    private fun GetCommentImage(holder: ViewHolder, data: UserCommentData) {
        val bitmap = lruCache.get(data.id)
        if (null != bitmap) {
            holder.mycomment_item_title.text = data.commentTitle
            holder.mycomment_item_content.text = data.commentContent
            holder.mycomment_item_image.setImageBitmap(bitmap)
            findDominateColor(bitmap, holder)
        } else {
            GetCommentImageAsync(data, this, holder).execute()
        }
    }

    private class GetCommentImageAsync internal constructor(val data: UserCommentData, adpter: MyOwnCommentRecyclerViewAdapter, val holder: ViewHolder) : BaseAsyncTask<Void, Void, String, MyOwnCommentRecyclerViewAdapter>(adpter) {
        override fun doInBackground(vararg params: Void?): String {
            return HandleFind.GetUserCommentImageUrl(data.id)
        }

        override fun onPostExecute(url: String) {
            if (!TextUtils.isEmpty(url) && url != ERROR) {
                val adpter = weakRefrece.get()
                adpter?.let {
                    holder.mycomment_item_title.text = data.commentTitle
                    holder.mycomment_item_content.text = data.commentContent
                    Glide.with(adpter.context).load(url).into(simpleTarget(holder, adpter))
                    holder.view.visibility = View.VISIBLE
                }
            }
        }

        class simpleTarget(val holder: ViewHolder, val adapter: MyOwnCommentRecyclerViewAdapter) : SimpleTarget<Drawable>() {
            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                holder.mycomment_item_image.setImageDrawable(resource)
                adapter.findDominateColor(resource.toBitmap(), holder)
            }

        }

    }

    private fun findDominateColor(bitmap: Bitmap, holder: ViewHolder) {
        val color = Palette.from(bitmap).generate().getDarkMutedColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
        holder.mycomment_item_image.setImageBitmap(bitmap)
        holder.mycomment_item_title.setTextColor(color)
        holder.mycomment_item_content.setTextColor(color)
    }

}