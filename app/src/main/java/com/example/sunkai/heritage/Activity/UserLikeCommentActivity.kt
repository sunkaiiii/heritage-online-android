package com.example.sunkai.heritage.Activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.View
import com.example.sunkai.heritage.Activity.LoginActivity.LoginActivity
import com.example.sunkai.heritage.Adapter.MyLikeCommentRecyclerAdapter
import com.example.sunkai.heritage.ConnectWebService.HandleFind
import com.example.sunkai.heritage.Interface.OnItemClickListener
import com.example.sunkai.heritage.Interface.OnPageLoaded
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.ThreadPool
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
            onPreLoad()
            getMyLikeData()
        }
    }

    private fun getMyLikeData(){
        ThreadPool.execute {
            val data=HandleFind.GetUserLikeComment(LoginActivity.userID)
            runOnUiThread {
                onPostLoad()
                val adapter=MyLikeCommentRecyclerAdapter(this,data)
                setAdapterClick(adapter)
                userLikeActivityRecyclerView.layoutManager=GridLayoutManager(this,2)
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
        userLikeActivityRecyclerView.adapter=null
        userLikeActivityRefresh.isRefreshing=true
    }

    override fun onPostLoad() {
        userLikeActivityRefresh.isRefreshing=false
    }
}
