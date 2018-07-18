package com.example.sunkai.heritage.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.example.sunkai.heritage.tools.Views.FollowThemeEdgeRecyclerView
import com.example.sunkai.heritage.Adapter.BottomFolkNewsRecyclerviewAdapter
import com.example.sunkai.heritage.ConnectWebService.HandleMainFragment
import com.example.sunkai.heritage.Fragment.BaseFragment.BaseGlideFragment
import com.example.sunkai.heritage.Interface.OnPageLoaded
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.OnSrollHelper
import kotlinx.android.synthetic.main.bottom_news_framgent.*

class BottomNewsFragment : BaseGlideFragment(), OnPageLoaded {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottom_news_framgent, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initview()
        loadBottomNews()
        bottomNewsRefreshLayout.setOnRefreshListener { loadBottomNews() }
    }


    private fun initview() {
        fragmentMainRecyclerview.addOnScrollListener(onScroller)
    }


    private fun loadBottomNews() {
        onPreLoad()
        requestHttp {
            val datas = HandleMainFragment.GetBottomNewsLiteInformation()
            val activity = activity
            activity?.let {
                activity.runOnUiThread {
                    val adapter = BottomFolkNewsRecyclerviewAdapter(activity, datas, glide)
                    fragmentMainRecyclerview.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
                    fragmentMainRecyclerview.adapter = adapter
                    onPostLoad()
                }

            }
        }
    }


    override fun onPreLoad() {
        bottomNewsRefreshLayout.isRefreshing = true
        fragmentMainRecyclerview.adapter = null
    }

    override fun onPostLoad() {
        bottomNewsRefreshLayout.isRefreshing = false
        //顶部卡片加载完成后，显示顶部卡片的背景图片
    }

    private val onScroller = object : OnSrollHelper() {
        override fun loadMoreData(recyclerView: RecyclerView) {
            requestHttp {
                val activity = activity
                activity?.let {
                    val adapter = recyclerView.adapter
                    if (adapter is BottomFolkNewsRecyclerviewAdapter) {
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