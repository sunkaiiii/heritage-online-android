package com.example.sunkai.heritage.Fragment

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sunkai.heritage.Activity.NewsDetailActivity
import com.example.sunkai.heritage.Adapter.SeeMoreNewsRecyclerViewAdapter
import com.example.sunkai.heritage.ConnectWebService.HandleMainFragment
import com.example.sunkai.heritage.Fragment.BaseFragment.BaseLazyLoadFragment
import com.example.sunkai.heritage.Interface.OnItemClickListener
import com.example.sunkai.heritage.Interface.OnPageLoaded
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.OnSrollHelper
import com.example.sunkai.heritage.tools.ThreadPool
import com.example.sunkai.heritage.value.CATEGORY
import kotlinx.android.synthetic.main.see_more_news_viewpager_item.*

/**
 * 查看更多新闻的viewpager的页面
 * Created by sunkai on 2018/2/17.
 */
class SeeMoreNewsFragment : BaseLazyLoadFragment(), OnPageLoaded {
    var category: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        this.category = arguments?.getString(CATEGORY)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.see_more_news_viewpager_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(savedInstanceState==null) {
            initView()
        }
    }

    override fun onRestoreFragmentLoadInformation(){
        initView()
        lazyLoad()
    }


    private fun initView() {
        seeMoreNewsRefresh.setOnRefreshListener {
            val category = this.category
            category?.let {
                getMoreNews(category)
            }
        }
    }

    override fun startLoadInformation() {
        val category = this.category
        category?.let {
            getMoreNews(category)
        }
    }

    private fun getMoreNews(category: String) {
        onPreLoad()
        Thread {
            val datas = HandleMainFragment.GetFolkNewsList(category, 0, 20)
            val activity = activity
            activity?.runOnUiThread {
                val adapter = SeeMoreNewsRecyclerViewAdapter(activity, datas,glide)
                seeMoreNewsRecyclerView.adapter = adapter
                onPostLoad()
                setRecyclerClick(adapter)
                setRecyclerScrollListener()
            }
        }.start()
    }

    private fun setRecyclerClick(adapter: SeeMoreNewsRecyclerViewAdapter) {
        adapter.setOnItemClickListen(object : OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                val data = adapter.getItem(position)
                val intent = Intent(activity, NewsDetailActivity::class.java)
                intent.putExtra("category", data.category)
                intent.putExtra("data", data)
                startActivity(intent)
            }
        })
    }

    private fun setRecyclerScrollListener() {
        seeMoreNewsRecyclerView.addOnScrollListener(onScroll)
    }

    override fun onPreLoad() {
        seeMoreNewsRefresh.isRefreshing = true
        seeMoreNewsRecyclerView.adapter = null
    }

    override fun onPostLoad() {
        seeMoreNewsRefresh.isRefreshing = false
    }

    //滑动到接近底部的时候，自动加载更多的数据
    private val onScroll = object : OnSrollHelper() {
        override fun loadMoreData(recyclerView: RecyclerView) {
            val adapter = recyclerView.adapter
            if (adapter is SeeMoreNewsRecyclerViewAdapter) {
                ThreadPool.execute {
                    val category = category
                    val activity = activity
                    category?.let {
                        val datas = HandleMainFragment.GetFolkNewsList(category, adapter.itemCount, 20)
                        activity?.runOnUiThread {
                            adapter.addNewData(datas)
                            this.setPageLoaded()
                        }
                    }
                }
            }
        }
    }

    companion object {
        fun newInstances(category: String): SeeMoreNewsFragment {
            val fragment = SeeMoreNewsFragment()
            val args = Bundle()
            args.putString(CATEGORY, category)
            fragment.arguments = args
            return fragment
        }
    }
}