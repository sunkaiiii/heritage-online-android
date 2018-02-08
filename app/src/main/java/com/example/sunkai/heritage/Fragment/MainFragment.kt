package com.example.sunkai.heritage.Fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sunkai.heritage.Activity.NewsDetailActivity
import com.example.sunkai.heritage.Adapter.MainNewsAdapter
import com.example.sunkai.heritage.ConnectWebService.HandleMainFragment
import com.example.sunkai.heritage.Data.FolkNewsLite
import com.example.sunkai.heritage.Interface.OnItemClickListener
import com.example.sunkai.heritage.Interface.OnPageLoaded
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.MakeToast
import com.example.sunkai.heritage.tools.MakeToast.toast
import kotlinx.android.synthetic.main.fragment_main.*


/**
 * 民间页的类
 */
class MainFragment : android.support.v4.app.Fragment(),View.OnClickListener,OnPageLoaded {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initview()
        loadSomeMainNews()
    }

    private fun initview(){
        swipeRefresh.setOnRefreshListener {
            mainFramgentNewsRecyclerView.adapter=null
            loadSomeMainNews()
        }
    }

    private fun loadSomeMainNews(){
        onPreLoad()
        Thread{
            val news=HandleMainFragment.ReadMainNews()
            val activity=activity
            activity?.let{
                activity.runOnUiThread{
                    val adapter=MainNewsAdapter(activity,news)
                    mainFramgentNewsRecyclerView.layoutManager=LinearLayoutManager(activity)
                    mainFramgentNewsRecyclerView.adapter=adapter
                    onPostLoad()
                }
            }
        }.start()
    }

    override fun onPreLoad() {
        swipeRefresh.isRefreshing=true
    }

    override fun onPostLoad() {
        swipeRefresh.isRefreshing=false
    }


    override fun onClick(v: View) {
        when(v.id){
        }
    }



}
