package com.example.sunkai.heritage.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.databinding.ViewImageItemBinding
import com.example.sunkai.heritage.interfaces.onPhotoViewImageClick
import com.example.sunkai.heritage.network.IMAGE_HOST
import com.example.sunkai.heritage.tools.EHeritageApplication
import com.example.sunkai.heritage.tools.loadImageFromServerWithoutBackground
import com.example.sunkai.heritage.tools.saveImage
import com.example.sunkai.heritage.views.SwipePhotoView

/**
 * 其他用户照片墙的adapter
 * Created by sunkai on 2018/3/7.
 */
class ViewImageGalleryAdapter(
    val context: Context,
    val datas: Array<String>,
    private val compressedUrls: Array<String?>?,
    val glide: RequestManager
) : RecyclerView.Adapter<ViewImageGalleryAdapter.ImageGalleryHolder>() {
    private var photoViewImageClickListener: onPhotoViewImageClick? = null
    private var swipePhotoViewListener: SwipePhotoView.OnDragListner? = null

    class ImageGalleryHolder(binding: ViewImageItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val photoView = binding.photoView
    }

    override fun getItemCount(): Int = datas.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageGalleryHolder {
        return ImageGalleryHolder(
            ViewImageItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ImageGalleryHolder, position: Int) {
        val photoView = holder.photoView
        photoView.scaleType = ImageView.ScaleType.FIT_CENTER
        photoView.setImageDrawable(
            ContextCompat.getDrawable(
                EHeritageApplication.instance,
                R.drawable.backgound_grey
            )
        )
        photoView.setIsInViewPager(true)
        photoView.setOnDragListner(swipePhotoViewListener)
        setClick(position, photoView)
        val url = if (datas[position % itemCount].contains(IMAGE_HOST)) {
            datas[position % itemCount]
        } else {
            IMAGE_HOST + datas[position % itemCount]
        }
        val compressUrl = compressedUrls?.get(position % itemCount) ?: url
        glide.loadImageFromServerWithoutBackground(url)
            .thumbnail(glide.loadImageFromServerWithoutBackground(compressUrl)).into(photoView)
        photoView.setOnCreateContextMenuListener { contextMenu, view, contextMenuInfo ->
            MenuInflater(context).inflate(R.menu.view_image_menu, contextMenu)
            contextMenu.getItem(0).setOnMenuItemClickListener {
                glide.saveImage(url)
                return@setOnMenuItemClickListener true
            }
        }
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

    fun setOnDragListener(listener: SwipePhotoView.OnDragListner) {
        this.swipePhotoViewListener = listener
    }
}