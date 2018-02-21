package com.example.sunkai.heritage.Fragment

import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.sunkai.heritage.Activity.AddFindCommentActivity
import com.example.sunkai.heritage.Activity.LoginActivity
import com.example.sunkai.heritage.Activity.SearchActivity
import com.example.sunkai.heritage.Activity.UserCommentDetailActivity
import com.example.sunkai.heritage.Adapter.FindFragmentRecyclerViewAdapter
import com.example.sunkai.heritage.ConnectWebService.HandleFindNew
import com.example.sunkai.heritage.Interface.OnItemClickListener
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.MakeToast.toast
import kotlinx.android.synthetic.main.fragment_find.*
import java.io.ByteArrayOutputStream

/**
 * 发现页面的类
 */

class FindFragment : BaseLazyLoadFragment(), View.OnClickListener {

    private lateinit var findSearchBtn: ImageView
    private lateinit var findEdit: TextView
    internal lateinit var view: View
    private lateinit var recyclerViewAdpter: FindFragmentRecyclerViewAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var selectSpiner: Spinner



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        view = inflater.inflate(R.layout.fragment_find, container, false)
        initview(view)
        return view
    }

    private fun initview(view:View){
        findEdit = view.findViewById(R.id.find_text)
        findSearchBtn = view.findViewById(R.id.find_searchbtn)
        selectSpiner = view.findViewById(R.id.find_select_spinner)
        recyclerView = view.findViewById(R.id.fragment_find_recyclerView)
        findEdit.setOnClickListener(this)
        findSearchBtn.setOnClickListener(this)
    }

    private fun loadInformation(){

        //程序默认显示广场的全部帖子
        loadUserCommentData(1)

        //Spinear切换，重新加载adpater的数据
        selectSpiner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                when (position) {
                    0 -> {
                        loadUserCommentData(1)
                    }
                    1 -> {
                        checkUserIsLogin()
                        loadUserCommentData(2)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
        //发帖
        fragmentFindAddCommentBtn.setOnClickListener {
            if (LoginActivity.userID == 0) {
                Toast.makeText(activity, "没有登录", Toast.LENGTH_SHORT).show()
                val intent = Intent(activity, LoginActivity::class.java)
                intent.putExtra("isInto", 1)
                startActivityForResult(intent, 1)
            }else {
                val intent = Intent(activity, AddFindCommentActivity::class.java)
                startActivityForResult(intent, 1)
            }
        }
    }
    override fun startLoadInformation() {
        loadInformation()
    }

    private fun checkUserIsLogin(){
        if (LoginActivity.userID == 0) {
            toast("没有登录")
            val intent = Intent(activity, LoginActivity::class.java)
            intent.putExtra("isInto", 1)
            startActivityForResult(intent, 1)
            selectSpiner.setSelection(0)
            return
        }
    }

    private fun loadUserCommentData(what:Int){
        val activiy=activity
        activiy?.let{
            Thread{
                val datas=when(what){
                    1->HandleFindNew.GetUserCommentInformation(LoginActivity.userID)
                    2->HandleFindNew.GetUserCommentInformationByUser(LoginActivity.userID)
                    else->HandleFindNew.GetUserCommentInformation(LoginActivity.userID)
                }
                for(data in datas){
                    Log.d("findData",data.toString())
                }
                activiy.runOnUiThread {
                    val adapter=FindFragmentRecyclerViewAdapter(activiy,datas,what)
                    setAdpterClick(adapter)
                    recyclerView.adapter=adapter
                }
            }.start()
        }
    }

    private fun setAdpterClick(adpter: FindFragmentRecyclerViewAdapter?) {
        adpter!!.setOnItemClickListen(object :OnItemClickListener{
            override fun onItemClick(view: View, position: Int) {
                val intent = Intent(activity, UserCommentDetailActivity::class.java)
                val bundle = Bundle()
                bundle.putSerializable("data", adpter.getItem(position))
                bundle.putInt("position", position)
                val getview:View? = view.findViewById(R.id.fragment_find_litview_img)
                getview?.let {
                    val imageView=getview as ImageView
                    imageView.isDrawingCacheEnabled = true
                    val drawable = imageView.drawable
                    val bitmapDrawable = drawable as BitmapDrawable
                    val bitmap = bitmapDrawable.bitmap
                    val out = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                    intent.putExtra("bitmap", out.toByteArray())
                    intent.putExtras(bundle)

                    //如果手机是Android 5.0以上的话，使用新的Activity切换动画
                    if(Build.VERSION.SDK_INT>=21)
                        startActivityForResult(intent, FROM_USER_COMMENT_DETAIL,ActivityOptions.makeSceneTransitionAnimation(activity,imageView,"shareView").toBundle())
                    else
                        startActivityForResult(intent, FROM_USER_COMMENT_DETAIL)
                }
            }
        })
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.find_text, R.id.find_searchbtn -> {
                if (LoginActivity.userID == 0) {
                    Toast.makeText(activity, "没有登录", Toast.LENGTH_SHORT).show()
                    val intent = Intent(activity, LoginActivity::class.java)
                    intent.putExtra("isInto", 1)
                    startActivityForResult(intent, 1)
                }
                val intent = Intent(activity, SearchActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            1 -> {
//                recyclerViewAdpter.reFreshList()
            }
            FROM_USER_COMMENT_DETAIL -> if (resultCode == UserCommentDetailActivity.ADD_COMMENT) {
                val bundle = data!!.extras
                val commentID = bundle?.getInt("commentID") ?: BUNDLE_ERROR
                val position = bundle?.getInt("position") ?: BUNDLE_ERROR
                if (commentID != BUNDLE_ERROR && position != BUNDLE_ERROR) {
                    recyclerViewAdpter.getReplyCount(commentID, position)
                }
            } else if (resultCode == UserCommentDetailActivity.DELETE_COMMENT) {
//                recyclerViewAdpter.reFreshList()
            }
        }
    }

    companion object {
        private const val FROM_USER_COMMENT_DETAIL = 2

        private const val BUNDLE_ERROR = -4
    }
}
