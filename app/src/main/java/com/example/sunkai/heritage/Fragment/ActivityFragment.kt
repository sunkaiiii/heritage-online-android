package com.example.sunkai.heritage.Fragment

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.sunkai.heritage.Activity.ActivityInformationActivity
import com.example.sunkai.heritage.Adapter.ActivityRecyclerViewAdapter
import com.example.sunkai.heritage.Interface.OnItemClickListener
import com.example.sunkai.heritage.Interface.OnPageLoaded
import com.example.sunkai.heritage.R

/**
 * 首页viewpager五个页面的fragment
 */
class ActivityFragment : Fragment() {
    var activityListviewAdapter: ActivityRecyclerViewAdapter? = null
    private lateinit var activityRecyclerView: RecyclerView
    internal lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var channelName: String
    private var index:Int=0
    private var isLoadedData: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        channelName = arguments!!.getString(CHANNEL)
        index=arguments!!.getInt(INDEX)
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
        if (activityListviewAdapter == null) {
            activityListviewAdapter = ActivityRecyclerViewAdapter(activity!!, channelName)
        }
        swipeRefresh.setOnRefreshListener { activityListviewAdapter!!.startGetInformation() }
        activityListviewAdapter!!.setOnPageLoadListner(onPageLoadListner)
        val layoutManager = LinearLayoutManager(context)
        activityRecyclerView.layoutManager = layoutManager
        activityRecyclerView.adapter = activityListviewAdapter
        swipeRefresh.isRefreshing = true
        activityListviewAdapter!!.setOnItemClickListen(object : OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                val activitydata = activityListviewAdapter!!.getItem(position)
                val intent = Intent(activity, ActivityInformationActivity::class.java)
                val bundle = Bundle()
                bundle.putSerializable("activity", activitydata)
                intent.putExtra("image", activitydata.img)
                intent.putExtras(bundle)
                startActivity(intent)
            }
        })

        //因为默认启动是首页，于是直接加载首页内容
        if(index==0)
            activityListviewAdapter!!.startGetInformation()
        return view
    }

    fun getInformation() {
        if (!isLoadedData) {
            if (activityListviewAdapter == null&&activity!=null)
                activityListviewAdapter = ActivityRecyclerViewAdapter(activity!!, channelName)
            activityListviewAdapter?.startGetInformation()
        }
    }


    private val onPageLoadListner = object : OnPageLoaded {
        override fun onPreLoad() {
        }

        override fun onPostLoad() {
            swipeRefresh.isRefreshing = false
            isLoadedData = true
        }

    }

    companion object {
        private const val CHANNEL = "channel"
        private const val INDEX="index"

        /**
         * 创建一个此instance的实例，传入的参数为channel的名字，用于Fragment里面的ListView获取对应通道的内容
         */
        fun newInstance(channelName: String,index:Int): ActivityFragment {
            val fragment = ActivityFragment()
            val args = Bundle()
            args.putString(CHANNEL, channelName)
            args.putInt(INDEX,index)
            fragment.arguments = args
            return fragment
        }
    }
}
