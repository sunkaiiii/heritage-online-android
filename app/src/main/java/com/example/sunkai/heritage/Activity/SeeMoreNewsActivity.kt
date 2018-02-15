package com.example.sunkai.heritage.Activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.View
import com.example.sunkai.heritage.Adapter.SeeMoreNewsRecyclerViewAdapter
import com.example.sunkai.heritage.ConnectWebService.HandleMainFragment
import com.example.sunkai.heritage.Interface.OnItemClickListener
import com.example.sunkai.heritage.Interface.OnPageLoaded
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.OnSrollHelper
import kotlinx.android.synthetic.main.activity_see_more_news.*

class SeeMoreNewsActivity : AppCompatActivity(),OnPageLoaded {

    var category:String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_see_more_news)
        val category=intent.getStringExtra("category")
        if(!TextUtils.isEmpty(category)) {
            initView(category)
            getMoreNews(category)
            this.category=category
        }
    }
    private fun initView(category: String){
        seeMoreNewsRefresh.setOnRefreshListener {
            getMoreNews(category)
        }
    }

    private fun getMoreNews(category:String){
        onPreLoad()
        Thread{
            val datas=HandleMainFragment.GetFolkNewsList(category,0,20)
            runOnUiThread {
                val adapter=SeeMoreNewsRecyclerViewAdapter(this,datas)
                seeMoreNewsRecyclerView.adapter=adapter
                onPostLoad()
                setRecyclerClick(adapter)
                setRecyclerScrollListener()
            }
        }.start()
    }

    private fun setRecyclerClick(adapter: SeeMoreNewsRecyclerViewAdapter){
        adapter.setOnItemClickListen(object :OnItemClickListener{
            override fun onItemClick(view: View, position: Int) {
                val data=adapter.getItem(position)
                val intent=Intent(this@SeeMoreNewsActivity,NewsDetailActivity::class.java)
                intent.putExtra("id",data.id)
                startActivity(intent)
            }
        })
    }

    private fun setRecyclerScrollListener(){
        seeMoreNewsRecyclerView.addOnScrollListener(onScroll)
    }

    override fun onPreLoad() {
        seeMoreNewsRefresh.isRefreshing=true
        seeMoreNewsRecyclerView.adapter=null
    }

    override fun onPostLoad() {
        seeMoreNewsRefresh.isRefreshing=false
    }

    //滑动到接近底部的时候，自动加载更多的数据
    private val onScroll=object:OnSrollHelper(){
        override fun loadMoreData(recyclerView: RecyclerView) {
            val adapter=recyclerView.adapter
            if( adapter is SeeMoreNewsRecyclerViewAdapter){
                Thread{
                    setPageOnLoad()
                    val category=category
                    category?.let{
                        val datas=HandleMainFragment.GetFolkNewsList(category,adapter.itemCount,adapter.itemCount+20)
                        runOnUiThread {
                            adapter.addNewData(datas)
                            setPageLoaded()
                        }
                    }
                }.start()
            }
        }

    }
}
