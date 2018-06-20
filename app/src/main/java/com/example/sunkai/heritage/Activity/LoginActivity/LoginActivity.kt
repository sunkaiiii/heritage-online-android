package com.example.sunkai.heritage.Activity.LoginActivity

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.transition.Slide
import androidx.transition.TransitionManager
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.GravityCompat
import androidx.appcompat.app.AppCompatActivity
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.ArrayAdapter
import com.bumptech.glide.RequestManager
import com.example.sunkai.heritage.Activity.LoginActivity.Implement.LoginInteractorImpl
import com.example.sunkai.heritage.Activity.LoginActivity.Implement.LoginPresnterImpl
import com.example.sunkai.heritage.Activity.LoginActivity.Interface.LoginView
import com.example.sunkai.heritage.Activity.MainActivity
import com.example.sunkai.heritage.Activity.RegistActivity
import com.example.sunkai.heritage.Dialog.FindPasswordDialog
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.WindowHelper
import com.example.sunkai.heritage.value.*
import kotlinx.android.synthetic.main.activity_login.*

/**
 * 登陆页面采用了MVP结构
 * 目前只是作为实验，未来根据是否需要MVP架构来改变其他的Activity
 */
class LoginActivity : AppCompatActivity(), LoginView, View.OnClickListener {

    private val presenter = LoginPresnterImpl(this, LoginInteractorImpl())
    private var isIntoMainpage = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        window.setBackgroundDrawable(null)
        initview()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setWindowFullScreen()
        }
        //如果是从欢迎页进入的，则显示跳过登录
        isIntoMainpage = intent.getIntExtra(IS_INTO, FROM_WELCOME)
        jumpSignIn.visibility = if (isIntoMainpage == FROM_WELCOME) View.VISIBLE else View.GONE
        presenter.loadUserNamesInShareprefrence()
    }


    private fun initview() {
        jumpSignIn.setOnClickListener(this)
        goToLoginButton.setOnClickListener(this)
        signInButton.setOnClickListener(this)
        registButton.setOnClickListener(this)
        findPasswordTextView.setOnClickListener(this)
    }

    //设置全屏沉浸窗口
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun setWindowFullScreen() {
        WindowHelper.setWindowFullScreen(this)
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.jumpSignIn -> jumpToMainActivity()
            R.id.goToLoginButton -> showLoginWidge()
            R.id.signInButton -> presenter.attemptoLogin(userNameEditText.text.toString(), passwordEditText.text.toString())
            R.id.registButton -> jumpToRegist()
            R.id.findPasswordTextView -> showFindPassWordDialog()
        }
    }

    //跳过登录
    private fun jumpToMainActivity() {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    //点击登录按钮，显示登陆模块
    override fun showLoginWidge() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            TransitionManager.beginDelayedTransition(login_constraintLayout, Slide(GravityCompat.getAbsoluteGravity(Gravity.END,resources.configuration.layoutDirection)))
        }
        ll_activity_login_navagate.visibility = View.INVISIBLE
        include_login_view.visibility = View.VISIBLE
    }

    //在登录模块点击返回，显示第一页
    override fun showFirstPageWidge() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            TransitionManager.beginDelayedTransition(login_constraintLayout, Slide(GravityCompat.getAbsoluteGravity(GravityCompat.START, resources.configuration.layoutDirection)))
        }
        include_login_view.visibility = View.INVISIBLE
        ll_activity_login_navagate.visibility = View.VISIBLE
    }

    private fun jumpToRegist() {
        val intent = Intent(this@LoginActivity, RegistActivity::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.exitTransition = android.transition.TransitionInflater.from(this).inflateTransition(android.R.transition.fade)
            ActivityCompat.startActivityForResult(this, intent, FROM_REGIST, ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle())
        } else {
            startActivityForResult(intent, FROM_REGIST)
        }
    }

    private fun showFindPassWordDialog() {
        val dialog = FindPasswordDialog()
        dialog.show(supportFragmentManager, "找回密码问题")
    }


    override fun disableWidge() {
        setWidhgeState(false)
    }

    override fun enableWidge() {
        setWidhgeState(true)
    }

    private fun setWidhgeState(state: Boolean) {
        signInButton.isEnabled = state
        signInButton.visibility = if (state) View.VISIBLE else View.INVISIBLE
        loginProgressBar.visibility = if (state) View.GONE else View.VISIBLE
    }

    //读取登陆过的用户的用户名（不包括密码）
    override fun updateAutoCompleteUserNames(userNames: Set<String>) {
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1)
        adapter.addAll(userNames)
        userNameEditText.setAdapter(adapter)
    }

    override fun showTextEmptyError() {
        if (TextUtils.isEmpty(userNameEditText.text.toString())) {
            userNameEditText.error = "用户名不能为空"
        }
        if (TextUtils.isEmpty(passwordEditText.text.toString())) {
            passwordEditText.error = "密码不能为空"
        }
    }

    //登陆返回密码错误的时候，显示密码错误
    override fun setPasswordError() {
        passwordEditText.error = getString(R.string.error_incorrect_password)
        passwordEditText.requestFocus()
    }

    override fun gotoMainPage() {

        //从Welcome页过来而并非从二级页面登录，则直接进入主页
        if (isIntoMainpage == FROM_WELCOME || isIntoMainpage == LOG_OUT) {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
            return
        }
        //从首页跳过登录在二级页面登陆后，通知所有页面信息刷新
        setResult(1, intent)
        finish()
    }


    //从注册页会带回用户名和密码，执行登陆
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            FROM_REGIST -> {
                val loginName = data?.getStringExtra(USER_NAME) ?: return
                val loginPassword = data.getStringExtra(PASSWORD) ?: return
                presenter.attemptoLogin(loginName, loginPassword)
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

