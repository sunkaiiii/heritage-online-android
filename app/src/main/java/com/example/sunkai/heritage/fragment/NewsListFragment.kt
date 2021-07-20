package com.example.sunkai.heritage.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.adapter.NewsListAdapter
import com.example.sunkai.heritage.entity.NewsListViewModel
import com.example.sunkai.heritage.entity.response.NewsListResponse
import com.example.sunkai.heritage.fragment.baseFragment.BaseGlideFragment
import com.example.sunkai.heritage.interfaces.OnPageLoaded
import com.example.sunkai.heritage.tools.EHeritageApplication
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.news_list_framgent.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NewsListFragment : BaseGlideFragment(), OnPageLoaded {
    var reqeustArgument: MainFragment.NewsPages? = null
    private var databaseList: List<NewsListResponse>? = null
    private val viewModel by lazy{ViewModelProvider(this).get(NewsListViewModel::class.java)}

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.news_list_framgent, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initview()
    }


    private fun initview() {
        bottomNewsRefreshLayout.setOnRefreshListener {
            loadInformation()
        }
        loadInformation()
    }


    private fun loadInformation() {
        onPreLoad()
        reqeustArgument = arguments?.getSerializable(MainFragment.PAGE) as MainFragment.NewsPages
        val adapter = NewsListAdapter(glide, reqeustArgument ?: return)
        fragmentMainRecyclerview.adapter = adapter
        lifecycleScope.launch {
            viewModel.getNewsListPagingData(reqeustArgument?.reqeustApi?:return@launch).collect {
                adapter.submitData(it)
            }
        }
//        runOnBackGround {
//            databaseList = fetchDataFromDatabase()
//            runOnUiThread {
//                if (!databaseList.isNullOrEmpty()) {
//                    val adapter = NewsListAdapter(activity
//                            ?: return@runOnUiThread, databaseList!!, glide, reqeustArgument
//                            ?: return@runOnUiThread)
//                    fragmentMainRecyclerview.adapter = adapter
//
//                }
//            }
//        }
    }


    private fun fetchDataFromDatabase(): List<NewsListResponse>? {
        val data = arrayListOf<NewsListResponse>()
        val typeName = reqeustArgument?.newsListDaoName ?: return null
        val dao = EHeritageApplication.newsDetailDatabase.newsListaDao()
        dao.getAllByType(typeName.typeName).forEach {
            data.add(NewsListResponse(it))
        }
        return data
    }

//    override fun onTaskReturned(api: RequestHelper, action: RequestAction, response: String) {
//        super.onTaskReturned(api, action, response)
//        when (api.getRequestApi()) {
//            reqeustArgument?.reqeustApi -> {
//                val data = convertAndProcessData(response)
//                if (fragmentMainRecyclerview.adapter == null || api.getRequestBean().getPathParamerater()[0] == "2") {
//                    val adapter = NewsListAdapter(activity
//                            ?: return, data, glide, reqeustArgument?.detailApi ?: return)
//                    fragmentMainRecyclerview.adapter = adapter
//                    onPostLoad()
//                } else {
//                    val adapter = fragmentMainRecyclerview.adapter as NewsListAdapter
//                    adapter.addNewData(data)
//                }
//            }
//        }
//    }

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



}