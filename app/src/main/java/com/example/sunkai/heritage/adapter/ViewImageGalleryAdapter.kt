package com.example.sunkai.heritage.adapter

import android.content.Context
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.RequestManager
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.network.BaseSetting
import com.example.sunkai.heritage.interfaces.onPhotoViewImageClick
import com.example.sunkai.heritage.tools.EHeritageApplication
import com.example.sunkai.heritage.tools.loadImageFromServerWithoutBackground
import com.example.sunkai.heritage.tools.saveImage
import com.example.sunkai.heritage.views.SwipePhotoView

/**
 * 其他用户照片墙的adapter
 * Created by sunkai on 2018/3/7.
 */
class ViewImageGalleryAdapter(val context: Context, val datas: Array<String>, private val compressedUrls: Array<String?>?, val glide: RequestManager) : PagerAdapter() {
    private var photoViewImageClickListener: onPhotoViewImageClick? = null
    private var swipePhotoViewListener: SwipePhotoView.OnDragListner? = null
    override fun getCount(): Int {
        return datas.size
    }


    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val photoView = SwipePhotoView(EHeritageApplication.instance)
        photoView.scaleType = ImageView.ScaleType.FIT_CENTER
        container.addView(photoView)
        photoView.setImageDrawable(ContextCompat.getDrawable(EHeritageApplication.instance, R.drawable.backgound_grey))
        photoView.setIsInViewPager(true)
        photoView.setOnDragListner(swipePhotoViewListener)
        setClick(position, photoView)
        val url = if (datas[position % count].contains(BaseSetting.IMAGE_HOST)) {
            datas[position % count]
        } else {
            BaseSetting.IMAGE_HOST + datas[position % count]
        }
        val compressUrl = compressedUrls?.get(position % count) ?: url
        glide.loadImageFromServerWithoutBackground(url).thumbnail(glide.loadImageFromServerWithoutBackground(compressUrl)).into(photoView)
        photoView.setOnCreateContextMenuListener { contextMenu, view, contextMenuInfo ->
//            if (Build.VERSION.SDK_INT >= 23) {
//                if (GlobalContext.instance.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                    if (context is Activity) {
//                        context.requestPermissions(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.READ_EXTERNAL_STORAGE), 1)
//                    }
//                }
//            }
            MenuInflater(context).inflate(R.menu.view_image_menu, contextMenu)
            contextMenu.getItem(0).setOnMenuItemClickListener {
                glide.saveImage(url)
                return@setOnMenuItemClickListener true
            }
        }
        return photoView
    }

    private fun setClick(position: Int, photoView: SwipePhotoView) {
        photoView.setOnClickListener {
            photoViewImageClickListener?.onImageClick(position, photoView)
        }

        photoView.setOnLongClickListener {
            photoViewImageClickListener?.onImageLongCick(position, photoView)
            return@setOnLongClickListener true
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