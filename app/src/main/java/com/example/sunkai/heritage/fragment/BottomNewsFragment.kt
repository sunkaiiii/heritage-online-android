package com.example.sunkai.heritage.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sunkai.heritage.adapter.BottomFolkNewsRecyclerviewAdapter
import com.example.sunkai.heritage.connectWebService.HandleMainFragment
import com.example.sunkai.heritage.fragment.baseFragment.BaseGlideFragment
import com.example.sunkai.heritage.interfaces.OnPageLoaded
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.connectWebService.EHeritageApi
import com.example.sunkai.heritage.connectWebService.RequestHelper
import com.example.sunkai.heritage.entity.NewsListResponse
import com.example.sunkai.heritage.entity.request.BasePathRequest
import com.example.sunkai.heritage.interfaces.RequestAction
import com.example.sunkai.heritage.tools.OnSrollHelper
import kotlinx.android.synthetic.main.bottom_news_framgent.*

class BottomNewsFragment : BaseGlideFragment(), OnPageLoaded {
    var reqeustArgument: MainFragment.NewsPages? = null
    var pageNumber = 1
    val requestBean = object : BasePathRequest() {
        override fun getPathParamerater(): List<String> {
            return listOf(pageNumber++.toString())
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottom_news_framgent, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initview()
        loadInformation()
        bottomNewsRefreshLayout.setOnRefreshListener { loadInformation() }
    }


    private fun initview() {
        fragmentMainRecyclerview.addOnScrollListener(onScroller)
    }


    private fun loadInformation() {
        onPreLoad()
        reqeustArgument = arguments?.getSerializable(MainFragment.PAGE) as MainFragment.NewsPages
        requestHttp(reqeustArgument?.reqeustApi ?: return,requestBean)
    }

    override fun onTaskReturned(api: RequestHelper, action: RequestAction, response: String) {
        when (api.getRequestApi()) {
            reqeustArgument?.reqeustApi -> {
                val data = fromJsonToList(response, NewsListResponse::class.java)
                val adapter = BottomFolkNewsRecyclerviewAdapter(activity ?: return, data, glide,reqeustArgument?.detailApi?:return)
                fragmentMainRecyclerview.adapter = adapter
                onPostLoad()
            }
        }
    }


    override fun onPreLoad() {
        bottomNewsRefreshLayout.isRefreshing = true
        fragmentMainRecyclerview.adapter = null
    }

    override fun onPostLoad() {
        bottomNewsRefreshLayout.isRefreshing = false
        //顶部卡片加载完成后，显示顶部卡片的背景图片
    }

    private val onScroller = object : OnSrollHelper() {
        override fun loadMoreData(recyclerView: RecyclerView) {
            requestHttp(reqeustArgument?.reqeustApi?:return,requestBean)
        }

    }

}