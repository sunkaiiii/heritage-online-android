package com.example.sunkai.heritage.Fragment

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuBuilder
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.ActionMenuView
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.view.forEach
import androidx.core.view.get
import com.bumptech.glide.request.RequestOptions
import com.example.sunkai.heritage.Activity.*
import com.example.sunkai.heritage.Activity.LoginActivity.LoginActivity
import com.example.sunkai.heritage.ConnectWebService.HandlePerson
import com.example.sunkai.heritage.Data.HandlePic
import com.example.sunkai.heritage.Dialog.ChangePasswordDialog
import com.example.sunkai.heritage.Dialog.NormalWarningDialog
import com.example.sunkai.heritage.Fragment.BaseFragment.BaseTakePhotoLazyLoadFragment
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.MakeToast.toast
import com.example.sunkai.heritage.tools.getDarkThemeColor
import com.example.sunkai.heritage.tools.getLightThemeColor
import com.example.sunkai.heritage.tools.getThemeColor
import com.example.sunkai.heritage.value.*
import kotlinx.android.synthetic.main.fragment_person.*
import kotlinx.android.synthetic.main.user_view.*

/**
 * 此类是用于处理个人中心页面
 */


class PersonFragment : BaseTakePhotoLazyLoadFragment(), View.OnClickListener {

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
        if (savedInstanceState == null) {
            initview()
        }
        if (LoginActivity.userName == null) {
            sign_name_textview.text = "没有登录"
        } else {
            sign_name_textview.text = LoginActivity.userName
        }
    }

    override fun onRestoreFragmentLoadInformation() {
        initview()
        lazyLoad()
    }


    override fun setNeedChangeThemeColorWidget() {
        changeThemeWidge.add(R.id.fragment_person_my_tiezi)
        changeThemeWidge.add(R.id.fragment_person_my_like)
        changeThemeWidge.add(R.id.fragment_person_my_collect)
        changeThemeWidge.add(R.id.fragment_person_my_message)
        changeThemeWidge.add(R.id.person_fragment_setting)
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

    private fun initview() {
        setClick(fragmentPersonLinearLayout)
        val activity = activity
        activity?.let {
            if (activity is AppCompatActivity) {
                fragment_person_toolbar.title = ""
                fragment_person_toolbar.popupTheme
                activity.setSupportActionBar(fragment_person_toolbar)
                setHasOptionsMenu(true)
            }
        }
    }

    private fun setClick(viewGroup: ViewGroup) {
        viewGroup.forEach { if (it is ViewGroup) setClick(it) else it.setOnClickListener(this) }
    }

    override fun startLoadInformation() {
        checkLogin()
        GetUserInfo()
    }

    //获取用户的信息
    private fun GetUserInfo() {
        requestHttp {
            val userInfo = HandlePerson.GetUserAllInfo(LoginActivity.userID) ?: return@requestHttp
            val userImageUrl = HandlePerson.GetUserImageURL(LoginActivity.userID)
            val activity = activity ?: return@requestHttp
            activity.runOnUiThread {
                sign_name_textview.text = userInfo.userName
                person_fans_number.text = userInfo.fansNumber.toString()
                person_follow_number.text = userInfo.focusNumber.toString()
                if (userImageUrl != null) {
                    this.userImageUrl = userImageUrl
                    val requestOption = RequestOptions().placeholder(R.drawable.ic_assignment_ind_deep_orange_200_48dp).error(R.drawable.ic_assignment_ind_deep_orange_200_48dp)
                    glide.load(userImageUrl).apply(requestOption).into(sign_in_icon)
                }
            }
        }
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
                    sign_in_icon.setImageResource(R.drawable.ic_assignment_ind_deep_orange_200_48dp)
                }
            }
        }.start()

    }


    override fun onClick(v: View) {
        if (checkLogin()) {
            val intent: Intent
            when (v.id) {
                R.id.fragment_person_my_tiezi -> {
                    intent = Intent(activity, UserOwnCommentActivity::class.java)
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
                    intent.putExtra(INFORMATION, FROM_FOCUS)
                    startActivityForResult(intent, FROM_FOCUS_AND_FANS_INFORMATION)
                }
                R.id.person_fans, R.id.person_fans_number -> {
                    intent = Intent(activity, FocusInformationActivity::class.java)
                    intent.putExtra(INFORMATION, FROM_FANS)
                    startActivityForResult(intent, FROM_FOCUS_AND_FANS_INFORMATION)
                }
                R.id.fragment_person_my_like -> {
                    intent = Intent(activity, UserLikeCommentActivity::class.java)
                    startActivity(intent)
                }
                R.id.fragment_person_my_collect -> {
                    intent = Intent(activity, MyCollectionActivity::class.java)
                    startActivity(intent)
                }
                R.id.fragment_person_my_message -> {
                    intent = Intent(activity, MyMessageActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    override fun getNeedOpenChooseImageView(): Array<View> {
        return arrayOf(sign_in_icon)
    }

    //设置用户头像的回调
    override fun setImageToImageView(bitmap: Bitmap) {
        val compressBitmap = HandlePic.compressBitmapToFile(bitmap, 192, 192)
        sign_in_icon.setImageBitmap(compressBitmap)
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
            if (menu.javaClass == MenuBuilder::class.java) {
                try {
                    val m = menu.javaClass.getDeclaredMethod("setOptionalIconsVisible", Boolean::class.java)
                    m.isAccessible = true
                    m.invoke(menu, true)
                } catch (e: Exception) {
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
            R.id.search_user -> {
                searchUser()
            }
            R.id.about_us -> {
                showAboutUs()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            IS_INTO_LOGIN -> {
                sign_name_textview.text = LoginActivity.userName
                GetUserInfo()
            }
            SETTING_ACTIVITY -> {
                if (resultCode == SIGN_OUT) {
                    checkLogin()
                    activity?.finish()
                }
            }
            FROM_FOCUS_AND_FANS_INFORMATION, FROM_SEARCH_ACTIVITY -> {
                if (resultCode == STATE_CHANGE) {
                    GetUserInfo()
                }
            }
        }
    }

    private fun sign_out() {
        NormalWarningDialog().setTitle("是否注销?")
                .setOnSubmitClickListener(object : NormalWarningDialog.onSubmitClickListener {
                    override fun onSubmit(view: View, dialog: NormalWarningDialog) {
                        activity?.getSharedPreferences("data", Context.MODE_PRIVATE)?.edit()?.clear()?.apply()//清除自动登录的信息
                        LoginActivity.userID = 0
                        LoginActivity.userName = null
                        activity?.finish()
                        checkLogin()
                    }
                })
                .setSubmitText("退出")
                .show(activity?.supportFragmentManager, "exitDialog")
    }

    private fun changePassword() {
        if (checkLogin()) {
            val activity = activity
            activity?.let {
                val dialog = ChangePasswordDialog()
                dialog.show(fragmentManager, "修改密码")
            }
        }
    }

    private fun searchUser() {
        val intent = Intent(activity, SearchActivity::class.java)
        intent.putExtra(SEARCH_TYPE, TYPE_USER)
        startActivityForResult(intent, FROM_SEARCH_ACTIVITY)
    }

    private fun showAboutUs() {
        val intent = Intent(activity, AboutUSActivity::class.java)
        startActivity(intent)
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
        private const val FROM_SEARCH_ACTIVITY = 3
    }
}
