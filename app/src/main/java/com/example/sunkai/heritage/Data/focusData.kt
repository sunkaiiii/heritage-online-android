package com.example.sunkai.heritage.Data

/**
 * Created by sunkai on 2017/12/15.
 * 此类用于存放用户关注、粉丝的情况信息
 */


class focusData(var focusUserid: Int = 0
                ,var focusFansID: Int = 0
                ,var name: String? = null
                ,var followeachother:Boolean = false
                ,var userImage: ByteArray? = null
                ,var isCheck:Boolean = true){
    fun getCheck():Boolean=isCheck //我也不知道为什么isCheck没有生成get函数
}