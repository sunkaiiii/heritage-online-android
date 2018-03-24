package com.example.sunkai.heritage.Adapter

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.sunkai.heritage.tools.GlobalContext
import com.example.sunkai.heritage.Interface.onPhotoViewImageClick
import com.example.sunkai.heritage.R
import com.github.chrisbanes.photoview.PhotoView
import java.util.*

/**
 * 其他用户照片墙的adapter
 * Created by sunkai on 2018/3/7.
 */
class OtherUserActivityImageAdapter(val context:Context,val datas:List<Int>):PagerAdapter() {
    val photoViewMap: WeakHashMap<Int, PhotoView>
    private var photoViewImageClickListener:onPhotoViewImageClick?=null
    init {
        photoViewMap = WeakHashMap()
    }
    override fun getCount(): Int {
        return datas.size
    }


    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val photoView = PhotoView(GlobalContext.instance)
        photoView.scaleType = ImageView.ScaleType.FIT_CENTER
        container.addView(photoView)
        photoView.setImageDrawable(ContextCompat.getDrawable(GlobalContext.instance, R.drawable.backgound_grey))
        setClick(position,photoView)
        photoViewMap[position] = photoView
        return photoView
    }

    private fun setClick(position: Int,photoView: PhotoView){
        photoView.setOnClickListener {
            photoViewImageClickListener?.onImageClick(position,photoView)
        }
    }

    fun setOnPhotoViewImageClickListener(listner:onPhotoViewImageClick){
        this.photoViewImageClickListener=listner
    }

    override fun destroyItem(container: ViewGroup, position: Int, view: Any) {
        container.removeView(view as View)
    }
}