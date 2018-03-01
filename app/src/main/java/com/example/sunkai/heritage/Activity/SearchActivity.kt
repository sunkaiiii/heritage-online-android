package com.example.sunkai.heritage.Activity


import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.MakeToast
import kotlinx.android.synthetic.main.activity_search.*

/**
 * 此类用于处理用户搜索的页面
 */
class SearchActivity : AppCompatActivity(), View.OnClickListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        initView()
    }

    private fun initView() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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
//        searchClass(edit)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

//    private fun searchClass(searchText:String) {
//        var datas: List<FocusData>?
//        Thread{
//            datas = HandlePerson.Get_Search_UserInfo(searchText)
//            datas?.let {
//                runOnUiThread {
//                    val adapter = FocusListviewAdapter(this, datas!!, 3)
//                    search_activity_list.adapter = adapter
//                }
//            }
//        }.start()
//    }
}
