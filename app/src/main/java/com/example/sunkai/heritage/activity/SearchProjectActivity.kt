package com.example.sunkai.heritage.activity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.inputmethod.EditorInfo
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.activity.base.BaseGlideActivity
import com.example.sunkai.heritage.adapter.ProjectInformationAdapter
import com.example.sunkai.heritage.connectWebService.EHeritageApi
import com.example.sunkai.heritage.connectWebService.RequestHelper
import com.example.sunkai.heritage.entity.request.SearchRequest
import com.example.sunkai.heritage.entity.response.ProjectListInformation
import com.example.sunkai.heritage.interfaces.RequestAction
import com.example.sunkai.heritage.tools.OnSrollHelper
import kotlinx.android.synthetic.main.activity_search_project.*

class SearchProjectActivity : BaseGlideActivity() {
    private var page = 1
    private val searchHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            if (msg.what == SEARCH) {
                page = 1
                searchRecyclerview.adapter = null
                requestSearch()
            }
        }
    }

    private val onScrollHelper = object : OnSrollHelper() {
        override fun loadMoreData(recyclerView: RecyclerView) {
            requestSearch()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_project)
        initViews()
    }

    private fun initViews() {
        searchButton.setOnClickListener { finish() }
        searchEditext.addTextChangedListener {
            searchHandler.removeMessages(SEARCH)
            if (it?.isEmpty() != false) {
                return@addTextChangedListener
            }
            searchHandler.sendEmptyMessageDelayed(SEARCH, SEARCH_DELAY)
        }
        searchEditext.setOnEditorActionListener { v, actionId, event ->
            searchHandler.removeMessages(SEARCH)
            if (actionId == EditorInfo.IME_ACTION_GO) {
                if (v.text.isEmpty())
                    return@setOnEditorActionListener false
                page = 1
                searchRecyclerview.adapter = null
                requestSearch()
            }
            false
        }
    }

    private fun requestSearch() {
        val request = SearchRequest()
        request.title = searchEditext.text.toString()
        request.page = page++
        requestHttp(request, EHeritageApi.SearchProject)
    }

    override fun onTaskReturned(api: RequestHelper, action: RequestAction, response: String) {
        super.onTaskReturned(api, action, response)
        when (api.getRequestApi()) {
            EHeritageApi.SearchProject -> {
                val searchResult = fromJsonToList(response, ProjectListInformation::class.java)
                var adapter = searchRecyclerview.adapter
                if (adapter == null) {
                    adapter = ProjectInformationAdapter(this, searchResult, glide)
                    searchRecyclerview.adapter = adapter
                    searchRecyclerview.addOnScrollListener(onScrollHelper)
                } else {
                    if (adapter is ProjectInformationAdapter) {
                        adapter.addNewData(searchResult)
                        onScrollHelper.setPageLoaded()
                    }
                }
            }
        }
    }

    companion object {
        const val SEARCH = 1
        const val SEARCH_DELAY = 700L
    }

}
