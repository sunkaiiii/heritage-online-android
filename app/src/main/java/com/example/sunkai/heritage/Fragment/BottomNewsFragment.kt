package com.example.sunkai.heritage.Fragment

import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v7.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import android.transition.Slide
import android.util.Pair
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.sunkai.heritage.Activity.BottomNewsDetailActivity
import com.example.sunkai.heritage.Adapter.BottomFolkNewsRecyclerviewAdapter
import com.example.sunkai.heritage.ConnectWebService.HandleMainFragment
import com.example.sunkai.heritage.Fragment.BaseFragment.BaseGlideFragment
import com.example.sunkai.heritage.Interface.OnItemClickListener
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
                    setAdapterClick(adapter)
                    fragmentMainRecyclerview.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
                    fragmentMainRecyclerview.adapter = adapter
                    onPostLoad()
                }

            }
        }
    }

    private fun setAdapterClick(adapter: BottomFolkNewsRecyclerviewAdapter) {
        adapter.setOnItemClickListen(object : OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                val activity = activity
                val data = adapter.getItem(position)
                val intent = Intent(activity, BottomNewsDetailActivity::class.java)
                intent.putExtra("data", data)
                intent.putExtra("title", getString(R.string.focus_heritage))
                if (activity != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    val titleView = view.findViewById<TextView>(R.id.bottom_view_title)
                    val timeView=view.findViewById<TextView>(R.id.bottom_view_time)
                    val slide= Slide(GravityCompat.getAbsoluteGravity(GravityCompat.START, resources.configuration.layoutDirection))
                    slide.duration=500
                    activity.window.exitTransition=slide
                    val paris= arrayListOf<android.util.Pair<View,String>>(android.util.Pair(titleView,getString(R.string.bottom_news_share_title)),Pair(timeView,getString(R.string.bottom_news_share_time))).toTypedArray()
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(activity, *paris).toBundle())
                } else {
                    startActivity(intent)
                }
            }
        })
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