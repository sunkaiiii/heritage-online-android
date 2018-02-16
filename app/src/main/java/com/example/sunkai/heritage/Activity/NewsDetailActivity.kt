package com.example.sunkai.heritage.Activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.example.sunkai.heritage.Adapter.NewsDetailRecyclerAdapter
import com.example.sunkai.heritage.ConnectWebService.HandleMainFragment
import com.example.sunkai.heritage.Data.FolkNewsLite
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.value.RESULT_NULL
import kotlinx.android.synthetic.main.activity_news_detail.*

class NewsDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_detail)
        if(intent.getSerializableExtra("data") is FolkNewsLite){
            val data=intent.getSerializableExtra("data") as FolkNewsLite
            getNewsDetail(data.id)
            setDataToView(data)
        }

    }

    private fun setDataToView(data:FolkNewsLite){
        news_detail_title.text=data.title
        news_detail_time.text=data.time
    }
    private fun getNewsDetail(id:Int){
        Thread{
            val datas=HandleMainFragment.GetFolkNewsInformation(id)
            runOnUiThread {
                val adapter=NewsDetailRecyclerAdapter(this,datas)
                newsDetailRecyclerView.layoutManager=LinearLayoutManager(this)
                newsDetailRecyclerView.adapter=adapter
            }
        }.start()
    }
}
