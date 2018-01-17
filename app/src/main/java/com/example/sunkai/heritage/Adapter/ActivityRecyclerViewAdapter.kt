package com.example.sunkai.heritage.Adapter

import android.content.ContentValues
import android.content.Context

import android.graphics.Bitmap
import android.os.AsyncTask
import android.support.v7.widget.RecyclerView
import android.util.LruCache
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.sunkai.heritage.Adapter.BaseAdapter.BaseRecyclerAdapter
import com.example.sunkai.heritage.ConnectWebService.BaseSetting.Companion.host

import com.example.sunkai.heritage.ConnectWebService.HandleMainFragment
import com.example.sunkai.heritage.Data.HandlePic
import com.example.sunkai.heritage.Data.MySqliteHandler
import com.example.sunkai.heritage.Data.ClassifyActiviyData
import com.example.sunkai.heritage.Data.ClassifyDivideData
import com.example.sunkai.heritage.R

import java.io.ByteArrayInputStream
import java.lang.ref.WeakReference

/*
 * Created by sunkai on 2017/12/22.
 */

class ActivityRecyclerViewAdapter(private val context: Context, private val channel: String) : BaseRecyclerAdapter() {
    private var activityDatas: List<ClassifyDivideData>? = null
    internal var imageAnimation: Animation//图片出现动画

    internal var thisRecyclerView: RecyclerView? = null
    internal var lruCache: LruCache<Int, Bitmap>

    class ViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        val img: ImageView
        val textView: TextView
        init {
            img = view.findViewById<View>(R.id.activity_layout_img) as ImageView
            textView = view.findViewById<View>(R.id.activity_layout_text) as TextView
        }
    }

    init {
        this.activityDatas = null
        val maxMemory = Runtime.getRuntime().maxMemory().toInt()
        val avilableMemory = maxMemory / 32
        lruCache = object : LruCache<Int, Bitmap>(avilableMemory) {
            override fun sizeOf(key: Int?, bitmap: Bitmap): Int {
                return bitmap.byteCount
            }
        }
        imageAnimation = AnimationUtils.loadAnimation(context, R.anim.image_apear)
        getChannelInformation(this).execute()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (thisRecyclerView == null)
            thisRecyclerView = parent as RecyclerView
        val view = LayoutInflater.from(context).inflate(R.layout.activity_layout, parent, false)
        val viewHolder = ViewHolder(view)
        view.setOnClickListener(this)
        return viewHolder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        super.onBindViewHolder(holder, position)
        if(holder is ViewHolder) {
            val data = activityDatas!![position]
            val text = data.content
            holder.textView.text = text
            holder.img.setImageResource(R.drawable.empty_background)
            Glide.with(context).load(host+data.img).into(holder.img)
//            val bitmap = lruCache.get(data.id)
//            if (bitmap != null) {
//                holder.img.setImageBitmap(bitmap)
//            } else {
//                getChannelImage(data.id, holder.img, this).execute()
//            }
        }
    }
    override fun getItemCount(): Int {
        return if (activityDatas == null) 0 else activityDatas!!.size
    }

    override fun getItem(position: Int): ClassifyDivideData {
        return activityDatas!![position]
    }

    internal class getChannelInformation(adapter: ActivityRecyclerViewAdapter) : AsyncTask<Void, Void, Void>() {
        val weakReference: WeakReference<ActivityRecyclerViewAdapter>

        init {
            this.weakReference = WeakReference(adapter)
        }

        override fun doInBackground(vararg voids: Void): Void? {
            val adpter = weakReference.get()
            adpter?.activityDatas = HandleMainFragment.GetChannelInformation(adpter?.channel!!)
            return null
        }

        override fun onPostExecute(aVoid: Void?) {
            val adpter = weakReference.get()
            adpter?.notifyDataSetChanged()
        }
    }


    internal class getChannelImage(var id: Int, imageView: ImageView, adapter: ActivityRecyclerViewAdapter) : AsyncTask<Void, Void, Bitmap>() {
        var db = MySqliteHandler.GetReadableDatabase()
        val table = "channel_activity_image"
        val selection = "imageID=?"
        val weakReferenceImageView: WeakReference<ImageView>
        val weakReference: WeakReference<ActivityRecyclerViewAdapter>

        init {
            weakReferenceImageView = WeakReference(imageView)
            weakReference = WeakReference(adapter)
        }

        override fun doInBackground(vararg voids: Void): Bitmap? {
            val adpter = weakReference.get() ?: return null
            val bitmap: Bitmap
            val selectionArgs = arrayOf(id.toString())
            val cursor = db.query(table, null, selection, selectionArgs, null, null, null)
            cursor.moveToFirst()
            var imgByte: ByteArray?
            if (!cursor.isAfterLast) {
                val image = cursor.getColumnIndex("image")
                imgByte = cursor.getBlob(image)
                cursor.close()
                if (imgByte != null) {
                    val `in` = ByteArrayInputStream(imgByte)
                    bitmap = HandlePic.handlePic(`in`, 0)
                    adpter.lruCache.put(id, bitmap)
                    return bitmap
                }
            }
            imgByte = HandleMainFragment.GetChannelImage(id)
            if (imgByte == null)
                return null
            val contentValues = ContentValues()
            contentValues.put("imageID", id)
            contentValues.put("image", imgByte)
            db = MySqliteHandler.GetWritableDatabase()
            db.insert("channel_activity_image", null, contentValues)
            val `in` = ByteArrayInputStream(imgByte)
            bitmap = HandlePic.handlePic( `in`, 0)
            adpter.lruCache.put(id, bitmap)
            return bitmap
        }

        override fun onPostExecute(bitmap: Bitmap?) {
            if (bitmap != null) {
                val adpter = weakReference.get()
                val imageView = weakReferenceImageView.get()
                if (imageView != null && adpter != null) {
                    imageView.setImageBitmap(bitmap)
                    imageView.startAnimation(adpter.imageAnimation)
                }
            }
        }
    }
}