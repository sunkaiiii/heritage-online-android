package com.example.sunkai.heritage.tools

import android.widget.ImageView
import com.bumptech.glide.RequestManager

fun loadImage(glide:RequestManager,url:String,view:ImageView){
    glide.load(url).into(view)
}