package com.example.sunkai.heritage.Activity

import android.os.Build
import android.os.Bundle
import com.example.sunkai.heritage.Activity.BaseActivity.BaseHandleCollectActivity
import com.example.sunkai.heritage.Adapter.NewsDetailRecyclerAdapter
import com.example.sunkai.heritage.ConnectWebService.HandleMainFragment
import com.example.sunkai.heritage.Data.FolkNewsLite
import com.example.sunkai.heritage.Data.MainPageSlideNews
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.ThreadPool
import com.example.sunkai.heritage.value.TYPE_MAIN
import kotlinx.android.synthetic.main.activity_news_detail.*

class NewsDetailActivity : BaseHandleCollectActivity() {

    var id:Int?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_detail)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.enterTransition.duration=500
        }
        val title=intent.getStringExtra("category")
        supportActionBar?.title=title
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if(intent.getSerializableExtra("data") is FolkNewsLite){
            val data=intent.getSerializableExtra("data") as FolkNewsLite
            getNewsDetail(data.id)
            setDataToView(data)
            id=data.id
        }else if(intent.getSerializableExtra("data") is MainPageSlideNews){
            val data=intent.getSerializableExtra("data") as MainPageSlideNews
            setDataToView(data)
        }
    }

    private fun setDataToView(data:FolkNewsLite){
        news_detail_title.text=data.title
        news_detail_time.text=data.time
    }

    private fun setDataToView(data:MainPageSlideNews){
        news_detail_title.text=data.content
        news_detail_time.text=""
        generateDetail(data.detail)
    }

    private fun getNewsDetail(id:Int){
        ThreadPool.execute{
            val datas=HandleMainFragment.GetFolkNewsInformation(id)
            runOnUiThread {
                val adapter=NewsDetailRecyclerAdapter(this,datas)
                newsDetailRecyclerView.adapter=adapter
            }
        }
    }

    private fun generateDetail(detail:String){
        ThreadPool.execute{
            val data=HandleMainFragment.GetMainPageSlideDetailInfo(detail)
            runOnUiThread {
                val adapter=NewsDetailRecyclerAdapter(this,data)
                newsDetailRecyclerView.adapter=adapter
            }
        }
    }

    override fun getType(): String {
        return TYPE_MAIN
    }

    override fun getID(): Int? {
        return id
    }

}
