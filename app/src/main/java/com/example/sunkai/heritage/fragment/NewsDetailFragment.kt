package com.example.sunkai.heritage.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.adapter.NewsDetailRecyclerViewAdapter
import com.example.sunkai.heritage.databinding.FragmentNewsDetailBinding
import com.example.sunkai.heritage.entity.NewsDetailViewModel
import com.example.sunkai.heritage.entity.NewsPages
import com.example.sunkai.heritage.entity.response.NewsDetail
import com.example.sunkai.heritage.entity.response.NewsDetailRelativeNews
import com.example.sunkai.heritage.entity.response.NewsListResponse
import com.example.sunkai.heritage.fragment.baseFragment.BaseViewBindingFragment
import com.example.sunkai.heritage.value.API
import com.example.sunkai.heritage.value.DATA
import dagger.hilt.android.AndroidEntryPoint
import java.io.Serializable

@AndroidEntryPoint
class NewsDetailFragment : BaseViewBindingFragment<FragmentNewsDetailBinding>() {

    private var link: String? = null
    private var api: NewsPages? = null
    private val newsDetailViewModel by lazy { ViewModelProvider(this).get(NewsDetailViewModel::class.java) }

    override fun initView() {}

    override fun getBindingClass() = FragmentNewsDetailBinding::class.java

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val data = arguments?.getSerializable(DATA)
        if (data is NewsListResponse) {
            this.link = data.link

        } else if (data is String) {
            this.link = data
        }
        this.api = arguments?.getSerializable(API) as NewsPages?
        val api = this.api
        if (api == null || this.link == null) {
            findNavController().popBackStack()
        }
        newsDetailViewModel.newsDetail.observe(viewLifecycleOwner, { newsDetail ->
            setDataToView(newsDetail)
        })
        newsDetailViewModel.loadNewsDetail(link ?: return, api ?: return)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }



    private fun setDataToView(data: NewsDetail) {
        binding.bottomNewsDetailTitle.text =
            data.title.replace("\r", "").replace("\n", "").replace("\t", "")
        binding.newsDetailSubtitleLayout.removeAllViews()
        data.subtitle?.let { list ->
            list.forEach {
                val textView = TextView(requireContext())
                textView.text = it
                val layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                layoutParams.weight = 1f
                binding.newsDetailSubtitleLayout.addView(textView)
            }
        }
        binding.bottomNewsDetailAuther.text = data.author
        val adapter =
            NewsDetailRecyclerViewAdapter(requireContext(), data.content, glide, data.relativeNews)
        adapter.setOnRelevantNewsClickListner(object :
            NewsDetailRecyclerViewAdapter.onRelevantNewsClick {
            override fun onClick(v: View, news: NewsDetailRelativeNews) {
                val api = api ?:return
                findNavController().navigate(
                    R.id.relative_news_to_news_detail,
                    bundleOf(
                        Pair<String, Serializable>(DATA, NewsListResponse(news)),
                        Pair<String, Serializable>(API, api)
                    )
                )
            }

        })
        binding.bottomNewsDetailRecyclerview.adapter = adapter
    }

}