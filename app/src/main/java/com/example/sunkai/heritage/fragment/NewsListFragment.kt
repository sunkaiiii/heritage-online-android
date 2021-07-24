package com.example.sunkai.heritage.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.adapter.NewsListAdapter
import com.example.sunkai.heritage.databinding.NewsListFramgentBinding
import com.example.sunkai.heritage.entity.NewsListViewModel
import com.example.sunkai.heritage.entity.NewsPages
import com.example.sunkai.heritage.fragment.baseFragment.BaseGlideFragment
import com.example.sunkai.heritage.fragment.baseFragment.BaseViewBindingFragment
import com.example.sunkai.heritage.value.API
import com.example.sunkai.heritage.value.DATA
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.Serializable

@AndroidEntryPoint
class NewsListFragment : BaseViewBindingFragment<NewsListFramgentBinding>() {

    var reqeustArgument: NewsPages? = null
    private val viewModel by lazy { ViewModelProvider(this).get(NewsListViewModel::class.java) }

    override fun getBindingClass() = NewsListFramgentBinding::class.java

    override fun initView() {
        reqeustArgument = arguments?.getSerializable(MainFragment.PAGE) as NewsPages

        val adapter = NewsListAdapter(glide)

        binding.fragmentMainRecyclerview.adapter = adapter
        viewModel.newsListPagingData.observe(viewLifecycleOwner, { data ->
            lifecycleScope.launch {
                adapter.submitData(data)
            }
        })
        reqeustArgument?.let {
            adapter.setOnNewsItemClickListener { item ->
                findNavController().navigate(
                    R.id.main_fragment_to_news_detail,
                    bundleOf(
                        Pair<String, Serializable>(DATA, item),
                        Pair<String, Serializable>(API, it)
                    )
                )
            }
            binding.root.post {
                viewModel.setListCaller(it.reqeustApi)
            }

        }
    }
}