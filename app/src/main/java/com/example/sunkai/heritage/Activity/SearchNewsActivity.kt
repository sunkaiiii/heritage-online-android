package com.example.sunkai.heritage.Activity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.Editable
import com.example.sunkai.heritage.Activity.BaseActivity.BaseGlideActivity
import com.example.sunkai.heritage.Activity.LoginActivity.LoginActivity
import com.example.sunkai.heritage.Adapter.BaseAdapter.BaseRecyclerAdapter
import com.example.sunkai.heritage.Adapter.BottomFolkNewsRecyclerviewAdapter
import com.example.sunkai.heritage.Adapter.FindFragmentRecyclerViewAdapter
import com.example.sunkai.heritage.Adapter.SearchUserRecclerAdapter
import com.example.sunkai.heritage.Adapter.SeeMoreNewsRecyclerViewAdapter
import com.example.sunkai.heritage.ConnectWebService.HandleFind
import com.example.sunkai.heritage.ConnectWebService.HandleMainFragment
import com.example.sunkai.heritage.ConnectWebService.HandlePerson
import com.example.sunkai.heritage.Interface.OnFocusChangeListener
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.BaseOnTextChangeListner
import com.example.sunkai.heritage.value.*
import kotlinx.android.synthetic.main.activity_search_news.*

class SearchNewsActivity : BaseGlideActivity() {

    var searchType= TYPE_USER
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_news)
        if(!intent.getStringExtra(SEARCH_TYPE).isNullOrEmpty()) {
            searchType = intent.getStringExtra(SEARCH_TYPE)
        }
        initView()
    }

    private fun initView() {
        searchActivitySearchEditText.addTextChangedListener(object :BaseOnTextChangeListner(){
            override fun afterTextChanged(p0: Editable?) {
                p0?:return
                handleSearchText(p0)
            }
        })
    }

    private fun handleSearchText(editable: Editable){
        if(searchHandler.hasMessages(SEARCH_FLAG)){
            searchHandler.removeMessages(SEARCH_FLAG)
        }
        val msg=Message()
        val bundle=Bundle()
        bundle.putString(SEARCH_TEXT,editable.toString())
        msg.what= SEARCH_FLAG
        msg.data=bundle
        searchHandler.sendMessageDelayed(msg, DELAY)
    }
    private fun startSearchInfo(searchInfo:String){
        requestHttp {
            val data:List<*>
            val adapter:BaseRecyclerAdapter<*,*>
            when(searchType){
                TYPE_BOTTOM_NEWS->{
                    data=HandleMainFragment.SearchBottomNewsInfo(searchInfo)
                    adapter=BottomFolkNewsRecyclerviewAdapter(this,data,glide)
                }
                TYPE_NEWS->{
                    data=HandleMainFragment.SearchAllNewsInfo(searchInfo)
                    adapter=SeeMoreNewsRecyclerViewAdapter(this,data,glide)
                }
                TYPE_COMMENT->{
                    data=HandleFind.SearchUserCommentInfo(searchInfo)
                    adapter=FindFragmentRecyclerViewAdapter(this,data, ALL_COMMENT,glide)
                }
                TYPE_USER->{
                    data=HandlePerson.GetSearchUserInfo(searchInfo,LoginActivity.userID)
                    adapter=SearchUserRecclerAdapter(this,data,glide)
                }
                else-> {
                    data= HandlePerson.GetSearchUserInfo(searchInfo,LoginActivity.userID)
                    adapter=SearchUserRecclerAdapter(this,data,glide)
                }
            }

            if(adapter is SearchUserRecclerAdapter){
                setListener(adapter)
            }
            runOnUiThread {
                searchActivityRecyclerView.adapter=adapter
            }
        }
    }

    private fun setListener(adapter: SearchUserRecclerAdapter) {
        adapter.setOnFocusChangeListener(object : OnFocusChangeListener {
            override fun onFocusChange() {
                setResult(STATE_CHANGE)
            }
        })
    }

    private val searchHandler=object : Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message?) {
            if(msg?.what== SEARCH_FLAG){
                val searchInfo=msg.data.getString(SEARCH_TEXT)
                if(!searchInfo.isNullOrEmpty()){
                    startSearchInfo(searchInfo)
                }
            }
        }
    }

    companion object {
        const val SEARCH_FLAG=1
        const val SEARCH_TEXT="searchText"
        const val DELAY=400L
    }
}
