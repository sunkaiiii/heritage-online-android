package com.example.sunkai.heritage.Fragment

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.*
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition

import com.example.sunkai.heritage.ConnectWebService.HandleMainFragment
import com.example.sunkai.heritage.Data.*
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.generateColor
import com.example.sunkai.heritage.tools.generateTextColor
import com.example.sunkai.heritage.value.HOST
import kotlinx.android.synthetic.main.fragment_main.*

import java.util.ArrayList


class MainFragment : Fragment() {

    internal lateinit var view: View
    internal lateinit var tableLayout: TabLayout
    private lateinit var viewPager: ViewPager

    private var urls: List<ActivityData>? = null

    var index = 0


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_main, container, false)
        //活动主页分类图片数据
        getMainFragmentDivideImageUrl()
        //此处是活动分类
        tableLayout = view.findViewById(R.id.tab_layout)
        viewPager = view.findViewById(R.id.main_tab_content)
        initViews()


        return view
    }

    private fun initViews() {
        setupViewPager(viewPager)
        tableLayout.setupWithViewPager(viewPager)
        tableLayout.setTabTextColors(Color.GRAY, Color.WHITE)
        for (i in 0 until ClassifyActivityDivide.divide.size) {
            tableLayout.getTabAt(i)?.text = ClassifyActivityDivide.divide[i]
        }
        tableLayout.addOnTabSelectedListener(tabLayoutListener)
        tableLayout.getTabAt(0)!!.select()
    }


    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = ViewPagerAdapter(activity!!.supportFragmentManager)
        //给viewpager添加Fragment，以并传输通道名以显示对应通道的内容,并传入对应的viewpager当中的index
        for ((count, channelName) in ClassifyActivityDivide.divide.withIndex()) {
            adapter.insertNewFragment(ActivityFragment.newInstance(channelName, count))
        }

        //懒加载处理，将网络请求延迟到滚动到对应位置的时候再加载
        viewPager.addOnPageChangeListener(object :ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                val frament=adapter.getItem(position)
                if(frament is ActivityFragment){
                    frament.getInformation()
                }
            }

        })
        viewPager.adapter = adapter
    }

    private fun getMainFragmentDivideImageUrl() {
        Thread {
            urls = HandleMainFragment.Get_Main_Divide_Activity_Image_Url()
            activity?.runOnUiThread {
                getDivideImage(tableLayout.selectedTabPosition)
            }
        }.start()
    }

    internal inner class ViewPagerAdapter(manager: android.support.v4.app.FragmentManager) : FragmentPagerAdapter(manager) {
        private val mFragmentList = ArrayList<android.support.v4.app.Fragment>()

        override fun getItem(position: Int): android.support.v4.app.Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        }

        fun insertNewFragment(fragment: android.support.v4.app.Fragment) {
            mFragmentList.add(fragment)
        }

    }

    private fun getDivideImage(index: Int) {
        urls?.let {
            val url = urls!![index].url
            Glide.with(context!!).load(HOST + url).into(simpleTarget)
        }
    }

    private val tabLayoutListener = object : TabLayout.OnTabSelectedListener {
        override fun onTabReselected(tab: TabLayout.Tab?) {}

        override fun onTabUnselected(tab: TabLayout.Tab?) {}

        override fun onTabSelected(tab: TabLayout.Tab?) {
            index = tab?.position!!
            getDivideImage(tab.position)
        }

    }

    private fun setColors(color:Int,resource:Drawable){
        val outAnimation = AnimationUtils.loadAnimation(activity!!, R.anim.fade_out_quick)
        val secondInAnimation = AnimationUtils.loadAnimation(activity, R.anim.fade_in_quick)
        outAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                iv_fragment_main_scroll_change_image.startAnimation(secondInAnimation)
                iv_fragment_main_scroll_change_image.setImageDrawable(resource)
            }

            override fun onAnimationStart(animation: Animation?) {}

        })
        iv_fragment_main_scroll_change_image.startAnimation(outAnimation)
        fragment_main_collapsing_toolbar_layout.setContentScrimColor(color)
        fragment_main_collapsing_toolbar_layout.setBackgroundColor(color)
        iv_fragment_main_scroll_change_image.setBackgroundColor(color)
        tableLayout.setBackgroundColor(color)
        if (Build.VERSION.SDK_INT >= 21) {
            activity?.window?.statusBarColor = color
        }
    }

    fun getStatusBarShouldChangeColor():Int{
        val drawable=iv_fragment_main_scroll_change_image.drawable
        return generateColor(drawable)
    }

    private val simpleTarget: SimpleTarget<Drawable> by lazy {
        object : SimpleTarget<Drawable>() {
            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                if (index == tableLayout.selectedTabPosition) {
                    val color = generateColor(resource)
                    val textColor = generateTextColor(resource)
                    textColor?.let {
                        tableLayout.setTabTextColors(textColor, Color.WHITE)
                    }
                    setColors(color,resource)
                }
            }
        }
    }


}
