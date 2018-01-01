package com.example.sunkai.heritage.Activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import com.example.sunkai.heritage.Adapter.FindFragmentRecyclerViewAdapter
import com.example.sunkai.heritage.ConnectWebService.HandleFind
import com.example.sunkai.heritage.Interface.OnItemClickListener
import com.example.sunkai.heritage.Interface.OnItemLongClickListener
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.MakeToast
import java.io.ByteArrayOutputStream

class UserOwnTieziActivity : AppCompatActivity() {
    private lateinit var myOwnList: RecyclerView
    private lateinit var adapter: FindFragmentRecyclerViewAdapter
    private var actionBack: ActionBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_own_tiezi)
        initView()
        adapter = FindFragmentRecyclerViewAdapter(this, 3)
        setAdpterClick(adapter)
        setAdpterLongClick(adapter)
        val layoutManager = LinearLayoutManager(this);
        myOwnList.layoutManager = layoutManager
        myOwnList.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        myOwnList.setHasFixedSize(true)
        myOwnList.adapter = adapter
        actionBack = supportActionBar
        actionBack?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initView() {
        myOwnList = findViewById(R.id.user_own_list)
    }

    private fun setAdpterClick(adapter: FindFragmentRecyclerViewAdapter) {

        adapter.setOnItemClickListen(object :OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                val intent = Intent(this@UserOwnTieziActivity, UserCommentDetailActivity::class.java)
                val bundle = Bundle()
                bundle.putSerializable("data", adapter.getItem(position))
                bundle.putInt("position", position)
                val imageView = view.findViewById<View>(R.id.fragment_find_litview_img) as ImageView
                imageView.isDrawingCacheEnabled = true
                val drawable = imageView.drawable
                val bitmapDrawable = drawable as BitmapDrawable
                val bitmap = bitmapDrawable.bitmap
                val out = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                intent.putExtra("bitmap", out.toByteArray())
                intent.putExtras(bundle)
                startActivity(intent)
            }
        })
    }

    private fun setAdpterLongClick(adapter: FindFragmentRecyclerViewAdapter) {
        this.adapter.setOnItemLongClickListener(object :OnItemLongClickListener{
            override fun onItemlongClick(view: View, position: Int) {
                AlertDialog.Builder(this@UserOwnTieziActivity).setTitle("是否删除帖子")
                        .setPositiveButton("删除", { dialog, which ->
                            val ad = AlertDialog.Builder(this@UserOwnTieziActivity)
                                    .setView(LayoutInflater.from(this@UserOwnTieziActivity).inflate(R.layout.progress_view, null))
                                    .create();
                            ad.show()
                            Thread {
                                val userCommentData = adapter.getItem(position)
                                val result = HandleFind.Delete_User_Comment_By_ID(userCommentData.id)
                                runOnUiThread {
                                    if (ad.isShowing) {
                                        ad.dismiss()
                                        if (result) {
                                            MakeToast.MakeText("删除成功")
                                        } else {
                                            MakeToast.MakeText("出现问题，请稍后再试")
                                        }
                                    }
                                    this@UserOwnTieziActivity.adapter.reFreshList()
                                }
                            }.start()
                        }).setNegativeButton("取消", { dialog, which -> })
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
}