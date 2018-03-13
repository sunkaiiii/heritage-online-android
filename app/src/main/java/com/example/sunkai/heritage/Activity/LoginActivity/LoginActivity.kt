package com.example.sunkai.heritage.Activity.LoginActivity

import android.annotation.SuppressLint
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
import android.view.Gravity
import android.view.View
import android.widget.*
import at.markushi.ui.CircleButton
import com.example.sunkai.heritage.Activity.LoginActivity.Implement.LoginInteractorImpl
import com.example.sunkai.heritage.Activity.LoginActivity.Implement.LoginPresnterImpl
import com.example.sunkai.heritage.Activity.LoginActivity.Interface.LoginView
import com.example.sunkai.heritage.Activity.MainActivity
import com.example.sunkai.heritage.Activity.RegistActivity
import com.example.sunkai.heritage.tools.GlobalContext
import com.example.sunkai.heritage.Dialog.FindPasswordDialog
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.value.LOG_OUT
import kotlinx.android.synthetic.main.activity_login.*

/**
 * 登陆页面采用了MVP结构
 * 目前只是作为实验，未来根据是否需要MVP架构来改变其他的Activity
 */
class LoginActivity : AppCompatActivity(),LoginView, View.OnClickListener {

    private lateinit var mEmailView: AutoCompleteTextView
    private lateinit var mPasswordView: EditText
    private lateinit var registButton: Button
    private lateinit var mEmailSignInButton: CircleButton
    private lateinit var jumpSignIn: TextView
    private lateinit var findPassword: TextView
    private lateinit var presenter:LoginPresnterImpl

    private val requestCode: Int = 0

    private var isIntoMainpage = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        window.setBackgroundDrawable(null)
        initview()
        setWindowFullScreen()
        isIntoMainpage = intent.getIntExtra("isInto", 0)
        jumpSignIn.visibility = if (isIntoMainpage == 0) View.VISIBLE else View.GONE
        presenter=LoginPresnterImpl(this,LoginInteractorImpl())
        presenter.loadUserNamesInShareprefrence()
    }



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

    }
    override fun setWindowFullScreen() {
        @SuppressLint("InlinedApi")
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



    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.activity_login_jump_login -> jumpToMainActivity()
            R.id.btn_activity_login_goto_login -> showLoginWidge()
            R.id.email_sign_in_button -> presenter.attemptoLogin(mEmailView.text.toString(),mPasswordView.text.toString())
            R.id.activity_login_regist -> jumpToRegist()
            R.id.activity_login_find_password -> showFindPassWordDialog()
        }
    }

    private fun jumpToMainActivity() {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun showLoginWidge() {
        TransitionManager.beginDelayedTransition(login_constraintLayout,Slide(Gravity.END))
        ll_activity_login_navagate.visibility = View.INVISIBLE
        include_login_view.visibility = View.VISIBLE
    }

    override fun showFirstPageWidge(){
        TransitionManager.beginDelayedTransition(login_constraintLayout,Slide(Gravity.START))
        include_login_view.visibility = View.INVISIBLE
        ll_activity_login_navagate.visibility = View.VISIBLE
    }

    private fun jumpToRegist() {
        val intent = Intent(this@LoginActivity, RegistActivity::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.exitTransition=android.transition.TransitionInflater.from(this).inflateTransition(android.R.transition.fade)
            ActivityCompat.startActivityForResult(this,intent, requestCode, ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle())
        } else {
            startActivityForResult(intent, requestCode)
        }
    }

    private fun showFindPassWordDialog() {
        val dialog = FindPasswordDialog()
        dialog.show(supportFragmentManager, "找回密码问题")
    }

    override fun disableWidge(){
        mEmailSignInButton.isEnabled = false
        mEmailSignInButton.visibility = View.INVISIBLE
        pg_activity_login_progress.visibility=View.VISIBLE
    }

    override fun enableWidge(){
        mEmailSignInButton.isEnabled = true
        mEmailSignInButton.visibility = View.VISIBLE
        pg_activity_login_progress.visibility=View.GONE
    }

    override fun updateAutoCompleteUserNames(userNames: Set<String>) {
        val adapter=ArrayAdapter<String>(this,android.R.layout.simple_list_item_1)
        adapter.addAll(userNames)
        mEmailView.setAdapter(adapter)
    }

    override fun showTextEmptyError() {
        if(TextUtils.isEmpty(mEmailView.text.toString())){
            mEmailView.error="用户名不能为空"
        }
        if(TextUtils.isEmpty(mPasswordView.text.toString())){
            mPasswordView.error="密码不能为空"
        }
    }

    override fun setPasswordError() {
        mPasswordView.error = getString(R.string.error_incorrect_password)
        mPasswordView.requestFocus()
    }

    override fun gotoMainPage() {

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
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            1 -> {
                val loginName = data!!.getStringExtra("userName")
                val loginPassword = data.getStringExtra("passWord")
                presenter.attemptoLogin(loginName,loginPassword)
            }
        }
    }

    override fun onBackPressed() {
        if (include_login_view.visibility == View.VISIBLE) {
           showFirstPageWidge()
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

