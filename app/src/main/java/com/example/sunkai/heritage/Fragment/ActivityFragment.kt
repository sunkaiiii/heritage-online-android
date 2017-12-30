package com.example.sunkai.heritage.Fragment

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

import com.example.sunkai.heritage.Activity.ActivityInformationActivity
import com.example.sunkai.heritage.Adapter.ActivityRecyclerViewAdpter
import com.example.sunkai.heritage.Data.GlobalContext
import com.example.sunkai.heritage.Interface.OnItemClickListener
import com.example.sunkai.heritage.R

import java.io.ByteArrayOutputStream


/**
 * 首页viewpager五个页面的第一个页面
 * 理论上五个页面其实可以公用一个Fragment类，通过传递的参数不同执行不同的方法即可
 * 但是因为时间紧张，故用了5个Fragment类
 * 除了第一个页面，其他四个全都继承与BaseFrament，并实现了Lazyload，使其在页面可见的时候才加载内容
 */
class ActivityFragment : Fragment() {
    internal lateinit var activityListviewAdapter: ActivityRecyclerViewAdpter
    internal lateinit var activityRecyclerView: RecyclerView
    private var channelName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        channelName = arguments?.getString(channel)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_activity1, container, false)
        activityRecyclerView = view.findViewById(R.id.activity_listview)
        activityListviewAdapter = ActivityRecyclerViewAdpter(activity, channelName)
        val layoutManager = LinearLayoutManager(context)
        activityRecyclerView.layoutManager = layoutManager
        activityRecyclerView.addItemDecoration(DividerItemDecoration(GlobalContext.instance, DividerItemDecoration.VERTICAL))
        activityRecyclerView.setHasFixedSize(true)
        activityRecyclerView.adapter = activityListviewAdapter
        activityListviewAdapter.setOnItemClickListen(object:OnItemClickListener{
            override fun onItemClick(view: View, position: Int) {
                val activitydata = activityListviewAdapter.getItem(position)
                val imageView = view.findViewById<ImageView>(R.id.activity_layout_img)
                imageView.isDrawingCacheEnabled = true
                val bitmap = imageView.drawingCache
                val byteArrayOutputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
                activitydata.activityImage = byteArrayOutputStream.toByteArray()
                val intent = Intent(activity, ActivityInformationActivity::class.java)
                val bundle = Bundle()
                bundle.putSerializable("activity", activitydata)
                intent.putExtras(bundle)
                startActivity(intent)
            }
        })
        return view
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
