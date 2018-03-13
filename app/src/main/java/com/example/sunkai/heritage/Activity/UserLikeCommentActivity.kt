package com.example.sunkai.heritage.Activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import com.example.sunkai.heritage.Activity.LoginActivity.LoginActivity
import com.example.sunkai.heritage.Adapter.MyLikeCommentRecyclerAdapter
import com.example.sunkai.heritage.ConnectWebService.HandleFind
import com.example.sunkai.heritage.Interface.OnItemClickListener
import com.example.sunkai.heritage.Interface.OnPageLoaded
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.ThreadPool
import com.example.sunkai.heritage.value.GRID_LAYOUT_DESTINY
import kotlinx.android.synthetic.main.activity_user_like_comment.*

class UserLikeCommentActivity : AppCompatActivity(),OnPageLoaded {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_like_comment)
        initview()
        getMyLikeData()
    }

    private fun initview(){
        userLikeActivityRefresh.setOnRefreshListener {
            getMyLikeData()
        }
    }

    private fun getMyLikeData(){
        onPreLoad()
        ThreadPool.execute {
            val data=HandleFind.GetUserLikeComment(LoginActivity.userID)
            runOnUiThread {
                onPostLoad()
                val adapter=MyLikeCommentRecyclerAdapter(this,data)
                setAdapterClick(adapter)
                Log.d("destiny", GRID_LAYOUT_DESTINY.toString())
                userLikeActivityRecyclerView.layoutManager=GridLayoutManager(this, GRID_LAYOUT_DESTINY)
                userLikeActivityRecyclerView.adapter=adapter
            }
        }
    }

    private fun setAdapterClick(adapter: MyLikeCommentRecyclerAdapter) {
        adapter.setOnItemClickListen(object :OnItemClickListener{
            override fun onItemClick(view: View, position: Int) {
                val data=adapter.getItem(position)
                val intent=Intent(this@UserLikeCommentActivity,UserCommentDetailActivity::class.java)
                intent.putExtra("data",data)
                startActivity(intent)
            }
        })
    }

    override fun onPreLoad() {
        userLikeActivityRefresh.isRefreshing=true
        userLikeActivityRecyclerView.adapter=null
    }

    override fun onPostLoad() {
        userLikeActivityRefresh.isRefreshing=false
    }
}
