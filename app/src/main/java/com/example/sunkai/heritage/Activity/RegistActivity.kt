package com.example.sunkai.heritage.Activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.bumptech.glide.Glide
import com.example.sunkai.heritage.Activity.BaseActivity.BaseTakeCameraActivity
import com.example.sunkai.heritage.ConnectWebService.HandleUser
import com.example.sunkai.heritage.Data.HandlePic
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.MakeToast
import kotlinx.android.synthetic.main.activity_regist.*
import java.io.ByteArrayOutputStream
import java.io.OutputStream

class RegistActivity : BaseTakeCameraActivity(), View.OnClickListener, TextWatcher {

    private lateinit var userName: String
    private lateinit var userPassword: String
    private lateinit var findPasswordQuestion: String
    private lateinit var findPasswordAnswer: String
    private lateinit var imageByte: ByteArray

    var isUploadImage = false

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
        setContentView(R.layout.activity_regist)
        initView()
    }

    @SuppressLint("InlinedApi")
    private fun initView() {
        views.add(regist_actitivy_username_editText)
        views.add(regist_actitivy_password_editText)
        views.add(regist_actitivy_insure_editText)
        views.add(regist_actitivy_question_editText)
        views.add(regist_actitivy_answer_editText)
        views.add(registUserImage)
        views.add(activity_regist_regist_button)

        setAllViewsOnclick()
        regist_actitivy_password_editText.addTextChangedListener(this)
        regist_actitivy_insure_editText.addTextChangedListener(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (Build.VERSION.SDK_INT >= 16) {
            val option = (View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
            window.decorView.systemUiVisibility = option
        }
    }

    override fun setImageToImageView(bitmap: Bitmap) {
        Glide.with(this).load(bitmap).into(registUserImage)
        val byte = HandlePic.drawableToByteArray(registUserImage.drawable)
        byte?.let {
            imageByte = byte
            isUploadImage = true
        }

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.activity_regist_regist_button -> submit()
            R.id.regist_activity_cancel_button -> {
                this.setResult(0)
                finish()
            }
            R.id.registUserImage -> {
                chooseAlertDialog.show()
            }
            else -> {
            }
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setViewsEnable(isEnable: Boolean) {
        for (view in views) {
            view.isEnabled = isEnable
        }
    }

    private fun setViewsIsEnable() {
        setViewsEnable(true)
    }

    private fun setViewsIsDisable() {
        setViewsEnable(false)
    }

    private fun judgeViewsTextIsEmpty(): Boolean {
        var OK = false
        for ((i, view) in views.withIndex()) {
            if (view is EditText) {
                val text = view.text.toString().trim()
                if (TextUtils.isEmpty(text)) {
                    view.error = errorMessage[i]
                    OK = true
                }
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
        if (judgeViewsTextIsEmpty() || judgePasswordIsSame()) {
            return
        }

        //开始注册
        userName = regist_actitivy_username_editText.text.toString().trim()
        userPassword = regist_actitivy_password_editText.text.toString().trim()
        findPasswordQuestion = regist_actitivy_question_editText.text.toString().trim()
        findPasswordAnswer = regist_actitivy_answer_editText.text.toString().trim()
        setViewsIsDisable()
        Thread(userRegist).start()
    }

    private var userRegist: Runnable = Runnable {
        val result = if (isUploadImage) {
            HandleUser.User_Regist(userName, userPassword, findPasswordQuestion, findPasswordAnswer, imageByte)
        } else {
            HandleUser.User_Regist(userName, userPassword, findPasswordQuestion, findPasswordAnswer, null)
        }
        val msg = Message()
        msg.what = result
        val userRegistHandler = object : Handler(mainLooper) {
            override fun handleMessage(msg: Message) {
                when {
                    msg.what == 1 -> {
                        regist_activity_cancel_button.visibility = View.VISIBLE
                        val intent = Intent()
                        intent.putExtra("userName", userName)
                        intent.putExtra("passWord", userPassword)
                        setResult(1, intent)
                        MakeToast.MakeText("注册成功")
                        finish()
                    }
                    msg.what == 0 -> MakeToast.MakeText("已有该用户")
                    else -> MakeToast.MakeText("注册失败")
                }
                setViewsIsEnable()
            }
        }
        userRegistHandler.sendMessage(msg)
    }

    override fun afterTextChanged(s: Editable?) {
        judgePasswordIsSame()
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }
}

