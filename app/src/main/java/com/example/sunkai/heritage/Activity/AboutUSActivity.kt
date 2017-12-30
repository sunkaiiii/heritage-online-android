package com.example.sunkai.heritage.Activity

import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.example.sunkai.heritage.R

/**
 * Created by sunkai on 2017/12/29.
 *
 */
class AboutUSActivity:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)
        val actionBack:ActionBar?= supportActionBar
        actionBack?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            android.R.id.home->finish()
        }
        return super.onOptionsItemSelected(item)
    }
}