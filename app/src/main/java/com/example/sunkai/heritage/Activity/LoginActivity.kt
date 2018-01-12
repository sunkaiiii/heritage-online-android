package com.example.sunkai.heritage.Activity

import android.annotation.TargetApi
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.*
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import at.markushi.ui.CircleButton

import com.example.sunkai.heritage.ConnectWebService.HandleUser
import com.example.sunkai.heritage.Data.GlobalContext
import com.example.sunkai.heritage.Data.MySqliteHandler
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.MakeToast
import kotlinx.android.synthetic.main.activity_login.*

/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : AppCompatActivity() {
    private var mAuthTask: UserLoginTask?=null

    private lateinit var mEmailView: EditText
    private lateinit var mPasswordView: EditText
    private lateinit var registButton: Button
    private lateinit var mEmailSignInButton: CircleButton
    private lateinit var jumpSignIn: TextView
    private lateinit var findPassword: TextView

    internal lateinit var infromRight:Animation
    internal lateinit var outToLeft:Animation
    internal lateinit var infromLeft:Animation
    internal lateinit var outToRight:Animation
    private val requestCode: Int = 0

    private var changePasswordUsername: String? = null

    private lateinit var builder: AlertDialog.Builder
    private lateinit var ad: AlertDialog

    internal var isIntoMainpage = 0
    internal var getUserID: Runnable = Runnable {
        userID = HandleUser.Get_User_ID(userName!!)
        if (userID > 0) {
            getUserIDHandler.sendEmptyMessage(1)
        } else {
            getUserIDHandler.sendEmptyMessage(0)
        }
    }
    internal var getUserIDHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            if (msg.what == 1) {
                val editor = getSharedPreferences("data", Context.MODE_PRIVATE).edit()
                editor.putInt("user_id", userID)
                editor.putString("user_name", userName)
                editor.putString("user_password", mPasswordView.text.toString())
                editor.apply()
                GlobalContext.instance.registUser()
                //从Welcome页过来而并非从二级页面登录，则直接进入主页
                if (isIntoMainpage == 0) {
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                    return
                }
                //从首页跳过登录在二级页面登陆后，通知所有页面信息刷新
                var intent = Intent("andrid.intent.action.refreshList")
                sendBroadcast(intent)
                intent = Intent("android.intent.action.refreInfomation")
                sendBroadcast(intent)
                setResult(1, getIntent())
                finish()
            } else {
                Toast.makeText(this@LoginActivity, "出现错误", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        resetLoginSql()
        infromRight=AnimationUtils.loadAnimation(this,R.anim.in_from_right)
        outToLeft=AnimationUtils.loadAnimation(this,R.anim.out_to_left)
        infromLeft=AnimationUtils.loadAnimation(this,R.anim.in_from_left)
        outToRight=AnimationUtils.loadAnimation(this,R.anim.out_to_right)

        isIntoMainpage = intent.getIntExtra("isInto", 0)
        // Set up the login form.
        mEmailView = findViewById(R.id.email)
        mPasswordView = findViewById(R.id.password)
        if(Build.VERSION.SDK_INT>=21){
            val decorView=window.decorView
            val option = (View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR)
            window.navigationBarColor = Color.TRANSPARENT
            decorView.systemUiVisibility=option
        }
//        mPasswordView.setOnEditorActionListener { _, id, _ ->
//            if (id == R.id.login || id == EditorInfo.IME_NULL) {
//                attemptLogin()
//                return@setOnEditorActionListener true
//            }
//            false
//        }

        btn_activity_login_goto_login.setOnClickListener{
            ll_activity_login_navagate.startAnimation(outToLeft)
            include_login_view.startAnimation(infromRight)
            ll_activity_login_navagate.visibility=View.INVISIBLE
            include_login_view.visibility=View.VISIBLE
        }

        mEmailSignInButton = findViewById(R.id.email_sign_in_button)
        mEmailSignInButton.setOnClickListener { _ -> attemptLogin() }
        registButton = findViewById(R.id.activity_login_regist)
        registButton.setOnClickListener { _ ->
            val intent = Intent(this@LoginActivity, RegistActivity::class.java)
            startActivityForResult(intent, requestCode)
        }

        jumpSignIn = findViewById(R.id.activity_login_jump_login)
        if (isIntoMainpage == 0) {
            jumpSignIn.visibility = View.VISIBLE
        } else {
            jumpSignIn.visibility = View.GONE
        }
        jumpSignIn.setOnClickListener { _ ->
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        findPassword = findViewById(R.id.activity_login_find_password)
        findPassword.setOnClickListener { _ ->
            builder = AlertDialog.Builder(this@LoginActivity).setTitle("忘记密码").setView(R.layout.find_password)
            ad = builder.create()
            ad.show()
            val find_password_user = ad.findViewById<EditText>(R.id.find_password_username)
            val find_password_question = ad.findViewById<EditText>(R.id.find_password_question)
            val find_password_answer = ad.findViewById<EditText>(R.id.find_password_answer)
            val queding = ad.findViewById<Button>(R.id.find_password_queding)
            val cancel = ad.findViewById<Button>(R.id.find_password_cancel)
            cancel?.setOnClickListener { _ -> ad.dismiss() }
            queding?.setOnClickListener { _ ->
                if (TextUtils.isEmpty(find_password_user!!.text)) {
                    MakeToast.MakeText("用户名不能为空")
                    return@setOnClickListener
                }
                val userName = find_password_user.text.toString()
                val findPasswordHandler = object : Handler(mainLooper) {
                    override fun handleMessage(msg: Message) {
                        queding.isEnabled = true
                        when {
                            msg.what == -1 -> Toast.makeText(this@LoginActivity, "发生错误", Toast.LENGTH_SHORT).show()
                            msg.what == 0 -> Toast.makeText(this@LoginActivity, "没有该用户", Toast.LENGTH_SHORT).show()
                            else -> {
                                find_password_question?.visibility=View.VISIBLE
                                find_password_answer?.visibility = View.VISIBLE
                                val result = msg.data.getString("userName")
                                find_password_question?.setText(result)
                                find_password_user.isEnabled = false
                            }
                        }
                    }
                }
                val findPasswordQuestion = Runnable{
                    val result: String? = HandleUser.Find_Password_Question(userName)
                    when (result) {
                        null -> findPasswordHandler.sendEmptyMessage(-1)
                        "noUser" -> findPasswordHandler.sendEmptyMessage(0)
                        else -> {
                            val msg = Message()
                            val bundle = Bundle()
                            msg.what = 1
                            bundle.putString("userName", result)
                            msg.data = bundle
                            findPasswordHandler.sendMessage(msg)
                        }
                    }
                }
                if (find_password_question != null) {
                    if (find_password_question.visibility == View.GONE) {
                        queding.isEnabled = false
                        Thread(findPasswordQuestion).start()
                    } else {
                        val checkUserAnswerHandler = object : Handler(mainLooper) {
                            override fun handleMessage(msg: Message) {
                                queding.isEnabled = true
                                if (msg.what == 1) {
                                    //                                        Toast.makeText(LoginActivity.this,"成功",Toast.LENGTH_SHORT).show();
                                    changePasswordUsername = userName
                                    ad.dismiss()
                                    changePassword()
                                } else {
                                    Toast.makeText(this@LoginActivity, "回答错误", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        val checkUserAnswer = Runnable{
                            val result = HandleUser.Check_Question_Answer(userName, find_password_answer!!.text.toString())
                            if (result) {
                                checkUserAnswerHandler.sendEmptyMessage(1)
                            } else {
                                checkUserAnswerHandler.sendEmptyMessage(0)
                            }
                        }
                        if (find_password_answer != null) {
                            if (TextUtils.isEmpty(find_password_answer.text)) {
                                Toast.makeText(this@LoginActivity, "答案不能为空", Toast.LENGTH_SHORT).show()
                            } else {
                                queding.isEnabled = false
                                Thread(checkUserAnswer).start()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun resetLoginSql() {
        val db = MySqliteHandler.GetWritableDatabase()
        val values = ContentValues()
        values.put("user_id", 0)
        values.put("user_name", "0")
        values.put("user_password", "0")
        val whereString = arrayOf("1")
        db.update("user_login_info", values, "id=?", whereString)
    }

    private fun changePassword() {
        builder = AlertDialog.Builder(this@LoginActivity).setTitle("修改密码").setView(R.layout.change_password)
        ad = builder.create()
        ad.show()
        val userName: EditText? = ad.findViewById(R.id.change_password_name)
        val password: EditText? = ad.findViewById(R.id.change_password_password)
        val insure: EditText? = ad.findViewById(R.id.change_password_insure)
        val submit: Button? = ad.findViewById(R.id.change_password_queding)
        val cancel: Button? = ad.findViewById(R.id.change_password_cancel)
        userName?.setText(changePasswordUsername)
        cancel?.setOnClickListener { _ -> ad.dismiss() }

        val changePasswordHandler = object : Handler(mainLooper) {
            override fun handleMessage(msg: Message) {
                if (msg.what == 1) {
                    MakeToast.MakeText("修改成功")
                    ad.dismiss()
                } else {
                    MakeToast.MakeText("修改失败，请稍后再试")
                    if (submit != null) {
                        submit.isEnabled = true
                    }
                }
            }
        }

        val changePasswordThread = Runnable{
            val result = HandleUser.Change_Password(changePasswordUsername!!, password!!.text.toString())
            if (result) {
                changePasswordHandler.sendEmptyMessage(1)
            } else {
                changePasswordHandler.sendEmptyMessage(0)
            }
        }

        submit?.setOnClickListener { _ ->
            if (TextUtils.isEmpty(password!!.text) || TextUtils.isEmpty(insure!!.text)) {
                MakeToast.MakeText("密码不能为空")
                return@setOnClickListener
            }
            if (password.text.toString() != insure.text.toString()) {
                MakeToast.MakeText("密码输入不一致")
                return@setOnClickListener
            }
            submit.isEnabled = false
            Thread(changePasswordThread).start()
        }
    }


    private fun attemptLogin() {
        if (mAuthTask != null) {
            return
        }

        // Reset errors.
        mEmailView.error = null
        mPasswordView.error = null

        // Store values at the time of the login attempt.
        val email = mEmailView.text.toString()
        val password = mPasswordView.text.toString()

        var cancel = false
        if (TextUtils.isEmpty(email)) {
            cancel = true
        }

        if (cancel) {
            mEmailView.error = "用户名不能为空"
        } else {
            pg_activity_login_progress.visibility=View.VISIBLE
            mEmailSignInButton.isEnabled=false
            mEmailSignInButton.visibility=View.INVISIBLE
            mAuthTask = UserLoginTask(email, password)
            mAuthTask!!.execute(null as Void?)
        }
    }


    inner class UserLoginTask internal constructor(private val mEmail: String, private val mPassword: String) : AsyncTask<Void, Void, Boolean>() {

        override fun doInBackground(vararg params: Void): Boolean? {
            return HandleUser.Sign_in(mEmail, mPassword)
        }

        override fun onPostExecute(success: Boolean?) {
            mEmailSignInButton.isEnabled=true
            mEmailSignInButton.visibility=View.VISIBLE
            pg_activity_login_progress.visibility=View.GONE
            mAuthTask = null
            if (success!!) {
                userName = mEmail
                Thread(getUserID).start()
            } else {
                mPasswordView.error = getString(R.string.error_incorrect_password)
                mPasswordView.requestFocus()
            }
        }

        override fun onCancelled() {
            mEmailSignInButton.isEnabled=true
            mEmailSignInButton.visibility=View.VISIBLE
            pg_activity_login_progress.visibility=View.GONE
            mAuthTask = null
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            1 -> {
                val loginName = data!!.getStringExtra("userName")
                val loginPassword = data.getStringExtra("passWord")
                mAuthTask = UserLoginTask(loginName, loginPassword)
                mAuthTask?.execute(null as Void?)
            }
            else -> {
            }
        }
    }

    override fun onBackPressed() {
        if(include_login_view.visibility==View.VISIBLE){
            include_login_view.startAnimation(outToRight)
            ll_activity_login_navagate.startAnimation(infromLeft)
            include_login_view.visibility=View.INVISIBLE
            ll_activity_login_navagate.visibility=View.VISIBLE
        }else{
            super.onBackPressed()
        }
    }

    companion object {

        /**
         * 登陆之后获取用户的ID
         */
        var userID: Int = 0
        var userName: String?=null
    }
}

