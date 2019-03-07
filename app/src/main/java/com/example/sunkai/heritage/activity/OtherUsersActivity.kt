package com.example.sunkai.heritage.activity

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.TextUtils
import android.transition.Fade
import android.transition.TransitionManager
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.example.sunkai.heritage.activity.base.BaseGlideActivity
import com.example.sunkai.heritage.activity.loginActivity.LoginActivity
import com.example.sunkai.heritage.adapter.OtherPersonActivityRecyclerViewAdapter
import com.example.sunkai.heritage.adapter.OtherUserActivityImageAdapter
import com.example.sunkai.heritage.connectWebService.HandleFind
import com.example.sunkai.heritage.connectWebService.HandlePerson
import com.example.sunkai.heritage.entity.FollowInformation
import com.example.sunkai.heritage.entity.SearchUserInfo
import com.example.sunkai.heritage.entity.UserInfo
import com.example.sunkai.heritage.interfaces.onPhotoViewImageClick
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.activity.base.BaseViewImageActivity
import com.example.sunkai.heritage.tools.MakeToast.toast
import com.example.sunkai.heritage.tools.getDarkThemeColor
import com.example.sunkai.heritage.tools.getLightThemeColor
import com.example.sunkai.heritage.tools.getThemeColor
import com.example.sunkai.heritage.value.*
import com.github.chrisbanes.photoview.PhotoView
import kotlinx.android.synthetic.main.activity_other_users.*
import kotlinx.android.synthetic.main.user_view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class OtherUsersActivity : BaseViewImageActivity(), View.OnClickListener {

    private var userID: Int = NO_USERID

    private var data: UserInfo? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other_users)
        settupWindowAnimations()
        val data = intent.getSerializableExtra(DATA)
        if (data is FollowInformation) {
            userID = data.focusFansID
            if (userID != NO_USERID) {
                getUserAllInfo(data)
            }
        }
        if (data is SearchUserInfo) {
            userID = data.id
            if (userID != NO_USERID) {
                getUserAllInfo(data)
            }
        }
    }

    override fun changeWidgeTheme() {
        super.changeWidgeTheme()
        val colorArray = IntArray(2)
        colorArray[0] = getThemeColor()
        colorArray[1] = getLightThemeColor()
        val drawable = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colorArray)
        userViewBackGround.setImageDrawable(drawable)
        sign_name_textview.setTextColor(getDarkThemeColor())
        person_follow.setTextColor(getDarkThemeColor())
        person_fans.setTextColor(getDarkThemeColor())
        person_fans_number.setTextColor(getDarkThemeColor())
        person_follow_number.setTextColor(getDarkThemeColor())
    }

    private fun settupWindowAnimations() {
        window.enterTransition.duration = 500
    }

    private fun setViewsOnClick() {
        sign_in_icon.setOnClickListener(this)
        person_follow_number.setOnClickListener(this)
        person_fans_number.setOnClickListener(this)
        person_follow.setOnClickListener(this)
        person_fans.setOnClickListener(this)
        ll_activity_other_users_background.setOnClickListener(this)
        vp_activity_other_users.setOnClickListener(this)
    }

    private fun setDataIntoView(userName: String, imageUrl: String?) {
        sign_name_textview.text = userName
        if (!imageUrl.isNullOrEmpty()) {
            glide.load(imageUrl).into(sign_in_icon)
        }
    }

    private fun getUserAllInfo(searchUserData: SearchUserInfo) {
        setDataIntoView(searchUserData.userName, searchUserData.imageUrl)
        requestAllUserInfo(searchUserData.id)
    }

    private fun getUserAllInfo(userData: FollowInformation) {
        setDataIntoView(userData.userName, userData.imageUrl)
        requestAllUserInfo(userData.focusFansID)
    }

    private fun requestAllUserInfo(userID: Int) {
        requestHttp {
            val data = HandlePerson.GetUserAllInfo(userID)
            data?.let {
                runOnUiThread {
                    this.data = data
                    setViews(data)
                    getUserImage(userID, sign_in_icon)
                    checkPermissions(data)
                }
            }
        }
    }

    private fun checkPermissions(data: UserInfo) {
        when (data.permission) {
            DENIALD -> setErrorMessage()
            ONLYFOCUS -> {
                GlobalScope.launch {
                    val result = judgeFocus(userID, LoginActivity.userID)
                    if (isDestroy) return@launch
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
                GlobalScope.launch {
                    val result = judgeFocus(userID, LoginActivity.userID)
                    if (isDestroy) return@launch
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
        tv_permission.visibility = View.VISIBLE
        rv_activity_other_users.visibility = View.GONE
        tv_permission.text = getString(R.string.permission_denaild_information)
    }


    private fun getUserImage(userID: Int, photoView: ImageView) {
        GlobalScope.launch {
            val url = HandlePerson.GetUserImageURL(userID)
            url?.let {
                if (isDestroy) return@launch
                runOnUiThread {
                    glide.load(url).into(photoView)
                }
            }
        }
    }

    private fun judgeFocus(otherUserID: Int, userID: Int): Boolean {
        return HandlePerson.IsUserFollow(otherUserID, userID)
    }

    private fun setViews(data: UserInfo) {
        addFocsFloatBtn.visibility = if (data.id == LoginActivity.userID) View.GONE else View.VISIBLE
        sign_name_textview.text = data.userName
        person_follow_number.text = data.focusNumber.toString()
        person_fans_number.text = data.fansNumber.toString()
        setFloatBtnState(data)
        addFocsFloatBtn.setOnClickListener(this)
    }

    private fun setFloatBtnState(data: UserInfo) {
        addFocsFloatBtn.setImageResource(if (data.checked) R.drawable.ic_remove_white_24dp else R.drawable.ic_add_white_24dp)
        //设置按下时候的颜色
        addFocsFloatBtn.rippleColor = if (data.checked) getThemeColor() else ContextCompat.getColor(this, R.color.lightGrey)
        //给FloatActionButton着色
        addFocsFloatBtn.backgroundTintList = if (data.checked) ContextCompat.getColorStateList(this, R.color.midGrey) else ColorStateList.valueOf(getLightThemeColor())
    }


    private fun setAdapter(userID: Int) {
        val adapter = OtherPersonActivityRecyclerViewAdapter(this, userID, arrayListOf(), glide)
        val layoutManager = androidx.recyclerview.widget.GridLayoutManager(this, 4)
        rv_activity_other_users.layoutManager = layoutManager
        rv_activity_other_users.adapter = adapter

        adapter.setOnItemClickListener { _, position ->
            openImageView(position, adapter)
        }
    }

    private fun handleFloatBtn() {
        addFocsFloatBtn.isEnabled = false
        val data = this.data
        playFloatBtnAnimation()
        data?.let {
            GlobalScope.launch {
                val result = if (data.checked) HandlePerson.CancelFocus(LoginActivity.userID, data.id) else HandlePerson.AddFocus(LoginActivity.userID, data.id)
                if (isDestroy) return@launch
                runOnUiThread {
                    addFocsFloatBtn.isEnabled = true
                    if (result) {
                        data.checked = !data.checked
                        this@OtherUsersActivity.data = data
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
        if (ll_activity_other_users_background.visibility == View.VISIBLE) {
            closeImageView()
        } else {
            super.onBackPressed()
        }
    }


    private fun setViewsOnFonsAndFocusPermissionDenailClick() {
        sign_in_icon.setOnClickListener(this)
        val onPermissionDenailListner = View.OnClickListener { toast(R.string.fans_and_view_permission_denail) }
        person_follow_number.setOnClickListener(onPermissionDenailListner)
        person_fans_number.setOnClickListener(onPermissionDenailListner)
        person_follow.setOnClickListener(onPermissionDenailListner)
        person_fans.setOnClickListener(onPermissionDenailListner)
    }

    private fun startActivity(what: Int) {
        val intent = Intent(this, FocusInformationActivity::class.java)
        intent.putExtra(INFORMATION, what.toString())
        intent.putExtra(USER_ID, userID)
        startActivity(intent)
    }

    private fun openImageView(position: Int, adapter: OtherPersonActivityRecyclerViewAdapter) {
        TransitionManager.beginDelayedTransition(otherUsersFrameLayout, Fade().setDuration(200))
        val viewPagerAdapter = OtherUserActivityImageAdapter(this, adapter.getAdapterDatas())
        viewPagerAdapter.setOnDragListener(this)
        viewPagerAdapter.setOnPhotoViewImageClickListener(object : onPhotoViewImageClick {
            override fun onImageClick(position: Int, photoView: PhotoView) {
                if (photoView.scale == 1.0f) {
                    closeImageView()
                } else {
                    photoView.setScale(1.0f, true)
                }
            }

        })
        vp_activity_other_users.adapter = viewPagerAdapter
        vp_activity_other_users.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageSelected(position: Int) {
                getImage(position, viewPagerAdapter)
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageScrollStateChanged(state: Int) {}

        })
        vp_activity_other_users.currentItem = position
        //如果点击的是第一个图片，则要指定载入图片，其他情况因为会触发OnPageChangeListener,则无需管理
        if (position == 0) {
            getImage(position, viewPagerAdapter)
        }
        addFocsFloatBtn.visibility = View.GONE
        ll_activity_other_users_background.visibility = View.VISIBLE
        vp_activity_other_users.visibility = View.VISIBLE
    }

    private fun showUserImage() {
        TransitionManager.beginDelayedTransition(otherUsersFrameLayout, Fade().setDuration(200))
        ll_activity_other_users_background.visibility = View.VISIBLE
        pv_activity_other_users.visibility = View.VISIBLE
        addFocsFloatBtn.visibility = View.GONE
        pv_activity_other_users.setImageDrawable(sign_in_icon.drawable)
    }

    private fun closeImageView() {
        TransitionManager.beginDelayedTransition(otherUsersFrameLayout, Fade().setDuration(200))
        ll_activity_other_users_background.visibility = View.GONE
        addFocsFloatBtn.visibility = if (data?.id == LoginActivity.userID) View.GONE else View.VISIBLE
        if (vp_activity_other_users.visibility == View.VISIBLE) {
            vp_activity_other_users.visibility = View.GONE
            vp_activity_other_users.adapter = null
        }
        if (pv_activity_other_users.visibility == View.VISIBLE) {
            pv_activity_other_users.visibility = View.GONE
        }
    }

    internal fun getImage(position: Int, adapter: OtherUserActivityImageAdapter) {
        GlobalScope.launch {
            val id = adapter.datas[position]
            val url = HandleFind.GetUserCommentImageUrl(id)
            if (!TextUtils.isEmpty(url) && url != ERROR) {
                if (isDestroy) return@launch
                runOnUiThread {
                    val photoView = adapter.photoViewMap[position] ?: return@runOnUiThread
                    glide.load(url).into(photoView)
                }
            }
        }
    }

    override fun setImageViewListener() {}

    override fun getRootView(): View {
        return ll_activity_other_users_background
    }

}
