package com.example.sunkai.heritage

import android.app.AlertDialog
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.example.sunkai.heritage.Data.GlobalContext
import com.xiaomi.mipush.sdk.MiPushClient

/**
 * Created by sunkai on 2017/12/15.
 */
class ConnectUsActivity : AppCompatActivity() {

    private var actionBack: ActionBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connect_us)
        actionBack = supportActionBar
        actionBack?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
