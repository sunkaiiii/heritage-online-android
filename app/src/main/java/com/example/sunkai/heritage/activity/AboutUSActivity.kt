package com.example.sunkai.heritage.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.ActionBar
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.sunkai.heritage.R
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
        binding.baseVersion.setContent {
            AboutUsCard()
        }
    }

    @Composable
    @Preview
    fun AboutUsCard() {
        val gitUrl = getString(R.string.program_git_url)
        val margin = Modifier.padding(8.dp)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.ic_launcher),
                contentDescription = getString(R.string.logo),
                Modifier.size(48.dp,48.dp).padding(0.dp, 0.dp, 0.dp, 12.dp)
            )
            Text(text = VERSION_NAME())
            ClickableText(text = AnnotatedString(gitUrl), onClick = {
                val intent = Intent(Intent.ACTION_VIEW).setData(Uri.parse(gitUrl))
                startActivity(intent)
            }, modifier = margin)
            ClickableText(text = AnnotatedString(getString(R.string.licence)), onClick = {
                LicenceDialog().show(supportFragmentManager, "licence")
            })
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }
}