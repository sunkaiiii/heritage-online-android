package com.example.sunkai.heritage.Activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import com.example.sunkai.heritage.Data.FolkDataLite
import com.example.sunkai.heritage.R
import kotlinx.android.synthetic.main.activity_join.*

/**
 * 此类用来处理预约界面
 */

class JoinActivity : AppCompatActivity(), View.OnClickListener {

    private var folkActiviyData: FolkDataLite? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)
        initView()
        folkActiviyData = intent.getSerializableExtra("activity") as FolkDataLite
        activity_join_collapsing_toolbar.setCollapsedTitleTextAppearance(R.style.CollapsedAppBarTextSize)
        activity_join_collapsing_toolbar.setExpandedTitleTextAppearance(R.style.ExpandedAppBarSize)
        activity_join_collapsing_toolbar.title = folkActiviyData!!.title
        join_activity_content.text = folkActiviyData!!.title
        /**
         * 在页面显示的时候判断此用户是否已经预约此活动
         */
    }

    private fun initView() {
        setSupportActionBar(activity_join_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        join_activity_btn.setOnClickListener(this)

        join_activity_img.scaleType = ImageView.ScaleType.FIT_XY


    }

    override fun onClick(v: View) {

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }


}
