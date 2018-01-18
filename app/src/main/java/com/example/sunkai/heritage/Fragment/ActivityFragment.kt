package com.example.sunkai.heritage.Fragment

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ProgressBar

import com.example.sunkai.heritage.Activity.ActivityInformationActivity
import com.example.sunkai.heritage.Adapter.ActivityRecyclerViewAdapter
import com.example.sunkai.heritage.Interface.OnItemClickListener
import com.example.sunkai.heritage.Interface.OnPageLoaded
import com.example.sunkai.heritage.R

/**
 * 首页viewpager五个页面的fragment
 * 除了第一个页面，其他四个全都继承与BaseFrament，并实现了Lazyload，使其在页面可见的时候才加载内容
 */
class ActivityFragment : Fragment() {
    internal lateinit var activityListviewAdapter: ActivityRecyclerViewAdapter
    internal lateinit var activityRecyclerView: RecyclerView
    internal lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var channelName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        channelName = arguments!!.getString(channel)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_activity1, container, false)
        activityRecyclerView = view.findViewById(R.id.activity_listview)
        swipeRefresh = view.findViewById(R.id.fragment_activity_swipe_refresh)
        val color = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity?.window?.statusBarColor
        } else {
            null
        }
        color?.let { swipeRefresh.setColorSchemeColors(color) }
        swipeRefresh.setOnRefreshListener { activityListviewAdapter.startGetInformation() }
        activityListviewAdapter = ActivityRecyclerViewAdapter(activity!!, channelName)
        activityListviewAdapter.setOnPageLoadListner(onPageLoadListner)
        val layoutManager = LinearLayoutManager(context)
        activityRecyclerView.layoutManager = layoutManager
        activityRecyclerView.adapter = activityListviewAdapter
        swipeRefresh.isRefreshing=true
        activityListviewAdapter.startGetInformation()
        activityListviewAdapter.setOnItemClickListen(object : OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                val activitydata = activityListviewAdapter.getItem(position)
                val intent = Intent(activity, ActivityInformationActivity::class.java)
                val bundle = Bundle()
                bundle.putSerializable("activity", activitydata)
                intent.putExtra("image", activitydata.img)
                intent.putExtras(bundle)
                startActivity(intent)
            }
        })
        return view
    }

    val onPageLoadListner = object : OnPageLoaded {
        override fun onPreLoad() {
        }

        override fun onPostLoad() {
            swipeRefresh.isRefreshing=false
        }

    }

    companion object {
        private val channel = "channel"

        /**
         * 创建一个此instance的实例，传入的参数为channel的名字，用于Fragment里面的ListView获取对应通道的内容
         */
        fun newInstance(channelName: String): ActivityFragment {
            val fragment = ActivityFragment()
            val args = Bundle()
            args.putString(channel, channelName)
            fragment.arguments = args
            return fragment
        }
    }
}
