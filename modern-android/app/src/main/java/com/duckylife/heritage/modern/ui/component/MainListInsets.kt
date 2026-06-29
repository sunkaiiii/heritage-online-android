package com.duckylife.heritage.modern.ui.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

/**
 * 主 Tab 列表底部 padding：系统导航栏 + 底部导航栏近似高度 + 额外间距。
 *
 * 四个主入口（文章、名录、传承人、发现）的 LazyColumn 统一使用，避免底部内容
 * 被底部导航栏遮挡。
 */
@Composable
fun mainTabContentPadding(extraDp: Int = 16): PaddingValues {
    val navigationBars = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    return PaddingValues(bottom = navigationBars + BottomNavHeightDp + extraDp.dp)
}

private val BottomNavHeightDp = 80.dp
