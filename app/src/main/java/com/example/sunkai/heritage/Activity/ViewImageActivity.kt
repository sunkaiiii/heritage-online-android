package com.example.sunkai.heritage.Activity

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.transition.Fade
import com.example.sunkai.heritage.Activity.BaseActivity.BaseGlideActivity
import com.example.sunkai.heritage.ConnectWebService.BaseSetting
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.WindowHelper
import com.example.sunkai.heritage.value.IMAGE_URL
import kotlinx.android.synthetic.main.activity_view_image.*

//用于浏览图片的Activity
class ViewImageActivity : BaseGlideActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_image)
        setWindowAnimation()
        setPhotoViewClickAction()
        getImage()
        window.setBackgroundDrawable(ColorDrawable(Color.BLACK))
    }


    private fun getImage() {
        val imageUrl = intent.getStringExtra(IMAGE_URL)
        glide.load(BaseSetting.URL + imageUrl).into(activityViewImage)
    }

    override fun onStart() {
        super.onStart()
        setWindowFullScreen()
    }

    private fun setPhotoViewClickAction() {
        activityViewImage.setOnClickListener {
            if (activityViewImage.scale == 1.0f) {
                onBackPressed()
            } else {
                activityViewImage.setScale(1.0f, true)
            }
        }
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
