package com.example.sunkai.heritage.Fragment

import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import com.example.sunkai.heritage.Activity.AddFindCommentActivity
import com.example.sunkai.heritage.Activity.LoginActivity.LoginActivity
import com.example.sunkai.heritage.Activity.UserCommentDetailActivity
import com.example.sunkai.heritage.Activity.UserCommentDetailActivity.Companion.DELETE_COMMENT
import com.example.sunkai.heritage.Adapter.FindFragmentRecyclerViewAdapter
import com.example.sunkai.heritage.ConnectWebService.HandleFind
import com.example.sunkai.heritage.Fragment.BaseFragment.BaseLazyLoadFragment
import com.example.sunkai.heritage.Interface.OnItemClickListener
import com.example.sunkai.heritage.Interface.OnPageLoaded
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.MakeToast.toast
import com.example.sunkai.heritage.tools.ThreadPool
import com.example.sunkai.heritage.value.ALL_COMMENT
import com.example.sunkai.heritage.value.MY_FOCUS_COMMENT
import kotlinx.android.synthetic.main.fragment_find.*

/**
 * 发现页面的类
 */

class FindFragment : BaseLazyLoadFragment(), View.OnClickListener,AdapterView.OnItemSelectedListener, OnPageLoaded {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_find, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(savedInstanceState==null) {
            initview()
        }
    }

    override fun onRestoreFragmentLoadInformation(){
        initview()
        lazyLoad()
    }

    private fun initview() {
        fragmentFindSwipeRefresh.setOnRefreshListener {
            when (selectSpinner.selectedItemPosition) {
                0 -> loadUserCommentData(ALL_COMMENT)
                1 -> loadUserCommentData(MY_FOCUS_COMMENT)
            }
        }

        //Spinear切换，重新加载adpater的数据
        selectSpinner.onItemSelectedListener =this
        //发帖
        fragmentFindAddCommentBtn.setOnClickListener {
            if (LoginActivity.userID == 0) {
                Toast.makeText(activity, "没有登录", Toast.LENGTH_SHORT).show()
                val intent = Intent(activity, LoginActivity::class.java)
                intent.putExtra("isInto", 1)
                startActivityForResult(intent, 1)
            } else {
                val intent = Intent(activity, AddFindCommentActivity::class.java)
                startActivityForResult(intent, FROM_ADD_COMMENT_DETAIL)
            }
        }
    }


    override fun startLoadInformation() {
        loadUserCommentData(ALL_COMMENT)
    }

    private fun checkUserIsLogin() {
        if (LoginActivity.userID == 0) {
            toast("没有登录")
            val intent = Intent(activity, LoginActivity::class.java)
            intent.putExtra("isInto", 1)
            startActivityForResult(intent, 1)
            selectSpinner.setSelection(0)
            return
        }
    }

    private fun loadUserCommentData(what: Int) {
        val activiy = activity
        activiy?.let {
            onPreLoad()
            ThreadPool.execute {
                val datas = when (what) {
                    ALL_COMMENT -> HandleFind.GetUserCommentInformation(LoginActivity.userID)
                    MY_FOCUS_COMMENT -> HandleFind.GetUserCommentInformationByUser(LoginActivity.userID)
                    else -> HandleFind.GetUserCommentInformation(LoginActivity.userID)
                }
                activiy.runOnUiThread {
                    val adapter = FindFragmentRecyclerViewAdapter(activiy, datas, what)
                    setAdpterClick(adapter)
                    fragmentFindRecyclerView?.adapter = adapter
                    onPostLoad()
                }
            }
        }
    }

    private fun setAdpterClick(adpter: FindFragmentRecyclerViewAdapter) {
        adpter.setOnItemClickListen(object : OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                val intent = Intent(activity, UserCommentDetailActivity::class.java)
                intent.putExtra("data", adpter.getItem(position))
                //如果手机是Android 5.0以上的话，使用新的Activity切换动画
                if (Build.VERSION.SDK_INT >= 21) {
                    val getview: View = view.findViewById(R.id.fragment_find_litview_img) ?: return
                    intent.putExtra("option", UserCommentDetailActivity.ANIMATION_SHOW)
                    startActivityForResult(intent, FROM_USER_COMMENT_DETAIL, ActivityOptions.makeSceneTransitionAnimation(activity, getview, getString(R.string.find_share_view)).toBundle())
                } else {
                    startActivityForResult(intent, FROM_USER_COMMENT_DETAIL)
                }
            }
        })
    }

    override fun onClick(v: View) {
        when (v.id) {
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            FROM_USER_COMMENT_DETAIL -> if (resultCode == UserCommentDetailActivity.ADD_COMMENT || resultCode == DELETE_COMMENT) {
                loadUserCommentData(selectSpinner.selectedItemPosition)
            }
            LOGIN -> loadUserCommentData(selectSpinner.selectedItemPosition)
            FROM_ADD_COMMENT_DETAIL->if(resultCode==AddFindCommentActivity.ADD_OK) loadUserCommentData(selectSpinner.selectedItemPosition)
        }
    }

    override fun onPreLoad() {
        fragmentFindSwipeRefresh.isRefreshing = true
    }

    override fun onPostLoad() {
        fragmentFindSwipeRefresh?.isRefreshing = false
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (position) {
            0 -> {
                loadUserCommentData(ALL_COMMENT)
            }
            1 -> {
                checkUserIsLogin()
                loadUserCommentData(MY_FOCUS_COMMENT)
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>) {

    }

    companion object {
        private const val LOGIN = 1
        private const val FROM_USER_COMMENT_DETAIL = 0
        private const val FROM_ADD_COMMENT_DETAIL=2
    }
}
