package com.example.sunkai.heritage.adapter

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.PagerAdapter
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.sunkai.heritage.tools.GlobalContext
import com.example.sunkai.heritage.interfaces.onPhotoViewImageClick
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.Views.SwipePhotoView
import com.github.chrisbanes.photoview.PhotoView
import java.util.*

/**
 * 其他用户照片墙的adapter
 * Created by sunkai on 2018/3/7.
 */
class OtherUserActivityImageAdapter(val context:Context,val datas:List<Int>): PagerAdapter() {
    val photoViewMap: WeakHashMap<Int, PhotoView> = WeakHashMap()
    private var photoViewImageClickListener:onPhotoViewImageClick?=null
    private var swipePhotoViewListener:SwipePhotoView.OnDragListner?=null
    override fun getCount(): Int {
        return datas.size
    }


    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val photoView = SwipePhotoView(GlobalContext.instance)
        photoView.scaleType = ImageView.ScaleType.FIT_CENTER
        container.addView(photoView)
        photoView.setImageDrawable(ContextCompat.getDrawable(GlobalContext.instance, R.drawable.backgound_grey))
        photoView.setIsInViewPager(true)
        photoView.setOnDragListner(swipePhotoViewListener)
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

    fun setOnDragListener(listener:SwipePhotoView.OnDragListner){
        this.swipePhotoViewListener=listener
    }
}