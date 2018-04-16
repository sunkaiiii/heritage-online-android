package com.example.sunkai.heritage.Activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import androidx.core.net.toUri
import com.example.sunkai.heritage.Dialog.LicenceDialog
import com.example.sunkai.heritage.R
import kotlinx.android.synthetic.main.activity_about_us.*

/**
 * Created by sunkai on 2017/12/29.
 *关于我们页面
 */
class AboutUSActivity:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)
        val actionBack:ActionBar?= supportActionBar
        actionBack?.setDisplayHomeAsUpEnabled(true)
        licenceTextView.setOnClickListener {
            LicenceDialog().show(supportFragmentManager,"licence")
        }
        base_git_url.setOnClickListener {
            val intent=Intent(Intent.ACTION_VIEW).setData(base_git_url.text.toString().toUri())
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            android.R.id.home->finish()
        }
        return super.onOptionsItemSelected(item)
    }
}