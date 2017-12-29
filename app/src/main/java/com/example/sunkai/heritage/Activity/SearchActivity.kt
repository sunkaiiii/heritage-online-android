package com.example.sunkai.heritage.Activity


import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView

import com.example.sunkai.heritage.ConnectWebService.HandlePerson
import com.example.sunkai.heritage.Data.FocusData
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.Adapter.FocusListviewAdpter
import com.example.sunkai.heritage.tools.MakeToast

/**
 * 此类用于处理用户搜索的页面
 */
class SearchActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var search_activity_btn: ImageView
    private lateinit var search_activity_edit: EditText
    private lateinit var search_activity_list: ListView
    private var actionBack: ActionBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        initView()
    }

    private fun initView() {
        search_activity_btn = findViewById(R.id.search_activity_btn)
        search_activity_edit = findViewById(R.id.search_activity_edit)
        search_activity_list = findViewById(R.id.search_activity_list)
        actionBack = supportActionBar
        actionBack?.setDisplayHomeAsUpEnabled(true)
        search_activity_btn.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.search_activity_btn -> submit()
            else -> {
            }
        }
    }

    private fun submit() {
        val edit = search_activity_edit.text.toString().trim()
        if (TextUtils.isEmpty(edit)) {
            MakeToast.MakeText("内容不能为空")
            return
        }
        /**
         * 将搜索的文本传入搜索类，并搜索内容
         */
        searchClass(edit)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun searchClass(searchText:String) {
        var datas: List<FocusData>?
        Thread{
            datas = HandlePerson.Get_Search_UserInfo(searchText)
            if (datas != null) {
                runOnUiThread {
                    val adapter = FocusListviewAdpter(this, datas, 3)
                    search_activity_list.adapter = adapter
                }
            }
        }.start()
    }
}
