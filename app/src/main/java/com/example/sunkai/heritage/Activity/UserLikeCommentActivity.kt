package com.example.sunkai.heritage.Activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.util.Pair
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.example.sunkai.heritage.Activity.BaseActivity.BaseStopGlideActivity
import com.example.sunkai.heritage.Activity.LoginActivity.LoginActivity
import com.example.sunkai.heritage.Adapter.MyLikeCommentRecyclerAdapter
import com.example.sunkai.heritage.ConnectWebService.HandleFind
import com.example.sunkai.heritage.Interface.OnItemClickListener
import com.example.sunkai.heritage.Interface.OnPageLoaded
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.ThreadPool
import com.example.sunkai.heritage.tools.TransitionHelper
import com.example.sunkai.heritage.value.DATA
import com.example.sunkai.heritage.value.GRID_LAYOUT_DESTINY
import kotlinx.android.synthetic.main.activity_user_like_comment.*

/**
 * 我的赞的Activity
 */

class UserLikeCommentActivity : BaseStopGlideActivity(), OnPageLoaded {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_like_comment)
        initview()
        getMyLikeData()
    }

    private fun initview() {
        userLikeActivityRefresh.setOnRefreshListener {
            getMyLikeData()
        }
    }

    private fun getMyLikeData() {
        onPreLoad()
        ThreadPool.execute {
            val data = HandleFind.GetUserLikeComment(LoginActivity.userID)
            if(isDestroy)return@execute
            runOnUiThread {
                onPostLoad()
                val adapter = MyLikeCommentRecyclerAdapter(this, data,glide)
                setAdapterClick(adapter)
                Log.d("destiny", GRID_LAYOUT_DESTINY.toString())
                userLikeActivityRecyclerView.layoutManager = GridLayoutManager(this, GRID_LAYOUT_DESTINY)
                userLikeActivityRecyclerView.adapter = adapter
            }
        }
    }

    private fun setAdapterClick(adapter: MyLikeCommentRecyclerAdapter) {
        adapter.setOnItemClickListen(object : OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                val data = adapter.getItem(position)
                val imageview = view.findViewById<ImageView>(R.id.coment_image)
                val infoBackGround = view.findViewById<LinearLayout>(R.id.info_background)
                val intent = Intent(this@UserLikeCommentActivity, UserCommentDetailActivity::class.java)
                intent.putExtra(DATA, data)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    val pairs = TransitionHelper.createSafeTransitionParticipants(this@UserLikeCommentActivity, false,
                            Pair(imageview, getString(R.string.find_share_view)),
                            Pair(infoBackGround, getString(R.string.find_share_background)))
                    startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this@UserLikeCommentActivity,*pairs).toBundle())
                } else {
                    startActivity(intent)
                }
            }
        })
    }

    override fun onPreLoad() {
        userLikeActivityRefresh.isRefreshing = true
        userLikeActivityRecyclerView.adapter = null
    }

    override fun onPostLoad() {
        userLikeActivityRefresh.isRefreshing = false
    }
}
