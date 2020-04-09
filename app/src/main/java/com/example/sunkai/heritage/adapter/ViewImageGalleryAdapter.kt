package com.example.sunkai.heritage.adapter

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.PagerAdapter
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.RequestManager
import com.example.sunkai.heritage.tools.GlobalContext
import com.example.sunkai.heritage.interfaces.onPhotoViewImageClick
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.views.SwipePhotoView
import com.example.sunkai.heritage.connectWebService.BaseSetting
import com.example.sunkai.heritage.tools.loadImageFromServer
import com.example.sunkai.heritage.tools.loadImageFromServerWithoutBackground
import com.github.chrisbanes.photoview.PhotoView

/**
 * 其他用户照片墙的adapter
 * Created by sunkai on 2018/3/7.
 */
class ViewImageGalleryAdapter(val context: Context, val datas: Array<String>,val compressedUrls:Array<String?>?, val glide: RequestManager) : PagerAdapter() {
    private var photoViewImageClickListener: onPhotoViewImageClick? = null
    private var swipePhotoViewListener: SwipePhotoView.OnDragListner? = null
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
        setClick(position, photoView)
        val url=if(datas[position%count].contains(BaseSetting.IMAGE_HOST)){
            datas[position%count]
        }else{
            BaseSetting.IMAGE_HOST + datas[position % count]
        }
        val compressUrl=compressedUrls?.get(position%count)?:url
        glide.loadImageFromServerWithoutBackground(url).thumbnail(glide.loadImageFromServerWithoutBackground(compressUrl)).into(photoView)
        return photoView
    }

    private fun setClick(position: Int, photoView: PhotoView) {
        photoView.setOnClickListener {
            photoViewImageClickListener?.onImageClick(position, photoView)
        }
    }

    fun setOnPhotoViewImageClickListener(listner: onPhotoViewImageClick) {
        this.photoViewImageClickListener = listner
    }

    override fun destroyItem(container: ViewGroup, position: Int, view: Any) {
        container.removeView(view as View)
    }

    fun setOnDragListener(listener: SwipePhotoView.OnDragListner) {
        this.swipePhotoViewListener = listener
    }
}