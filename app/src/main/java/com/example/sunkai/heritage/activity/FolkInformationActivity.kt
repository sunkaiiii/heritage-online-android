package com.example.sunkai.heritage.activity

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.sunkai.heritage.activity.base.BaseHandleCollectActivity
import com.example.sunkai.heritage.connectWebService.HandleFolk
import com.example.sunkai.heritage.entity.ClassifyDivideData
import com.example.sunkai.heritage.entity.FolkDataLite
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.MakeToast.toast
import com.example.sunkai.heritage.tools.ViewImageUtils
import com.example.sunkai.heritage.tools.generateDarkColor
import com.example.sunkai.heritage.tools.loadImageFromServer
import com.example.sunkai.heritage.value.*
import kotlinx.android.synthetic.main.activity_folk_info.*

/**
 * Created by sunkai on 2017-4-22.
 * 此类是在首页点击官方活动的list之后跳转的界面的类，用于显示活动的详细信息
 */

class FolkInformationActivity : BaseHandleCollectActivity(), View.OnClickListener {

    //用于添加收藏需要的id
    var id: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_folk_info)
        setIgnoreToolbar(true)
        initView()
        val from = intent.getStringExtra(FROM)
        when (from) {
            ACTIVITY_FRAGMENT -> {
                val data = intent.getSerializableExtra(ACTIVITY) as ClassifyDivideData
                setDatasToView(data)
                id = data.id
            }
            //从全部非遗的页面进入，使用lite的数据先初始化，然后获取非遗信息
            ALL_FOLK_INFO_ACTIVITY -> {
                val data = intent.getSerializableExtra(DATA) as FolkDataLite
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
        window.enterTransition.duration = 300
        window.sharedElementExitTransition.duration = 300
        window.sharedElementEnterTransition.duration = 300
        window.sharedElementReenterTransition.duration = 300
        window.sharedElementReturnTransition.duration = 300
    }

    //警报很烦，屏蔽掉
    @SuppressLint("SetTextI18n")
    private fun setDatasToView(data: ClassifyDivideData) {
        glide.loadImageFromServer(data.img).into(target)
        join_activity_img.setOnClickListener {
            ViewImageUtils.setViewImageClick(this, join_activity_img, data.img)
        }
        activityJoinCollapsingToolbar.title = data.title
        joinActivityTime.text = getString(R.string.apply_time) + data.time
        joinActivityLocaction.text = getString(R.string.location) + data.location
        joinActivityNumber.text = getString(R.string.number) + data.number
        joinActivityCategory.text = getString(R.string.category) + data.category
        joinActivityContent.text = data.content
    }

    //给非遗标题和状态栏着色
    private val target = object : SimpleTarget<Drawable>() {
        override fun onResourceReady(drawable: Drawable, transition: Transition<in Drawable>?) {
            val color = drawable.generateDarkColor()
            activityJoinCollapsingToolbar.setContentScrimColor(color)
            activityInformaitonTextBackground.setBackgroundColor(color)
            window.statusBarColor = color
            window.navigationBarColor = color
            join_activity_img.setImageDrawable(drawable)
        }
    }

    private fun setErrorInfo() {
        joinActivityLocaction.text = "未知"
        joinActivityNumber.text = "未知"
        toast("加载出现问题")
    }

    private fun getFolkData(id: Int) {
        requestHttp {
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

}
