package com.example.sunkai.heritage.Fragment.BaseFragment

import android.os.Bundle
import android.view.View
import com.example.sunkai.heritage.Interface.LazyLoad

/**
 * viewpager懒加载的Base类
 * Created by sunkai on 2018/1/19.
 */
abstract class BaseLazyLoadFragment:android.support.v4.app.Fragment(),LazyLoad{
    private var isLoaded=true
    protected var isDetachhed=true

    //当view初次创建创建或者因销毁被重建的时候，重置加载状态
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        this.isDetachhed=false
        this.isLoaded=false
        savedInstanceState?.let{
            onRestoreFragmentLoadInformation()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        this.isDetachhed=true
    }

    fun lazyLoad(){
        if(!isLoaded&&!isDetachhed) {
            startLoadInformation()
            isLoaded=true
        }
    }

}