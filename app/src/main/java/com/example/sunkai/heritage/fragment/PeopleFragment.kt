package com.example.sunkai.heritage.fragment

import android.os.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.recyclerview.widget.RecyclerView
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.adapter.FragmentPeopleBannerAdapter
import com.example.sunkai.heritage.adapter.PeopleFragmentListAdapter
import com.example.sunkai.heritage.connectWebService.EHeritageApi
import com.example.sunkai.heritage.connectWebService.RequestHelper
import com.example.sunkai.heritage.entity.response.NewsListResponse
import com.example.sunkai.heritage.entity.request.BasePathRequest
import com.example.sunkai.heritage.entity.response.PeopleMainPageResponse
import com.example.sunkai.heritage.fragment.baseFragment.BaseLazyLoadFragment
import com.example.sunkai.heritage.interfaces.NetworkRequest
import com.example.sunkai.heritage.interfaces.RequestAction
import com.example.sunkai.heritage.tools.BaseOnPageChangeListener
import com.example.sunkai.heritage.tools.OnSrollHelper
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.fragment_people.*
import kotlin.math.abs

class PeopleFragment : BaseLazyLoadFragment() {

    private var pageNumber = 1

    private val scrollHelper = object : OnSrollHelper() {
        override fun loadMoreData(recyclerView: RecyclerView) {
            requestHttp(EHeritageApi.GetPeopleList, createRequestBean())
        }

    }

    private val viewPageScrollHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            if (msg.what == SCROLL) {
                fragmentPeopleViewpager.currentItem++
                startViewpagerScrollDelay()
            }
        }
    }

    private fun createRequestBean(): NetworkRequest {
        return object : BasePathRequest() {
            override fun getPathParamerater(): List<String> {
                return listOf(pageNumber++.toString())
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_people, container, false)
    }

    private fun initviews() {
        view?.viewTreeObserver?.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                view?.viewTreeObserver?.removeOnGlobalLayoutListener(this)
                val layoutparams = fragmentPeopleCollapsingToolbarLayout.layoutParams
                layoutparams.height = fragmentPeopleCollapsingToolbarLayout.width
                fragmentPeopleCollapsingToolbarLayout.layoutParams = layoutparams
            }
        })
        peopleLoadingProgressBar.visibility = View.GONE
        peopleMainPage.visibility = View.VISIBLE

        fragmentPeopleAppBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
            val scrollRange = fragmentPeopleAppBarLayout.totalScrollRange
            val alpha = abs(verticalOffset.toFloat() / scrollRange)
            fragmentPeopleTopTitleBackground.alpha = alpha * alpha * alpha
        })
    }


    override fun startLoadInformation() {
        requestHttp(EHeritageApi.GetPeopleMainPage)
        requestHttp(EHeritageApi.GetPeopleList, createRequestBean())
    }

    override fun onTaskReturned(api: RequestHelper, action: RequestAction, response: String) {
        super.onTaskReturned(api, action, response)
        when (api.getRequestApi()) {
            EHeritageApi.GetPeopleMainPage -> {
                initviews()
                val data = fromJsonToObject(response, PeopleMainPageResponse::class.java)
                val adapter = FragmentPeopleBannerAdapter(context ?: return, data.table, glide)
                val middleItem = data.table.size * 2000
                fragmentPeopleViewpager.adapter = adapter
                fragmentPeopleViewpager.addOnPageChangeListener(object : BaseOnPageChangeListener() {
                    override fun onPageSelected(position: Int) {
                        startViewpagerScrollDelay()
                    }
                })
                fragmentPeopleViewpager.currentItem = middleItem
            }
            EHeritageApi.GetPeopleList -> {
                val data = fromJsonToList(response, NewsListResponse::class.java)
                if (peopleFragmentRecyclerView.adapter == null) {
                    val adapter = PeopleFragmentListAdapter(context ?: return, data, glide)
                    peopleFragmentRecyclerView.adapter = adapter
                    peopleFragmentRecyclerView.addOnScrollListener(scrollHelper)
                } else {
                    val adapter = peopleFragmentRecyclerView.adapter as PeopleFragmentListAdapter
                    adapter.addNewData(data)
                }

            }
        }
    }

    override fun beforeReuqestStart(request: RequestHelper) {
        super.beforeReuqestStart(request)
        when (request.getRequestApi()) {
            EHeritageApi.GetPeopleMainPage -> {
                peopleLoadingProgressBar.visibility = View.VISIBLE
                peopleMainPage.visibility = View.GONE
            }
        }
    }

    private fun startViewpagerScrollDelay() {
        viewPageScrollHandler.removeMessages(SCROLL)
        viewPageScrollHandler.sendEmptyMessageDelayed(SCROLL, DELAY)
    }

    override fun onRestoreFragmentLoadInformation() {
        initviews()
        pageNumber = 1
        startLoadInformation()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewPageScrollHandler.removeMessages(SCROLL)
    }

    companion object {
        const val SCROLL = 1
        const val DELAY = 3000L
        const val PAGE = "page"
    }
}