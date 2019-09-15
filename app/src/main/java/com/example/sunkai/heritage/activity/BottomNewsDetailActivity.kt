package com.example.sunkai.heritage.activity

import android.os.Bundle
import android.transition.Fade
import androidx.core.transition.doOnEnd
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.activity.base.BaseHandleCollectActivity
import com.example.sunkai.heritage.adapter.BottomNewsDetailRecyclerViewAdapter
import com.example.sunkai.heritage.connectWebService.HandleMainFragment
import com.example.sunkai.heritage.entity.BottomFolkNewsLite
import com.example.sunkai.heritage.interfaces.OnPageLoaded
import com.example.sunkai.heritage.tools.getThemeColor
import com.example.sunkai.heritage.value.DATA
import com.example.sunkai.heritage.value.TITLE
import com.example.sunkai.heritage.value.TYPE_FOCUS_HERITAGE
import kotlinx.android.synthetic.main.activity_bottom_news_detail.*

class BottomNewsDetailActivity : BaseHandleCollectActivity(), OnPageLoaded {

    var link: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom_news_detail)
        val title = intent.getStringExtra(TITLE)
        val data = intent.getSerializableExtra(DATA)
        if (data is BottomFolkNewsLite) {
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

    private fun setDataToView(data: BottomFolkNewsLite) {
        bottomNewsDetailTime.setTextColor(getThemeColor())
        bottomNewsDetailTitle.text = data.title
        bottomNewsDetailTime.text = data.date
    }

    private fun GetNewsDetail(link: String) {
        onPreLoad()
        requestHttp {
            val data = HandleMainFragment.GetBottomNewsInformationByLink(link)
            data?.let {
                runOnUiThread {
                    onPostLoad()
                    val adapter = BottomNewsDetailRecyclerViewAdapter(this, data.content, glide)
                    bottomNewsDetailRecyclerview.adapter = adapter
                }

            }
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
