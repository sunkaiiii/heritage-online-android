package com.example.sunkai.heritage.Activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.transition.Slide
import android.support.transition.TransitionManager
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.content.edit
import at.markushi.ui.CircleButton
import com.example.sunkai.heritage.ConnectWebService.HandleUser
import com.example.sunkai.heritage.Data.GlobalContext
import com.example.sunkai.heritage.Dialog.FindPasswordDialog
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.BaseAsyncTask
import com.example.sunkai.heritage.tools.MakeToast.toast
import com.example.sunkai.heritage.tools.ThreadPool
import com.example.sunkai.heritage.tools.infoToRSA
import com.example.sunkai.heritage.value.LOG_OUT
import kotlinx.android.synthetic.main.activity_login.*

/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : AppCompatActivity(), View.OnClickListener {
    private var mAuthTask: UserLoginTask? = null

    private lateinit var mEmailView: AutoCompleteTextView
    private lateinit var mPasswordView: EditText
    private lateinit var registButton: Button
    private lateinit var mEmailSignInButton: CircleButton
    private lateinit var jumpSignIn: TextView
    private lateinit var findPassword: TextView

    private val requestCode: Int = 0


    private var isIntoMainpage = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        window.setBackgroundDrawable(null)
        initview()
        loadUserNames()
        isIntoMainpage = intent.getIntExtra("isInto", 0)
        jumpSignIn.visibility = if (isIntoMainpage == 0) View.VISIBLE else View.GONE
    }


    @SuppressLint("InlinedApi")
    private fun initview() {
        jumpSignIn = findViewById(R.id.activity_login_jump_login)
        mEmailSignInButton = findViewById(R.id.email_sign_in_button)
        registButton = findViewById(R.id.activity_login_regist)
        mEmailView = findViewById(R.id.email)
        mPasswordView = findViewById(R.id.password)
        findPassword = findViewById(R.id.activity_login_find_password)
        activity_login_jump_login.setOnClickListener(this)
        btn_activity_login_goto_login.setOnClickListener(this)
        email_sign_in_button.setOnClickListener(this)
        activity_login_regist.setOnClickListener(this)
        activity_login_find_password.setOnClickListener(this)
        if (Build.VERSION.SDK_INT >= 21) {
            val decorView = window.decorView
            val option = (View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR)
            window.navigationBarColor = Color.TRANSPARENT
            decorView.systemUiVisibility = option
        }
    }
    private fun loadUserNames(){
        ThreadPool.execute {
            val usernames=loadSharePrefefenrecesUserNames().toList()
            val userNameAdapter=ArrayAdapter<String>(this,android.R.layout.simple_list_item_1)
            userNameAdapter.addAll(usernames)
            runOnUiThread {
                mEmailView.setAdapter(userNameAdapter)
            }
        }
    }

    private fun loadSharePrefefenrecesUserNames():Set<String>{
        val userNames=getSharedPreferences("userNames", Context.MODE_PRIVATE).getStringSet("userNames", mutableSetOf())
        Log.d("userNames",userNames.toString())
        return userNames
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.activity_login_jump_login -> jumpToMainActivity()
            R.id.btn_activity_login_goto_login -> showLoginWidge()
            R.id.email_sign_in_button -> attemptLogin()
            R.id.activity_login_regist -> jumpToRegist()
            R.id.activity_login_find_password -> showFindPassWordDialog()
        }
    }

    private fun jumpToMainActivity() {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showLoginWidge() {
        TransitionManager.beginDelayedTransition(login_constraintLayout,Slide(Gravity.END))
        ll_activity_login_navagate.visibility = View.INVISIBLE
        include_login_view.visibility = View.VISIBLE
    }

    private fun jumpToRegist() {
        val option = ActivityOptionsCompat.makeSceneTransitionAnimation(this)
        val intent = Intent(this@LoginActivity, RegistActivity::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ActivityCompat.startActivityForResult(this, intent, requestCode, option.toBundle())
        } else {
            startActivityForResult(intent, requestCode)
        }
    }

    private fun showFindPassWordDialog() {
        val dialog = FindPasswordDialog()
        dialog.show(supportFragmentManager, "找回密码问题")
    }


    private fun attemptLogin() {
        if (mAuthTask != null) {
            return
        }

        mEmailView.error = null
        mPasswordView.error = null

        val email = mEmailView.text.toString()
        val password = mPasswordView.text.toString()

        var cancel = false
        if (TextUtils.isEmpty(email)) {
            cancel = true
        }

        if (cancel) {
            mEmailView.error = "用户名不能为空"
        } else {
            pg_activity_login_progress.visibility = View.VISIBLE
            mEmailSignInButton.isEnabled = false
            mEmailSignInButton.visibility = View.INVISIBLE
            mAuthTask = UserLoginTask(email, password, this)
            mAuthTask!!.execute(null as Void?)
        }
    }


    internal class UserLoginTask internal constructor(private val mEmail: String, private var mPassword: String, loginActivity: LoginActivity) : BaseAsyncTask<Void, Void, Boolean, LoginActivity>(loginActivity) {

        override fun doInBackground(vararg params: Void): Boolean {
            mPassword = infoToRSA(mPassword) ?: return false
            return HandleUser.Sign_In(mEmail, mPassword)
        }

        override fun onPostExecute(success: Boolean) {
            val activity = getEntity()
            activity?.let {
                activity.mEmailSignInButton.isEnabled = true
                activity.mEmailSignInButton.visibility = View.VISIBLE
                activity.pg_activity_login_progress.visibility = View.GONE
                activity.mAuthTask = null
                if (success) {
                    userName = mEmail
                    activity.handleLogin(success)
                } else {
                    activity.mPasswordView.error = activity.getString(R.string.error_incorrect_password)
                    activity.mPasswordView.requestFocus()
                }
            }
        }

        override fun onCancelled() {
            val activity = getEntity()
            activity?.let {
                activity.mEmailSignInButton.isEnabled = true
                activity.mEmailSignInButton.visibility = View.VISIBLE
                activity.pg_activity_login_progress.visibility = View.GONE
                activity.mAuthTask = null
            }
        }
    }

    private fun handleLogin(success:Boolean) {
        val userName= userName?:return
        if (success) {
            val userNames=loadSharePrefefenrecesUserNames().toMutableSet()
            userNames.add(userName)
            Log.d("putUserName",userNames.toString())
            getSharedPreferences("data", Context.MODE_PRIVATE).edit {
                putInt("user_id", userID)
                putString("user_name", userName)
                putString("user_password", mPasswordView.text.toString())
            }
            getSharedPreferences("userNames", Context.MODE_PRIVATE).edit {
                putStringSet("userNames",userNames)
            }
            GlobalContext.instance.registUser()
            //从Welcome页过来而并非从二级页面登录，则直接进入主页
            if (isIntoMainpage == 0 || isIntoMainpage == LOG_OUT) {
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
                return
            }
            //从首页跳过登录在二级页面登陆后，通知所有页面信息刷新
            setResult(1, intent)
            finish()
        } else {
            toast("出现错误")
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            1 -> {
                val loginName = data!!.getStringExtra("userName")
                val loginPassword = data.getStringExtra("passWord")
                mAuthTask = UserLoginTask(loginName, loginPassword, this)
                mAuthTask?.execute(null as Void?)
            }
        }
    }

    override fun onBackPressed() {
        if (include_login_view.visibility == View.VISIBLE) {
            TransitionManager.beginDelayedTransition(login_constraintLayout,Slide(Gravity.START))
            include_login_view.visibility = View.INVISIBLE
            ll_activity_login_navagate.visibility = View.VISIBLE
        } else {
            super.onBackPressed()
        }
    }

    companion object {

        /**
         * 登陆之后获取用户的ID
         */
        var userID: Int = 0
        var userName: String? = null
    }
}

