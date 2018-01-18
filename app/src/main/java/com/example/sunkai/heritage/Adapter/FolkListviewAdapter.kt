package com.example.sunkai.heritage.Adapter

import android.annotation.SuppressLint
import android.content.ContentValues
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
import com.bumptech.glide.Glide

import com.example.sunkai.heritage.ConnectWebService.HandleFolk
import com.example.sunkai.heritage.Data.HandlePic
import com.example.sunkai.heritage.Data.MySqliteHandler
import com.example.sunkai.heritage.Data.FolkData
import com.example.sunkai.heritage.Data.FolkDataLite
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.Fragment.FolkFragment
import com.example.sunkai.heritage.value.HOST

import java.io.ByteArrayInputStream

/**
 * Created by sunkai on 2017/3/3.
 *
 */

class FolkListviewAdapter(private val context: Context, internal var folkFragment: FolkFragment?) : BaseAdapter() {
    var datas: List<FolkDataLite>?=null
    private var listView: ListView? = null
    private val lruCache: LruCache<Int, Bitmap>

    init {
        val maxSize = Runtime.getRuntime().maxMemory().toInt()
        val avilibleMemory = maxSize / 16
        lruCache = object : LruCache<Int, Bitmap>(avilibleMemory) {
            override fun sizeOf(key: Int?, bitmap: Bitmap): Int {
                return bitmap.byteCount
            }
        }
        if (this.folkFragment != null) {
            setProgress(true)
        }
        //获取folk的文本信息
        getInformation().execute()
    }

    override fun getCount(): Int {
        return if(datas==null)
            0
        else
            datas!!.size
    }

    override fun getItem(position: Int): Any {
        return datas!![position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("InflateParams")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view:View
        if (listView == null)
            listView = parent as ListView
        val vh: Holder
        if (convertView == null) {
            val inflater = LayoutInflater.from(context)
            view = inflater.inflate(R.layout.folk_listview_layout, null)
            vh = Holder()
            vh.v1 = view.findViewById<View>(R.id.list_text) as TextView
            vh.v2 = view.findViewById<View>(R.id.list_location) as TextView
            vh.v3 = view.findViewById<View>(R.id.list_title) as TextView
            vh.v4 = view.findViewById<View>(R.id.list_img) as ImageView
            vh.v5 = view.findViewById<View>(R.id.list_divide) as TextView
            view.tag = vh
        } else {
            view=convertView
            vh = convertView.tag as Holder
        }
        val data = datas!![position]
        val title = data.title
        val divide = data.divide
        vh.v3.text = title
        vh.v4.setImageResource(R.drawable.empty_background)
        Glide.with(context).load(HOST+data.img).into(vh.v4)
        vh.v5.text = divide
        return view
    }

    fun setProgress(show: Boolean) {
        if (show) {
            folkFragment!!.loadProgress.visibility = View.VISIBLE
            folkFragment!!.setWidgetEnable(!show)
        } else {
            folkFragment!!.loadProgress.visibility = View.GONE
            folkFragment!!.setWidgetEnable(!show)
        }
    }

    fun setNewDatas(datas: List<FolkDataLite>) {
        this.datas = datas
        notifyDataSetChanged()
    }

    fun getData(): List<FolkDataLite>? {
        return datas
    }

    inner class Holder {
        internal lateinit var v1: TextView
        internal lateinit var v2: TextView
        internal lateinit var v3: TextView
        internal lateinit var v5: TextView
        internal lateinit var v4: ImageView
    }

    inner class getInformation : AsyncTask<Void, Void, Boolean>() {
        override fun doInBackground(vararg voids: Void): Boolean {
            datas = HandleFolk.GetFolkInforMation()
            datas?.let {
                FolkFragment.isLoadData = true
                folkFragment?.setData(true, datas!!)
            }
            return true
        }

        override fun onPostExecute(result: Boolean) {
            if(result) {
                notifyDataSetChanged()
            }
            setProgress(false)
        }
    }
}

