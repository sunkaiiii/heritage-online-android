package com.example.sunkai.heritage.activity

import android.os.Bundle
import android.transition.Fade
import android.view.View
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.activity.base.BaseViewImageActivity
import com.example.sunkai.heritage.adapter.ViewImageGalleryAdapter
import com.example.sunkai.heritage.tools.WindowHelper
import com.example.sunkai.heritage.value.IMAGE_COMPRESS_URL
import com.example.sunkai.heritage.value.IMAGE_POSITION
import com.example.sunkai.heritage.value.IMAGE_URL
import kotlinx.android.synthetic.main.activity_view_image.*

//用于浏览图片的Activity
class ViewImageActivity : BaseViewImageActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_image)
        setWindowAnimation()
        getImage()
    }

    private fun getImage() {
        val imageUrl = intent.getStringArrayExtra(IMAGE_URL)
        val compressedUrls=intent.getStringArrayExtra(IMAGE_COMPRESS_URL)
        if(imageUrl==null)
        {
            finish()
            return
        }
        val position = intent.getIntExtra(IMAGE_POSITION, 0)
        val adapter = ViewImageGalleryAdapter(this, imageUrl,compressedUrls, glide)
        adapter.setOnDragListener(this)
        adapter.setOnPhotoViewImageClickListener(this)
        activityViewImageGallery.adapter = adapter
        activityViewImageGallery.currentItem = position
    }

    override fun onStart() {
        super.onStart()
        setWindowFullScreen()
    }

    override fun getRootView(): View {
        return rootView
    }

    private fun setWindowAnimation() {
        val fade = Fade()
        window.enterTransition = fade
        window.exitTransition = fade
    }

    private fun setWindowFullScreen() {
        WindowHelper.setWindowFullScreen(this)
    }
}
