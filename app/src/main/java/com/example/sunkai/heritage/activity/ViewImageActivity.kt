package com.example.sunkai.heritage.activity

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.transition.Fade
import android.util.Log
import android.view.ViewGroup
import com.example.sunkai.heritage.activity.baseActivity.BaseGlideActivity
import com.example.sunkai.heritage.connectWebService.BaseSetting
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.Views.SwipePhotoView
import com.example.sunkai.heritage.tools.WindowHelper
import com.example.sunkai.heritage.value.IMAGE_URL
import kotlinx.android.synthetic.main.activity_view_image.*

//用于浏览图片的Activity
class ViewImageActivity : BaseGlideActivity(), SwipePhotoView.OnDragListner {

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

        activityViewImage.setOnDragListner(this)
    }

    override fun onDrag(dx: Int, dy: Int) {
        Log.d("ViewImageActivity", String.format("dy:%d,alpha:%d", dy, (255 - Math.pow(255 * Math.abs(dy) / rootView.height / 2.0, 1.2)).toInt()))
        rootView.background.alpha = (255 - Math.pow(255 * Math.abs(dy) / rootView.height / 2.0, 1.2)).toInt()
        val scale = (1.0 - Math.pow(1.0 * Math.abs(dy) / rootView.height / 2.0, 0.8)).toFloat()
        val height = activityViewImage.getRawHeight()
        val width = activityViewImage.getRawWidth()
        if (height != -1 && width != -1) {
            val layoutParams = activityViewImage.layoutParams
            layoutParams.height = (height * scale).toInt()
            layoutParams.width = (width * scale).toInt()
            Log.d("ViewImageActivity",String.format("rawHeight:%d,rawWidth:%d,height:%d,width:%d",height,width,layoutParams.height,layoutParams.width))
            activityViewImage.layoutParams = layoutParams
        }
    }
    override fun onDragClose() {
        onBackPressed()
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
