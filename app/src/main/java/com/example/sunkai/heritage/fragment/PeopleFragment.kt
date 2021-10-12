package com.example.sunkai.heritage.fragment

import android.graphics.RenderEffect
import android.graphics.Shader
import android.graphics.drawable.ColorDrawable
import android.os.*
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.adapter.FragmentPeopleBannerAdapter
import com.example.sunkai.heritage.adapter.PeopleFragmentListAdapter
import com.example.sunkai.heritage.databinding.FragmentPeopleBinding
import com.example.sunkai.heritage.entity.CollaborativeViewModelImpl
import com.example.sunkai.heritage.entity.PeoplePageViewModel
import com.example.sunkai.heritage.entity.response.NewsListResponse
import com.example.sunkai.heritage.fragment.baseFragment.BaseViewBindingFragment
import com.example.sunkai.heritage.tools.Utils
import com.example.sunkai.heritage.tools.Utils.dip2px
import com.example.sunkai.heritage.value.DATA
import com.example.sunkai.heritage.views.CollaborativeBounceView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PeopleFragment : BaseViewBindingFragment<FragmentPeopleBinding>() {

    private val peopleViewModel by lazy {
        ViewModelProvider(requireActivity()).get(
            PeoplePageViewModel::class.java
        )
    }
    private val collaborativeViewModel by lazy { ViewModelProvider(this).get(CollaborativeViewModelImpl::class.java) }
    private val viewPageScrollHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            if (msg.what == SCROLL) {
                binding.fragmentPeopleViewpager.currentItem++
                startViewpagerScrollDelay()
            }
        }
    }


    override fun initView() {
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
        binding.peopleFragmentRecyclerView.layoutManager =
            StaggeredGridLayoutManager(2, GridLayoutManager.VERTICAL)
        binding.peopleFragmentRecyclerView.adapter = adapter
        peopleViewModel.peopleList.observe(viewLifecycleOwner) {
            binding.peopleLoadingProgressBar.visibility = View.GONE
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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.peopleContainer.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.peopleContainer.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val maxBoundry = binding.peopleContainer.height - 97.dip2px().toFloat()
                binding.peopleContainer.translationY =
                    maxBoundry
                val minBoundry = 50.dip2px()
                val minRadius = 0.dip2px()
                val maxRadius = 12.dip2px()
                val drawable = ColorDrawable(Utils.getColorResource(R.color.black))
                binding.peopleContainer.setBounceBoundry(minBoundry, maxBoundry.toInt(),collaborativeViewModel)
                binding.peopleContainer.setMoveEventBlocker { event, moveOrientation ->
                    if (moveOrientation == CollaborativeBounceView.MoveOrientation.Up || binding.peopleContainer.isClickOutRecyclerView()) {
                        return@setMoveEventBlocker false
                    }
                    val gridLayoutManager =
                        binding.peopleFragmentRecyclerView.layoutManager as? StaggeredGridLayoutManager
                            ?: return@setMoveEventBlocker false
                    val array = gridLayoutManager.findFirstCompletelyVisibleItemPositions(
                        IntArray(gridLayoutManager.spanCount)
                    )
                    return@setMoveEventBlocker array[0] != 0
                }
                binding.peopleContainer.setOnMoveAction { distance, offsetPercentage ->
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        val bannerBlurRadius =
                            minRadius + (maxRadius - minRadius) * offsetPercentage
                        binding.fragmentPeopleViewpager.setRenderEffect(
                            if (offsetPercentage == 0f) null else RenderEffect.createBlurEffect(
                                bannerBlurRadius, bannerBlurRadius,
                                Shader.TileMode.CLAMP
                            )
                        )
                    } else {
                        val maxAlpha = 175
                        drawable.alpha = (maxAlpha * offsetPercentage).toInt()
                        binding.fragmentPeopleViewpager.foreground = drawable
                    }
                }
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

    override fun getBindingClass() = FragmentPeopleBinding::class.java

}