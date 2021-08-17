package com.example.sunkai.heritage.fragment

import android.content.Intent
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.example.sunkai.heritage.adapter.NewsDetailRecyclerViewAdapter
import com.example.sunkai.heritage.databinding.FragmentPeopleDetailBinding
import com.example.sunkai.heritage.entity.PeopleDetailViewModel
import com.example.sunkai.heritage.entity.response.NewsDetail
import com.example.sunkai.heritage.entity.response.NewsDetailRelativeNews
import com.example.sunkai.heritage.fragment.baseFragment.BaseViewBindingFragment
import com.example.sunkai.heritage.value.DATA
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PeopleDetailFragment : BaseViewBindingFragment<FragmentPeopleDetailBinding>() {
    private val viewModel by lazy { ViewModelProvider(this).get(PeopleDetailViewModel::class.java) }

    override fun getBindingClass(): Class<FragmentPeopleDetailBinding> =
        FragmentPeopleDetailBinding::class.java

    override fun initView() {
        viewModel.peopleDetail.observe(viewLifecycleOwner, {
            setDataToView(it)
        })
        val link = arguments?.getString(DATA)
        link?.let {
            binding.root.post {
                viewModel.setLink(it)
            }
        }
    }

    private fun setDataToView(data: NewsDetail) {
        binding.peopleTitle.text =
            data.title.replace("\r", "").replace("\n", "").replace("\t", "")
//        binding.newsDetailSubtitleLayout.removeAllViews()
//        data.subtitle?.let { list ->
//            list.forEach {
//                val textView = TextView(requireContext())
//                textView.text = it
//                val layoutParams = LinearLayout.LayoutParams(
//                    ViewGroup.LayoutParams.WRAP_CONTENT,
//                    ViewGroup.LayoutParams.WRAP_CONTENT
//                )
//                layoutParams.weight = 1f
//                binding.newsDetailSubtitleLayout.addView(textView)
//            }
//        }
        binding.peopleDetailAuthor.text = data.author
        val adapter =
            NewsDetailRecyclerViewAdapter(requireContext(), data.content, glide, data.relativeNews)
        adapter.setOnRelevantNewsClickListner(object :
            NewsDetailRecyclerViewAdapter.onRelevantNewsClick {
            override fun onClick(v: View, news: NewsDetailRelativeNews) {
                val intent = Intent()
                intent.putExtra(DATA, news.link)
                startActivity(intent)
            }

        })
        binding.peopleDetailList.adapter = adapter
    }
}