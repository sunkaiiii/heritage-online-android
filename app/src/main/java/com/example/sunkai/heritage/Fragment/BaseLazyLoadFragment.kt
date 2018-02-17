package com.example.sunkai.heritage.Fragment

import com.example.sunkai.heritage.Interface.LazyLoad

/**
 * viewpager懒加载的Base类
 * Created by sunkai on 2018/1/19.
 */
abstract class BaseLazyLoadFragment:android.support.v4.app.Fragment(),LazyLoad{
    private var isLoaded=false

    fun lazyLoad(){
        if(!isLoaded) {
            startLoadInformation()
            isLoaded=true
        }
    }
}