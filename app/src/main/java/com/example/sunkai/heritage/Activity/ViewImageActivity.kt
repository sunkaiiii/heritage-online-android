package com.example.sunkai.heritage.Activity

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import android.transition.Fade
import android.view.View
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
        setWindowFullScreen()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setWindowAnimation()
        }
        setPhotoViewClickAction()
        getImage()
    }
    private fun getImage(){
        val imageUrl=intent.getStringExtra(IMAGE_URL)
        glide.load(BaseSetting.URL+imageUrl).into(activityViewImage)
    }

    private fun setPhotoViewClickAction(){
        activityViewImage.setOnClickListener {
            if(activityViewImage.scale==1.0f){
                onBackPressed()
            }else{
                activityViewImage.setScale(1.0f,true)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setWindowAnimation(){
        val fade=Fade()
        window.enterTransition=fade
        window.exitTransition=fade
    }

    private fun setWindowFullScreen(){
        WindowHelper.setWindowFullScreen(this)
    }
}
