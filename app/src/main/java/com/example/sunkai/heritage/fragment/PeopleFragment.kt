package com.example.sunkai.heritage.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ProgressBar
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.adapter.FragmentPeopleBannerAdapter
import com.example.sunkai.heritage.adapter.PeopleFragmentListAdapter
import com.example.sunkai.heritage.entity.PeoplePageViewModel
import com.example.sunkai.heritage.fragment.baseFragment.BaseGlideFragment
import com.example.sunkai.heritage.tools.BaseOnPageChangeListener
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_people.*
import kotlinx.android.synthetic.main.news_list_framgent.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.math.abs

@AndroidEntryPoint
class PeopleFragment : BaseGlideFragment() {

    private val peopleViewModel by lazy { ViewModelProvider(this).get(PeoplePageViewModel::class.java) }

    private lateinit var fragmentPeopleAppBarLayout: AppBarLayout
    private lateinit var peopleFragmentRecyclerView: RecyclerView
    private lateinit var fragmentPeopleViewpager: ViewPager
    private lateinit var fragmentPeopleCollapsingToolbarLayout: CollapsingToolbarLayout
    private lateinit var peopleLoadingProgressBar: ProgressBar
    private lateinit var peopleMainPage: View

    private val viewPageScrollHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            if (msg.what == SCROLL) {
                fragmentPeopleViewpager.currentItem++
                startViewpagerScrollDelay()
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_people, container, false)
        fragmentPeopleAppBarLayout = view.findViewById(R.id.fragmentPeopleAppBarLayout)
        peopleFragmentRecyclerView = view.findViewById(R.id.peopleFragmentRecyclerView)
        fragmentPeopleViewpager = view.findViewById(R.id.fragmentPeopleViewpager)
        peopleLoadingProgressBar = view.findViewById(R.id.peopleLoadingProgressBar)
        fragmentPeopleCollapsingToolbarLayout =
            view.findViewById(R.id.fragmentPeopleCollapsingToolbarLayout)
        peopleMainPage = view.findViewById(R.id.peopleMainPage)
        initviews(view)
        peopleViewModel.peopleTopBanner.observe(viewLifecycleOwner, { data ->
            val adapter = FragmentPeopleBannerAdapter(context ?: return@observe, data.table, glide)
            val middleItem = data.table.size * 2000
            fragmentPeopleViewpager.adapter = adapter
            fragmentPeopleViewpager.addOnPageChangeListener(object : BaseOnPageChangeListener() {
                override fun onPageSelected(position: Int) {
                    startViewpagerScrollDelay()
                }
            })
            fragmentPeopleViewpager.currentItem = middleItem
        })
        val adapter = PeopleFragmentListAdapter(glide)
        peopleFragmentRecyclerView.adapter = adapter
        lifecycleScope.launch {
            peopleViewModel.peopleList().collect {
                peopleLoadingProgressBar.visibility = View.GONE
                peopleMainPage.visibility = View.VISIBLE
                adapter.submitData(it)
            }
        }
        return view
    }

    private fun initviews(view: View) {
        view.viewTreeObserver?.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                view.viewTreeObserver?.removeOnGlobalLayoutListener(this)
                val layoutparams = fragmentPeopleCollapsingToolbarLayout.layoutParams
                layoutparams.height = fragmentPeopleCollapsingToolbarLayout.width
                fragmentPeopleCollapsingToolbarLayout.layoutParams = layoutparams
            }
        })

        fragmentPeopleAppBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
            val scrollRange = fragmentPeopleAppBarLayout.totalScrollRange
            val alpha = abs(verticalOffset.toFloat() / scrollRange)
            fragmentPeopleTopTitleBackground.alpha = alpha * alpha * alpha
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
}