package com.example.sunkai.heritage.ConnectWebService


import android.util.Base64
import com.example.sunkai.heritage.Activity.LoginActivity.LoginActivity
import okhttp3.FormBody


/*
 * Created by sunkai on 2018/1/30.
 */

object HandleUser : BaseSetting() {
    fun Sign_In(userName: String, userPassword: String): Boolean {
        val builder = FormBody.Builder()
        builder.add("username", userName)
        builder.add("password", userPassword.replace("\n", ""))
        val result = PutPost("$URL/Sign_In", builder.build())
        return if (result != ERROR && result.toInt() > 0) {
            LoginActivity.userID = result.toInt()
            true
        } else {
            false
        }
    }

    fun User_Regist(userName: String, passWord: String, findPasswordQuestion: String, findPassWordAnswer: String, userImage: ByteArray? = null): Int {
        val userImageString = if (userImage == null) {
            ""
        } else {
            Base64.encodeToString(userImage,Base64.DEFAULT)
        }
        println(userImageString)
        val form = FormBody.Builder()
                .add("username", userName)
                .add("password", passWord)
                .add("findPasswordQuestion", findPasswordQuestion)
                .add("findPasswordAnswer", findPassWordAnswer)
                .add("userImage", userImageString).build()
        val result = PutPost("$URL/UserRegist", form)
        return when (result) {
            "1" -> 1
            "0" -> 0
            else -> -1
        }
    }

    fun Find_Password_Question(userName: String): String? {
        val result = PutGet("$URL/FindPassWordQuestion?username=$userName")
        return if (ERROR != result) result else null
    }

    fun Check_Question_Answer(userName: String, questionAnswer: String): Boolean {
        val form = FormBody.Builder().add("username", userName).add("answer", questionAnswer).build()
        val result = PutPost("$URL/CheckQuestionAnswer", form)
        return SUCCESS == result
    }

    fun Change_Password(userName: String, Password: String): Boolean {
        val form = FormBody.Builder().add("username", userName).add("password", Password).build()
        val result = PutPost("$URL/ChangePassword", form)
        return SUCCESS == result
    }

    fun SendFCMToken(userName: String,token:String):Boolean{
        val form=FormBody.Builder().add("userName",userName).add("token",token).build();
        val result=PutPost("$URL/SendFCMToken",form)
        return SUCCESS==result
    }
}