package com.example.sunkai.heritage.Fragment

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.sunkai.heritage.Activity.BottomNewsDetailActivity
import com.example.sunkai.heritage.Adapter.BaseAdapter.BaseCardPagerAdapter
import com.example.sunkai.heritage.Adapter.BottomFolkNewsRecyclerviewAdapter
import com.example.sunkai.heritage.Adapter.MainPageCardViewPagerAdapter
import com.example.sunkai.heritage.ConnectWebService.HandleMainFragment
import com.example.sunkai.heritage.Interface.OnItemClickListener
import com.example.sunkai.heritage.Interface.OnPageLoaded
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.ShadowTransformer
import com.example.sunkai.heritage.tools.generateColor
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

    private fun loadSomeMainNews(refreshBottom:Boolean=true){
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
                    onPostLoad()
                    if(refreshBottom){
                        loadBottomNews()
                    }
                }
            }
        }.start()
    }

    private fun loadBottomNews(){
        Thread{
            val datas=HandleMainFragment.GetBottomNewsLiteInformation()
            val activity=activity
            activity?.let{
                activity.runOnUiThread {
                    val adapter=BottomFolkNewsRecyclerviewAdapter(activity,datas)
                    setAdapterClick(adapter)
                    fragmentMainRecyclerview.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
                    fragmentMainRecyclerview.adapter=adapter
                }

            }
        }.start()
    }

    private fun setAdapterClick(adapter: BottomFolkNewsRecyclerviewAdapter) {
        adapter.setOnItemClickListen(object :OnItemClickListener{
            override fun onItemClick(view: View, position: Int) {
                val activity=activity
                activity?.let{
                    val data=adapter.getItem(position)
                    val intent= Intent(activity,BottomNewsDetailActivity::class.java)
                    intent.putExtra("data",data)
                    startActivity(intent)
                }
            }

        })
    }

    override fun onPreLoad() {
//        swipeRefresh.isRefreshing=true
    }

    override fun onPostLoad() {
//        swipeRefresh.isRefreshing=false
        val context=activity
        context?.let {
            val drawable = ContextCompat.getDrawable(context,R.mipmap.main_page_background)
            Glide.with(context).load(drawable).into(mainPageTopImage)
        }
    }


    override fun onClick(v: View) {
        when(v.id){
        }
    }

}
