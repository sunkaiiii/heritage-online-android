package com.example.sunkai.heritage.Activity.BaseActivity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager

abstract class BaseStopGlideActivity :AppCompatActivity(){
    protected var isDestroy=true
    protected lateinit var glide: RequestManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isDestroy=false
        glide=Glide.with(this)
    }
    override fun onDestroy() {
        super.onDestroy()
        isDestroy=true
    }

//    protected fun requestHttp(runnable:(()->Runnable)){
//        requestHttp(runnable)
//    }
//
//    protected fun reqeustHttp(runnable: Runnable){
//        ThreadPool.execute(runnable)
//    }
}