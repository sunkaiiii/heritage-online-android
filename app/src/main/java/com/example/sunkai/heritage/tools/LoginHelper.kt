package com.example.sunkai.heritage.tools

import android.content.Context
import android.content.Intent
import com.example.sunkai.heritage.activity.loginActivity.LoginActivity
import com.example.sunkai.heritage.connectWebService.HandleUser

fun attempToLogin(userName: String?, userPassword: String?): Boolean {
    val name = userName ?: return false
    val password = userPassword ?: return false
    val encryptPassword = infoToRSA(password) ?: return false
    return HandleUser.Sign_In(name, encryptPassword)
}

fun checkLogin(): Boolean {
    return LoginActivity.userID != 0
}

fun backGroundLogin(): Boolean {
    val username = GlobalContext.instance.getSharedPreferences("data", Context.MODE_PRIVATE).getString("user_name", null)
            ?: return false
    val userPassword = GlobalContext.instance.getSharedPreferences("data", Context.MODE_PRIVATE).getString("user_password", null)
            ?: return false
    LoginActivity.userName = username
    return attempToLogin(username, userPassword)
}

fun gotoLogin() {
    val intent = Intent(GlobalContext.instance, LoginActivity::class.java)
    GlobalContext.instance.startActivity(intent)
}