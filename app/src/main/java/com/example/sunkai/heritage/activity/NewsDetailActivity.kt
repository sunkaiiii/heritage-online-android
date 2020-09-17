package com.example.sunkai.heritage.activity

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.activity.base.BaseGlideActivity
import com.example.sunkai.heritage.adapter.NewsDetailRecyclerViewAdapter
import com.example.sunkai.heritage.connectWebService.EHeritageApi
import com.example.sunkai.heritage.connectWebService.RequestHelper
import com.example.sunkai.heritage.database.entities.NewsDetailContent
import com.example.sunkai.heritage.database.entities.NewsDetailRelevantContent
import com.example.sunkai.heritage.entity.response.NewsDetail
import com.example.sunkai.heritage.entity.response.NewsListResponse
import com.example.sunkai.heritage.entity.request.BottomNewsDetailRequest
import com.example.sunkai.heritage.entity.response.NewsDetailRelativeNews
import com.example.sunkai.heritage.interfaces.OnPageLoaded
import com.example.sunkai.heritage.interfaces.RequestAction
import com.example.sunkai.heritage.tools.GlobalContext
import com.example.sunkai.heritage.tools.MakeToast.toast
import com.example.sunkai.heritage.value.API
import com.example.sunkai.heritage.value.DATA
import com.example.sunkai.heritage.value.TITLE
import kotlinx.android.synthetic.main.activity_news_detail.*

class NewsDetailActivity : BaseGlideActivity(), OnPageLoaded {

    var link: String? = null
    var requestApi: EHeritageApi? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_detail)
        val title = intent.getStringExtra(TITLE)
        val data = intent.getSerializableExtra(DATA)
        requestApi = intent.getSerializableExtra(API) as EHeritageApi
        setSupportActionBar(toolbar)
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
        runOnBackGround {
            val databaseData = tryToGetDataInDatabase(link)
            runOnUiThread {
                if (databaseData != null) {
                    setDataToView(databaseData)
                    onPostLoad()
                    return@runOnUiThread
                }
                val request = BottomNewsDetailRequest()
                request.link = link
                requestHttp(request, requestApi ?: return@runOnUiThread)
            }
        }

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
                val data = fromJsonToObject(response, NewsDetail::class.java)
                setDataToView(data)
                saveIntoDatabase(data)
            }
        }
    }

    private fun tryToGetDataInDatabase(link: String): NewsDetail? {
        val databaseData = GlobalContext.newsDetailDatabase.newsDetailDao().getNewsDetailWithContent(link)
                ?: return null
        return NewsDetail(databaseData)
    }

    private fun saveIntoDatabase(data: NewsDetail) {
        runOnBackGround {
            val database = GlobalContext.newsDetailDatabase
            val newsDetailContentList = arrayListOf<NewsDetailContent>()
            val newsRelevantNews = arrayListOf<NewsDetailRelevantContent>()
            data.content.forEach {
                newsDetailContentList.add(
                        NewsDetailContent(null,
                                it.type,
                                it.content,
                                it.compressImg,
                                data.link)
                )
            }
            data.relativeNews.forEach {
                newsRelevantNews.add(NewsDetailRelevantContent(null, it.link, it.title, it.date, data.link))
            }
            val dao = database.newsDetailDao()
            val contentdao = database.newsDetailContentDao()
            val relevantNewsDat = database.newsDetailRelevantNewsDao()
            dao.insert(com.example.sunkai.heritage.database.entities.NewsDetail(data))
            contentdao.insertAll(newsDetailContentList)
            relevantNewsDat.insertAll(newsRelevantNews)
        }
    }

    private fun setDataToView(data: NewsDetail) {
        bottomNewsDetailTitle.text = data.title.replace("\r", "").replace("\n", "").replace("\t", "")
        newsDetailSubtitleLayout.removeAllViews()
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
        val adapter = NewsDetailRecyclerViewAdapter(this, data.content, glide, data.relativeNews)
        adapter.setOnRelevantNewsClickListner(object : NewsDetailRecyclerViewAdapter.onRelevantNewsClick {
            override fun onClick(v: View, news: NewsDetailRelativeNews) {
                val intent = intent
                intent.putExtra(DATA, news.link)
                startActivity(intent)
            }

        })
        bottomNewsDetailRecyclerview.adapter = adapter
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
