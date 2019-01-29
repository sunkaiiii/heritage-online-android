package com.example.sunkai.heritage.fragment


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import com.example.sunkai.heritage.activity.AddFindCommentActivity
import com.example.sunkai.heritage.activity.loginActivity.LoginActivity
import com.example.sunkai.heritage.activity.SearchActivity
import com.example.sunkai.heritage.activity.UserCommentDetailActivity
import com.example.sunkai.heritage.activity.UserCommentDetailActivity.Companion.DELETE_COMMENT
import com.example.sunkai.heritage.adapter.FindFragmentRecyclerViewAdapter
import com.example.sunkai.heritage.connectWebService.HandleFind
import com.example.sunkai.heritage.fragment.baseFragment.BaseLazyLoadFragment
import com.example.sunkai.heritage.interfaces.OnPageLoaded
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.BaiduLocation
import com.example.sunkai.heritage.tools.MakeToast.toast
import com.example.sunkai.heritage.value.*
import kotlinx.android.synthetic.main.fragment_find.*

/**
 * 发现页面的类
 */

class FindFragment : BaseLazyLoadFragment(), View.OnClickListener,AdapterView.OnItemSelectedListener, OnPageLoaded {


    private var firstSpinnerSwitch=true

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

    override fun setNeedChangeThemeColorWidget() {
        changeThemeWidge.add(R.id.fragmentFindToolbar)
        changeThemeWidge.add(R.id.fragmentFindAddCommentBtn)
        changeThemeWidge.add(R.id.fragmentFindRecyclerView)
    }

    override fun onRestoreFragmentLoadInformation(){
        initview()
        lazyLoad()
    }

    private fun initview() {
        fragmentFindSwipeRefresh.setOnRefreshListener {
            when (selectSpinner.selectedItemPosition) {
                ALL_COMMENT -> loadUserCommentData(ALL_COMMENT)
                MY_FOCUS_COMMENT -> loadUserCommentData(MY_FOCUS_COMMENT)
                SAME_LOCATION->loadUserCommentData(SAME_LOCATION)
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
        fragmentFindSearchButton.setOnClickListener(this)
        selectSpinnerImage.setOnClickListener(this)
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
            requestHttp {
                val datas = when (what) {
                    ALL_COMMENT -> HandleFind.GetUserCommentInformation(LoginActivity.userID)
                    MY_FOCUS_COMMENT -> HandleFind.GetUserCommentInformationByUser(LoginActivity.userID)
                    SAME_LOCATION->HandleFind.GetUserCommentInformationBySameLocation(LoginActivity.userID,BaiduLocation.location)
                    else -> HandleFind.GetUserCommentInformation(LoginActivity.userID)
                }
                activiy.runOnUiThread {
                    val adapter = FindFragmentRecyclerViewAdapter(activiy, datas, what,glide)
                    fragmentFindRecyclerView?.adapter = adapter
                    onPostLoad()
                }
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.fragmentFindSearchButton->{
                val intent=Intent(activity,SearchActivity::class.java)
                intent.putExtra(SEARCH_TYPE, TYPE_COMMENT)
                startActivity(intent)
            }
            R.id.selectSpinnerImage->{
               selectSpinner.performClick()
            }
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
        //在绑定Spiner的itemLitener的时候，第一次帮顶会触发一次Listener，所以在这里做一次检验，过滤掉第一次切换响应
        if(firstSpinnerSwitch){
            firstSpinnerSwitch=false
            return
        }
        when (position) {
            ALL_COMMENT -> {
                loadUserCommentData(ALL_COMMENT)
            }
            MY_FOCUS_COMMENT -> {
                checkUserIsLogin()
                loadUserCommentData(MY_FOCUS_COMMENT)
            }
            SAME_LOCATION->{
                loadUserCommentData(SAME_LOCATION)
            }
        }
    }


    override fun onNothingSelected(parent: AdapterView<*>) {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        firstSpinnerSwitch=true
    }

    companion object {
        const val LOGIN = 1
        const val FROM_USER_COMMENT_DETAIL = 0
        const val FROM_ADD_COMMENT_DETAIL=2
    }
}
