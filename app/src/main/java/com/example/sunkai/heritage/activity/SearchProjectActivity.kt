package com.example.sunkai.heritage.activity

import android.os.*
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
import com.example.sunkai.heritage.dialog.SearchProjectDialog
import com.example.sunkai.heritage.entity.request.BaseQueryRequest
import com.example.sunkai.heritage.entity.request.SearchRequest
import com.example.sunkai.heritage.entity.response.ProjectListInformation
import com.example.sunkai.heritage.entity.response.SearchCategoryResponse
import com.example.sunkai.heritage.interfaces.RequestAction
import com.example.sunkai.heritage.tools.GlobalContext
import com.example.sunkai.heritage.tools.OnSrollHelper
import kotlinx.android.synthetic.main.activity_search_project.*

class SearchProjectActivity : BaseGlideActivity() {
    private var page = 1
    private var historyData: MutableList<SearchHistory>? = null
    private var searchRequest: SearchRequest? = null
    private var searchCategory:SearchCategoryResponse?=null
    private val searchHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            if (msg.what == SEARCH) {
                saveSearchHistory()
            }
            if (msg.what == HISTORY) {
                this.removeMessages(SEARCH)
            }
            refreshSearchRequest()
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
                refreshSearchRequest()
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

        activitySearchAdvanceButton.setOnClickListener {
            if(searchCategory==null){
                loadingBackground.visibility = View.VISIBLE
                requestHttp(BaseQueryRequest(), EHeritageApi.GetSearchCategory)
            }else{
                showSearchDialog()
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

    private fun refreshSearchRequest() {
        searchRequest = SearchRequest()
        searchRequest?.title = searchEditext.text.toString()
        searchRecyclerview.adapter = null
        page = 1
    }

    private fun requestSearch() {
        if (searchRecyclerview.adapter == null) {
            loadingBackground.visibility = View.VISIBLE
        }
        val request = searchRequest ?: return
        searchRequest?.page = request.page++
        requestHttp(request, EHeritageApi.SearchProject)
    }

    override fun onTaskReturned(api: RequestHelper, action: RequestAction, response: String) {
        super.onTaskReturned(api, action, response)
        when (api.getRequestApi()) {
            EHeritageApi.SearchProject -> {
                val searchResult = fromJsonToList(response, ProjectListInformation::class.java)
                var adapter = searchRecyclerview.adapter
                if (adapter == null) {
                    loadingBackground.visibility = View.GONE
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
            EHeritageApi.GetSearchCategory -> {
                loadingBackground.visibility = View.GONE
                val searchCategoryResponse = fromJsonToObject(response, SearchCategoryResponse::class.java)
                searchCategory=searchCategoryResponse
                showSearchDialog()
            }
        }
    }

    private fun showSearchDialog() {
        val seachCategory = searchCategory ?: return
        val dialog = SearchProjectDialog(seachCategory)
        dialog.setOnSearchButtonClickListener(object : SearchProjectDialog.OnSearchButtonClickListener {
            override fun onButtonClick(searchReqeust: SearchRequest) {
                refreshSearchRequest()
                this@SearchProjectActivity.searchRequest = searchReqeust
                requestSearch()
            }

        })
        dialog.show(supportFragmentManager, null)
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
