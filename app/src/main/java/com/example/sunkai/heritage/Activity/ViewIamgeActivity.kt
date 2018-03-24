package com.example.sunkai.heritage.Activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.sunkai.heritage.Data.HandlePic
import com.example.sunkai.heritage.R
import kotlinx.android.synthetic.main.activity_view_iamge.*
import java.io.ByteArrayInputStream

class ViewIamgeActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_iamge)
        val imageByte = intent.getByteArrayExtra("image")
        val bitmap = HandlePic.handlePic(ByteArrayInputStream(imageByte), 0)
        pvActivityViewImage.setImageBitmap(bitmap)
        pvActivityViewImage.setOnClickListener({
            if(pvActivityViewImage.scale==1.0f){
                finish()
            }
            else{
                pvActivityViewImage.setScale(1.0f,true)
            }
        })
    }
}
