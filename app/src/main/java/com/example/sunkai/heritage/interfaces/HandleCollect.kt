package com.example.sunkai.heritage.interfaces

import android.view.MenuItem

/**
 * 有关收藏的接口
 * Created by sunkai on 2018/3/14.
 */
interface HandleCollect {
    fun checkIsCollect(userID:Int,typeName:String,typeID:Int):Boolean
    fun handleCollect(item: MenuItem)
    fun setItemState(item: MenuItem, success:Boolean, checked:Boolean,showToast:Boolean=true)
}