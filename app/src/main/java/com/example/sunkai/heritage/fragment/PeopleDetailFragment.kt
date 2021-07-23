package com.example.sunkai.heritage.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.adapter.NewsDetailRecyclerViewAdapter
import com.example.sunkai.heritage.entity.PeopleDetailViewModel
import com.example.sunkai.heritage.entity.response.NewsDetail
import com.example.sunkai.heritage.entity.response.NewsDetailRelativeNews
import com.example.sunkai.heritage.fragment.baseFragment.BaseGlideFragment
import com.example.sunkai.heritage.value.DATA
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_news_detail.*

@AndroidEntryPoint
class PeopleDetailFragment:BaseGlideFragment() {
    private val viewModel by lazy { ViewModelProvider(this).get(PeopleDetailViewModel::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_news_detail, container, false)
        viewModel.peopleDetail.observe(viewLifecycleOwner,{
            setDataToView(it)
        })
        val link = arguments?.getString(DATA)
        link?.let {
            view.post {
                viewModel.setLink(it)
            }
        }
        return view
    }


    private fun setDataToView(data: NewsDetail) {
        bottomNewsDetailTitle.text = data.title.replace("\r", "").replace("\n", "").replace("\t", "")
        newsDetailSubtitleLayout.removeAllViews()
        data.subtitle?.let { list ->
            list.forEach {
                val textView = TextView(requireContext())
                textView.text = it
                val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                layoutParams.weight = 1f
                newsDetailSubtitleLayout.addView(textView)
            }
        }
        bottomNewsDetailAuther.text = data.author
        val adapter = NewsDetailRecyclerViewAdapter(requireContext(), data.content, glide, data.relativeNews)
        adapter.setOnRelevantNewsClickListner(object : NewsDetailRecyclerViewAdapter.onRelevantNewsClick {
            override fun onClick(v: View, news: NewsDetailRelativeNews) {
                val intent = Intent()
                intent.putExtra(DATA, news.link)
                startActivity(intent)
            }

        })
        bottomNewsDetailRecyclerview.adapter = adapter
    }
}