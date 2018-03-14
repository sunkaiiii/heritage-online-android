package com.example.sunkai.heritage.Activity

import android.os.Bundle
import com.example.sunkai.heritage.Activity.BaseActivity.BaseHandleCollectActivity
import com.example.sunkai.heritage.Adapter.BottomNewsDetailRecyclerViewAdapter
import com.example.sunkai.heritage.ConnectWebService.HandleMainFragment
import com.example.sunkai.heritage.Data.BottomFolkNewsLite
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.ThreadPool
import com.example.sunkai.heritage.value.TYPE_FOCUS_HERITAGE
import kotlinx.android.synthetic.main.activity_bottom_news_detail.*

class BottomNewsDetailActivity : BaseHandleCollectActivity() {

    var id:Int?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom_news_detail)
        val title=intent.getStringExtra("title")
        supportActionBar?.title=title
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if(intent.getSerializableExtra("data") is BottomFolkNewsLite) {
            val data = intent.getSerializableExtra("data") as BottomFolkNewsLite
            setDataToView(data)
            GetNewsDetail(data.id)
            this.id=data.id
        }
    }
    override fun getType(): String {
        return TYPE_FOCUS_HERITAGE
    }

    override fun getID(): Int? {
        return id
    }

    private fun setDataToView(data:BottomFolkNewsLite){
        bottomNewsDetailTitle.text=data.title
        bottomNewsDetailTime.text=data.time
    }

    private fun GetNewsDetail(id:Int){
        ThreadPool.execute{
            val data=HandleMainFragment.GetBottomNewsInformationByID(id)
            data?.let{
                val contentInfos=HandleMainFragment.GetBottomNewsDetailInfo(data.content)
                runOnUiThread {
                    val adapter=BottomNewsDetailRecyclerViewAdapter(this,contentInfos)
                    bottomNewsDetailRecyclerview.adapter=adapter
                }
            }
        }
    }
}
