package com.example.sunkai.heritage.Activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.sunkai.heritage.Data.HandlePic
import com.example.sunkai.heritage.R
import com.github.chrisbanes.photoview.PhotoView
import java.io.ByteArrayInputStream

class ViewIamgeActivity : AppCompatActivity() {

    lateinit var photoView: PhotoView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_iamge)
        initview()
        val imageByte = intent.getByteArrayExtra("image")
        val bitmap = HandlePic.handlePic(ByteArrayInputStream(imageByte), 0)
        photoView.setImageBitmap(bitmap)
        photoView.setOnClickListener({
            if(photoView.scale==1.0f){
                finish()
            }
            else{
                photoView.setScale(1.0f,true)
            }
        })
    }

    internal fun initview(){

        photoView=findViewById(R.id.pv_activity_view_image)
    }
}
