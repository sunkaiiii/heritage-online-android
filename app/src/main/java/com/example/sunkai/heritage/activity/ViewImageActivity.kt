package com.example.sunkai.heritage.activity

import android.os.Bundle
import android.transition.Fade
import android.view.View
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.activity.base.BaseViewImageActivity
import com.example.sunkai.heritage.connectWebService.BaseSetting
import com.example.sunkai.heritage.tools.WindowHelper
import com.example.sunkai.heritage.value.IMAGE_URL
import kotlinx.android.synthetic.main.activity_view_image.*

//用于浏览图片的Activity
class ViewImageActivity : BaseViewImageActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_image)
        setWindowAnimation()
        setPhotoViewAction()
        getImage()
    }

    private fun getImage() {
        val imageUrl = intent.getStringExtra(IMAGE_URL)
        glide.load(BaseSetting.URL + imageUrl).into(activityViewImage)
    }

    override fun onStart() {
        super.onStart()
        setWindowFullScreen()
    }

    private fun setPhotoViewAction() {
        activityViewImage.setOnClickListener {
            if (activityViewImage.scale == 1.0f) {
                onBackPressed()
            } else {
                activityViewImage.setScale(1.0f, true)
            }
        }
    }

    override fun getRootView(): View {
        return rootView
    }

    override fun setImageViewListener() {
        activityViewImage.setOnDragListner(this)
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
