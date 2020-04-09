package com.example.sunkai.heritage.tools

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.connectWebService.BaseSetting.Companion.IMAGE_HOST

private fun buildUrl(baseImageUrl:String):String{
    var newUrl=baseImageUrl.replace(",","")
    newUrl = if (!newUrl.contains(IMAGE_HOST)) IMAGE_HOST + newUrl else newUrl
    return newUrl
}

fun RequestManager.loadImageFromServerToList(url:String,holderImageView:ImageView){
    loadImageFromServerWithoutBackground(url).placeholder(R.drawable.place_holder).into(DrawableImageViewTarget(holderImageView,true))
}

fun RequestManager.loadImageFromServer(url: String): RequestBuilder<Drawable> {
    return load(buildUrl(url)).placeholder(R.color.midGrey)
}

fun RequestManager.loadImageFromServerWithoutBackground(url: String): RequestBuilder<Drawable> {

    return load(buildUrl(url))
}
