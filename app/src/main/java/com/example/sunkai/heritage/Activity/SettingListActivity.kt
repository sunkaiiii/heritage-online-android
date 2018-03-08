package com.example.sunkai.heritage.Activity

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.CompoundButton
import android.widget.Spinner
import android.widget.Switch
import com.example.sunkai.heritage.Activity.LoginActivity.LoginActivity
import com.example.sunkai.heritage.ConnectWebService.HandlePerson
import com.example.sunkai.heritage.Data.GlobalContext
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.MakeToast
import com.example.sunkai.heritage.tools.ThreadPool
import com.example.sunkai.heritage.value.ALL
import com.example.sunkai.heritage.value.DENIALD
import com.example.sunkai.heritage.value.ONLYFOCUS

/**
 * 用户设置的activity
 * Created by sunkai on 2017/12/15.
 */
class SettingListActivity : AppCompatActivity(), CompoundButton.OnCheckedChangeListener,AdapterView.OnItemSelectedListener {

    private var sharePreferences:SharedPreferences?=null
    private lateinit var pushSwitch:Switch
    private lateinit var permissionSpinner:Spinner
    private lateinit var focusAndFansViewPermissionSpinner:Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_list)
        sharePreferences=getSharedPreferences("setting",Context.MODE_PRIVATE)
        initview()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        checkUserPermission()

    }
    private fun initview(){
        pushSwitch=findViewById(R.id.push_swith)
        permissionSpinner=findViewById(R.id.permission_spinner)
        focusAndFansViewPermissionSpinner=findViewById(R.id.permission_focus_fans_spinner)
        permissionSpinner.isEnabled=false
        focusAndFansViewPermissionSpinner.isEnabled=false
        pushSwitch.isChecked = sharePreferences!!.getBoolean("pushSwitch",false)

        pushSwitch.setOnCheckedChangeListener(this)
    }

    private fun checkUserPermission(){
        ThreadPool.execute{
            val permission= HandlePerson.GetUserPermission(LoginActivity.userID)
            val focusAndFansViewPermission= HandlePerson.GetUserFocusAndFansViewPermission(LoginActivity.userID)
            runOnUiThread {
                permissionSpinner.setSelection(permission+1,true)
                permissionSpinner.isEnabled=true
                focusAndFansViewPermissionSpinner.setSelection(focusAndFansViewPermission+1,true)
                focusAndFansViewPermissionSpinner.isEnabled=true

                //防止在check之后切换spinner状态的时候重复设定权限
                permissionSpinner.onItemSelectedListener = this
                focusAndFansViewPermissionSpinner.onItemSelectedListener=this
            }
        }

    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when(buttonView?.id){
            R.id.push_swith ->setPushStatus(isChecked)
        }
    }

    private fun setPushStatus(isChecked: Boolean){
        val editor=getSharedPreferences("setting",Context.MODE_PRIVATE).edit()
        if(isChecked) {
            GlobalContext.instance.registMipush() //在这里延迟一些注册用户，同时注册用户会注册失败，需要重新启动程序才可以
            ThreadPool.execute{
                Thread.sleep(5000)
                GlobalContext.instance.registUser()
            }

            editor?.putBoolean("pushSwitch",true)
            editor?.apply()
        }
        else {
            GlobalContext.instance.unregistUser()
            GlobalContext.instance.unregistMipush()
            editor?.putBoolean("pushSwitch",false)
            editor?.apply()
        }
    }

    private fun setPermission(id:Int,position: Int){
        val spinner=findViewById<Spinner>(id)
        when (position - 1) {
            DENIALD, ONLYFOCUS, ALL -> {
                spinner.isEnabled = false
                ThreadPool.execute {
                    val result:Boolean
                    result = when(id) {
                        R.id.permission_spinner-> HandlePerson.SetUserPermission(LoginActivity.userID, position - 1)
                        R.id.permission_focus_fans_spinner-> HandlePerson.SetUserFocusAndFansViewPermission(LoginActivity.userID,position-1)
                        else-> false
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
        when(parent?.id) {
            R.id.permission_spinner,R.id.permission_focus_fans_spinner->setPermission(parent.id,position)

        }
    }
    override fun onNothingSelected(parent: AdapterView<*>?) {    }
}
