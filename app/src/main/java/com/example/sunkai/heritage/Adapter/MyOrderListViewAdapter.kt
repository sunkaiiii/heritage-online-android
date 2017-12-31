package com.example.sunkai.heritage.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.AsyncTask
import android.util.LruCache
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView

import com.example.sunkai.heritage.ConnectWebService.HandleFolk
import com.example.sunkai.heritage.Data.HandlePic
import com.example.sunkai.heritage.Data.FolkData
import com.example.sunkai.heritage.R

import java.io.ByteArrayInputStream
import java.lang.ref.WeakReference

/**
 * Created by sunkai on 2017-4-24.
 * 此类用于处理我的预约的List
 */

class MyOrderListViewAdapter(private val context: Context, private val datas: List<FolkData>) : BaseAdapter() {
    private val lruCache: LruCache<Int, Bitmap>
    private var thisListView: ListView? = null

    init {
        val maxMemory = Runtime.getRuntime().maxMemory().toInt()
        val avilableMemory = maxMemory / 8
        lruCache = object : LruCache<Int, Bitmap>(avilableMemory) {
            override fun sizeOf(key: Int?, bitmap: Bitmap): Int {
                return bitmap.byteCount
            }
        }
    }

    override fun getCount(): Int {
        return datas.size
    }

    override fun getItem(position: Int): Any {
        return datas[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("InflateParams")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val view:View
        if (thisListView == null) {
            thisListView = parent as ListView
        }
        val vh: Holder
        if (convertView == null) {
            val inflater = LayoutInflater.from(context)
            view = inflater.inflate(R.layout.folk_listview_layout, null)
            vh = Holder()
            vh.v1 = view.findViewById(R.id.list_text)
            vh.v2 = view.findViewById(R.id.list_location)
            vh.v3 = view.findViewById(R.id.list_title)
            vh.v4 = view.findViewById(R.id.list_img)
            view.tag = vh
        } else {
            view=convertView
            vh = view.tag as Holder
        }
        val data = datas[position]
        val content = data.content
        val location = data.location
        val title = data.title
        vh.v1.text = "        " + content!!
        vh.v2.text = location
        vh.v3.text = title
        val bitmap = lruCache.get(data.id)
        vh.v4.tag = data.id
        if (null == bitmap && data.id != null)
            LoadImageAsync(data.id!!, position, this).execute()
        else
            vh.v4.setImageBitmap(bitmap)
        return view
    }

    inner class Holder {
        internal lateinit var v1: TextView
        internal lateinit var v2: TextView
        internal lateinit var v3: TextView
        internal lateinit var v4: ImageView
    }

    internal class LoadImageAsync(val id: Int, val position: Int, adapter: MyOrderListViewAdapter) : AsyncTask<Void, Void, Bitmap>() {
        val myOrderListViewAdapterWeakReference: WeakReference<MyOrderListViewAdapter> = WeakReference(adapter)
        override fun doInBackground(vararg voids: Void): Bitmap? {
            val adapter = myOrderListViewAdapterWeakReference.get() ?: return null
            val img = HandleFolk.GetFolkImage(id) ?: return null
            adapter.datas[position].image = img
            val `in` = ByteArrayInputStream(img)
            val bitmap = HandlePic.handlePic(adapter.context, `in`, 0)
            adapter.lruCache.put(id, bitmap)
            return bitmap
        }

        override fun onPostExecute(bitmap: Bitmap) {
            val adapter = myOrderListViewAdapterWeakReference.get() ?: return
            val imageView = adapter.thisListView?.findViewWithTag<ImageView>(id) ?: return
            imageView.setImageBitmap(bitmap)
        }
    }

}
