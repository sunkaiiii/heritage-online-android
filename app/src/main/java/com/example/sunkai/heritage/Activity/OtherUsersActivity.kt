package com.example.sunkai.heritage.Activity

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.sunkai.heritage.Adapter.OtherPersonActivityRecyclerViewAdapter
import com.example.sunkai.heritage.ConnectWebService.HandleFind
import com.example.sunkai.heritage.ConnectWebService.HandlePerson
import com.example.sunkai.heritage.Data.GlobalContext
import com.example.sunkai.heritage.Data.UserInfo
import com.example.sunkai.heritage.Interface.OnItemClickListener
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.MakeToast
import com.example.sunkai.heritage.tools.ThreadPool
import com.example.sunkai.heritage.tools.inAnimation
import com.example.sunkai.heritage.tools.outAnimation
import com.example.sunkai.heritage.value.*
import com.github.chrisbanes.photoview.PhotoView
import com.makeramen.roundedimageview.RoundedImageView
import kotlinx.android.synthetic.main.activity_other_users.*
import java.lang.ref.WeakReference
import java.util.*

class OtherUsersActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var userNameTextView: TextView
    private lateinit var userImageView: RoundedImageView
    private lateinit var focusNumberTextView: TextView
    private lateinit var fansNumberTextView: TextView
    private lateinit var focusText: TextView
    private lateinit var fansText: TextView
    private lateinit var userinfosRecyclerView: RecyclerView
    private lateinit var llBackground: LinearLayout
    private lateinit var vpViewPager: ViewPager
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private lateinit var tvPermission:TextView

    private var userID: Int = NO_USERID



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other_users)
        initview()
        userID = intent.getIntExtra("userID", NO_USERID)
        if (userID != NO_USERID) {
            getUserAllInfo(userID)

        }
    }

    private fun getUserAllInfo(userID: Int) {
        ThreadPool.execute {
            val data = HandlePerson.GetUserAllInfo(userID)
            data?.let {
                runOnUiThread({
                    setViews(data)
                    getUserImage(userID,userImageView)
                    checkPermissions(data)
                })
            }

        }
    }
    private fun checkPermissions(data:UserInfo){
        when(data.permission){
            DENIALD -> setErrorMessage()
            ONLYFOCUS->{
                ThreadPool.execute {
                    val result=judgeFocus(userID, LoginActivity.userID)
                    runOnUiThread {
                        if (result) {
                            setAdapter(userID)

                        } else {
                            setErrorMessage()
                        }
                    }
                }
            }
            ALL -> {
                setAdapter(userID)
            }
        }

        when(data.focusAndFansPermission){
            DENIALD -> setViewsOnFonsAndFocusPermissionDenailClick()
            ONLYFOCUS->{
                ThreadPool.execute {
                    val result=judgeFocus(userID, LoginActivity.userID)
                    runOnUiThread {
                        if (result) {
                            setViewsOnClick()

                        } else {
                            setViewsOnFonsAndFocusPermissionDenailClick()
                        }
                    }
                }
            }
            ALL -> {
                setViewsOnClick()
            }
        }
    }
    private fun setErrorMessage() {
        tvPermission.visibility=View.VISIBLE
        rv_activity_other_users.visibility=View.GONE
        tvPermission.text = getString(R.string.permission_denaild_information)
    }


    private fun getUserImage(userID: Int, photoView: ImageView){
        ThreadPool.execute {
            val url=HandlePerson.GetUserImageURL(userID)
            url?.let{
                runOnUiThread {
                    Glide.with(this).load(url).into(photoView)
                }
            }
        }
    }

    private fun judgeFocus(otherUserID:Int, userID: Int):Boolean{
        return HandlePerson.IsUserFollow(otherUserID,userID)
    }

    private fun setViews(data: UserInfo) {
        userNameTextView.text = data.userName
        focusNumberTextView.text = data.focusNumber.toString()
        fansNumberTextView.text = data.fansNumber.toString()
    }


    private fun setAdapter(userID: Int) {
        val adapter = OtherPersonActivityRecyclerViewAdapter(this,userID, arrayListOf())
        val layoutManager = GridLayoutManager(this, 4)
        rv_activity_other_users.layoutManager = layoutManager
        rv_activity_other_users.adapter = adapter

        adapter.setOnItemClickListen(object : OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                openImageView(position, adapter)
            }
        })
    }

    internal class ViewPagerAdapter(val datas: List<Int>, activity: OtherUsersActivity) : PagerAdapter() {
        val weakRefrence = WeakReference(activity)
        val photoViewMap: WeakHashMap<Int, PhotoView>

        init {
            photoViewMap = WeakHashMap()
        }

        override fun getCount(): Int {
            return datas.size
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view == `object`
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val photoView = PhotoView(GlobalContext.instance)
            photoView.scaleType = ImageView.ScaleType.FIT_CENTER
            container.addView(photoView)
            photoView.setImageDrawable(ContextCompat.getDrawable(GlobalContext.instance, R.drawable.backgound_grey))
            photoView.setOnClickListener {
                if (photoView.scale == 1.0f) {
                    weakRefrence.get()?.closeImageView()
                } else {
                    photoView.setScale(1.0f, true)
                }
            }
            photoViewMap.put(position, photoView)
            return photoView
        }

        override fun destroyItem(container: ViewGroup, position: Int, view: Any) {
            container.removeView(view as View)
        }
    }


    internal fun initview() {
        userNameTextView = findViewById(R.id.sign_name_textview)
        userImageView = findViewById(R.id.sign_in_icon)
        focusNumberTextView = findViewById(R.id.person_follow_number)
        fansNumberTextView = findViewById(R.id.person_fans_number)
        focusText = findViewById(R.id.person_follow)
        fansText = findViewById(R.id.person_fans)
        userinfosRecyclerView = findViewById(R.id.rv_activity_other_users)
        llBackground = findViewById(R.id.ll_activity_other_users_background)
        vpViewPager = findViewById(R.id.vp_activity_other_users)
        tvPermission=findViewById(R.id.tv_permission)
    }

    internal fun setViewsOnClick(){
        inAnimation.duration = 300
        outAnimation.duration = 300

        userImageView.setOnClickListener(this)
        focusNumberTextView.setOnClickListener(this)
        fansNumberTextView.setOnClickListener(this)
        focusText.setOnClickListener(this)
        fansText.setOnClickListener(this)
        llBackground.setOnClickListener(this)
        vpViewPager.setOnClickListener(this)
    }

    internal fun setViewsOnFonsAndFocusPermissionDenailClick(){
        userImageView.setOnClickListener(this)
        val onPermissionDenailListner= View.OnClickListener { MakeToast.MakeText(getString(R.string.fans_and_view_permission_denail)) }
        focusNumberTextView.setOnClickListener(onPermissionDenailListner)
        fansNumberTextView.setOnClickListener(onPermissionDenailListner)
        focusText.setOnClickListener(onPermissionDenailListner)
        fansText.setOnClickListener(onPermissionDenailListner)
    }

    internal fun startActivity(what: Int) {
        val intent = Intent(this, FocusInformationActivity::class.java)
        intent.putExtra("information", what.toString())
        intent.putExtra("userID", userID)
        startActivity(intent)
    }

    internal fun openImageView(position: Int, adapter: OtherPersonActivityRecyclerViewAdapter) {
        llBackground.startAnimation(inAnimation)
        vpViewPager.startAnimation(inAnimation)
        llBackground.visibility = View.VISIBLE
        vpViewPager.visibility = View.VISIBLE
        viewPagerAdapter = ViewPagerAdapter(adapter.datas, this)
        vpViewPager.adapter = viewPagerAdapter
        vpViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageSelected(position: Int) {
                getImage(position, viewPagerAdapter)
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageScrollStateChanged(state: Int) {}

        })
        vpViewPager.currentItem = position
        //如果点击的是第一个图片，则要指定载入图片，其他情况因为会触发OnPageChangeListener,则无需管理
        if (position == 0) {
            getImage(position, viewPagerAdapter)
        }
    }

    internal fun showUserImage(){
        llBackground.startAnimation(inAnimation)
        pv_activity_other_users.startAnimation(inAnimation)
        llBackground.visibility=View.VISIBLE
        pv_activity_other_users.visibility=View.VISIBLE
        pv_activity_other_users.setImageDrawable(userImageView.drawable)
    }

    internal fun closeImageView() {
        llBackground.startAnimation(outAnimation)
        llBackground.visibility = View.GONE
        if(vpViewPager.visibility==View.VISIBLE) {
            vpViewPager.startAnimation(outAnimation)
            vpViewPager.visibility = View.GONE
        }
        if(pv_activity_other_users.visibility==View.VISIBLE) {
            pv_activity_other_users.startAnimation(outAnimation)
            pv_activity_other_users.visibility = View.GONE
        }
    }

    internal fun getImage(position: Int, adapter: ViewPagerAdapter) {
        ThreadPool.execute {
            val id=adapter.datas[position]
            val url=HandleFind.GetUserCommentImageUrl(id)
            if(!TextUtils.isEmpty(url)&&url!= ERROR){
                runOnUiThread {
                    val photoView=viewPagerAdapter.photoViewMap[position]?:return@runOnUiThread
                    Glide.with(this).load(url).into(photoView)
                }
            }
        }
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.person_follow_number, R.id.person_follow -> startActivity(OTHER_FOLLOW)
            R.id.person_fans_number, R.id.person_fans -> startActivity(OTHER_FANS)
            R.id.ll_activity_other_users_background, R.id.vp_activity_other_users -> closeImageView()
            R.id.sign_in_icon-> showUserImage()
            R.id.pv_activity_other_users->{
                if(pv_activity_other_users.scale==1.0f)
                    closeImageView()
                else
                    pv_activity_other_users.setScale(1.0f,true)
            }
        }
    }

    override fun onBackPressed() {
        if (llBackground.visibility == View.VISIBLE) {
            closeImageView()
        } else {
            super.onBackPressed()
        }
    }
}
