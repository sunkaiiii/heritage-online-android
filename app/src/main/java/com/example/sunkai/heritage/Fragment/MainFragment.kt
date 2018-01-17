package com.example.sunkai.heritage.Fragment

import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.*
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v7.graphics.Palette
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.sunkai.heritage.ConnectWebService.BaseSetting

import com.example.sunkai.heritage.ConnectWebService.HandleMainFragment
import com.example.sunkai.heritage.Data.*
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.R.id.fragment_main_collapsing_toolbar_layout
import com.example.sunkai.heritage.R.id.iv_fragment_main_scroll_change_image
import kotlinx.android.synthetic.main.fragment_main.*

import java.util.ArrayList


class MainFragment : Fragment() {

    internal lateinit var view: View
    internal lateinit var tableLayout:TabLayout
    internal lateinit var viewPager:ViewPager

    var urls:List<ActivityData>?=null

    var index=0



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


    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = ViewPagerAdapter(activity!!.supportFragmentManager)
        //给viewpager添加Fragment，以并传输通道名以显示对应通道的内容
        for (channelName in ClassifyActivityDivide.divide) {
            adapter.insertNewFragment(ActivityFragment.newInstance(channelName))
        }
        viewPager.adapter = adapter
    }

    private fun getMainFragmentDivideImageUrl(){
        Thread{
            urls=HandleMainFragment.Get_Main_Divide_Activity_Image_Url()
            activity?.runOnUiThread{
                getDivideImage(tableLayout.selectedTabPosition)
            }
        }.start()
    }

    private fun initViews(){
        setupViewPager(viewPager)
        tableLayout.setupWithViewPager(viewPager)
        tableLayout.setTabTextColors(Color.GRAY, Color.WHITE)
        for (i in 0 until ClassifyActivityDivide.divide.size) {
            tableLayout.getTabAt(i)?.text = ClassifyActivityDivide.divide[i]
        }
        tableLayout.addOnTabSelectedListener(tabLayoutListener)
        tableLayout.getTabAt(0)!!.select()
    }

    internal inner class ViewPagerAdapter(manager: android.support.v4.app.FragmentManager) : FragmentPagerAdapter(manager) {
        private val mFragmentList = ArrayList<android.support.v4.app.Fragment>()

        override fun getItem(position: Int): android.support.v4.app.Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun insertNewFragment(fragment: android.support.v4.app.Fragment) {
            mFragmentList.add(fragment)
        }
    }

    private fun getDivideImage(index:Int){
        urls?.let{
            val url= urls!![index].url
            Glide.with(context!!).load(BaseSetting.host+url).into(simpleTarget)
        }
    }

    val tabLayoutListener=object :TabLayout.OnTabSelectedListener{
        override fun onTabReselected(tab: TabLayout.Tab?) {}

        override fun onTabUnselected(tab: TabLayout.Tab?) {}

        override fun onTabSelected(tab: TabLayout.Tab?) {
            index=tab?.position!!
            getDivideImage(tab.position)
        }

    }

    var simpleTarget: SimpleTarget<Drawable> = object : SimpleTarget<Drawable>() {
        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
            if(index==tableLayout.selectedTabPosition) {
                val outAnimation=AnimationUtils.loadAnimation(activity,R.anim.fade_out_quick)
                val secondInAnimation=AnimationUtils.loadAnimation(activity,R.anim.fade_in_quick)
                val bitmap = (resource as BitmapDrawable).bitmap
                val color = Palette.from(bitmap).generate().getDominantColor(ContextCompat.getColor(context!!, R.color.colorPrimaryDark))
                val textColor=Palette.from(bitmap).generate().dominantSwatch?.titleTextColor
                textColor?.let{
                    tableLayout.setTabTextColors(textColor, Color.WHITE)
                }
                outAnimation.setAnimationListener(object :Animation.AnimationListener{
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
            }
        }

    }


}
