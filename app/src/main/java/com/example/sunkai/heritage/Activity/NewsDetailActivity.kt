package com.example.sunkai.heritage.Activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.example.sunkai.heritage.Adapter.NewsDetailRecyclerAdapter
import com.example.sunkai.heritage.ConnectWebService.HandleMainFragment
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.value.RESULT_NULL
import kotlinx.android.synthetic.main.activity_news_detail.*

class NewsDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_detail)
        val id=intent.getIntExtra("id", RESULT_NULL)
        if(id!= RESULT_NULL){
            getNewsDetail(id)
        }
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
