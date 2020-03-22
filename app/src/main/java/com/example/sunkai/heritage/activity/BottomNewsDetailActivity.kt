package com.example.sunkai.heritage.activity

import android.os.Bundle
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.activity.base.BaseHandleCollectActivity
import com.example.sunkai.heritage.adapter.BottomNewsDetailRecyclerViewAdapter
import com.example.sunkai.heritage.connectWebService.EHeritageApi
import com.example.sunkai.heritage.connectWebService.RequestHelper
import com.example.sunkai.heritage.entity.BottomFolkNews
import com.example.sunkai.heritage.entity.NewsListResponse
import com.example.sunkai.heritage.entity.request.BottomNewsDetailRequest
import com.example.sunkai.heritage.interfaces.OnPageLoaded
import com.example.sunkai.heritage.interfaces.RequestAction
import com.example.sunkai.heritage.tools.MakeToast.toast
import com.example.sunkai.heritage.tools.getThemeColor
import com.example.sunkai.heritage.value.API
import com.example.sunkai.heritage.value.DATA
import com.example.sunkai.heritage.value.TITLE
import com.example.sunkai.heritage.value.TYPE_FOCUS_HERITAGE
import kotlinx.android.synthetic.main.activity_bottom_news_detail.*

class BottomNewsDetailActivity : BaseHandleCollectActivity(), OnPageLoaded {

    var link: String? = null
    var requestApi:EHeritageApi?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom_news_detail)
        val title = intent.getStringExtra(TITLE)
        val data = intent.getSerializableExtra(DATA)
        requestApi=intent.getSerializableExtra(API) as EHeritageApi
        if (data is NewsListResponse) {
            this.link = data.link
            setDataToView(data)
            supportActionBar?.title = title
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            GetNewsDetail(data.link)
            bottomNewsDetailRefresh.setOnRefreshListener {
                GetNewsDetail(data.link)
            }
        }
    }


    override fun getType(): String {
        return TYPE_FOCUS_HERITAGE
    }

    override fun getID(): Int? {
        //TODO 我的收藏
        return null
    }

    private fun setDataToView(data: NewsListResponse) {
        bottomNewsDetailTime.setTextColor(getThemeColor())
        bottomNewsDetailTitle.text = data.title
        bottomNewsDetailTime.text = data.date
    }

    private fun GetNewsDetail(link: String) {
        onPreLoad()
        val request = BottomNewsDetailRequest();
        request.link=link
        requestHttp(request,requestApi?:return)
    }

    override fun beforeReuqestStart(request: RequestHelper) {
        super.beforeReuqestStart(request)
        when(request.getRequestApi())
        {
            EHeritageApi.GetNewsDetail->onPreLoad()
        }
    }

    override fun onTaskReturned(api: RequestHelper, action: RequestAction, response: String) {
        super.onTaskReturned(api, action, response)
        when(api.getRequestApi())
        {
            requestApi->
            {
                val data=fromJsonToObject(response, BottomFolkNews::class.java)
                val adapter = BottomNewsDetailRecyclerViewAdapter(this, data.content, glide)
                bottomNewsDetailRecyclerview.adapter = adapter
            }
        }
    }

    override fun onRequestError(api: RequestHelper, action: RequestAction, ex: Exception) {
        super.onRequestError(api, action, ex)
        toast(getString(R.string.network_error))
    }

    //TODO 可以整合到基类里
    override fun onRequestEnd(request: RequestHelper) {
        when(request.getRequestApi())
        {
            requestApi->onPostLoad()
        }
    }


    override fun onPreLoad() {
        bottomNewsDetailRefresh.isRefreshing = true
        bottomNewsDetailRecyclerview.adapter = null
    }

    override fun onPostLoad() {
        bottomNewsDetailRefresh.isRefreshing = false
    }
}
