package com.example.sunkai.heritage.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
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
import com.example.sunkai.heritage.tools.loadImageFromServer
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
//        setCollectionAction()
    }


    private fun setDataToView(data: NewsDetail) {
        val imgUrl = data.compressImg ?: data.img
        if (imgUrl != null) {
            glide.loadImageFromServer(imgUrl).into(binding.newsDetailMainImage)
        }
        binding.bottomNewsDetailTitle.text =
            data.title.replace("\r", "").replace("\n", "").replace("\t", "")
        binding.newsDetailSubContent.text = data.subContent
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
            binding.newsDetailSubtitleLayout.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW).setData(Uri.parse(getString(R.string.source_url)+link))
                startActivity(intent)
            }
        }
        binding.bottomNewsDetailAuther.text = data.author
        val adapter =
            NewsDetailRecyclerViewAdapter(data.content, glide, data.relativeNews)
        adapter.setOnRelevantNewsClickListner(object :
            NewsDetailRecyclerViewAdapter.onRelevantNewsClick {
            override fun onClick(v: View, news: NewsDetailRelativeNews) {
                val api = api ?: return
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


//    fun setCollectionAction(){
//        val item = binding.toolbar.menu.findItem(R.id.collect)
//        newsDetailViewModel.isCollected.observe(viewLifecycleOwner, {
//            item.icon = if (it) ContextCompat.getDrawable(
//                requireContext(),
//                R.drawable.outline_bookmark_black_36
//            ) else ContextCompat.getDrawable(requireContext(), R.drawable.outline_bookmark_border_black_36)
//        })
//        item.setOnMenuItemClickListener {
//            val isCollected = newsDetailViewModel.isCollected.value?:false
//            if(isCollected){
//                newsDetailViewModel.deleteCollection()
//            }else{
//                val newsDetail = newsDetailViewModel.newsDetail.value ?: return@setOnMenuItemClickListener true
//                newsDetailViewModel.addCollection(newsDetail.link,newsDetail.title,newsDetail.compressImg)
//            }
//            return@setOnMenuItemClickListener true
//        }
//    }

}
