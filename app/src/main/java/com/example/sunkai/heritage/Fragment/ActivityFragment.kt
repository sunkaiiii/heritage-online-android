package com.example.sunkai.heritage.Fragment

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.Fragment
import android.support.v4.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.sunkai.heritage.Activity.FolkInformationActivity
import com.example.sunkai.heritage.Adapter.ActivityRecyclerViewAdapter
import com.example.sunkai.heritage.ConnectWebService.HandleFolk
import com.example.sunkai.heritage.Interface.OnItemClickListener
import com.example.sunkai.heritage.Interface.OnPageLoaded
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.ThreadPool
import com.example.sunkai.heritage.tools.TransitionHelper
import com.example.sunkai.heritage.tools.runOnUiThread
import com.example.sunkai.heritage.value.ACTIVITY_FRAGMENT
import kotlinx.android.synthetic.main.fragment_activity1.*

/**
 * 民间viewpager五个页面的fragment
 */
class ActivityFragment : Fragment(), OnPageLoaded {
    private lateinit var channelName: String
    private var index: Int = 0
    private var isLoadedData: Boolean = false

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
        fragmentActivitySwipeRefresh.setOnRefreshListener {
            isLoadedData = false
            getInformation()
        }
        //因为默认启动是首页，于是直接加载首页内容
        if (index == 0)
            getInformation()
    }

    private fun setTransitionStartActivity(intent: Intent, view: View) {
        val activity = activity
        if (activity == null) {
            startActivity(intent)
        } else {
            val image = view.findViewById<ImageView>(R.id.activity_layout_img)
            val title = view.findViewById<TextView>(R.id.activity_layout_title)
            val time = view.findViewById<TextView>(R.id.activity_layout_time)
            val number = view.findViewById<TextView>(R.id.activity_layout_number)
            val location = view.findViewById<TextView>(R.id.activity_layout_location)
            val content = view.findViewById<TextView>(R.id.activity_layout_content)
            val pairs = TransitionHelper.createSafeTransitionParticipants(activity, false,
                    Pair(image, getString(R.string.share_user_image)),
                    Pair(title, getString(R.string.share_folk_title)),
                    Pair(time, getString(R.string.share_folk_time)),
                    Pair(number, getString(R.string.share_folk_number)),
                    Pair(location, getString(R.string.share_folk_location)),
                    Pair(content, getString(R.string.share_folk_content))
            )
            val transitionOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, *pairs)
            startActivity(intent, transitionOptions.toBundle())
        }
    }

    fun getInformation() {
        val activity = activity ?: return
        if (!isLoadedData) {
            onPreLoad()
            ThreadPool.execute {
                val datas = HandleFolk.GetChannelInformation(channelName)
                runOnUiThread(Runnable {
                    val adapter = ActivityRecyclerViewAdapter(activity, datas)
                    setAdapterItemClick(adapter)
                    activityRecyclerView.adapter = adapter
                    refreshRefreshViewColor()
                    onPostLoad()
                })
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

    private fun setAdapterItemClick(adapter: ActivityRecyclerViewAdapter) {
        adapter.setOnItemClickListen(object : OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                val activitydata = adapter.getItem(position)
                val intent = Intent(activity, FolkInformationActivity::class.java)
                val bundle = Bundle()
                bundle.putSerializable("activity", activitydata)
                intent.putExtra("image", activitydata.img)
                intent.putExtra("from", ACTIVITY_FRAGMENT)
                intent.putExtras(bundle)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    setTransitionStartActivity(intent, view)
                } else {
                    startActivity(intent)
                }
            }

        })
    }


    override fun onPreLoad() {
        activityRecyclerView.adapter = null
        fragmentActivitySwipeRefresh.isRefreshing = true
    }

    override fun onPostLoad() {
        fragmentActivitySwipeRefresh.isRefreshing = false
        isLoadedData = true
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
