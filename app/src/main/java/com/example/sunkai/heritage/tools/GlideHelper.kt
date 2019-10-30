package com.example.sunkai.heritage.tools

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.example.sunkai.heritage.connectWebService.BaseSetting.Companion.IMAGE_HOST

fun loadImage(glide: RequestManager, url: String, view: ImageView) {
    glide.loadImageFromServer(url).into(view)
}

fun RequestManager.loadImageFromServer(url: String): RequestBuilder<Drawable> {
    var newUrl=url.replace(",","")
    newUrl = if (!newUrl.contains(IMAGE_HOST)) IMAGE_HOST + newUrl else newUrl
    return load(newUrl)
}