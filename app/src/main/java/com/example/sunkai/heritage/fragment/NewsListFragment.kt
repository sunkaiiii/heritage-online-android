package com.example.sunkai.heritage.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.adapter.NewsListAdapter
import com.example.sunkai.heritage.entity.NewsListViewModel
import com.example.sunkai.heritage.entity.response.NewsListResponse
import com.example.sunkai.heritage.fragment.baseFragment.BaseGlideFragment
import com.example.sunkai.heritage.interfaces.OnPageLoaded
import com.example.sunkai.heritage.tools.EHeritageApplication
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.news_list_framgent.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NewsListFragment : BaseGlideFragment(){
    lateinit var recycletView:RecyclerView
    var reqeustArgument: MainFragment.NewsPages? = null
    private val viewModel by lazy{ViewModelProvider(this).get(NewsListViewModel::class.java)}


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.news_list_framgent, container, false)
        reqeustArgument = arguments?.getSerializable(MainFragment.PAGE) as MainFragment.NewsPages
        recycletView = view.findViewById(R.id.fragmentMainRecyclerview)
        val adapter = NewsListAdapter(glide, reqeustArgument ?: return null)
        recycletView.adapter = adapter
        viewModel.newsListPagingData.observe(viewLifecycleOwner,{data->
            lifecycleScope.launch {
                adapter.submitData(data)
            }
        })
        reqeustArgument?.let {
            view.post {
                viewModel.setListCaller(it.reqeustApi)
            }

        }
        return view
    }
}