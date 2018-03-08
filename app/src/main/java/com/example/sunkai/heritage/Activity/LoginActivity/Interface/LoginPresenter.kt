package com.example.sunkai.heritage.Activity.LoginActivity.Interface

/*
 * Created by sunkai on 2018/3/8.
 */
interface LoginPresenter {
    fun loadUserNamesInShareprefrence()
    fun attemptoLogin(username:String,password:String)
    fun onDestroy()
}