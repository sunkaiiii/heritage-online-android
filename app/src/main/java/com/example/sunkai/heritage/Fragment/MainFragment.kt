package com.example.sunkai.heritage.Fragment

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout

import com.example.sunkai.heritage.ConnectWebService.HandleMainFragment
import com.example.sunkai.heritage.Data.ClassifyActivityDivide
import com.example.sunkai.heritage.Data.HandlePic
import com.example.sunkai.heritage.Data.MainActivityData
import com.example.sunkai.heritage.R

import java.util.ArrayList


class MainFragment : Fragment(), ViewPager.OnPageChangeListener {

    private lateinit var viewPager: ViewPager
    private lateinit var tips: Array<ImageView?>
    private lateinit var mImageViews: Array<ImageView?>
    private lateinit var imgIdArray: IntArray
    internal lateinit var view: View
    internal var activityDatas: List<MainActivityData>?=null
    internal lateinit var bitmaps: Array<Bitmap?>
    internal var count = 0


    internal var loadMainActivity: Runnable = Runnable {
        activityDatas = HandleMainFragment.ReadMainActivity()
        val message = Message()
        if (null == activityDatas) {
            message.what = 0
        } else {
            message.what = 1
        }
        loadMainActivityHandler.sendMessage(message)
    }

    internal var loadMainActivityHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            if (msg.what == 1) {
                for (i in activityDatas!!.indices) {
                    val bitmap = BitmapFactory.decodeByteArray(activityDatas!![i].activityImage, 0, activityDatas!![i].activityImage!!.size)
                    bitmaps[i] = bitmap
                }
                loadMyPage()
                viewPager.adapter = MyAdapter()
            }
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_main, container, false)
        bitmaps = arrayOfNulls(4)
        //主页活动页面
        imgIdArray = intArrayOf(R.mipmap.img1, R.mipmap.img1, R.mipmap.img1, R.mipmap.img1)
        viewPager = view.findViewById(R.id.main_fragment_viewPager)
        loadMyPage()
        //设置Adapter
        viewPager.adapter = MyAdapter()
        //设置监听，主要是设置点点的背景
        @Suppress("DEPRECATION")
        viewPager.setOnPageChangeListener(this)
        //设置ViewPager的默认项, 设置为长度的100倍，这样子开始就能往左滑动
        viewPager.currentItem = mImageViews.size * 100
        Thread(loadMainActivity).start()

        //此处是活动分类
        val tableLayout = view.findViewById<TabLayout>(R.id.tab_layout)
        val viewPager = view.findViewById<ViewPager>(R.id.main_tab_content)
        setupViewPager(viewPager)
        tableLayout.setupWithViewPager(viewPager)
        tableLayout.setTabTextColors(Color.GRAY, Color.WHITE)
        for (i in 0 until ClassifyActivityDivide.divide.size) {
            tableLayout.getTabAt(i)?.text = ClassifyActivityDivide.divide[i]
        }
        tableLayout.getTabAt(0)!!.select()

        return view
    }

    fun loadMyPage() {
        val group = view.findViewById<ViewGroup>(R.id.main_fragment_imageView)
        tips = arrayOfNulls(imgIdArray.size)
        for (i in tips.indices) {
            val imageView = ImageView(activity)
            imageView.layoutParams = ViewGroup.LayoutParams(10, 10)
            tips[i] = imageView
            if (i == 0) {
                tips[i]?.setBackgroundResource(R.drawable.page_indicator_focused)
            } else {
                tips[i]?.setBackgroundResource(R.drawable.page_indicator_unfocused)
            }
            val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT))
            layoutParams.leftMargin = 5
            layoutParams.rightMargin = 5
            if (count >= tips.size) {
                group.addView(imageView, layoutParams)
            }
        }
        mImageViews = arrayOfNulls(imgIdArray.size)
        for (i in mImageViews.indices) {
            val imageView = ImageView(activity)
            mImageViews[i] = imageView
            if (count < mImageViews.size) {
                bitmaps[i] = HandlePic.handlePic(activity, imgIdArray[i], 0)
                count++
            }
            imageView.scaleType = ImageView.ScaleType.FIT_XY
            imageView.setImageBitmap(bitmaps[i])
            imageView.setOnClickListener {
            }
        }
    }

    inner class MyAdapter : PagerAdapter() {

        override fun getCount(): Int {
            return Integer.MAX_VALUE
        }

        override fun isViewFromObject(arg0: View, arg1: Any): Boolean {
            return arg0 === arg1
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(mImageViews[position % mImageViews.size])
        }

        /*
         * 载入图片进去，用当前的position 除以 图片数组长度取余数是关键
         */
        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            container.addView(mImageViews[position % mImageViews.size], 0)
            return mImageViews[position % mImageViews.size] as ImageView
        }


    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = ViewPagerAdapter(activity!!.supportFragmentManager)
        //给viewpager添加Fragment，以并传输通道名以显示对应通道的内容
        for (channelName in ClassifyActivityDivide.divide) {
            adapter.insertNewFragment(ActivityFragment.newInstance(channelName))
        }
        viewPager.adapter = adapter
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

    override fun onPageScrollStateChanged(arg0: Int) {

    }

    override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {

    }

    override fun onPageSelected(arg0: Int) {
        setImageBackground(arg0 % mImageViews.size)
    }

    /*
     * 设置选中的tip的背景
     */
    private fun setImageBackground(selectItems: Int) {
        for (i in tips.indices) {
            if (i == selectItems) {
                tips[i]?.setBackgroundResource(R.drawable.page_indicator_focused)
            } else {
                tips[i]?.setBackgroundResource(R.drawable.page_indicator_unfocused)
            }
        }
    }
}
