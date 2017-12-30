package com.example.sunkai.heritage.Activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.example.sunkai.heritage.ConnectWebService.HandleUser
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.MakeToast
import java.util.*

class RegistActivity : AppCompatActivity(), View.OnClickListener, View.OnFocusChangeListener {

    private lateinit var regist_actitivy_username_editText: EditText
    private lateinit var regist_actitivy_password_editText: EditText
    private lateinit var regist_actitivy_insure_editText: EditText
    private lateinit var regist_actitivy_question_editText: EditText
    private lateinit var regist_actitivy_answer_editText: EditText
    private lateinit var regist_activity_regist_button: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var editTextToTextViewHashMap: HashMap<EditText, TextView>
    private lateinit var regist_activity_cancel_button: Button

    private lateinit var userName: String
    private lateinit var userPassword: String
    private lateinit var findPasswordQuestion: String
    private lateinit var findPasswordAnswer: String


    internal var userRegist: Runnable = Runnable {
        val result = HandleUser.User_Regist(userName, userPassword, findPasswordQuestion, findPasswordAnswer)
        val msg = Message()
        msg.what = result
        val userRegistHandler = object : Handler(mainLooper) {
            override fun handleMessage(msg: Message) {
                if (msg.what == 1) {
                    progressBar.visibility = View.VISIBLE
                    regist_activity_cancel_button.visibility = View.VISIBLE
                    val intent = Intent()
                    intent.putExtra("userName", userName)
                    intent.putExtra("passWord", userPassword)
                    setResult(1, intent)
                    MakeToast.MakeText("注册成功")
                    finish()
                } else if (msg.what == 0) {
                    MakeToast.MakeText("已有该用户")
                } else {
                    MakeToast.MakeText("注册失败")
                }
                progressBar.visibility = View.GONE
                regist_actitivy_username_editText.isEnabled = true
                regist_actitivy_password_editText.isEnabled = true
                regist_actitivy_insure_editText.isEnabled = true
                regist_actitivy_question_editText.isEnabled = true
                regist_actitivy_answer_editText.isEnabled = true
                regist_activity_cancel_button.visibility = View.VISIBLE
                regist_activity_regist_button.isEnabled = true
            }
        }
        userRegistHandler.sendMessage(msg)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_regist)
        initView()
    }

    private fun initView() {
        editTextToTextViewHashMap = HashMap()
        val regist_actitivy_username_textView = findViewById<TextView>(R.id.regist_actitivy_username_textView)
        val regist_actitivy_password_textView = findViewById<TextView>(R.id.regist_actitivy_password_textView)
        val regist_actitivy_insure_textView = findViewById<TextView>(R.id.regist_actitivy_insure_textView)
        val regist_actitivy_question_textView = findViewById<TextView>(R.id.regist_actitivy_question_textView)
        val regist_actitivy_answer_textView = findViewById<TextView>(R.id.regist_actitivy_answer_textView)
        regist_actitivy_username_editText = findViewById(R.id.regist_actitivy_username_editText)
        regist_actitivy_password_editText = findViewById(R.id.regist_actitivy_password_editText)
        regist_actitivy_insure_editText = findViewById(R.id.regist_actitivy_insure_editText)
        regist_actitivy_question_editText = findViewById(R.id.regist_actitivy_question_editText)
        regist_actitivy_answer_editText = findViewById(R.id.regist_actitivy_answer_editText)
        regist_activity_regist_button = findViewById(R.id.activity_regist_regist_button)
        progressBar = findViewById(R.id.progressBar)
        regist_activity_cancel_button = findViewById(R.id.regist_activity_cancel_button)
        regist_activity_cancel_button.setOnClickListener(this)
        regist_actitivy_username_editText.onFocusChangeListener = this
        regist_actitivy_password_editText.onFocusChangeListener = this
        regist_actitivy_insure_editText.onFocusChangeListener = this
        regist_actitivy_question_editText.onFocusChangeListener = this
        regist_actitivy_answer_editText.onFocusChangeListener = this
        regist_activity_regist_button.setOnClickListener(this)
        progressBar.setOnClickListener(this)
        editTextToTextViewHashMap.put(regist_actitivy_question_editText, regist_actitivy_question_textView)
        editTextToTextViewHashMap.put(regist_actitivy_username_editText, regist_actitivy_username_textView)
        editTextToTextViewHashMap.put(regist_actitivy_password_editText, regist_actitivy_password_textView)
        editTextToTextViewHashMap.put(regist_actitivy_insure_editText, regist_actitivy_insure_textView)
        editTextToTextViewHashMap.put(regist_actitivy_answer_editText, regist_actitivy_answer_textView)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.activity_regist_regist_button -> submit()
            R.id.regist_activity_cancel_button -> {
                this.setResult(0)
                finish()
            }
            else -> {
            }
        }
    }

    override fun onFocusChange(v: View, hasFocus: Boolean) {
        when (v.id) {
            R.id.regist_actitivy_username_editText
                , R.id.regist_actitivy_password_editText
                , R.id.regist_actitivy_insure_editText
                , R.id.regist_actitivy_question_editText
                , R.id.regist_actitivy_answer_editText
            -> changeTextviewStatus(v as EditText, hasFocus)
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

    private fun changeTextviewStatus(editText: EditText, hasFocus: Boolean) {
        val textView = editTextToTextViewHashMap[editText] ?: return
        if (hasFocus)
            textView.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
        else
            textView.setTextColor(ContextCompat.getColor(this, R.color.deepGrey))
    }

    private fun submit() {
        // validate
        var editText = regist_actitivy_username_editText.text.toString().trim()
        if (TextUtils.isEmpty(editText)) {
            regist_actitivy_username_editText.error = "用户名不能为空"
            regist_actitivy_username_editText.requestFocus()
            return
        }

        editText = regist_actitivy_password_editText.text.toString().trim()
        if (TextUtils.isEmpty(editText)) {
            regist_actitivy_password_editText.error = "密码不能为空"
            regist_actitivy_password_editText.requestFocus()
            return
        }

        editText = regist_actitivy_insure_editText.text.toString().trim()
        if (TextUtils.isEmpty(editText)) {
            regist_actitivy_insure_editText.error = "确认密码不能为空"
            regist_actitivy_insure_editText.requestFocus()
            return
        }

        editText = regist_actitivy_question_editText.text.toString().trim()
        if (TextUtils.isEmpty(editText)) {
            regist_actitivy_question_editText.error = "密码召回问题不能为空"
            regist_actitivy_question_editText.requestFocus()
            return
        }

        editText = regist_actitivy_answer_editText.text.toString().trim()
        if (TextUtils.isEmpty(editText)) {
            regist_actitivy_answer_editText.error = "密码找回答案不能为空"
            regist_actitivy_answer_editText.requestFocus()
            return
        }
        val password = regist_actitivy_password_editText.text.toString()
        val insurePassword = regist_actitivy_insure_editText.text.toString()
        if (password != insurePassword) {
            Toast.makeText(this, "密码输入不一致", Toast.LENGTH_SHORT).show()
            regist_actitivy_insure_editText.requestFocus()
            return
        }

        // TODO validate success, do something
        userName = regist_actitivy_username_editText.text.toString().trim()
        userPassword = regist_actitivy_password_editText.text.toString().trim()
        findPasswordQuestion = regist_actitivy_question_editText.text.toString().trim()
        findPasswordAnswer = regist_actitivy_answer_editText.text.toString().trim()
        progressBar.visibility = View.VISIBLE
        regist_actitivy_username_editText.isEnabled = false
        regist_actitivy_password_editText.isEnabled = false
        regist_actitivy_insure_editText.isEnabled = false
        regist_actitivy_question_editText.isEnabled = false
        regist_actitivy_answer_editText.isEnabled = false
        regist_activity_cancel_button.visibility = View.GONE
        regist_activity_regist_button.isEnabled = false
        Thread(userRegist).start()
    }


}