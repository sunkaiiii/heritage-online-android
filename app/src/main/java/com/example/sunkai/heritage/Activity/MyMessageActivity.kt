package com.example.sunkai.heritage.Activity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.sunkai.heritage.Activity.BaseActivity.BaseGlideActivity
import com.example.sunkai.heritage.Activity.LoginActivity.LoginActivity
import com.example.sunkai.heritage.Adapter.MyMessageRecyclerAdapter
import com.example.sunkai.heritage.ConnectWebService.HandlePush
import com.example.sunkai.heritage.Interface.OnItemClickListener
import com.example.sunkai.heritage.Interface.OnPageLoaded
import com.example.sunkai.heritage.R
import kotlinx.android.synthetic.main.activity_my_message.*

/**
 * 我的消息Activity
 */
class MyMessageActivity : BaseGlideActivity(),OnPageLoaded {

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
        requestHttp {
            val messages=HandlePush.getUserPushMessages(LoginActivity.userID)
            runOnUiThread {
                val adapter=MyMessageRecyclerAdapter(this,messages,glide)
                myMessageRecyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
                setAdapterClick(adapter)
                myMessageRecyclerView.adapter=adapter
                onPostLoad()
            }
        }
    }

    private fun setAdapterClick(adapter: MyMessageRecyclerAdapter) {
        adapter.setOnItemClickListener { view, position ->
            val data=adapter.getItem(position)
            val intent=Intent(this@MyMessageActivity,UserCommentDetailActivity::class.java)
            intent.putExtra("id",data.replyCommentID)
            startActivity(intent)
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
