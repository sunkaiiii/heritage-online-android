package com.example.sunkai.heritage.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import com.example.sunkai.heritage.activity.base.BaseGlideActivity
import com.example.sunkai.heritage.databinding.ActivityAboutUsBinding
import com.example.sunkai.heritage.dialog.LicenceDialog
import com.example.sunkai.heritage.value.VERSION_NAME

/**
 * Created by sunkai on 2017/12/29.
 *关于我们页面
 */
class AboutUSActivity : BaseGlideActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAboutUsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val actionBack: ActionBar? = supportActionBar
        actionBack?.setDisplayHomeAsUpEnabled(true)
        binding.licenceTextView.setOnClickListener {
            LicenceDialog().show(supportFragmentManager, "licence")
        }
        binding.baseGitUrl.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW).setData(Uri.parse(binding.baseGitUrl.text.toString()))
            startActivity(intent)
        }

        binding.baseVersion.text = VERSION_NAME()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }
}