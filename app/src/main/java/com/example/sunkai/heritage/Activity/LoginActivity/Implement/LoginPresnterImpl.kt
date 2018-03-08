package com.example.sunkai.heritage.Activity.LoginActivity.Implement

import android.text.TextUtils
import com.example.sunkai.heritage.Activity.LoginActivity.Interface.LoginPresenter
import com.example.sunkai.heritage.Activity.LoginActivity.Interface.LoginView
import com.example.sunkai.heritage.Activity.LoginActivity.Interface.Logininteractor
import com.example.sunkai.heritage.tools.MakeToast.toast

/**
 * 处理登录页view与model的presenter
 * Created by sunkai on 2018/3/8.
 */
class LoginPresnterImpl(private var loginView:LoginView?, private val logininteractor: Logininteractor):LoginPresenter,Logininteractor.OnLoginFinishedListner {


    override fun attemptoLogin(username: String, password: String) {
        if(TextUtils.isEmpty(username)||TextUtils.isEmpty(password)){
            loginView?.showTextEmptyError()
            return
        }
        loginView?.disableWidge()
        logininteractor.login(username,password,this)
    }

    override fun onEncryError() {
        toast("出现问题")
    }

    override fun onDestroy() {
        loginView=null
    }

    override fun onPasswordError() {
        loginView?.setPasswordError()
        loginView?.enableWidge()
    }

    override fun onSuccess() {
        loginView?.gotoMainPage()
    }
    override fun loadUserNamesInShareprefrence() {
        val userNames=logininteractor.loadUserNamesFromSharePrefrece()
        loginView?.updateAutoCompleteUserNames(userNames)
    }
}