package com.example.sunkai.heritage.fragment

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.adapter.FragmentPeopleBannerAdapter
import com.example.sunkai.heritage.adapter.PeopleFragmentListAdapter
import com.example.sunkai.heritage.databinding.FragmentPeopleBinding
import com.example.sunkai.heritage.entity.PeoplePageViewModel
import com.example.sunkai.heritage.entity.response.NewsListResponse
import com.example.sunkai.heritage.fragment.baseFragment.BaseViewBindingFragment
import com.example.sunkai.heritage.value.DATA
import com.google.android.material.appbar.AppBarLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.math.abs

@AndroidEntryPoint
class PeopleFragment : BaseViewBindingFragment<FragmentPeopleBinding>() {

    private val peopleViewModel by lazy { ViewModelProvider(this).get(PeoplePageViewModel::class.java) }
    private val viewPageScrollHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            if (msg.what == SCROLL) {
                binding.fragmentPeopleViewpager.currentItem++
                startViewpagerScrollDelay()
            }
        }
    }


    override fun initView() {
        binding.fragmentPeopleAppBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
            val scrollRange = binding.fragmentPeopleAppBarLayout.totalScrollRange
            val alpha = abs(verticalOffset.toFloat() / scrollRange)
            binding.fragmentPeopleTopTitleBackground.alpha = alpha * alpha * alpha
        })
        peopleViewModel.peopleTopBanner.observe(viewLifecycleOwner, { data ->
            val adapter = FragmentPeopleBannerAdapter(data.table, glide)
            val middleItem = data.table.size * 2000
            binding.fragmentPeopleViewpager.adapter = adapter
            binding.fragmentPeopleViewpager.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    startViewpagerScrollDelay()
                }
            })
            binding.fragmentPeopleViewpager.currentItem = middleItem
            adapter.setBannerClickListener { _, people ->
                findNavController().navigate(
                    R.id.action_people_list_to_detail,
                    bundleOf(DATA to people.link)
                )
            }
        })
        val adapter = PeopleFragmentListAdapter(glide)
        binding.peopleFragmentRecyclerView.adapter = adapter
        peopleViewModel.peopleList.observe(viewLifecycleOwner) {
            binding.peopleLoadingProgressBar.visibility = View.GONE
            binding.peopleMainPage.visibility = View.VISIBLE
            lifecycleScope.launch {
                adapter.submitData(it)
            }
        }
        adapter.setOnItemClickListener(object : PeopleFragmentListAdapter.PeopleClickListner {
            override fun onItemClick(itemView: View, item: NewsListResponse) {
                val bundle = bundleOf(DATA to item.link)
                findNavController().navigate(R.id.action_people_list_to_detail, bundle)
            }

        })
    }


    private fun startViewpagerScrollDelay() {
        viewPageScrollHandler.removeMessages(SCROLL)
        viewPageScrollHandler.sendEmptyMessageDelayed(SCROLL, DELAY)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        viewPageScrollHandler.removeMessages(SCROLL)
    }

    companion object {
        const val SCROLL = 1
        const val DELAY = 3000L
        const val PAGE = "page"
    }

    override fun getBindingClass()= FragmentPeopleBinding::class.java

}