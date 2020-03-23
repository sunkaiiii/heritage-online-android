package com.example.sunkai.heritage.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.adapter.FragmentPeopleBannerAdapter
import com.example.sunkai.heritage.connectWebService.EHeritageApi
import com.example.sunkai.heritage.connectWebService.RequestHelper
import com.example.sunkai.heritage.entity.request.BasePathRequest
import com.example.sunkai.heritage.entity.response.PeopleMainPageResponse
import com.example.sunkai.heritage.fragment.baseFragment.BaseLazyLoadFragment
import com.example.sunkai.heritage.interfaces.RequestAction
import com.example.sunkai.heritage.tools.BaseOnPageChangeListener
import kotlinx.android.synthetic.main.fragment_people.*

class PeopleFragment : BaseLazyLoadFragment() {

    private var pageNumber = 1
    private val pathRequest = object : BasePathRequest() {
        override fun getPathParamerater(): List<String> {
            return listOf(pageNumber++.toString())
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


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_people, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initviews()
    }

    private fun initviews() {
        view?.viewTreeObserver?.addOnGlobalLayoutListener(object:ViewTreeObserver.OnGlobalLayoutListener{
            override fun onGlobalLayout() {
                view?.viewTreeObserver?.removeOnGlobalLayoutListener(this)
                val layoutparams=fragmentPeopleCollapsingToolbarLayout.layoutParams
                layoutparams.height=fragmentPeopleCollapsingToolbarLayout.width
                fragmentPeopleCollapsingToolbarLayout.layoutParams=layoutparams
            }
        })
    }


    override fun startLoadInformation() {
        requestHttp(EHeritageApi.GetPeopleMainPage)
        requestHttp(EHeritageApi.GetPeopleList, pathRequest)
    }

    override fun onTaskReturned(api: RequestHelper, action: RequestAction, response: String) {
        super.onTaskReturned(api, action, response)
        when (api.getRequestApi()) {
            EHeritageApi.GetPeopleMainPage -> {
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

    companion object {
        const val SCROLL = 1
        const val DELAY = 3000L
        const val PAGE = "page"
    }
}