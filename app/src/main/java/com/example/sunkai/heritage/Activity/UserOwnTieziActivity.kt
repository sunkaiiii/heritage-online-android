package com.example.sunkai.heritage.Activity

import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import com.example.sunkai.heritage.Activity.LoginActivity.LoginActivity
import com.example.sunkai.heritage.Adapter.MyOwnCommentRecyclerViewAdapter
import com.example.sunkai.heritage.ConnectWebService.HandleFind
import com.example.sunkai.heritage.Data.UserCommentData
import com.example.sunkai.heritage.Interface.OnItemClickListener
import com.example.sunkai.heritage.Interface.OnItemLongClickListener
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.BaseAsyncTask
import com.example.sunkai.heritage.tools.MakeToast
import java.io.ByteArrayOutputStream

class UserOwnTieziActivity : AppCompatActivity() {
    private lateinit var myOwnList: RecyclerView
    private lateinit var adapter: MyOwnCommentRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_own_tiezi)
        initView()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        GetInformationAsyncTask(this).execute()
    }

    private fun initView() {
        myOwnList = findViewById(R.id.user_own_list)
    }

    private fun setAdpterClick(adapter: MyOwnCommentRecyclerViewAdapter) {

        adapter.setOnItemClickListen(object :OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                val intent = Intent(this@UserOwnTieziActivity, UserCommentDetailActivity::class.java)
                val bundle = Bundle()
                bundle.putSerializable("data", adapter.getItem(position))
                bundle.putInt("position", position)
                val imageView = view.findViewById<View>(R.id.mycomment_item_image) as ImageView
                imageView.isDrawingCacheEnabled = true
                val drawable = imageView.drawable
                val bitmapDrawable = drawable as BitmapDrawable
                val bitmap = bitmapDrawable.bitmap
                val out = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                intent.putExtra("bitmap", out.toByteArray())
                intent.putExtras(bundle)
                if(Build.VERSION.SDK_INT>=21) {
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this@UserOwnTieziActivity, imageView, "shareView").toBundle())
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

    private class GetInformationAsyncTask(activity:UserOwnTieziActivity):BaseAsyncTask<Void,Void,List<UserCommentData>,UserOwnTieziActivity>(activity){
        override fun doInBackground(vararg params: Void?): List<UserCommentData>? {
            return HandleFind.GetUserCommentInformaitonByOwn(LoginActivity.userID)
        }

        override fun onPostExecute(datas: List<UserCommentData>) {
                val activity=weakRefrece.get()
                activity?.let{
                    activity.adapter = MyOwnCommentRecyclerViewAdapter(activity,datas)
                    activity.setAdpterClick(activity.adapter)
                    activity.setAdpterLongClick(activity.adapter)
                    val layoutManager = GridLayoutManager(activity,2)
                    activity.myOwnList.layoutManager = layoutManager
                    activity.myOwnList.adapter = activity.adapter
                }

        }
    }

    fun refreshList(){
        GetInformationAsyncTask(this).execute()
    }
}