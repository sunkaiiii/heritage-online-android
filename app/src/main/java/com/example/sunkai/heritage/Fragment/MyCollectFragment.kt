package com.example.sunkai.heritage.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sunkai.heritage.Activity.LoginActivity.LoginActivity
import com.example.sunkai.heritage.Adapter.BottomFolkNewsRecyclerviewAdapter
import com.example.sunkai.heritage.Adapter.FindFragmentRecyclerViewAdapter
import com.example.sunkai.heritage.Adapter.FolkRecyclerViewAdapter
import com.example.sunkai.heritage.Adapter.SeeMoreNewsRecyclerViewAdapter
import com.example.sunkai.heritage.ConnectWebService.HandlePerson
import com.example.sunkai.heritage.Data.BottomFolkNewsLite
import com.example.sunkai.heritage.Data.FolkDataLite
import com.example.sunkai.heritage.Data.FolkNewsLite
import com.example.sunkai.heritage.Data.UserCommentData
import com.example.sunkai.heritage.Interface.OnPageLoaded
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.ThreadPool
import com.example.sunkai.heritage.tools.runOnUiThread
import com.example.sunkai.heritage.value.*
import kotlinx.android.synthetic.main.my_collect_item.*
import java.io.Serializable


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
        my_colelct_refresh.setOnRefreshListener {
            startLoadInformation()
        }
    }

    override fun startLoadInformation() {
        onPreLoad()
        val typeName = typeName ?: return
        when (typeName) {
            TYPE_MAIN, TYPE_FOCUS_HERITAGE, TYPE_FOLK, TYPE_FIND -> {
                getCollectInformation(typeName)
            }
        }
    }

    private fun getCollectInformation(typeName: String) {
        ThreadPool.execute {
            val result = when (typeName) {
                TYPE_MAIN -> HandlePerson.GetMainCollection(LoginActivity.userID, typeName)
                TYPE_FOCUS_HERITAGE -> HandlePerson.GetFocusOnHeritageCollection(LoginActivity.userID, typeName)
                TYPE_FOLK -> HandlePerson.GetFolkColelction(LoginActivity.userID, typeName)
                TYPE_FIND -> HandlePerson.GetFindCollection(LoginActivity.userID, typeName)
                else -> return@execute
            }
            runOnUiThread(Runnable {
                onPostLoad()
                handleColelction(result, typeName)
            })
        }
    }

    private fun handleColelction(result: List<Serializable>, typeName: String) {
        when (typeName) {
            TYPE_MAIN -> handleMainCollection(result.map { it -> it as FolkNewsLite }.toList())
            TYPE_FOCUS_HERITAGE -> handleFocusOnHeritage(result.map { it -> it as BottomFolkNewsLite }.toList())
            TYPE_FOLK -> handleFolkCollection(result.map { it -> it as FolkDataLite }.toList())
            TYPE_FIND -> handleFindCollection(result.map { it -> it as UserCommentData }.toList())
        }
    }

    private fun handleMainCollection(result: List<FolkNewsLite>) {
        val activity = activity ?: return
        val adapter = SeeMoreNewsRecyclerViewAdapter(activity, result)
        my_collect_recyclerview.adapter = adapter
    }

    private fun handleFocusOnHeritage(result: List<BottomFolkNewsLite>) {
        val activity = activity ?: return
        val adaper = BottomFolkNewsRecyclerviewAdapter(activity, result)
        my_collect_recyclerview.adapter = adaper
    }

    private fun handleFolkCollection(result: List<FolkDataLite>) {
        val activity = activity ?: return
        val adapter = FolkRecyclerViewAdapter(activity, result)
        my_collect_recyclerview.adapter = adapter

    }

    private fun handleFindCollection(result: List<UserCommentData>) {
        val activity = activity ?: return
        val adapter = FindFragmentRecyclerViewAdapter(activity, result, MY_FOCUS_COMMENT)
        my_collect_recyclerview.adapter = adapter
    }

    override fun onPreLoad() {
        my_colelct_refresh.isRefreshing = true
        my_collect_recyclerview.adapter = null
    }

    override fun onPostLoad() {
        my_colelct_refresh.isRefreshing = false
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