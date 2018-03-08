package com.example.sunkai.heritage.Fragment

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.sunkai.heritage.Activity.AllFolkInfoActivity
import com.example.sunkai.heritage.Activity.MainActivity
import com.example.sunkai.heritage.ConnectWebService.HandleFolk
import com.example.sunkai.heritage.Data.ActivityData
import com.example.sunkai.heritage.Data.ClassifyActivityDivide
import com.example.sunkai.heritage.Data.GlobalContext
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.BaseOnPageChangeListener
import com.example.sunkai.heritage.tools.generateColor
import com.example.sunkai.heritage.tools.generateTextColor
import com.example.sunkai.heritage.value.HOST
import kotlinx.android.synthetic.main.fragment_folk.*
import java.lang.ref.WeakReference
import java.util.*


class FolkFragment : BaseLazyLoadFragment() {

    internal lateinit var tableLayout: TabLayout
    private lateinit var viewPager: ViewPager

    private var urls: List<ActivityData>? = null

    var index = 0
    lateinit var view:WeakReference<View>


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_folk, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        this.view= WeakReference(view)
    }

    private fun initViews(view: View) {
        tableLayout = view.findViewById(R.id.tab_layout)
        viewPager = view.findViewById(R.id.main_tab_content)

        setupViewPager(viewPager)
        tableLayout.setupWithViewPager(viewPager)
        tableLayout.setTabTextColors(Color.GRAY, Color.WHITE)
        for (i in 0 until ClassifyActivityDivide.divide.size) {
            tableLayout.getTabAt(i)?.text = ClassifyActivityDivide.divide[i]
        }
        tableLayout.addOnTabSelectedListener(tabLayoutListener)
        tableLayout.getTabAt(0)!!.select()
        iv_fragment_main_scroll_change_image.setOnClickListener {
            val intent = Intent(activity, AllFolkInfoActivity::class.java)
            startActivity(intent)
        }
    }

    override fun startLoadInformation() {
        //活动主页分类图片数据
        getMainFragmentDivideImageUrl()
        val view=view.get()
        view?.let {
            initViews(view)
        }
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = ViewPagerAdapter(activity!!.supportFragmentManager)
        //给viewpager添加Fragment，以并传输通道名以显示对应通道的内容,并传入对应的viewpager当中的index
        for ((count, channelName) in ClassifyActivityDivide.divide.withIndex()) {
            adapter.insertNewFragment(ActivityFragment.newInstance(channelName, count))
        }

        //懒加载处理，将网络请求延迟到滚动到对应位置的时候再加载
        viewPager.addOnPageChangeListener(object : BaseOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                val frament = adapter.getItem(position)
                if (frament is ActivityFragment) {
                    frament.getInformation()
                }
            }

        })
        viewPager.adapter = adapter
    }

    private fun getMainFragmentDivideImageUrl() {
        Thread {
            urls = HandleFolk.Get_Main_Divide_Activity_Image_Url()
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

    private fun setColors(color: Int, resource: Drawable) {
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
        tableLayout.setBackgroundColor(color)
        if (Build.VERSION.SDK_INT >= 21) {
            if (MainActivity.GetViewpagerSelectPosition() == R.id.folk_layout) {
                activity?.window?.statusBarColor = color
            }
        }
    }

    fun getStatusBarShouldChangeColor(): Int {
        return iv_fragment_main_scroll_change_image.drawable?.generateColor() ?: ContextCompat.getColor(GlobalContext.instance,R.color.colorPrimary)
    }

    private val simpleTarget: SimpleTarget<Drawable> by lazy {
        object : SimpleTarget<Drawable>() {
            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                if (index == tableLayout.selectedTabPosition) {
                    val color = resource.generateColor()
                    val textColor = resource.generateTextColor()
                    tableLayout.setTabTextColors(textColor, Color.WHITE)
                    setColors(color, resource)
                }
            }
        }
    }


}
