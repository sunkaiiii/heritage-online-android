package com.example.sunkai.heritage.fragment

import android.graphics.Color
import android.graphics.RenderEffect
import android.graphics.Shader
import android.graphics.drawable.ColorDrawable
import android.os.*
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.activity.MainActivity
import com.example.sunkai.heritage.adapter.MainPageBannerAdapter
import com.example.sunkai.heritage.adapter.MainPageViewPagerAdapter
import com.example.sunkai.heritage.databinding.FragmentMainBinding
import com.example.sunkai.heritage.entity.MainPageBanner
import com.example.sunkai.heritage.entity.MainPageViewModel
import com.example.sunkai.heritage.fragment.baseFragment.BaseViewBindingFragment
import com.example.sunkai.heritage.tools.Utils.dip2px
import com.example.sunkai.heritage.tools.Utils.getColorResource
import com.example.sunkai.heritage.value.MAIN_PAGE_TABLAYOUT_TEXT
import com.example.sunkai.heritage.views.CollaborativeBounceView
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint


/**
 * 首页
 */
@AndroidEntryPoint
class MainFragment : BaseViewBindingFragment<FragmentMainBinding>() {

    private val viewModel by lazy { ViewModelProvider(requireActivity()).get(MainPageViewModel::class.java) }

    override fun initView() {
        viewModel.banner.observe(viewLifecycleOwner, {
            setMainPageSlideAdapter(it)
        })
        setOnMenuButtonClicked()
        setNewsListScrollBehaviour()
    }

    private fun setNewsListScrollBehaviour() {
        val minBoundry = 50.dip2px()
        val maxBoundry = 250.dip2px()
        val minRadius = 0.dip2px()
        val maxRadius = 12.dip2px()
        binding.newsListContainer.maxCardElevation = maxRadius.toFloat()
        binding.newsListContainer.setBounceBoundry(minBoundry,maxBoundry)
        binding.newsListContainer.setMoveEventBlocker { event, orientation ->
            if(orientation == CollaborativeBounceView.MoveOrientation.Up){
                return@setMoveEventBlocker false
            }
            val linearLayoutManager = binding.mainPageViewPager[0].findViewById<RecyclerView>(R.id.fragmentMainRecyclerview).layoutManager as? LinearLayoutManager
            val position = linearLayoutManager?.findFirstCompletelyVisibleItemPosition() ?: return@setMoveEventBlocker false
            return@setMoveEventBlocker position!=0
        }
        val drawable = ColorDrawable(getColorResource(R.color.black))
        binding.newsListContainer.setOnMoveAction { distance, offsetPercentage ->
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
                val blurRadius = minRadius + (maxRadius - minRadius) * offsetPercentage
                binding.mainPageSlideViewpager.setRenderEffect(
                        if (offsetPercentage == 0f) null else RenderEffect.createBlurEffect(
                                blurRadius, blurRadius,
                                Shader.TileMode.CLAMP
                ))
            }else{
                val maxAlpha = 175
                drawable.alpha = (maxAlpha * offsetPercentage).toInt()
                binding.mainPageSlideViewpager.foreground = drawable
            }
        }
    }


    override fun getBindingClass(): Class<FragmentMainBinding> = FragmentMainBinding::class.java


    override fun setNeedChangeThemeColorWidget() {
        changeThemeWidge.add(R.id.mainPageTabLayout)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewPager()
    }

    override fun onStart() {
        super.onStart()
        val activity = activity ?: return
        if (activity is AppCompatActivity) {
            activity.supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    private fun setViewPager() {
        val adapter = MainPageViewPagerAdapter(this)
        binding.mainPageViewPager.adapter = adapter
        TabLayoutMediator(binding.mainPageTabLayout, binding.mainPageViewPager) { tab, position ->
            tab.text = MAIN_PAGE_TABLAYOUT_TEXT[position]
        }.attach()
    }


    private fun setMainPageSlideAdapter(data: List<MainPageBanner>) {
        val activity = activity ?: return
        val adapter = MainPageBannerAdapter(activity, data, glide)
        binding.mainPageSlideViewpager.adapter = adapter
        //让他在初始的时候在中间的位置，且保证是第一个页面，可以做到左翻页
        val middleItem = 0 + 4 * 200
        binding.mainPageSlideViewpager.setCurrentItem(middleItem, false)
        binding.mainPageSlideViewpager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                startRecyclerScroll()
            }
        })
        startRecyclerScroll()
    }

    //执行自动翻页
    private fun startRecyclerScroll() {
        viewpagerRecyclerScrollHandler.removeMessages(SCROLL)
        viewpagerRecyclerScrollHandler.sendEmptyMessageDelayed(SCROLL, DELAY)
    }

    private val viewpagerRecyclerScrollHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            if (msg.what == SCROLL) {
                binding.mainPageSlideViewpager.currentItem =
                    binding.mainPageSlideViewpager.currentItem + 1
                startRecyclerScroll()
            }
        }
    }

    private fun setOnMenuButtonClicked() {
        binding.menuImage.setOnClickListener {
            val activity = requireActivity()
            if (activity is MainActivity) {
                activity.showNavigationDrawerLayout()
            }
        }
        binding.newsSearch.setOnClickListener {
            findNavController().navigate(R.id.main_fragment_to_search_news)
        }
    }

    override fun onResume() {
        super.onResume()
        viewpagerRecyclerScrollHandler.sendEmptyMessageDelayed(SCROLL, DELAY)
    }

    //view被暂停销毁之后，清除handler里面所有的信息
    override fun onPause() {
        super.onPause()
        viewpagerRecyclerScrollHandler.removeMessages(SCROLL)
    }


    companion object {
        const val SCROLL = 1
        const val DELAY = 3000L
        const val PAGE = "page"
    }

    interface MenuToggleClickListener {
        fun onClick(view: View)
    }
}

