package com.example.sunkai.heritage.interfaces

import com.example.sunkai.heritage.views.SwipePhotoView

interface onPhotoViewImageClick {
    fun onImageClick(position:Int, swipePhotoView: SwipePhotoView)
    fun onImageLongCick(position: Int, swipePhotoView: SwipePhotoView)
}