package com.example.sunkai.heritage.Activity

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity

import android.view.MenuItem
import android.widget.CompoundButton
import android.widget.Switch
import com.example.sunkai.heritage.Data.GlobalContext
import com.example.sunkai.heritage.R

/**
 * Created by sunkai on 2017/12/15.
 */
class SettingListActivity : AppCompatActivity(), CompoundButton.OnCheckedChangeListener {

    private var actionBack: ActionBar? = null
    private var sharePreferences:SharedPreferences?=null
    private var editor:SharedPreferences.Editor?=null
    private var pushSwitch:Switch?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.setting_list_activity)
        sharePreferences=getSharedPreferences("setting",Context.MODE_PRIVATE)
        editor=getSharedPreferences("setting",Context.MODE_PRIVATE).edit()
        initview()
        actionBack = supportActionBar
        actionBack?.setDisplayHomeAsUpEnabled(true)

    }
    private fun initview(){
        pushSwitch=findViewById(R.id.push_swith) as Switch
        pushSwitch?.isChecked = sharePreferences!!.getBoolean("pushSwitch",false)

        pushSwitch?.setOnCheckedChangeListener(this)
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when(buttonView?.id){
            R.id.push_swith ->setPushStatus(isChecked)
        }
    }

    private fun setPushStatus(isChecked: Boolean){
        if(isChecked) {
            GlobalContext.instance?.registMipush() //在这里延迟一些注册用户，同时注册用户会注册失败，需要重新启动程序才可以
            Thread{
                Thread.sleep(5000)
                GlobalContext.instance?.registUser()
            }.start()

            editor?.putBoolean("pushSwitch",true)
            editor?.apply()
        }
        else {
            GlobalContext.instance?.unregistUser()
            GlobalContext.instance?.unregistMipush()
            editor?.putBoolean("pushSwitch",false)
            editor?.apply()
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
