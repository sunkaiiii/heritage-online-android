package com.example.sunkai.heritage.Activity

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.support.design.widget.CollapsingToolbarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.sunkai.heritage.Data.ClassifyDivideData
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.generateColor
import com.example.sunkai.heritage.tools.generateDarkColor
import com.example.sunkai.heritage.tools.generateTextColor
import com.example.sunkai.heritage.value.HOST
import kotlinx.android.synthetic.main.activity_join.*

/**
 * Created by sunkai on 2017-4-22.
 * 此类是在首页点击官方活动的list之后跳转的界面的类，用于显示活动的详细信息
 */

class ActivityInformationActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var textBackGround: View
    lateinit var collapsingToolbarLayout: CollapsingToolbarLayout

    /**
     * 在首页载入的时候，活动信息已被载入，点击list内容之后，将点击的类传入bundle（类实现了serializable接口，可以序列化），传入之后读取出来并赋值在控件上
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)
        initView()
        val data = intent.getSerializableExtra("activity") as ClassifyDivideData
        collapsingToolbarLayout.title = data.title
        join_activity_content.text = data.content
        Glide.with(this).load(HOST + data.img).into(target)
    }

    private fun initView() {
        collapsingToolbarLayout = findViewById(R.id.activity_join_collapsing_toolbar)
        textBackGround = findViewById(R.id.activity_informaiton_text_background)
        setSupportActionBar(activity_join_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onClick(v: View) {
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private val target = object : SimpleTarget<Drawable>() {
        override fun onResourceReady(drawable: Drawable, transition: Transition<in Drawable>?) {
            val color = generateDarkColor(drawable)
            collapsingToolbarLayout.setContentScrimColor(color)
            textBackGround.setBackgroundColor(color)
            if (Build.VERSION.SDK_INT >= 21) {
                window.statusBarColor = color
            }
            join_activity_img.setImageDrawable(drawable)
        }

    }
}
