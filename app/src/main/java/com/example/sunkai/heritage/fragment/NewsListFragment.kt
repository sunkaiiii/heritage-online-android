package com.example.sunkai.heritage.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.adapter.NewsListAdapter
import com.example.sunkai.heritage.connectWebService.EHeritageApiRetrofitServiceCreator
import com.example.sunkai.heritage.connectWebService.RequestHelper
import com.example.sunkai.heritage.connectWebService.await
import com.example.sunkai.heritage.database.entities.NewsList
import com.example.sunkai.heritage.entity.request.BasePathRequest
import com.example.sunkai.heritage.entity.response.NewsListResponse
import com.example.sunkai.heritage.fragment.baseFragment.BaseGlideFragment
import com.example.sunkai.heritage.interfaces.NetworkRequest
import com.example.sunkai.heritage.interfaces.OnPageLoaded
import com.example.sunkai.heritage.interfaces.RequestAction
import com.example.sunkai.heritage.tools.GlobalContext
import com.example.sunkai.heritage.tools.OnSrollHelper
import com.example.sunkai.heritage.tools.runOnUiThread
import kotlinx.android.synthetic.main.news_list_framgent.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class NewsListFragment : BaseGlideFragment(), OnPageLoaded {
    var reqeustArgument: MainFragment.NewsPages? = null
    var pageNumber = 1
    private var databaseList: List<NewsListResponse>? = null

    private fun createRequestBean(): NetworkRequest {
        return object : BasePathRequest() {
            override fun getPathParamerater(): List<String> {
                return listOf(pageNumber++.toString())
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.news_list_framgent, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initview()
    }


    private fun initview() {
        fragmentMainRecyclerview.addOnScrollListener(onScroller)
        bottomNewsRefreshLayout.setOnRefreshListener {
            pageNumber = 1
            loadInformation()
        }
        loadInformation()
    }


    private fun loadInformation() {
        onPreLoad()
        reqeustArgument = arguments?.getSerializable(MainFragment.PAGE) as MainFragment.NewsPages
        if (reqeustArgument==MainFragment.NewsPages.NewsPage){
            GlobalScope.launch {
                getNewsListAsync()
            }
            return
        }
        runOnBackGround {
            databaseList = fetchDataFromDatabase()
            runOnUiThread {
                if (!databaseList.isNullOrEmpty()) {
                    val adapter = NewsListAdapter(activity
                            ?: return@runOnUiThread, databaseList!!, glide, reqeustArgument?.detailApi
                            ?: return@runOnUiThread)
                    fragmentMainRecyclerview.adapter = adapter

                }
                requestHttp(reqeustArgument?.reqeustApi
                        ?: return@runOnUiThread, createRequestBean())
            }
        }
    }

    private suspend fun getNewsListAsync(){
        val newsList = EHeritageApiRetrofitServiceCreator.EhritageService.getNewsList(pageNumber++).await()
        val adapter = NewsListAdapter(requireActivity(), newsList, glide, reqeustArgument?.detailApi ?: return)
        runOnUiThread {
            onPostLoad()
            fragmentMainRecyclerview.adapter = adapter
        }
    }

    private fun fetchDataFromDatabase(): List<NewsListResponse>? {
        val data = arrayListOf<NewsListResponse>()
        val typeName = reqeustArgument?.newsListDaoName ?: return null
        val dao = GlobalContext.newsDetailDatabase.newsListaDao()
        dao.getAllByType(typeName.typeName).forEach {
            data.add(NewsListResponse(it))
        }
        return data
    }

    override fun onTaskReturned(api: RequestHelper, action: RequestAction, response: String) {
        super.onTaskReturned(api, action, response)
        when (api.getRequestApi()) {
            reqeustArgument?.reqeustApi -> {
                val data = convertAndProcessData(response)
                if (fragmentMainRecyclerview.adapter == null || api.getRequestBean().getPathParamerater()[0] == "2") {
                    val adapter = NewsListAdapter(activity
                            ?: return, data, glide, reqeustArgument?.detailApi ?: return)
                    fragmentMainRecyclerview.adapter = adapter
                    onPostLoad()
                } else {
                    val adapter = fragmentMainRecyclerview.adapter as NewsListAdapter
                    adapter.addNewData(data)
                }
            }
        }
    }

    private fun convertAndProcessData(response: String): List<NewsListResponse> {
        val data = fromJsonToList(response, NewsListResponse::class.java)
        data.forEach { networkData ->
            val dataInDatabase = databaseList?.find {
                it.link == networkData.link
            }
            if (dataInDatabase != null) {
                networkData.idFromDataBase = dataInDatabase.idFromDataBase
                networkData.isRead = dataInDatabase.isRead
                networkData.typeFromDatabase = dataInDatabase.typeFromDatabase
            }
        }
        return data
    }


    override fun onPreLoad() {
        bottomNewsRefreshLayout.isRefreshing = true
        fragmentMainRecyclerview.adapter = null
    }

    override fun onPostLoad() {
        bottomNewsRefreshLayout.isRefreshing = false
        //顶部卡片加载完成后，显示顶部卡片的背景图片

    }

    override fun onDestroyView() {
        super.onDestroyView()
        val adapter = fragmentMainRecyclerview.adapter
        if (adapter is NewsListAdapter) {
            val datacount = adapter.itemCount
            runOnBackGround {
                for (i in 0 until datacount) {
                    val data = adapter.getItem(i)
                    val dao = GlobalContext.newsDetailDatabase.newsListaDao()
                    if (data.idFromDataBase != null || dao.getCountNumberByLink(data.link) > 0) {
                        dao.update(NewsList(data))
                    } else {
                        val typeanme = reqeustArgument?.newsListDaoName?.typeName ?: continue
                        dao.insert(NewsList(data, typeanme))
                    }
                }
            }

        }
    }

    private val onScroller = object : OnSrollHelper() {
        override fun loadMoreData(recyclerView: RecyclerView) {
            requestHttp(reqeustArgument?.reqeustApi ?: return, createRequestBean())
        }

    }

}