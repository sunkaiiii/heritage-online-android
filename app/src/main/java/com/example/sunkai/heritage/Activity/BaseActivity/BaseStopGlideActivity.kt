package com.example.sunkai.heritage.Activity.BaseActivity

import android.support.v7.app.AppCompatActivity
import com.bumptech.glide.Glide

abstract class BaseStopGlideActivity :AppCompatActivity(){
    override fun onPause() {
        super.onPause()
        Glide.with(this).pauseRequests()
    }
}