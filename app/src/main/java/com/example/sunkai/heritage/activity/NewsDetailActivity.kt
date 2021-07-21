package com.example.sunkai.heritage.activity

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.activity.base.BaseGlideActivity
import com.example.sunkai.heritage.adapter.NewsDetailRecyclerViewAdapter
import com.example.sunkai.heritage.network.RequestHelper
import com.example.sunkai.heritage.entity.NewsDetailViewModel
import com.example.sunkai.heritage.entity.response.NewsDetail
import com.example.sunkai.heritage.entity.response.NewsListResponse
import com.example.sunkai.heritage.entity.response.NewsDetailRelativeNews
import com.example.sunkai.heritage.interfaces.RequestAction
import com.example.sunkai.heritage.tools.MakeToast.toast
import com.example.sunkai.heritage.value.DATA
import com.example.sunkai.heritage.value.TITLE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_news_detail.*

@AndroidEntryPoint
class NewsDetailActivity : BaseGlideActivity() {

    var link: String? = null
    private val newsDetailViewModel by lazy { ViewModelProvider(this).get(NewsDetailViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_detail)
        val title = intent.getStringExtra(TITLE)
        val data = intent.getSerializableExtra(DATA)
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

        newsDetailViewModel.newsDetail.observe(this,{newsDetail->
            setDataToView(newsDetail)
        })
        newsDetailViewModel.loadNewsDetail(link)
    }


    private fun setDataToView(data: NewsListResponse) {
        bottomNewsDetailTitle.text = data.title
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

}
