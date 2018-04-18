package com.example.sunkai.heritage.Fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.sunkai.heritage.Activity.BottomNewsDetailActivity
import com.example.sunkai.heritage.Adapter.BottomFolkNewsRecyclerviewAdapter
import com.example.sunkai.heritage.Adapter.MainPageCardViewPagerAdapter
import com.example.sunkai.heritage.ConnectWebService.HandleMainFragment
import com.example.sunkai.heritage.Interface.OnItemClickListener
import com.example.sunkai.heritage.Interface.OnPageLoaded
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.OnSrollHelper
import com.example.sunkai.heritage.tools.ShadowTransformer
import com.example.sunkai.heritage.tools.ThreadPool
import kotlinx.android.synthetic.main.fragment_main.*


/**
 * 首页
 */
class MainFragment : Fragment(),View.OnClickListener,OnPageLoaded {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initview()
        loadSomeMainNews()
    }


    private fun initview(){
        fragmentMainViewpager.offscreenPageLimit=1
        fragmentMainRecyclerview.addOnScrollListener(onScroller)
    }

    private fun loadSomeMainNews(refreshBottom:Boolean=true){
        onPreLoad()
        ThreadPool.execute{
            val news=HandleMainFragment.ReadMainNews()
            val activity=activity
            activity?.let{
                val views=ArrayList<CardView>()
                for(i in news){
                    views.add(CardView(activity))
                }
                activity.runOnUiThread{
                    //初始化顶部viewpager
                    val adapter=MainPageCardViewPagerAdapter(views,news)
                    fragmentMainViewpager.adapter=adapter
                    //加载滑动时候的阴影效果
                    val transformer=ShadowTransformer(fragmentMainViewpager,adapter)
                    fragmentMainViewpager.setPageTransformer(false,transformer)
                    onPostLoad()
                    if(refreshBottom){
                        loadBottomNews()
                    }
                }
            }
        }
    }

    private fun loadBottomNews(){
        ThreadPool.execute{
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
        }
    }

    private fun setAdapterClick(adapter: BottomFolkNewsRecyclerviewAdapter) {
        adapter.setOnItemClickListen(object :OnItemClickListener{
            override fun onItemClick(view: View, position: Int) {
                val activity=activity
                activity?.let{
                    val data=adapter.getItem(position)
                    val intent= Intent(activity,BottomNewsDetailActivity::class.java)
                    intent.putExtra("data",data)
                    intent.putExtra("title",getString(R.string.focus_heritage))
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
        //顶部卡片加载完成后，显示顶部卡片的背景图片
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

    private val onScroller=object:OnSrollHelper(){
        override fun loadMoreData(recyclerView: RecyclerView) {
            ThreadPool.execute{
                val activity=activity
                activity?.let {
                    val adapter = recyclerView.adapter
                    if(adapter is BottomFolkNewsRecyclerviewAdapter) {
                        val moreData = HandleMainFragment.GetBottomNewsLiteInformation(adapter.itemCount)
                        activity.runOnUiThread {
                            adapter.addNewData(moreData)
                            this.setPageLoaded()
                        }
                    }
                }

            }
        }

    }

}
