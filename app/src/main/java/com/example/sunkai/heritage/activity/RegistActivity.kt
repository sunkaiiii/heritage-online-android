package com.example.sunkai.heritage.activity

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.transition.Slide
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.view.children
import com.example.sunkai.heritage.activity.baseActivity.BaseTakeCameraActivity
import com.example.sunkai.heritage.connectWebService.HandleUser
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.MakeToast.toast
import com.example.sunkai.heritage.tools.WindowHelper
import com.example.sunkai.heritage.tools.encryptionPassWord
import com.example.sunkai.heritage.tools.toByteArray
import com.example.sunkai.heritage.value.ERROR
import com.example.sunkai.heritage.value.PASSWORD
import com.example.sunkai.heritage.value.USER_NAME
import kotlinx.android.synthetic.main.activity_regist.*
import java.util.*


class RegistActivity : BaseTakeCameraActivity(), View.OnClickListener, TextWatcher {

    private var userImageBitmap: Bitmap? = null

    private var isUploadImage = false

    private val views: ArrayList<View>by lazy {
        ArrayList<View>()
    }
    private val errorMessage: Array<String> by lazy {
        arrayOf("用户名不能为空",
                "密码不能为空",
                "确认密码不能为空",
                "密码召回问题不能为空",
                "密码找回答案不能为空",
                "密码输入不一致")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setBackgroundDrawable(null)
        startAnimation()
        setContentView(R.layout.activity_regist)
        initView()
    }

    override fun onStart() {
        super.onStart()
        setWindowFullScreen()
    }

    private fun startAnimation() {
        window.enterTransition = Slide(Gravity.END)
    }

    private fun setWindowFullScreen() {
       WindowHelper.setWindowFullScreen(this)
    }

    private fun initView() {
        addAllViews(registAllViewLinearLatyout)
        setBackGround()
        setAllViewsOnclick()
        regist_actitivy_password_editText.addTextChangedListener(this)
        regist_actitivy_insure_editText.addTextChangedListener(this)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    //将所有的view放入到list
    private fun addAllViews(viewgroup: ViewGroup) {
        viewgroup.children.toList().forEach { if (it is ViewGroup) addAllViews(it) else views.add(it) }
    }

    private fun setBackGround() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        if (hour >= 18 || hour <= 9) {
            activityRegistBackground.setBackgroundResource(R.mipmap.at_night_background)
        } else {
            activityRegistBackground.setBackgroundResource(R.mipmap.day_background)
        }
    }

    override fun setImageToImageView(bitmap: Bitmap) {
        glide.load(bitmap).into(registUserImage)
        userImageBitmap = bitmap
        isUploadImage = true

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.activity_regist_regist_button -> submit()
            R.id.registCancel -> {
                this.setResult(CANCEL)
                onBackPressed()
            }
        }
    }

    override fun getNeedOpenChooseImageView(): Array<View> {
        return arrayOf(registUserImage)
    }


    private fun setViewsIsEnable(isEnable: Boolean) {
        views.forEach {
            it.isEnabled = isEnable
        }
    }

    private fun setViewsIsEnable() {
        setViewsIsEnable(true)
    }

    private fun setViewsIsDisable() {
        setViewsIsEnable(false)
    }

    private fun judgeViewsTextIsEmpty(): Boolean {
        var OK = false
        var count = 0
        for (view in views) {
            //遍历所有的edittext，发现为空的时候显示对应的提示
            if (view is EditText) {
                val text = view.text.toString().trim()
                if (TextUtils.isEmpty(text)) {
                    view.error = errorMessage[count]
                    OK = true
                }
                count++
            }
        }
        return OK
    }

    private fun judgePasswordIsSame(): Boolean {
        val password = regist_actitivy_password_editText.text.toString()
        val insurePassword = regist_actitivy_insure_editText.text.toString()
        return if (password != insurePassword) {
            regist_actitivy_password_editText.error = "密码输入不一致"
            regist_actitivy_insure_editText.error = "密码输入不一致"
            true
        } else {
            regist_actitivy_password_editText.error = null
            regist_actitivy_insure_editText.error = null
            false
        }
    }

    private fun setAllViewsOnclick() {
        views.forEach { view -> view.setOnClickListener(this) }
    }

    private fun submit() {
        setViewsIsDisable()
        if (judgeViewsTextIsEmpty() || judgePasswordIsSame()) {
            setViewsIsEnable()
            return
        }
        //开始注册
        val userName = regist_actitivy_username_editText.text.toString().trim()
        val userPassword = regist_actitivy_password_editText.text.toString().trim()
        val findPasswordQuestion = regist_actitivy_question_editText.text.toString().trim()
        val findPasswordAnswer = regist_actitivy_answer_editText.text.toString().trim()
        requestHttp {
            userRegist(userName, userPassword, findPasswordQuestion, findPasswordAnswer)
        }
    }

    private fun userRegist(userName: String, userPassword: String, findPasswordQuestion: String, findPasswordAnswer: String) {
        val userPasswordDecript = (infoToRSA(userPassword) ?: return)
        val findPasswordAnswerDecript = (infoToRSA(findPasswordAnswer) ?: return)
        val result = if (isUploadImage) {
            val bitmap = userImageBitmap ?: return
            HandleUser.User_Regist(userName, userPasswordDecript, findPasswordQuestion, findPasswordAnswerDecript, bitmap.toByteArray())
        } else {
            HandleUser.User_Regist(userName, userPasswordDecript, findPasswordQuestion, findPasswordAnswerDecript, null)
        }
        runOnUiThread {
            when (result) {
                SUCCESS -> {
                    val intent = Intent()
                    intent.putExtra(USER_NAME, userName)
                    intent.putExtra(PASSWORD, userPassword)
                    setResult(SUCCESS, intent)
                    toast("注册成功")
                    finish()
                }
                HAD_USER -> toast("已有该用户")
                else -> toast("注册失败")
            }
            setViewsIsEnable()
        }
    }

    private fun infoToRSA(infos: String): String? {
        val encrtData = encryptionPassWord(infos)
        return if (ERROR == encrtData) {
            null
        } else encrtData
    }

    override fun afterTextChanged(s: Editable?) {
        judgePasswordIsSame()
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    companion object {
        const val CANCEL = 0
        const val SUCCESS = 1
        const val HAD_USER = 0
    }
}

