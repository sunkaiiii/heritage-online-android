package com.example.sunkai.heritage.Activity

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.CompoundButton
import android.widget.Spinner
import androidx.core.content.edit
import androidx.recyclerview.widget.GridLayoutManager
import com.example.sunkai.heritage.Activity.BaseActivity.BaseGlideActivity
import com.example.sunkai.heritage.Activity.LoginActivity.LoginActivity
import com.example.sunkai.heritage.Adapter.SettingListSelectThemeColorAdapter
import com.example.sunkai.heritage.ConnectWebService.HandlePerson
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.MakeToast.toast
import com.example.sunkai.heritage.tools.Utils
import com.example.sunkai.heritage.tools.Views.GridLayoutManagerItemDecoration
import com.example.sunkai.heritage.value.*
import kotlinx.android.synthetic.main.activity_setting_list.*

/**
 * 用户设置的activity
 * Created by sunkai on 2017/12/15.
 */
class SettingListActivity : BaseGlideActivity(), CompoundButton.OnCheckedChangeListener, AdapterView.OnItemSelectedListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_list)
        initview()
        checkUserPermission()
        readThemeColorList()
    }

    override fun setNeedChangeThemeColorWidget() {
        changeThemeWidge.add(R.id.permissionTextView)
        changeThemeWidge.add(R.id.focusAndFansPermisstionTextView)
        changeThemeWidge.add(R.id.pushSwitch)
    }

    private fun initview() {
        permissionSpinner.isEnabled = false
        focusAndFansViewPermissionSpinner.isEnabled = false
        pushSwitch.setOnCheckedChangeListener(this)
        val sharedPreferences = getSharedPreferences(SETTING, Context.MODE_PRIVATE) ?: return
        pushSwitch.isChecked = sharedPreferences.getBoolean(PUSH_SWITCH, false)

    }

    private fun checkUserPermission() {
        requestHttp {
            val permission = HandlePerson.GetUserPermission(LoginActivity.userID)
            val focusAndFansViewPermission = HandlePerson.GetUserFocusAndFansViewPermission(LoginActivity.userID)
            runOnUiThread {
                permissionSpinner.setSelection(permission + 1, true)
                permissionSpinner.isEnabled = true
                focusAndFansViewPermissionSpinner.setSelection(focusAndFansViewPermission + 1, true)
                focusAndFansViewPermissionSpinner.isEnabled = true

                //防止在check之后切换spinner状态的时候重复设定权限
                permissionSpinner.onItemSelectedListener = this
                focusAndFansViewPermissionSpinner.onItemSelectedListener = this
            }
        }

    }

    private fun readThemeColorList(){
        val adapter=SettingListSelectThemeColorAdapter(this, THEME_COLOR_ARRAYS.asList(),glide)
        val spanCount=(Utils.getScreenWidth()/SettingListSelectThemeColorAdapter.IMAGE_VIEW_SIZE)-1
        themeColorList.layoutManager=GridLayoutManager(this,spanCount)
        themeColorList.addItemDecoration(GridLayoutManagerItemDecoration(spanCount, Utils.dip2px(8)))
        themeColorList.adapter=adapter
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when (buttonView?.id) {
            R.id.pushSwitch -> setPushStatus(isChecked)
        }
    }

    private fun setPushStatus(isChecked: Boolean) {
        getSharedPreferences(SETTING, Context.MODE_PRIVATE).edit {
            putBoolean(PUSH_SWITCH, isChecked)
        }
        if(isChecked){
            MainActivity.activityRef?.get()?.startPushService()
        }else{
            MainActivity.activityRef?.get()?.doUnbindService()
        }
    }

    private fun setPermission(id: Int, position: Int) {
        val spinner = findViewById<Spinner>(id)
        when (position - 1) {
            DENIALD, ONLYFOCUS, ALL -> {
                spinner.isEnabled = false
                requestHttp {
                    val result: Boolean = when (id) {
                        R.id.permissionSpinner -> HandlePerson.SetUserPermission(LoginActivity.userID, position - 1)
                        R.id.focusAndFansViewPermissionSpinner -> HandlePerson.SetUserFocusAndFansViewPermission(LoginActivity.userID, position - 1)
                        else -> false
                    }
                    runOnUiThread {
                        spinner.isEnabled = true
                        if (!result) {
                            toast(getString(R.string.had_error))
                        }
                    }

                }
            }
        }
    }



    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (parent?.id) {
            R.id.permissionSpinner, R.id.focusAndFansViewPermissionSpinner -> setPermission(parent.id, position)

        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

}
