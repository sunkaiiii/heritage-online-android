package com.example.sunkai.heritage.Activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import com.bumptech.glide.Glide
import com.example.sunkai.heritage.R
import kotlinx.android.synthetic.main.activity_setting.*


class SettingActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        initView()
    }

    private fun initView() {
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


}
