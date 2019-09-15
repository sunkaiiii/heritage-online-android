package com.example.sunkai.heritage.activity

import android.os.Bundle
import android.transition.Fade
import android.transition.Slide
import android.view.Gravity
import androidx.core.transition.doOnEnd
import com.example.sunkai.heritage.activity.base.BaseHandleCollectActivity
import com.example.sunkai.heritage.adapter.BottomNewsDetailRecyclerViewAdapter
import com.example.sunkai.heritage.connectWebService.HandleMainFragment
import com.example.sunkai.heritage.entity.BottomFolkNewsLite
import com.example.sunkai.heritage.interfaces.OnPageLoaded
import com.example.sunkai.heritage.R
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
            initAnimationAndLoadData(data)
            setDataToView(data)
            supportActionBar?.title = title
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            bottomNewsDetailRefresh.setOnRefreshListener {
                GetNewsDetail(data.link)
            }
        }
    }

    private fun initAnimationAndLoadData(data: BottomFolkNewsLite) {
        val fade = Fade()
        fade.duration = 500
        window.returnTransition = fade
        window.enterTransition.doOnEnd {
            GetNewsDetail(data.link)
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
            //TODO 详情页网络请求
            //val data = HandleMainFragment.GetBottomNewsInformationByID(id)
//            data?.let {
//                val contentInfos = HandleMainFragment.GetBottomNewsDetailInfo(data.content)
//                runOnUiThread {
//                    onPostLoad()
//                    val adapter = BottomNewsDetailRecyclerViewAdapter(this, contentInfos, glide)
//                    bottomNewsDetailRecyclerview.adapter = adapter
//                }
//            }
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
