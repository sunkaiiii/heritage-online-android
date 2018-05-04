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
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.sunkai.heritage.Activity.AllFolkInfoActivity
import com.example.sunkai.heritage.Activity.MainActivity
import com.example.sunkai.heritage.Activity.SearchNewsActivity
import com.example.sunkai.heritage.ConnectWebService.HandleFolk
import com.example.sunkai.heritage.Data.ActivityData
import com.example.sunkai.heritage.Fragment.BaseFragment.BaseLazyLoadFragment
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.BaseOnPageChangeListener
import com.example.sunkai.heritage.tools.GlobalContext
import com.example.sunkai.heritage.tools.generateColor
import com.example.sunkai.heritage.tools.generateTextColor
import com.example.sunkai.heritage.value.*
import kotlinx.android.synthetic.main.fragment_folk.*
import java.util.*


class FolkFragment : BaseLazyLoadFragment(),View.OnClickListener {

    private var urls: List<ActivityData>? = null

    var index = 0


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_folk, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(savedInstanceState==null) {
            initViews()
        }
    }

    override fun onRestoreFragmentLoadInformation() {
        initViews()
        lazyLoad()
    }


    private fun initViews() {

        setupViewPager(mainTabContent)
        tabayout.setupWithViewPager(mainTabContent)
        tabayout.setTabTextColors(Color.GRAY, Color.WHITE)
        for (i in 0 until CLASSIFY_DIVIDE_TABVIEWSHOW.size) {
            tabayout.getTabAt(i)?.text = CLASSIFY_DIVIDE_TABVIEWSHOW[i]
        }
        tabayout.addOnTabSelectedListener(tabLayoutListener)
        tabayout.getTabAt(0)?.select()
        fragmentFolkSearch.setOnClickListener(this)
        fragmentFolkSeeAllFolk.setOnClickListener(this)
    }

    override fun startLoadInformation() {
        //活动主页分类图片数据
        getMainFragmentDivideImageUrl()
    }

    private fun setupViewPager(viewPager: ViewPager) {
        viewPager.adapter=null
        val manager=activity?.supportFragmentManager?:return
        val adapter = ViewPagerAdapter(manager)
        //给viewpager添加Fragment，以并传输通道名以显示对应通道的内容,并传入对应的viewpager当中的index
        for ((count, channelName) in CLASIIFY_DIVIDE.withIndex()) {
            adapter.insertNewFragment(ActivityFragment.newInstance(channelName, count))
        }

        //懒加载处理，将网络请求延迟到滚动到对应位置的时候再加载
        viewPager.addOnPageChangeListener(object : BaseOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                val frament = adapter.getItem(position)
                if (frament is BaseLazyLoadFragment) {
                    frament.lazyLoad()
                }
            }

        })
        viewPager.adapter = adapter
    }

    private fun getMainFragmentDivideImageUrl() {
        requestHttp {
            urls = HandleFolk.Get_Main_Divide_Activity_Image_Url()
            activity?.runOnUiThread {
                getDivideImage(tabayout.selectedTabPosition)
            }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.fragmentFolkSeeAllFolk->{
                val intent = Intent(activity, AllFolkInfoActivity::class.java)
                startActivity(intent)
            }
            R.id.fragmentFolkSearch->{
                val intent =Intent(activity,SearchNewsActivity::class.java)
                intent.putExtra(SEARCH_TYPE, TYPE_FOLK_HERITAGE)
                startActivity(intent)
            }
        }
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
            glide.load(HOST + url).into(simpleTarget)
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
        tabayout.setBackgroundColor(color)
        if (Build.VERSION.SDK_INT >= 21) {
            if (MainActivity.GetViewpagerSelectPosition() == R.id.folk_layout) {
                activity?.window?.statusBarColor = color
            }
        }
    }

    fun getStatusBarShouldChangeColor(): Int {
        return if (!this.isDetachhed)
            iv_fragment_main_scroll_change_image.drawable?.generateColor()
                    ?: ContextCompat.getColor(GlobalContext.instance, R.color.colorPrimary)
        else ContextCompat.getColor(GlobalContext.instance, R.color.colorPrimary)
    }

    private val simpleTarget: SimpleTarget<Drawable> by lazy {
        object : SimpleTarget<Drawable>() {
            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                if (index == tabayout.selectedTabPosition) {
                    val color = resource.generateColor()
                    val textColor = resource.generateTextColor()
                    tabayout.setTabTextColors(textColor, Color.WHITE)
                    setColors(color, resource)
                }
            }
        }
    }


}
