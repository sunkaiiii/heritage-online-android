package com.example.sunkai.heritage.Activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.view.MenuItem
import com.example.sunkai.heritage.Activity.LoginActivity.LoginActivity
import com.example.sunkai.heritage.Adapter.MyMessageRecyclerAdapter
import com.example.sunkai.heritage.ConnectWebService.HandlePush
import com.example.sunkai.heritage.Interface.OnPageLoaded
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.ThreadPool
import kotlinx.android.synthetic.main.activity_my_message.*

class MyMessageActivity : AppCompatActivity(),OnPageLoaded {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_message)
        setRefresh()
        getMessageData()
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setRefresh() {
        myMessageSwipeRefresh.setOnRefreshListener {
            getMessageData()
        }
    }

    private fun getMessageData() {
        if(LoginActivity.userID==0) return
        onPreLoad()
        ThreadPool.execute {
            val messages=HandlePush.getUserPushMessages(LoginActivity.userID)
            runOnUiThread {
                val adapter=MyMessageRecyclerAdapter(this,messages)
                myMessageRecyclerView.addItemDecoration(DividerItemDecoration(this,DividerItemDecoration.VERTICAL))
                myMessageRecyclerView.adapter=adapter
                onPostLoad()
            }
        }
    }

    override fun onPreLoad() {
        myMessageSwipeRefresh.isRefreshing=true
        myMessageRecyclerView.adapter=null
    }

    override fun onPostLoad() {
        myMessageSwipeRefresh.isRefreshing=false
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            android.R.id.home->{
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
