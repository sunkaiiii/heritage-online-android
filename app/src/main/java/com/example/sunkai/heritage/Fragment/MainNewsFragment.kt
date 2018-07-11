package com.example.sunkai.heritage.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sunkai.heritage.Adapter.MainNewsAdapter
import com.example.sunkai.heritage.ConnectWebService.HandleMainFragment
import com.example.sunkai.heritage.Fragment.BaseFragment.BaseLazyLoadFragment
import com.example.sunkai.heritage.Interface.OnPageLoaded
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.runOnUiThread
import kotlinx.android.synthetic.main.main_news_fragment_layout.*

class MainNewsFragment : BaseLazyLoadFragment(), OnPageLoaded {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.main_news_fragment_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainNewsSwipeRefresh.setOnRefreshListener { startLoadInformation() }
    }

    override fun startLoadInformation() {
        onPreLoad()
        loadCategoryNews()
    }

    private fun loadCategoryNews() {
        requestHttp {
            val news = HandleMainFragment.ReadMainNews()
            runOnUiThread {
                val activity = activity ?: return@runOnUiThread
                val adapter=MainNewsAdapter(activity,news,glide)
                mainNewsList.adapter=adapter
                onPostLoad()
            }
        }

    }


    override fun onPreLoad() {
        mainNewsList.adapter=null
        mainNewsSwipeRefresh.isRefreshing = true
    }

    override fun onPostLoad() {
        mainNewsSwipeRefresh?.isRefreshing = false
    }

    override fun onRestoreFragmentLoadInformation() {
        startLoadInformation()
    }

}