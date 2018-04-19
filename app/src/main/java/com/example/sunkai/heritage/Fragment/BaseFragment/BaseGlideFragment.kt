package com.example.sunkai.heritage.Fragment.BaseFragment

import android.os.Bundle
import android.support.v4.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager

abstract class BaseGlideFragment:Fragment() {
    protected lateinit var glide: RequestManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        glide= Glide.with(this)
    }
}