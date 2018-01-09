package com.example.sunkai.heritage.ConnectWebService

/**
 * Created by sunkai on 2018/1/9.
 * 此类封装了关于登录页有关的服务器请求的方法
 */

import org.ksoap2.serialization.SoapObject

object HandleUser : BaseSetting() {
    fun Sign_in(userName: String, PassWord: String): Boolean {
        BaseSetting.methodName = "Sign_In"
        BaseSetting.soapAction = BaseSetting.namespace + "/" + BaseSetting.methodName
        val soapObject = SoapObject(BaseSetting.namespace, BaseSetting.methodName)
        soapObject.addProperty("userName", userName)
        soapObject.addProperty("PassWord", PassWord)
        val result = BaseSetting.Get_Post(soapObject)
        return BaseSetting.success == result
    }

    fun User_Regist(userName: String, passWord: String, findPasswordQuestion: String, findPassWordAnswer: String): Int {
        BaseSetting.methodName = "User_Regist"
        BaseSetting.soapAction = BaseSetting.namespace + "/" + BaseSetting.methodName
        val soapObject = SoapObject(BaseSetting.namespace, BaseSetting.methodName)
        soapObject.addProperty("userName", userName)
        soapObject.addProperty("passWord", passWord)
        soapObject.addProperty("findPasswordQuestion", findPasswordQuestion)
        soapObject.addProperty("findPassWordAnswer", findPassWordAnswer)
        val result = BaseSetting.Get_Post(soapObject)
        return if (BaseSetting.success == result) {
            1
        } else if ("hadUser" == result) {
            0
        } else {
            -1
        }
    }

    fun Get_User_ID(userName: String): Int {
        BaseSetting.methodName = "Get_User_ID"
        BaseSetting.soapAction = BaseSetting.namespace + "/" + BaseSetting.methodName
        val soapObject = SoapObject(BaseSetting.namespace, BaseSetting.methodName)
        soapObject.addProperty("userName", userName)
        val result = BaseSetting.Get_Post(soapObject)
        return if (result != null && BaseSetting.error != result && Integer.parseInt(result) > 0) {
            Integer.parseInt(result)
        } else {
            -1
        }
    }

    fun Find_Password_Question(userName: String): String? {
        BaseSetting.methodName = "Find_Password_Question"
        BaseSetting.soapAction = BaseSetting.namespace + "/" + BaseSetting.methodName
        val soapObject = SoapObject(BaseSetting.namespace, BaseSetting.methodName)
        soapObject.addProperty("userName", userName)
        val result = BaseSetting.Get_Post(soapObject)
        return if (result != null && BaseSetting.error != result) result else null
    }

    fun Check_Question_Answer(userName: String, questionAnswer: String): Boolean {
        BaseSetting.methodName = "Check_Question_Answer"
        BaseSetting.soapAction = BaseSetting.namespace + "/" + BaseSetting.methodName
        val soapObject = SoapObject(BaseSetting.namespace, BaseSetting.methodName)
        soapObject.addProperty("userName", userName)
        soapObject.addProperty("questionAnswer", questionAnswer)
        val result = BaseSetting.Get_Post(soapObject)
        return BaseSetting.success == result
    }

    fun Change_Password(userName: String, Password: String): Boolean {
        BaseSetting.methodName = "Change_Password"
        BaseSetting.soapAction = BaseSetting.namespace + "/" + BaseSetting.methodName
        val soapObject = SoapObject(BaseSetting.namespace, BaseSetting.methodName)
        soapObject.addProperty("userName", userName)
        soapObject.addProperty("Password", Password)
        val result = BaseSetting.Get_Post(soapObject)
        return BaseSetting.success == result
    }
}
