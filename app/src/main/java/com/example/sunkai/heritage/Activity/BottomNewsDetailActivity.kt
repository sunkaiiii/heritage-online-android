package com.example.sunkai.heritage.Activity

import android.os.Bundle
import android.transition.Fade
import android.transition.Slide
import android.view.Gravity
import androidx.core.transition.doOnEnd
import com.example.sunkai.heritage.Activity.BaseActivity.BaseHandleCollectActivity
import com.example.sunkai.heritage.Adapter.BottomNewsDetailRecyclerViewAdapter
import com.example.sunkai.heritage.ConnectWebService.HandleMainFragment
import com.example.sunkai.heritage.Data.BottomFolkNewsLite
import com.example.sunkai.heritage.Interface.OnPageLoaded
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.getThemeColor
import com.example.sunkai.heritage.value.DATA
import com.example.sunkai.heritage.value.TITLE
import com.example.sunkai.heritage.value.TYPE_FOCUS_HERITAGE
import kotlinx.android.synthetic.main.activity_bottom_news_detail.*

class BottomNewsDetailActivity : BaseHandleCollectActivity(), OnPageLoaded {

    var id: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom_news_detail)
        val title = intent.getStringExtra(TITLE)
        val data = intent.getSerializableExtra(DATA)
        if (data is BottomFolkNewsLite) {
            this.id = data.id
            initAnimationAndLoadData(data)
            setDataToView(data)
            supportActionBar?.title = title
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            bottomNewsDetailRefresh.setOnRefreshListener {
                GetNewsDetail(data.id)
            }
        }
    }

    private fun initAnimationAndLoadData(data: BottomFolkNewsLite) {
        val slide = Slide(Gravity.getAbsoluteGravity(Gravity.END, resources.configuration.layoutDirection))
        val fade = Fade()
        slide.duration = 500
        fade.duration = 500
        window.enterTransition = slide
        window.exitTransition = fade
        window.returnTransition = fade
        window.enterTransition.doOnEnd {
            GetNewsDetail(data.id)
        }
    }

    override fun getType(): String {
        return TYPE_FOCUS_HERITAGE
    }

    override fun getID(): Int? {
        return id
    }

    private fun setDataToView(data: BottomFolkNewsLite) {
        bottomNewsDetailTime.setTextColor(getThemeColor())
        bottomNewsDetailTitle.text = data.title
        bottomNewsDetailTime.text = data.time
    }

    private fun GetNewsDetail(id: Int) {
        onPreLoad()
        requestHttp {
            val data = HandleMainFragment.GetBottomNewsInformationByID(id)
            data?.let {
                val contentInfos = HandleMainFragment.GetBottomNewsDetailInfo(data.content)
                runOnUiThread {
                    onPostLoad()
                    val adapter = BottomNewsDetailRecyclerViewAdapter(this, contentInfos, glide)
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
