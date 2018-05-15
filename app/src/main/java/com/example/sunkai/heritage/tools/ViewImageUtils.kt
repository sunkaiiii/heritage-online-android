package com.example.sunkai.heritage.tools

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Build
import android.transition.Fade
import android.widget.ImageView
import com.example.sunkai.heritage.Activity.ViewImageActivity
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.value.IMAGE_URL

object ViewImageUtils {
    fun setViewImageClick(context:Context,imageView: ImageView, url:String){
        val intent= Intent(context, ViewImageActivity::class.java)
        intent.putExtra(IMAGE_URL,url)
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.LOLLIPOP&&context is Activity){
            val fade= Fade()
            context.window.exitTransition=fade
            context.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(context,imageView,context.getString(R.string.share_view_image)).toBundle())
        }else{
            context.startActivity(intent)
        }
    }
}