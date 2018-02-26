package com.example.sunkai.heritage.Activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.bumptech.glide.Glide
import com.example.sunkai.heritage.ConnectWebService.HandleUser
import com.example.sunkai.heritage.Data.GlobalContext
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.MakeToast
import com.example.sunkai.heritage.tools.infoToRSA
import com.example.sunkai.heritage.value.SIGN_OUT
import kotlinx.android.synthetic.main.activity_setting.*


class SettingActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var builder: AlertDialog.Builder
    private lateinit var ad: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        initView()
    }

    private fun initView() {
        setting_sigh_out_text.setOnClickListener(this)
        setting_sigh_out_img.setOnClickListener(this)
        setting_changepassword_img.setOnClickListener(this)
        setting_changepassword_text.setOnClickListener(this)
        setting_about_us_img.setOnClickListener(this)
        setting_about_us_text.setOnClickListener(this)
        setting_push_switch_text.setOnClickListener(this)
        setting_push_switch_img.setOnClickListener(this)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        sign_name_textview.text = LoginActivity.userName
        val imageUrl = intent.getStringExtra("userImage")
        if(imageUrl!=null) {
            Glide.with(this).load(imageUrl).into(sign_in_icon)
        }
    }

    override fun onClick(v: View) {
        val intent: Intent
        when (v.id) {
            R.id.setting_sigh_out_img, R.id.setting_sigh_out_text -> sign_out()
            R.id.setting_changepassword_img, R.id.setting_changepassword_text -> {
                if (LoginActivity.userID == 0) {
                    MakeToast.MakeText("没有登录")
                    intent = Intent(this, LoginActivity::class.java)
                    intent.putExtra("isInto", 1)
                    startActivityForResult(intent, 1)
                    return
                }
                changePassword()
            }
            R.id.setting_about_us_img, R.id.setting_about_us_text -> {
                intent = Intent(this, AboutUSActivity::class.java)
                startActivity(intent)
            }
            R.id.setting_push_switch_text, R.id.setting_push_switch_img -> {
                intent = Intent(this, SettingListActivity::class.java)
                startActivity(intent)
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

    private fun changePassword() {
        builder = AlertDialog.Builder(this@SettingActivity).setTitle("修改密码").setView(R.layout.change_password)
        ad = builder.create()
        ad.show()
        val userName: EditText = ad.findViewById<View>(R.id.change_password_name) as EditText
        val password: EditText = ad.findViewById<View>(R.id.change_password_password) as EditText
        val insure: EditText = ad.findViewById<View>(R.id.change_password_insure) as EditText
        val submit: Button = ad.findViewById<View>(R.id.change_password_queding) as Button
        val cancel: Button = ad.findViewById<View>(R.id.change_password_cancel) as Button
        userName.setText(LoginActivity.userName)
        cancel.setOnClickListener { ad.dismiss() }


        val changePasswordThread = Runnable {
            val encryPassword= infoToRSA(password.text.toString())?:return@Runnable
            val result = HandleUser.Change_Password(LoginActivity.userName!!, encryPassword)
            runOnUiThread({
                if(result){
                    MakeToast.MakeText("修改成功")
                    if(ad.isShowing){
                        ad.dismiss()
                    }else{
                        MakeToast.MakeText("修改失败，请稍后再试")
                        submit.isEnabled=true
                    }
                }
            })
        }

        submit.setOnClickListener(View.OnClickListener {
            if (TextUtils.isEmpty(password.text) || TextUtils.isEmpty(insure.text)) {
                MakeToast.MakeText("密码不能为空")
                return@OnClickListener
            }
            if (password.text.toString() != insure.text.toString()) {
                MakeToast.MakeText("密码输入不一致")
                return@OnClickListener
            }
            submit.isEnabled = false
            Thread(changePasswordThread).start()
        })
    }

    private fun sign_out() {
        AlertDialog.Builder(this).setTitle("是否注销?").setPositiveButton("确定") { _, _ ->
            setResult(SIGN_OUT)
            GlobalContext.instance.unregistUser() //注销的时候退出当前账号
            getSharedPreferences("data", Context.MODE_PRIVATE).edit().clear().apply()//清除自动登录的信息
            LoginActivity.userID = 0
            LoginActivity.userName = null
            finish()
        }.setNegativeButton("取消") { dialog, _ -> dialog.dismiss() }.show()
    }
}
