package com.example.sunkai.heritage.Activity

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
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
import kotlinx.android.synthetic.main.activity_join.*

/**
 * Created by sunkai on 2017-4-22.
 * 此类是在首页点击官方活动的list之后跳转的界面的类，用于显示活动的详细信息
 */

class ActivityInformationActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var textBackGround: View
    lateinit var collapsingToolbarLayout: CollapsingToolbarLayout
    lateinit var time: TextView
    lateinit var location: TextView
    lateinit var number: TextView
    lateinit var category: TextView
    lateinit var content: TextView

    /**
     * 在首页载入的时候，活动信息已被载入，点击list内容之后，将点击的类传入bundle（类实现了serializable接口，可以序列化），传入之后读取出来并赋值在控件上
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)
        initView()
        val from = intent.getStringExtra("from")
        when (from) {
            ACTIVITY_FRAGMENT -> {
                val data = intent.getSerializableExtra("activity") as ClassifyDivideData
                setDatasToView(data)
            }
            ALL_FOLK_INFO_ACTIVITY->{
                val data=intent.getSerializableExtra("data") as FolkDataLite
                //使用lite的数据初始化完整的数据，并展示，同时后台读取全部的数据
                setDatasToView(ClassifyDivideData(data))
                getFolkData(data.id)
            }
        }

    }

    private fun initView() {
        time = findViewById(R.id.join_activity_time)
        location = findViewById(R.id.join_activity_locaction)
        number = findViewById(R.id.join_activity_number)
        category = findViewById(R.id.join_activity_category)
        content = findViewById(R.id.join_activity_content)
        collapsingToolbarLayout = findViewById(R.id.activity_join_collapsing_toolbar)
        textBackGround = findViewById(R.id.activity_informaiton_text_background)
        setSupportActionBar(activity_join_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    @SuppressLint("SetTextI18n")
    private fun setDatasToView(data: ClassifyDivideData) {
        Glide.with(this).load(HOST + data.img).into(target)
        collapsingToolbarLayout.title = data.title
        time.text = getString(R.string.apply_time) + data.time
        location.text = getString(R.string.location) + data.location
        number.text = getString(R.string.number) + data.number
        category.text = getString(R.string.category) + data.category
        content.text = data.content
    }

    private fun setErrorInfo(){
        location.text="未知"
        number.text="未知"
        toast("加载出现问题")
    }

    private fun getFolkData(id:Int){
        ThreadPool.execute {
            val folkData=HandleFolk.Get_Channel_Folk_Single_Information(id)
            if(folkData!=null){
                val data=ClassifyDivideData(folkData)
                runOnUiThread {
                    setDatasToView(data)
                }
            }else{
                runOnUiThread{
                    setErrorInfo()
                }
            }
        }
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
