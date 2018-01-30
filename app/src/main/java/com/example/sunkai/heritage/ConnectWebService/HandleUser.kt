package com.example.sunkai.heritage.ConnectWebService


import android.util.Log
import com.example.sunkai.heritage.Activity.LoginActivity
import okhttp3.FormBody
import org.kobjects.base64.Base64
import org.ksoap2.serialization.SoapObject
import kotlin.math.log


/*
 * Created by sunkai on 2018/1/30.
 */

object HandleUser : BaseSettingNew() {
    fun Sign_In(userName: String, userPassword: String): Boolean {
        val builder = FormBody.Builder()
        builder.add("username", userName)
        builder.add("password", userPassword.replace("\n", ""))
        val result = PutPost(URL + "/Sign_In", builder.build())
        return if (result.toInt() > 0) {
            LoginActivity.userID = result.toInt()
            true
        } else {
            false
        }
    }

    fun User_Regist(userName: String, passWord: String, findPasswordQuestion: String, findPassWordAnswer: String, userImage: ByteArray? = null): Int {
        val userImageString=if(userImage==null){
            ""
        }else{
            Base64.encode(userImage)
        }
        println(userImageString)
        val form = FormBody.Builder()
                .add("username", userName)
                .add("password", passWord)
                .add("findPasswordQuestion", findPasswordQuestion)
                .add("findPasswordAnswer", findPassWordAnswer)
                .add("userImage",userImageString).build()
        val result = PutPost(URL+"/UserRegist",form)
        return when (result) {
            "1" -> 1
            "0" -> 0
            else -> -1
        }
    }
    fun Find_Password_Question(userName: String): String? {
        val result=PutGet(URL+"/FindPassWordQuestion"+"?username="+userName)
        return if (ERROR != result) result else null
    }

    fun Check_Question_Answer(userName: String, questionAnswer: String): Boolean {
        val form=FormBody.Builder().add("username",userName).add("answer",questionAnswer).build()
        val result = PutPost(URL+"/CheckQuestionAnswer",form)
        Log.d("Check_Question_Answer",result)
        return SUCCESS == result
    }

    fun Change_Password(userName: String, Password: String): Boolean {
        val form=FormBody.Builder().add("username",userName).add("password",Password).build()
        val result = PutPost(URL+"/ChangePassword",form)
        return SUCCESS == result
    }
}