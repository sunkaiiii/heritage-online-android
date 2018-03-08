package com.example.sunkai.heritage.Activity.LoginActivity.Interface

/*
 * Created by sunkai on 2018/3/8.
 */
interface LoginView {
    fun setWindowFullScreen()
    fun showLoginWidge()
    fun showFirstPageWidge()
    fun setPasswordError()
    fun gotoMainPage()
    fun disableWidge()
    fun enableWidge()
    fun updateAutoCompleteUserNames(userNames:Set<String>)
    fun showTextEmptyError()
}