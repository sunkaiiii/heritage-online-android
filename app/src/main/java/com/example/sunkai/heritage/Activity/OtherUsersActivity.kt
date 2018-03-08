package com.example.sunkai.heritage.Activity

import android.content.Intent
import android.os.Bundle
import android.support.transition.Fade
import android.support.transition.TransitionManager
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.sunkai.heritage.Activity.LoginActivity.LoginActivity
import com.example.sunkai.heritage.Adapter.OtherPersonActivityRecyclerViewAdapter
import com.example.sunkai.heritage.Adapter.OtherUserActivityImageAdapter
import com.example.sunkai.heritage.ConnectWebService.HandleFind
import com.example.sunkai.heritage.ConnectWebService.HandlePerson
import com.example.sunkai.heritage.Data.UserInfo
import com.example.sunkai.heritage.Interface.OnItemClickListener
import com.example.sunkai.heritage.Interface.onPhotoViewImageClick
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.MakeToast
import com.example.sunkai.heritage.tools.MakeToast.toast
import com.example.sunkai.heritage.tools.ThreadPool
import com.example.sunkai.heritage.value.*
import com.github.chrisbanes.photoview.PhotoView
import com.makeramen.roundedimageview.RoundedImageView
import kotlinx.android.synthetic.main.activity_other_users.*

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
    private lateinit var viewPagerAdapter: OtherUserActivityImageAdapter
    private lateinit var tvPermission: TextView

    private var userID: Int = NO_USERID

    private var data: UserInfo? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other_users)
        initview()
        userID = intent.getIntExtra("userID", NO_USERID)
        if (userID != NO_USERID) {
            getUserAllInfo(userID)

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
        tvPermission = findViewById(R.id.tv_permission)
    }

    private fun setViewsOnClick() {
        userImageView.setOnClickListener(this)
        focusNumberTextView.setOnClickListener(this)
        fansNumberTextView.setOnClickListener(this)
        focusText.setOnClickListener(this)
        fansText.setOnClickListener(this)
        llBackground.setOnClickListener(this)
        vpViewPager.setOnClickListener(this)
    }

    private fun getUserAllInfo(userID: Int) {
        ThreadPool.execute {
            val data = HandlePerson.GetUserAllInfo(userID)
            data?.let {
                runOnUiThread({
                    this.data = data
                    setViews(data)
                    getUserImage(userID, userImageView)
                    checkPermissions(data)
                })
            }

        }
    }

    private fun checkPermissions(data: UserInfo) {
        when (data.permission) {
            DENIALD -> setErrorMessage()
            ONLYFOCUS -> {
                ThreadPool.execute {
                    val result = judgeFocus(userID, LoginActivity.userID)
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

        when (data.focusAndFansPermission) {
            DENIALD -> setViewsOnFonsAndFocusPermissionDenailClick()
            ONLYFOCUS -> {
                ThreadPool.execute {
                    val result = judgeFocus(userID, LoginActivity.userID)
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
        tvPermission.visibility = View.VISIBLE
        rv_activity_other_users.visibility = View.GONE
        tvPermission.text = getString(R.string.permission_denaild_information)
    }


    private fun getUserImage(userID: Int, photoView: ImageView) {
        ThreadPool.execute {
            val url = HandlePerson.GetUserImageURL(userID)
            url?.let {
                runOnUiThread {
                    Glide.with(this).load(url).into(photoView)
                }
            }
        }
    }

    private fun judgeFocus(otherUserID: Int, userID: Int): Boolean {
        return HandlePerson.IsUserFollow(otherUserID, userID)
    }

    private fun setViews(data: UserInfo) {
        userNameTextView.text = data.userName
        focusNumberTextView.text = data.focusNumber.toString()
        fansNumberTextView.text = data.fansNumber.toString()
        setFloatBtnState(data)
        addFocsFloatBtn.setOnClickListener(this)
    }

    private fun setFloatBtnState(data: UserInfo) {
        addFocsFloatBtn.setImageResource(if (data.checked) R.drawable.ic_remove_white_24dp else R.drawable.ic_add_white_24dp)
        //设置按下时候的颜色
        addFocsFloatBtn.rippleColor = ContextCompat.getColor(this, if (data.checked) R.color.colorAccent else R.color.lightGrey)
        //给FloatActionButton着色
        ViewCompat.setBackgroundTintList(addFocsFloatBtn, ContextCompat.getColorStateList(this, if (data.checked) R.color.midGrey else R.color.colorAccent))
    }


    private fun setAdapter(userID: Int) {
        val adapter = OtherPersonActivityRecyclerViewAdapter(this, userID, arrayListOf())
        val layoutManager = GridLayoutManager(this, 4)
        rv_activity_other_users.layoutManager = layoutManager
        rv_activity_other_users.adapter = adapter

        adapter.setOnItemClickListen(object : OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                openImageView(position, adapter)
            }
        })
    }

    private fun handleFloatBtn() {
        addFocsFloatBtn.isEnabled = false
        val data = this.data
        playFloatBtnAnimation()
        data?.let {
            ThreadPool.execute {
                val result = if (data.checked) HandlePerson.CancelFocus(LoginActivity.userID, data.id) else HandlePerson.AddFocus(LoginActivity.userID, data.id)
                runOnUiThread {
                    addFocsFloatBtn.isEnabled = true
                    if (result) {
                        data.checked = !data.checked
                        this.data = data
                        setFloatBtnState(data)
                        toast((if (data.checked) "关注" else "取消关注") + "成功")
                    } else {
                        toast("出现问题")
                    }
                }
            }

        }
    }

    private fun playFloatBtnAnimation() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.float_btn_translate)
        addFocsFloatBtn.startAnimation(animation)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.person_follow_number, R.id.person_follow -> startActivity(OTHER_FOLLOW)
            R.id.person_fans_number, R.id.person_fans -> startActivity(OTHER_FANS)
            R.id.ll_activity_other_users_background, R.id.vp_activity_other_users -> closeImageView()
            R.id.sign_in_icon -> showUserImage()
            R.id.pv_activity_other_users -> {
                if (pv_activity_other_users.scale == 1.0f)
                    closeImageView()
                else
                    pv_activity_other_users.setScale(1.0f, true)
            }
            R.id.addFocsFloatBtn -> {
                handleFloatBtn()
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


    private fun setViewsOnFonsAndFocusPermissionDenailClick() {
        userImageView.setOnClickListener(this)
        val onPermissionDenailListner = View.OnClickListener { MakeToast.MakeText(getString(R.string.fans_and_view_permission_denail)) }
        focusNumberTextView.setOnClickListener(onPermissionDenailListner)
        fansNumberTextView.setOnClickListener(onPermissionDenailListner)
        focusText.setOnClickListener(onPermissionDenailListner)
        fansText.setOnClickListener(onPermissionDenailListner)
    }

    private fun startActivity(what: Int) {
        val intent = Intent(this, FocusInformationActivity::class.java)
        intent.putExtra("information", what.toString())
        intent.putExtra("userID", userID)
        startActivity(intent)
    }

    private fun openImageView(position: Int, adapter: OtherPersonActivityRecyclerViewAdapter) {
        TransitionManager.beginDelayedTransition(otherUsersFrameLayout,Fade().setDuration(200))
        viewPagerAdapter = OtherUserActivityImageAdapter(this, adapter.datas)
        viewPagerAdapter.setOnPhotoViewImageClickListener(object : onPhotoViewImageClick {
            override fun onImageClick(position: Int, photoView: PhotoView) {
                if (photoView.scale == 1.0f) {
                    closeImageView()
                } else {
                    photoView.setScale(1.0f, true)
                }
            }

        })
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
        addFocsFloatBtn.visibility = View.GONE
        llBackground.visibility = View.VISIBLE
        vpViewPager.visibility = View.VISIBLE
    }

    private fun showUserImage() {
        TransitionManager.beginDelayedTransition(otherUsersFrameLayout,Fade().setDuration(200))
        llBackground.visibility = View.VISIBLE
        pv_activity_other_users.visibility = View.VISIBLE
        addFocsFloatBtn.visibility = View.GONE
        pv_activity_other_users.setImageDrawable(userImageView.drawable)
    }

    private fun closeImageView() {
        TransitionManager.beginDelayedTransition(otherUsersFrameLayout,Fade().setDuration(200))
        llBackground.visibility = View.GONE
        addFocsFloatBtn.visibility = View.VISIBLE
        if (vpViewPager.visibility == View.VISIBLE) {
            vpViewPager.visibility = View.GONE
            vpViewPager.adapter = null
        }
        if (pv_activity_other_users.visibility == View.VISIBLE) {
            pv_activity_other_users.visibility = View.GONE
        }
    }

    internal fun getImage(position: Int, adapter: OtherUserActivityImageAdapter) {
        ThreadPool.execute {
            val id = adapter.datas[position]
            val url = HandleFind.GetUserCommentImageUrl(id)
            if (!TextUtils.isEmpty(url) && url != ERROR) {
                runOnUiThread {
                    val photoView = viewPagerAdapter.photoViewMap[position] ?: return@runOnUiThread
                    Glide.with(this).load(url).into(photoView)
                }
            }
        }
    }


}
