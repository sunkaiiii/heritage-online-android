package com.example.sunkai.heritage.Fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sunkai.heritage.Adapter.ActivityRecyclerViewAdapter
import com.example.sunkai.heritage.ConnectWebService.HandleFolk
import com.example.sunkai.heritage.Fragment.BaseFragment.BaseLazyLoadFragment
import com.example.sunkai.heritage.Interface.OnPageLoaded
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.runOnUiThread
import kotlinx.android.synthetic.main.fragment_activity1.*

/**
 * 民间viewpager五个页面的fragment
 */
class ActivityFragment : BaseLazyLoadFragment(), OnPageLoaded {
    private lateinit var channelName: String
    private var index: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        channelName = arguments!!.getString(CHANNEL)
        index = arguments!!.getInt(INDEX)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_activity1, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentActivitySwipeRefresh.setOnRefreshListener {
            getInformation()
        }

        //如果是第一页，直接加载内容
        if (index==0){
            lazyLoad()
        }
    }

    override fun startLoadInformation() {
        getInformation()
    }

    override fun onRestoreFragmentLoadInformation() {
        getInformation()
    }


    private fun getInformation() {
        val activity = activity ?: return
        onPreLoad()
        requestHttp {
            val datas = HandleFolk.GetChannelInformation(channelName)
            runOnUiThread {
                val adapter = ActivityRecyclerViewAdapter(activity, datas,glide)
                activityRecyclerView?.adapter = adapter
                refreshRefreshViewColor()
                onPostLoad()
            }
        }
    }

    private fun refreshRefreshViewColor() {
        val color = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity?.window?.statusBarColor
        } else {
            null
        }
        color?.let { fragmentActivitySwipeRefresh.setColorSchemeColors(color) }
    }


    override fun onPreLoad() {
        activityRecyclerView.adapter = null
        fragmentActivitySwipeRefresh.isRefreshing = true
    }

    override fun onPostLoad() {
        fragmentActivitySwipeRefresh?.isRefreshing = false
        refreshRefreshViewColor()
    }


    companion object {
        private const val CHANNEL = "channel"
        private const val INDEX = "index"

        /**
         * 创建一个此instance的实例，传入的参数为channel的名字，用于Fragment里面的ListView获取对应通道的内容
         */
        fun newInstance(channelName: String, index: Int): ActivityFragment {
            val fragment = ActivityFragment()
            val args = Bundle()
            args.putString(CHANNEL, channelName)
            args.putInt(INDEX, index)
            fragment.arguments = args
            return fragment
        }
    }
}
