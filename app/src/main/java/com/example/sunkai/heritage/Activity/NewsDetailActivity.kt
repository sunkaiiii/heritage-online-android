package com.example.sunkai.heritage.Activity

import android.os.Build
import android.os.Bundle
import androidx.core.view.GravityCompat
import android.transition.Fade
import android.transition.Slide
import android.view.Gravity
import androidx.core.transition.doOnEnd
import com.example.sunkai.heritage.Activity.BaseActivity.BaseHandleCollectActivity
import com.example.sunkai.heritage.Adapter.NewsDetailRecyclerAdapter
import com.example.sunkai.heritage.ConnectWebService.HandleMainFragment
import com.example.sunkai.heritage.Data.FolkNewsLite
import com.example.sunkai.heritage.Data.MainPageSlideNews
import com.example.sunkai.heritage.Interface.OnPageLoaded
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.value.CATEGORY
import com.example.sunkai.heritage.value.DATA
import com.example.sunkai.heritage.value.TYPE_MAIN
import kotlinx.android.synthetic.main.activity_news_detail.*
import java.io.Serializable

/**
 * 新闻详情页的Activity
 */
class NewsDetailActivity : BaseHandleCollectActivity(),OnPageLoaded {

    //用于处理收藏
    var id:Int?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_detail)
        val title=intent.getStringExtra(CATEGORY)
        val data=intent.getSerializableExtra(DATA)
        setupWindowAnimations(data)
        setDataToView(data)
        supportActionBar?.title=title
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupWindowAnimations(data:Serializable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val slide= Slide(GravityCompat.getAbsoluteGravity(Gravity.END,resources.configuration.layoutDirection))
            val fade=Fade()
            slide.duration=500
            fade.duration=300
            window.enterTransition=slide
            window.exitTransition=fade
            window.returnTransition=fade
            window.enterTransition.doOnEnd {
                continueSetIntentData(data)
            }
        }else{
            continueSetIntentData(data)
        }
    }

    private fun continueSetIntentData(data: Serializable){
        if(data is FolkNewsLite){
            getNewsDetail(data.id)
            id=data.id
        }else if(data is MainPageSlideNews){
            generateDetail(data.detail)
        }
    }

    private fun setDataToView(data:Serializable){
        if(data is FolkNewsLite) {
            news_detail_title.text = data.title
            news_detail_time.text = data.time
            newsDetailRefresh.setOnRefreshListener {
                getNewsDetail(data.id)
            }
        }else if (data is MainPageSlideNews){
            news_detail_title.text=data.content
            news_detail_time.text=""
            newsDetailRefresh.setOnRefreshListener {
                generateDetail(data.detail)
            }
        }
    }

    private fun getNewsDetail(id:Int){
        onPreLoad()
        requestHttp{
            val datas=HandleMainFragment.GetFolkNewsInformation(id)
            runOnUiThread {
                onPostLoad()
                val adapter=NewsDetailRecyclerAdapter(this,datas,glide)
                newsDetailRecyclerView.adapter=adapter
            }
        }
    }

    private fun generateDetail(detail:String){
        onPreLoad()
        requestHttp{
            val data=HandleMainFragment.GetMainPageSlideDetailInfo(detail)
            runOnUiThread {
                onPostLoad()
                val adapter=NewsDetailRecyclerAdapter(this,data,glide)
                newsDetailRecyclerView.adapter=adapter
            }
        }
    }

    override fun onPreLoad() {
        newsDetailRefresh.isRefreshing=true
        newsDetailRecyclerView.adapter=null
    }

    override fun onPostLoad() {
        newsDetailRefresh.isRefreshing=false
    }

    override fun getType(): String {
        return TYPE_MAIN
    }

    override fun getID(): Int? {
        return id
    }

}
