package com.example.sunkai.heritage.Activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.util.Pair
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.example.sunkai.heritage.Activity.LoginActivity.LoginActivity
import com.example.sunkai.heritage.Adapter.MyOwnCommentRecyclerViewAdapter
import com.example.sunkai.heritage.ConnectWebService.HandleFind
import com.example.sunkai.heritage.Interface.OnItemClickListener
import com.example.sunkai.heritage.Interface.OnItemLongClickListener
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.MakeToast
import com.example.sunkai.heritage.tools.ThreadPool
import com.example.sunkai.heritage.tools.TransitionHelper
import com.example.sunkai.heritage.value.GRID_LAYOUT_DESTINY
import kotlinx.android.synthetic.main.activity_user_own_tiezi.*

class UserOwnTieziActivity : AppCompatActivity() {
    private lateinit var adapter: MyOwnCommentRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_own_tiezi)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        getInformation()
    }

    private fun getInformation(){
        ThreadPool.execute {
            val datas=HandleFind.GetUserCommentInformaitonByOwn(LoginActivity.userID)
            runOnUiThread {
                adapter = MyOwnCommentRecyclerViewAdapter(this,datas)
                setAdpterClick(adapter)
                setAdpterLongClick(adapter)
                userOwnList.layoutManager = GridLayoutManager(this, GRID_LAYOUT_DESTINY)
                userOwnList.adapter = adapter
            }
        }
    }

    private fun setAdpterClick(adapter: MyOwnCommentRecyclerViewAdapter) {

        adapter.setOnItemClickListen(object :OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                val intent = Intent(this@UserOwnTieziActivity, UserCommentDetailActivity::class.java)
                intent.putExtra("data",adapter.getItem(position))
                if(Build.VERSION.SDK_INT>=21) {
                    val imageView = view.findViewById<ImageView>(R.id.mycomment_item_image)
                    val title=view.findViewById<TextView>(R.id.mycomment_item_title)
                    val content=view.findViewById<TextView>(R.id.mycomment_item_content)
                    val pairs=TransitionHelper.createSafeTransitionParticipants(this@UserOwnTieziActivity,false,
                            Pair(imageView,getString(R.string.find_share_view)),
                            Pair(title,getString(R.string.find_share_title)),
                            Pair(content,getString(R.string.find_share_content)))
                    startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this@UserOwnTieziActivity, *pairs).toBundle())
                }else{
                    startActivity(intent)
                }
            }
        })
    }

    private fun setAdpterLongClick(adapter: MyOwnCommentRecyclerViewAdapter) {
        this.adapter.setOnItemLongClickListener(object :OnItemLongClickListener{
            override fun onItemlongClick(view: View, position: Int) {
                AlertDialog.Builder(this@UserOwnTieziActivity).setTitle("是否删除帖子")
                        .setPositiveButton("删除", { _, _ ->
                            val ad = AlertDialog.Builder(this@UserOwnTieziActivity)
                                    .setView(LayoutInflater.from(this@UserOwnTieziActivity).inflate(R.layout.progress_view, null))
                                    .create()
                            ad.show()
                            Thread {
                                val userCommentData = adapter.getItem(position)
                                val result = HandleFind.DeleteUserCommentByID(userCommentData.id)
                                runOnUiThread {
                                    if (ad.isShowing) {
                                        ad.dismiss()
                                        if (result) {
                                            MakeToast.MakeText("删除成功")
                                        } else {
                                            MakeToast.MakeText("出现问题，请稍后再试")
                                        }
                                    }
                                    refreshList()
                                }
                            }.start()
                        }).setNegativeButton("取消", { _, _ -> })
                        .create().show()
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }


    fun refreshList(){
        getInformation()
    }
}