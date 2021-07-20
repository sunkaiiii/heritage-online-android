package com.example.sunkai.heritage.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
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
    var reqeustArgument: MainFragment.NewsPages? = null
    private val viewModel by lazy{ViewModelProvider(this).get(NewsListViewModel::class.java)}

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.news_list_framgent, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadInformation()
    }




    private fun loadInformation() {
        reqeustArgument = arguments?.getSerializable(MainFragment.PAGE) as MainFragment.NewsPages
        val adapter = NewsListAdapter(glide, reqeustArgument ?: return)
        fragmentMainRecyclerview.adapter = adapter
        lifecycleScope.launch {
            viewModel.getNewsListPagingData(reqeustArgument?.reqeustApi?:return@launch).collect {
                adapter.submitData(it)
            }
        }
    }


}