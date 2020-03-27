package com.example.sunkai.heritage.activity

import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.activity.base.BaseGlideActivity
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
import kotlinx.android.synthetic.main.activity_bottom_news_detail.*

class BottomNewsDetailActivity : BaseGlideActivity(), OnPageLoaded {

    var link: String? = null
    var requestApi: EHeritageApi? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom_news_detail)
        val title = intent.getStringExtra(TITLE)
        val data = intent.getSerializableExtra(DATA)
        requestApi = intent.getSerializableExtra(API) as EHeritageApi
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (data is NewsListResponse) {
            this.link = data.link
            setDataToView(data)
            supportActionBar?.title = title

        } else if (data is String) {
            this.link = data
        }
        val link = this.link ?: return
        GetNewsDetail(link)
        bottomNewsDetailRefresh.setOnRefreshListener {
            GetNewsDetail(link)
        }
    }


    private fun setDataToView(data: NewsListResponse) {
        bottomNewsDetailTitle.text = data.title
    }

    private fun GetNewsDetail(link: String) {
        onPreLoad()
        val request = BottomNewsDetailRequest();
        request.link = link
        requestHttp(request, requestApi ?: return)
    }

    override fun beforeReuqestStart(request: RequestHelper) {
        super.beforeReuqestStart(request)
        when (request.getRequestApi()) {
            EHeritageApi.GetNewsDetail -> onPreLoad()
        }
    }

    override fun onTaskReturned(api: RequestHelper, action: RequestAction, response: String) {
        super.onTaskReturned(api, action, response)
        when (api.getRequestApi()) {
            requestApi -> {
                val data = fromJsonToObject(response, BottomFolkNews::class.java)
                initTitleAndSubtitle(data)
                val adapter = BottomNewsDetailRecyclerViewAdapter(this, data.content, glide)
                bottomNewsDetailRecyclerview.adapter = adapter
            }
        }
    }

    private fun initTitleAndSubtitle(data: BottomFolkNews) {
        bottomNewsDetailTitle.text = data.title.replace("\r", "").replace("\n", "").replace("\t", "")
        data.subtitle?.let { list ->
            list.forEach {
                val textView = TextView(this)
                textView.text = it
                val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                layoutParams.weight = 1f
                newsDetailSubtitleLayout.addView(textView)
            }
        }
        bottomNewsDetailAuther.text = data.author
    }

    override fun onRequestError(api: RequestHelper, action: RequestAction, ex: Exception) {
        super.onRequestError(api, action, ex)
        toast(getString(R.string.network_error))
    }

    //TODO 可以整合到基类里
    override fun onRequestEnd(request: RequestHelper) {
        when (request.getRequestApi()) {
            requestApi -> onPostLoad()
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
