package com.example.sunkai.heritage.Activity.LoginActivity.Interface

/*
 * Created by sunkai on 2018/3/8.
 */
interface Logininteractor {
    interface OnLoginFinishedListner{
        fun onEncryError()
        fun onPasswordError()
        fun onSuccess()
    }

    fun login(username:String,password:String,listner:OnLoginFinishedListner)
    fun loadUserNamesFromSharePrefrece():Set<String>
}