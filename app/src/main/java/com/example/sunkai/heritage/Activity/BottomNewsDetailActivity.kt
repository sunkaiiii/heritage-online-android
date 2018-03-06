package com.example.sunkai.heritage.Activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.example.sunkai.heritage.Adapter.BottomNewsDetailRecyclerViewAdapter
import com.example.sunkai.heritage.ConnectWebService.HandleMainFragment
import com.example.sunkai.heritage.Data.BottomFolkNewsLite
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.ThreadPool
import kotlinx.android.synthetic.main.activity_bottom_news_detail.*

class BottomNewsDetailActivity : AppCompatActivity() {

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
        }
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

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            android.R.id.home->{
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
