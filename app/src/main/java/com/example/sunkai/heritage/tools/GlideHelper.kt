package com.example.sunkai.heritage.tools

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.connectWebService.BaseSetting.Companion.IMAGE_HOST
import com.example.sunkai.heritage.tools.MakeToast.toast
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

private fun buildUrl(baseImageUrl: String): String {
    var newUrl = baseImageUrl.replace(",", "")
    newUrl = if (!newUrl.contains(IMAGE_HOST)) IMAGE_HOST + newUrl else newUrl
    return newUrl
}

fun RequestManager.loadImageFromServerToList(url: String, holderImageView: ImageView) {
    loadImageFromServerWithoutBackground(url).placeholder(R.drawable.place_holder).into(DrawableImageViewTarget(holderImageView, true))
}

fun RequestManager.loadImageFromServer(url: String): RequestBuilder<Drawable> {
    return load(buildUrl(url)).placeholder(R.color.midGrey)
}

fun RequestManager.loadImageFromServerWithoutBackground(url: String): RequestBuilder<Drawable> {
    return load(buildUrl(url))
}

fun RequestManager.saveImage(url: String) {
    asBitmap().load(url).into(object : SimpleTarget<Bitmap>() {
        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
            try {
                val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                ImageSaver.saveBitmap(EHeritageApplication.instance, resource, Bitmap.CompressFormat.JPEG, "image/jpg", "IMG_${timeStamp}.jpg")
                toast(R.string.save_success)
            } catch (e: IOException) {
                toast(R.string.save_failed)
                e.printStackTrace()
            }
        }

        override fun onLoadFailed(errorDrawable: Drawable?) {
            super.onLoadFailed(errorDrawable)
            toast(R.string.save_failed)
        }
    })
}