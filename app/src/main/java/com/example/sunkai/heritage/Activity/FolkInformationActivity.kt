package com.example.sunkai.heritage.Activity

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.sunkai.heritage.Activity.BaseActivity.BaseHandleCollectActivity
import com.example.sunkai.heritage.ConnectWebService.HandleFolk
import com.example.sunkai.heritage.Data.ClassifyDivideData
import com.example.sunkai.heritage.Data.FolkDataLite
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.MakeToast.toast
import com.example.sunkai.heritage.tools.ThreadPool
import com.example.sunkai.heritage.tools.generateDarkColor
import com.example.sunkai.heritage.value.ACTIVITY_FRAGMENT
import com.example.sunkai.heritage.value.ALL_FOLK_INFO_ACTIVITY
import com.example.sunkai.heritage.value.HOST
import com.example.sunkai.heritage.value.TYPE_FOLK
import kotlinx.android.synthetic.main.activity_folk_info.*

/**
 * Created by sunkai on 2017-4-22.
 * 此类是在首页点击官方活动的list之后跳转的界面的类，用于显示活动的详细信息
 */

class FolkInformationActivity : BaseHandleCollectActivity(), View.OnClickListener {

    var id: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_folk_info)
        initView()
        val from = intent.getStringExtra("from")
        when (from) {
            ACTIVITY_FRAGMENT -> {
                val data = intent.getSerializableExtra("activity") as ClassifyDivideData
                setDatasToView(data)
                id = data.id
            }
            ALL_FOLK_INFO_ACTIVITY -> {
                val data = intent.getSerializableExtra("data") as FolkDataLite
                //使用lite的数据初始化完整的数据，并展示，同时后台读取全部的数据
                setDatasToView(ClassifyDivideData(data))
                getFolkData(data.id)
                id = data.id
            }
        }
    }

    private fun initView() {
        setSupportActionBar(activity_join_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.enterTransition.duration = 300
            window.sharedElementExitTransition.duration = 300
            window.sharedElementEnterTransition.duration = 300
            window.sharedElementReenterTransition.duration = 300
            window.sharedElementReturnTransition.duration = 300
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setDatasToView(data: ClassifyDivideData) {
        Glide.with(this).load(HOST + data.img).into(target)
        activityJoinCollapsingToolbar.title = data.title
        joinActivityTime.text = getString(R.string.apply_time) + data.time
        joinActivityLocaction.text = getString(R.string.location) + data.location
        joinActivityNumber.text = getString(R.string.number) + data.number
        joinActivityCategory.text = getString(R.string.category) + data.category
        joinActivityContent.text = data.content
    }

    private fun setErrorInfo() {
        joinActivityLocaction.text = "未知"
        joinActivityNumber.text = "未知"
        toast("加载出现问题")
    }

    private fun getFolkData(id: Int) {
        ThreadPool.execute {
            val folkData = HandleFolk.Get_Channel_Folk_Single_Information(id)
            if (folkData != null) {
                val data = ClassifyDivideData(folkData)
                runOnUiThread {
                    setDatasToView(data)
                }
            } else {
                runOnUiThread {
                    setErrorInfo()
                }
            }
        }
    }

    override fun onClick(v: View) {
    }

    override fun getType(): String {
        return TYPE_FOLK
    }

    override fun getID(): Int? {
        return id
    }

    private val target = object : SimpleTarget<Drawable>() {
        override fun onResourceReady(drawable: Drawable, transition: Transition<in Drawable>?) {
            val color = drawable.generateDarkColor()
            activityJoinCollapsingToolbar.setContentScrimColor(color)
            activityInformaitonTextBackground.setBackgroundColor(color)
            if (Build.VERSION.SDK_INT >= 21) {
                window.statusBarColor = color
            }
            join_activity_img.setImageDrawable(drawable)
        }

    }
}
