package com.example.sunkai.heritage.Fragment

import android.os.Bundle
import android.support.v7.widget.CardView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sunkai.heritage.Adapter.BaseAdapter.BaseCardPagerAdapter
import com.example.sunkai.heritage.Adapter.MainPageCardViewPagerAdapter
import com.example.sunkai.heritage.ConnectWebService.HandleMainFragment
import com.example.sunkai.heritage.Interface.OnPageLoaded
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.ShadowTransformer
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
        fragmentMainViewpager.offscreenPageLimit=1
    }

    private fun loadSomeMainNews(){
        onPreLoad()
        Thread{
            val news=HandleMainFragment.ReadMainNews()
            val activity=activity
            activity?.let{
                val views=ArrayList<CardView>()
                for(i in news){
                    views.add(CardView(activity))
                }
                activity.runOnUiThread{
                    val adapter=MainPageCardViewPagerAdapter(views,news)
                    fragmentMainViewpager.adapter=adapter
                    val transformer=ShadowTransformer(fragmentMainViewpager,adapter)
                    fragmentMainViewpager.setPageTransformer(false,transformer)
                }
            }
        }.start()
    }

    override fun onPreLoad() {
//        swipeRefresh.isRefreshing=true
    }

    override fun onPostLoad() {
//        swipeRefresh.isRefreshing=false
    }


    override fun onClick(v: View) {
        when(v.id){
        }
    }

}
