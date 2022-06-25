package com.example.sunkai.heritage.views

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.sunkai.heritage.tools.EHeritageDarkColorScheme
import com.example.sunkai.heritage.tools.EHeritageLightColorScheme

@Composable
fun EHeritageComposeTheme(
    dark:Boolean = isSystemInDarkTheme(),
    dynamic:Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S,
    content:@Composable ()->Unit
){
    val colorScheme = if(dynamic){
        val context = LocalContext.current
        if(dark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }else{
        if(dark) EHeritageDarkColorScheme else EHeritageLightColorScheme
    }

    MaterialTheme(colorScheme=colorScheme,content=content)
}