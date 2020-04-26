package com.example.sunkai.heritage.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.activity.base.BaseGlideActivity
import com.example.sunkai.heritage.dialog.LicenceDialog
import com.example.sunkai.heritage.value.VERSION_NAME
import kotlinx.android.synthetic.main.activity_about_us.*

/**
 * Created by sunkai on 2017/12/29.
 *关于我们页面
 */
class AboutUSActivity : BaseGlideActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)
        val actionBack: ActionBar? = supportActionBar
        actionBack?.setDisplayHomeAsUpEnabled(true)
        licenceTextView.setOnClickListener {
            LicenceDialog().show(supportFragmentManager, "licence")
        }
        base_git_url.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW).setData(Uri.parse(base_git_url.text.toString()))
            startActivity(intent)
        }

        base_version.text = VERSION_NAME()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }
}