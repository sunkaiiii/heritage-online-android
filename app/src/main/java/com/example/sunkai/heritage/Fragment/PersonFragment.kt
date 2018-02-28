package com.example.sunkai.heritage.Fragment

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.sunkai.heritage.Activity.FocusInformationActivity
import com.example.sunkai.heritage.Activity.LoginActivity
import com.example.sunkai.heritage.Activity.SettingActivity
import com.example.sunkai.heritage.Activity.UserOwnTieziActivity
import com.example.sunkai.heritage.ConnectWebService.HandlePersonNew
import com.example.sunkai.heritage.Data.HandlePic
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.MakeToast.toast
import com.example.sunkai.heritage.value.SETTING_ACTIVITY
import com.example.sunkai.heritage.value.SIGN_OUT
import com.makeramen.roundedimageview.RoundedImageView

/**
 * 此类是用于处理个人中心页面
 */


class PersonFragment : BaseTakePhotoLazyLoadFragment(), View.OnClickListener {

    private lateinit var myOwnTiezi: LinearLayout
    private lateinit var settingLayout: LinearLayout
    private lateinit var userName: TextView
    private lateinit var follow: TextView
    private lateinit var followNumber: TextView
    private lateinit var fans: TextView
    private lateinit var fansNumber: TextView
    private lateinit var userImage: RoundedImageView

    private lateinit var builder: AlertDialog.Builder
    private lateinit var ad: AlertDialog

    //记录用户头像的url
    private var userImageUrl: String? = null




    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_person, container, false)
        initview(view)
        if (LoginActivity.userName == null) {
            userName.text = "没有登录"
        } else {
            userName.text = LoginActivity.userName
        }
        return view
    }

    private fun initview(view: View) {
        myOwnTiezi = view.findViewById(R.id.fragment_person_my_tiezi)
        settingLayout = view.findViewById(R.id.person_fragment_setting)
        userName = view.findViewById(R.id.sign_name_textview)
        follow = view.findViewById(R.id.person_follow)
        followNumber = view.findViewById(R.id.person_follow_number)
        fans = view.findViewById(R.id.person_fans)
        fansNumber = view.findViewById(R.id.person_fans_number)
        userImage = view.findViewById(R.id.sign_in_icon)
        follow.setOnClickListener(this)
        followNumber.setOnClickListener(this)
        fans.setOnClickListener(this)
        fansNumber.setOnClickListener(this)
        userImage.setOnClickListener(this)
        myOwnTiezi.setOnClickListener(this)
        settingLayout.setOnClickListener(this)
    }

    override fun startLoadInformation() {
        GetUserInfo()
    }

    //获取用户的信息
    private fun GetUserInfo() {
        Thread {
            val userInfo = HandlePersonNew.GetUserAllInfo(LoginActivity.userID) ?: return@Thread
            val userImageUrl = HandlePersonNew.GetUserImageURL(LoginActivity.userID)
            activity?.runOnUiThread {
                userName.text = userInfo.userName
                fansNumber.text = userInfo.fansNumber.toString()
                followNumber.text = userInfo.focusNumber.toString()
                if (userImageUrl != null) {
                    this.userImageUrl = userImageUrl
                    Glide.with(this).load(userImageUrl).into(userImage)
                }
            }
        }.start()
    }

    private fun UpdateUserImage(bitmap: Bitmap) {
        Thread {
            val result = HandlePersonNew.UpdateUserImage(LoginActivity.userID, HandlePic.bitmapToByteArray(bitmap))
            activity?.runOnUiThread {
                ad.dismiss()
                if (result) {
                    Toast.makeText(activity, "更新头像成功", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(activity, "出现错误，请稍后再试", Toast.LENGTH_SHORT).show()
                    userImage.setImageResource(R.drawable.ic_assignment_ind_deep_orange_200_48dp)
                }
            }
        }.start()

    }


    override fun onClick(v: View) {
        if(checkLogin()) {
            val intent: Intent
            when (v.id) {
                R.id.fragment_person_my_tiezi -> {
                    intent = Intent(activity, UserOwnTieziActivity::class.java)
                    startActivity(intent)
                }
                R.id.person_fragment_setting -> {
                    if(checkLogin()) {
                        intent = Intent(activity, SettingActivity::class.java)
                        if (userImageUrl != null) {
                            intent.putExtra("userImage", userImageUrl)
                        }
                        startActivityForResult(intent, SETTING_ACTIVITY)
                    }
                }
                R.id.person_follow, R.id.person_follow_number -> {
                    intent = Intent(activity, FocusInformationActivity::class.java)
                    intent.putExtra("information", "focus")
                    startActivity(intent)
                }
                R.id.person_fans, R.id.person_fans_number -> {
                    intent = Intent(activity, FocusInformationActivity::class.java)
                    intent.putExtra("information", "fans")
                    startActivity(intent)
                }
                R.id.sign_in_icon -> {
                    chooseAlertDialog.show()
                }
            }
        }
    }

    //设置用户头像的回调
    override fun setImageToImageView(bitmap: Bitmap) {
        val compressBitmap = HandlePic.compressBitmapToFile(bitmap, 192, 192)
        userImage.setImageBitmap(compressBitmap)
        builder = AlertDialog.Builder(activity!!).setTitle("上传中").setView(R.layout.update_image_builder)
        ad = builder.create()
        ad.show()
        UpdateUserImage(compressBitmap)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            IS_INTO_LOGIN -> {
                userName.text = LoginActivity.userName
                GetUserInfo()
            }
            SETTING_ACTIVITY -> {
                if (resultCode == SIGN_OUT) {
                    checkLogin()
                }
            }
        }
    }
    private fun checkLogin():Boolean {
        if(LoginActivity.userID==0) {
            toast("没有登录")
            val intent = Intent(activity, LoginActivity::class.java)
            intent.putExtra("isInto", 1)
            startActivityForResult(intent, 1)
            return false
        }
        return true
    }

    companion object {
        private const val IS_INTO_LOGIN = 1
    }
}
