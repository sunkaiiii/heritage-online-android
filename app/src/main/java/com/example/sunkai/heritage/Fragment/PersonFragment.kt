package com.example.sunkai.heritage.Fragment

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.view.menu.MenuBuilder
import android.view.*
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.sunkai.heritage.Activity.*
import com.example.sunkai.heritage.Activity.LoginActivity.LoginActivity
import com.example.sunkai.heritage.ConnectWebService.HandlePerson
import com.example.sunkai.heritage.tools.GlobalContext
import com.example.sunkai.heritage.Data.HandlePic
import com.example.sunkai.heritage.Dialog.ChangePasswordDialog
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.MakeToast.toast
import com.example.sunkai.heritage.value.LOG_OUT
import com.example.sunkai.heritage.value.SETTING_ACTIVITY
import com.example.sunkai.heritage.value.SIGN_OUT
import com.example.sunkai.heritage.value.STATE_CHANGE
import com.makeramen.roundedimageview.RoundedImageView
import kotlinx.android.synthetic.main.fragment_person.*

/**
 * 此类是用于处理个人中心页面
 */


class PersonFragment : BaseTakePhotoLazyLoadFragment(), View.OnClickListener {

    private lateinit var myOwnTiezi: TextView
    private lateinit var settingLayout: TextView
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
        return inflater.inflate(R.layout.fragment_person, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initview(view)
        if (LoginActivity.userName == null) {
            userName.text = "没有登录"
        } else {
            userName.text = LoginActivity.userName
        }
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
        fragment_person_about_us.setOnClickListener(this)
        fragment_person_my_like.setOnClickListener(this)
        val toolbar = view.findViewById<android.support.v7.widget.Toolbar>(R.id.fragment_person_toolbar)
        val activity = activity
        activity?.let {
            if (activity is AppCompatActivity) {
                toolbar.title = ""
                activity.setSupportActionBar(toolbar)
                setHasOptionsMenu(true)
            }
        }
    }

    override fun startLoadInformation() {
        checkLogin()
        GetUserInfo()
    }

    //获取用户的信息
    private fun GetUserInfo() {
        Thread {
            val userInfo = HandlePerson.GetUserAllInfo(LoginActivity.userID) ?: return@Thread
            val userImageUrl = HandlePerson.GetUserImageURL(LoginActivity.userID)
            activity?.runOnUiThread {
                userName.text = userInfo.userName
                fansNumber.text = userInfo.fansNumber.toString()
                followNumber.text = userInfo.focusNumber.toString()
                if (userImageUrl != null) {
                    this.userImageUrl = userImageUrl
                    val requestOption = RequestOptions().placeholder(R.drawable.ic_assignment_ind_deep_orange_200_48dp).error(R.drawable.ic_assignment_ind_deep_orange_200_48dp)
                    Glide.with(this).load(userImageUrl).apply(requestOption).into(userImage)
                }
            }
        }.start()
    }

    private fun UpdateUserImage(bitmap: Bitmap) {
        Thread {
            val result = HandlePerson.UpdateUserImage(LoginActivity.userID, HandlePic.bitmapToByteArray(bitmap))
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
        if (checkLogin()) {
            val intent: Intent
            when (v.id) {
                R.id.fragment_person_my_tiezi -> {
                    intent = Intent(activity, UserOwnTieziActivity::class.java)
                    startActivity(intent)
                }
                R.id.person_fragment_setting -> {
                    if (checkLogin()) {
                        intent = Intent(activity, SettingListActivity::class.java)
                        startActivity(intent)
                    }
                }
                R.id.person_follow, R.id.person_follow_number -> {
                    intent = Intent(activity, FocusInformationActivity::class.java)
                    intent.putExtra("information", "focus")
                    startActivityForResult(intent, FROM_FOCUS_AND_FANS_INFORMATION)
                }
                R.id.person_fans, R.id.person_fans_number -> {
                    intent = Intent(activity, FocusInformationActivity::class.java)
                    intent.putExtra("information", "fans")
                    startActivityForResult(intent, FROM_FOCUS_AND_FANS_INFORMATION)
                }
                R.id.sign_in_icon -> {
                    chooseAlertDialog.show()
                }
                R.id.fragment_person_about_us -> {
                    intent = Intent(activity, AboutUSActivity::class.java)
                    startActivity(intent)
                }
                R.id.fragment_person_my_like->{
                    intent=Intent(activity,UserLikeCommentActivity::class.java)
                    startActivity(intent)
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

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.person_fragment_menu, menu)
    }

    //通过反射来让弹出的菜单显示图标
    override fun onPrepareOptionsMenu(menu: Menu?) {
        menu?.let {
            if (menu.javaClass==MenuBuilder::class.java){
                try{
                    val m=menu.javaClass.getDeclaredMethod("setOptionalIconsVisible", Boolean::class.java)
                    m.isAccessible=true
                    m.invoke(menu,true)
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }
        }
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.log_out -> {
                sign_out()
            }
            R.id.change_password -> {
                changePassword()
            }
            R.id.search_user->{
                searchUser()
            }
        }
        return super.onOptionsItemSelected(item)
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
                    activity?.finish()
                }
            }
            FROM_FOCUS_AND_FANS_INFORMATION,FROM_SEARCH_ACTIVITY -> {
                if (resultCode == STATE_CHANGE) {
                    GetUserInfo()
                }
            }
        }
    }

    private fun sign_out() {
        val activity = activity
        activity?.let {
            AlertDialog.Builder(activity).setTitle("是否注销?").setPositiveButton("确定") { _, _ ->
                GlobalContext.instance.unregistUser() //注销的时候退出当前账号
                activity.getSharedPreferences("data", Context.MODE_PRIVATE).edit().clear().apply()//清除自动登录的信息
                LoginActivity.userID = 0
                LoginActivity.userName = null
                activity.finish()
                checkLogin()
            }.setNegativeButton("取消") { dialog, _ -> dialog.dismiss() }.show()
        }
    }

    private fun changePassword() {
        if (checkLogin()) {
            val activity = activity
            activity?.let {
                val dialog = ChangePasswordDialog()
                dialog.show(fragmentManager,"修改密码")
            }
        }
    }

    private fun searchUser(){
        val intent=Intent(activity,SearchActivity::class.java)
        startActivityForResult(intent, FROM_SEARCH_ACTIVITY)
    }

    private fun checkLogin(): Boolean {
        if (LoginActivity.userID == 0) {
            toast("没有登录")
            val intent = Intent(activity, LoginActivity::class.java)
            intent.putExtra("isInto", LOG_OUT)
            startActivityForResult(intent, 1)
            return false
        }
        return true
    }


    companion object {
        private const val IS_INTO_LOGIN = 1
        private const val FROM_FOCUS_AND_FANS_INFORMATION = 2
        private const val FROM_SEARCH_ACTIVITY=3
    }
}
