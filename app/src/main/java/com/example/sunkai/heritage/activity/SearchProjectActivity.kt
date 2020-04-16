package com.example.sunkai.heritage.activity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.activity.base.BaseGlideActivity
import com.example.sunkai.heritage.adapter.ProjectInformationAdapter
import com.example.sunkai.heritage.adapter.ProjectSearchHistoryAdapter
import com.example.sunkai.heritage.connectWebService.EHeritageApi
import com.example.sunkai.heritage.connectWebService.RequestHelper
import com.example.sunkai.heritage.database.entities.SearchHistory
import com.example.sunkai.heritage.entity.request.SearchRequest
import com.example.sunkai.heritage.entity.response.ProjectListInformation
import com.example.sunkai.heritage.interfaces.RequestAction
import com.example.sunkai.heritage.tools.GlobalContext
import com.example.sunkai.heritage.tools.OnSrollHelper
import kotlinx.android.synthetic.main.activity_search_project.*

class SearchProjectActivity : BaseGlideActivity() {
    private var page = 1
    private var historyData: MutableList<SearchHistory>? = null
    private val searchHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            if (msg.what == SEARCH) {
                saveSearchHistory()
            }
            if (msg.what == HISTORY) {
                this.removeMessages(SEARCH)
            }
            searchRecyclerview.adapter = null
            page = 1
            requestSearch()
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
            if (it?.isEmpty() == true) {
                searchClearText.visibility = View.GONE
                setHistoryDataToRecyclerView()
                return@addTextChangedListener
            }
            searchClearText.visibility = View.VISIBLE
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
        searchClearText.setOnClickListener {
            searchEditext.setText("")
        }

        runOnBackGround {
            historyData = GlobalContext.newsDetailDatabase.searchHistoryDao().getAllSearchHistory().toMutableList()
            runOnUiThread {
                setHistoryDataToRecyclerView()
            }
        }
    }

    private fun setHistoryDataToRecyclerView() {
        val historyData = historyData ?: return
        val adapter = ProjectSearchHistoryAdapter(this, historyData, glide)
        adapter.setOnItemClickListener { view, position ->
            searchEditext.setText(adapter.getItem(position).title ?: "")
            searchHandler.sendEmptyMessage(HISTORY)
        }
        searchRecyclerview.adapter = adapter
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


    private fun saveSearchHistory() {
        val searchHistory = SearchHistory()
        searchHistory.title = searchEditext.text.toString()
        runOnBackGround {
            GlobalContext.newsDetailDatabase.searchHistoryDao().insert(searchHistory)
        }
        historyData?.add(0, searchHistory)
    }

    companion object {
        const val SEARCH = 1
        const val HISTORY = 2
        const val SEARCH_DELAY = 700L
    }

}
