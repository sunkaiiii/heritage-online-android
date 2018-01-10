package com.example.sunkai.heritage.Activity
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View


import com.example.sunkai.heritage.Data.ClassifyActiviyData
import com.example.sunkai.heritage.R
import kotlinx.android.synthetic.main.activity_join.*

/**
 * Created by sunkai on 2017-4-22.
 * 此类是在首页点击官方活动的list之后跳转的界面的类，用于显示活动的详细信息
 */

class ActivityInformationActivity : AppCompatActivity(), View.OnClickListener {


    /**
     * 在首页载入的时候，活动信息已被载入，点击list内容之后，将点击的类传入bundle（类实现了serializable接口，可以序列化），传入之后读取出来并赋值在控件上
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)
        initView()
        val data = intent.getSerializableExtra("activity") as ClassifyActiviyData
        if (null != data.activityImage) {
            val bitmap = BitmapFactory.decodeByteArray(data.activityImage, 0, data.activityImage!!.size)
            join_activity_img.setImageBitmap(bitmap)
        }
        join_activity_title.text = data.activityTitle
        join_activity_content.text = data.activityContent
    }

    private fun initView() {
        join_activity_btn.visibility = View.GONE
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        join_activity_btn.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.join_activity_btn -> {
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
