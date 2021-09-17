package com.example.sunkai.heritage.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.fragment.findNavController
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.dialog.LicenceDialog
import com.example.sunkai.heritage.fragment.baseFragment.BaseGlideFragment
import com.example.sunkai.heritage.tools.getThemeColor
import com.example.sunkai.heritage.value.VERSION_NAME

/**
 * Created by sunkai on 2017/12/29.
 *关于我们页面
 */
class AboutUSFragment : BaseGlideFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                AboutUsCard()
            }
        }
    }

    @Composable
    @Preview
    fun AboutUsCard() {
        val gitUrl = getString(R.string.program_git_url)
        val margin = Modifier.padding(8.dp)
        Column(Modifier.fillMaxSize()) {
            TopAppBar(title = {
                Text(getString(R.string.about_us))
            }, navigationIcon = {
                IconButton(onClick = { findNavController().popBackStack() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = getString(R.string.back))
                }
            }, backgroundColor = Color(getThemeColor()),contentColor = Color.White)
            Column(
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_launcher),
                    contentDescription = getString(R.string.logo),
                    Modifier
                        .size(48.dp, 48.dp)
                        .padding(0.dp, 0.dp, 0.dp, 12.dp)
                )
                Text(text = VERSION_NAME())
                ClickableText(text = AnnotatedString(gitUrl), onClick = {
                    val intent = Intent(Intent.ACTION_VIEW).setData(Uri.parse(gitUrl))
                    startActivity(intent)
                }, modifier = margin)
                ClickableText(text = AnnotatedString(getString(R.string.licence)), onClick = {
                    LicenceDialog().show(parentFragmentManager, "licence")
                })
            }
        }

    }

//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            android.R.id.home -> finish()
//        }
//        return super.onOptionsItemSelected(item)
//    }
}