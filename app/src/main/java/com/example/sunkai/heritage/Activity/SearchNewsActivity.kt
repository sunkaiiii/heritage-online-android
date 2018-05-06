package com.example.sunkai.heritage.Activity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.widget.toast
import com.example.sunkai.heritage.Activity.BaseActivity.BaseGlideActivity
import com.example.sunkai.heritage.Activity.LoginActivity.LoginActivity
import com.example.sunkai.heritage.Adapter.*
import com.example.sunkai.heritage.Adapter.BaseAdapter.BaseRecyclerAdapter
import com.example.sunkai.heritage.ConnectWebService.HandleFind
import com.example.sunkai.heritage.ConnectWebService.HandleFolk
import com.example.sunkai.heritage.ConnectWebService.HandleMainFragment
import com.example.sunkai.heritage.ConnectWebService.HandlePerson
import com.example.sunkai.heritage.Interface.OnFocusChangeListener
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.BaseOnPageChangeListener
import com.example.sunkai.heritage.tools.BaseOnTextChangeListner
import com.example.sunkai.heritage.tools.HandleAdapterClickUtils
import com.example.sunkai.heritage.tools.SoftInputTools
import com.example.sunkai.heritage.value.*
import kotlinx.android.synthetic.main.activity_search_news.*

class SearchNewsActivity : BaseGlideActivity(), TextView.OnEditorActionListener {

    var searchType = TYPE_USER
    var searchString = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_news)
        if (!intent.getStringExtra(SEARCH_TYPE).isNullOrEmpty()) {
            searchType = intent.getStringExtra(SEARCH_TYPE)
        }
        initView()
    }

    private fun initView() {
        searchActivitySearchEditText.addTextChangedListener(object : BaseOnTextChangeListner() {
            override fun afterTextChanged(p0: Editable?) {
                p0 ?: return
                searchString = p0.toString()
                handleSearchText(p0)
            }
        })
        searchActivitySearchEditText.setOnEditorActionListener(this)
        val divide = arrayOf(getString(R.string.focus_heritage),
                getString(R.string.all_news),
                getString(R.string.folk_page),
                getString(R.string.user_comment),
                getString(R.string.user))
        val searchTypes = arrayOf(TYPE_BOTTOM_NEWS, TYPE_NEWS, TYPE_FOLK_HERITAGE, TYPE_COMMENT, TYPE_USER)
        searchActivityViewPager.adapter = SearchActivityViewpagerAdapter(this, divide, searchTypes)
        searchActivityViewPager.addOnPageChangeListener(viewpagerChangeListner)
        searchActivityTablayout.setupWithViewPager(searchActivityViewPager)
        divide.withIndex().forEach { searchActivityTablayout.getTabAt(it.index)?.text=it.value }
    }

    private fun handleSearchText(editable: Editable) {
        if (searchHandler.hasMessages(SEARCH_FLAG)) {
            searchHandler.removeMessages(SEARCH_FLAG)
        }
        val msg = Message()
        val bundle = Bundle()
        bundle.putString(SEARCH_TEXT, editable.toString())
        msg.what = SEARCH_FLAG
        msg.data = bundle
        searchHandler.sendMessageDelayed(msg, DELAY)
    }

    private fun getSearchDataAndAdapter(searchInfo: String, searchType: String = this.searchType): BaseRecyclerAdapter<*, *> {
        val data: List<*>
        val adapter: BaseRecyclerAdapter<*, *>
        when (searchType) {
            TYPE_BOTTOM_NEWS -> {
                data = HandleMainFragment.SearchBottomNewsInfo(searchInfo)
                adapter = BottomFolkNewsRecyclerviewAdapter(this, data, glide)
            }
            TYPE_NEWS -> {
                data = HandleMainFragment.SearchAllNewsInfo(searchInfo)
                adapter = SeeMoreNewsRecyclerViewAdapter(this, data, glide)
            }
            TYPE_FOLK_HERITAGE -> {
                data = HandleFolk.Search_Folk_Info(searchInfo)
                adapter = FolkRecyclerViewAdapter(this, data, glide)
            }
            TYPE_COMMENT -> {
                data = HandleFind.SearchUserCommentInfo(searchInfo)
                adapter = FindFragmentRecyclerViewAdapter(this, data, ALL_COMMENT, glide)
            }
            TYPE_USER -> {
                data = HandlePerson.GetSearchUserInfo(searchInfo, LoginActivity.userID)
                adapter = SearchUserRecclerAdapter(this, data, glide)
            }
            else -> {
                data = HandlePerson.GetSearchUserInfo(searchInfo, LoginActivity.userID)
                adapter = SearchUserRecclerAdapter(this, data, glide)
            }
        }
        return adapter
    }

    private fun startSearchInfo(searchInfo: String) {
        requestHttp {
            val adapter = getSearchDataAndAdapter(searchInfo)
            HandleAdapterClickUtils.handleAdapterItemClick(this, adapter)
            if (adapter is SearchUserRecclerAdapter) {
                setListener(adapter)
            }
            runOnUiThread {
                searchActivityRecyclerView.adapter = adapter
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

    private val searchHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message?) {
            if (msg?.what == SEARCH_FLAG) {
                val searchInfo = msg.data.getString(SEARCH_TEXT)
                if (!searchInfo.isNullOrEmpty()) {
                    startSearchInfo(searchInfo)
                }
            }
        }
    }

    private val viewpagerChangeListner = object : BaseOnPageChangeListener() {
        override fun onPageSelected(position: Int) {
            val adapter = searchActivityViewPager.adapter ?: return
            if (adapter is SearchActivityViewpagerAdapter) {
                val view = adapter.views[position]
                if (!adapter.isDivideSetData[position]&&view is RecyclerView) {
                    adapter.isDivideSetData[position]=true
                    requestHttp {
                        val recyclerViewAdapter = getSearchDataAndAdapter(searchString, adapter.searchType[position])
                        runOnUiThread {
                            view.adapter = recyclerViewAdapter
                        }
                    }
                }

            }
        }
    }

    //监控在输入框按下搜索键
    override fun onEditorAction(p0: TextView?, p1: Int, p2: KeyEvent?): Boolean {
        if (p1 == KeyEvent.ACTION_DOWN || p1 == EditorInfo.IME_ACTION_DONE) {
            SoftInputTools.hideKeyboard(this)
            searchActivityRecyclerView.visibility = View.GONE
            searchActivityTablayout.visibility = View.VISIBLE
            searchActivityViewPager.visibility = View.VISIBLE
        }
        return true
    }


    companion object {
        const val SEARCH_FLAG = 1
        const val SEARCH_TEXT = "searchText"
        const val DELAY = 300L
    }
}
