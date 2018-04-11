package com.example.sunkai.heritage.Activity

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.CompoundButton
import android.widget.Spinner
import androidx.core.content.edit
import com.example.sunkai.heritage.Activity.LoginActivity.LoginActivity
import com.example.sunkai.heritage.ConnectWebService.HandlePerson
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.MakeToast
import com.example.sunkai.heritage.tools.ThreadPool
import com.example.sunkai.heritage.value.ALL
import com.example.sunkai.heritage.value.DENIALD
import com.example.sunkai.heritage.value.ONLYFOCUS
import kotlinx.android.synthetic.main.activity_setting_list.*

/**
 * 用户设置的activity
 * Created by sunkai on 2017/12/15.
 */
class SettingListActivity : AppCompatActivity(), CompoundButton.OnCheckedChangeListener, AdapterView.OnItemSelectedListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_list)
        initview()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        checkUserPermission()
    }

    private fun initview() {
        permissionSpinner.isEnabled = false
        focusAndFansViewPermissionSpinner.isEnabled = false
        pushSwitch.setOnCheckedChangeListener(this)
        val sharedPreferences = getSharedPreferences(SETTING, Context.MODE_PRIVATE) ?: return
        pushSwitch.isChecked = sharedPreferences.getBoolean("pushSwitch", false)

    }

    private fun checkUserPermission() {
        ThreadPool.execute {
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

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when (buttonView?.id) {
            R.id.pushSwitch -> setPushStatus(isChecked)
        }
    }

    private fun setPushStatus(isChecked: Boolean) {
        getSharedPreferences(SETTING, Context.MODE_PRIVATE).edit {
            putBoolean("pushSwitch", isChecked)
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
                ThreadPool.execute {
                    val result: Boolean
                    result = when (id) {
                        R.id.permissionSpinner -> HandlePerson.SetUserPermission(LoginActivity.userID, position - 1)
                        R.id.focusAndFansViewPermissionSpinner -> HandlePerson.SetUserFocusAndFansViewPermission(LoginActivity.userID, position - 1)
                        else -> false
                    }
                    runOnUiThread {
                        spinner.isEnabled = true
                        if (!result) {
                            MakeToast.MakeText(getString(R.string.had_error))
                        }
                    }

                }
            }
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (parent?.id) {
            R.id.permissionSpinner, R.id.focusAndFansViewPermissionSpinner -> setPermission(parent.id, position)

        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    companion object {
        const val SETTING = "setting"
    }
}
