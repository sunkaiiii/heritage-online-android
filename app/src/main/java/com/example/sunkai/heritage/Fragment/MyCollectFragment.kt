package com.example.sunkai.heritage.Fragment

import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sunkai.heritage.Adapter.BaseAdapter.BaseRecyclerAdapter
import com.example.sunkai.heritage.Fragment.BaseFragment.BaseLazyLoadFragment
import com.example.sunkai.heritage.Interface.OnPageLoaded
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.CreateMyCollectAdapterUtils.CreateMyCollectAdapterFactory
import com.example.sunkai.heritage.tools.runOnUiThread
import com.example.sunkai.heritage.value.*
import kotlinx.android.synthetic.main.my_collect_item.*


/**
 * 我的收藏viewpager每个页面的Fragment
 * Created by sunkai on 2018/3/14.
 */
class MyCollectFragment : BaseLazyLoadFragment(), OnPageLoaded {

    private var typeName: String? = null
    private var index: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val argument = arguments ?: return
        typeName = argument.getString(TYPE_NAME)
        index = argument.getInt(INDEX)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.my_collect_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(savedInstanceState==null) {
            initViews()
        }
    }

    private fun initViews(){
        my_colelct_refresh.setOnRefreshListener {
            startLoadInformation()
        }
    }

    override fun onRestoreFragmentLoadInformation() {
        initViews()
        lazyLoad()
    }

    override fun startLoadInformation() {
        onPreLoad()
        val typeName = typeName ?: return
        when (typeName) {
            TYPE_MAIN, TYPE_FOCUS_HERITAGE, TYPE_FOLK, TYPE_FIND -> getCollectInformation(typeName)
        }
    }

    private fun getCollectInformation(typeName: String) {
        requestHttp {
            val adapter=getCorrespondingMyCollectAdapter(typeName)
            activity?.runOnUiThread{
                onPostLoad()
                if(typeName== TYPE_FOCUS_HERITAGE){
                    my_collect_recyclerview.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
                }
                my_collect_recyclerview.adapter = adapter
            }
        }
    }

    private fun getCorrespondingMyCollectAdapter(typeName: String):BaseRecyclerAdapter<*,*>?{
        return CreateMyCollectAdapterFactory.createCorrespondingAdapter(activity?:return null,glide,typeName)
    }

    override fun onPreLoad() {
        my_colelct_refresh.isRefreshing = true
        my_collect_recyclerview.adapter = null
    }

    override fun onPostLoad() {
        my_colelct_refresh?.isRefreshing = false
    }

    companion object {
        private const val TYPE_NAME = "channel"
        private const val INDEX = "index"

        //创建一个此instance的实例，传同样需要传入TypeName
        fun newInstance(channelName: String, index: Int): MyCollectFragment {
            val fragment = MyCollectFragment()
            val args = Bundle()
            args.putString(TYPE_NAME, channelName)
            args.putInt(INDEX, index)
            fragment.arguments = args
            return fragment
        }
    }
}