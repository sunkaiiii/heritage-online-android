package com.duckylife.heritage.modern.ui.component

import android.view.View
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/**
 * 主 Tab 列表底部 padding：系统导航栏 + 底部导航栏近似高度 + 额外间距。
 *
 * 四个主入口（文章、名录、传承人、发现）的 LazyColumn 统一使用，避免底部内容
 * 被底部导航栏遮挡。
 *
 * 这里同时读取 [WindowInsets.navigationBars] 与根视图的 [WindowInsetsCompat]，
 * 避免 Scaffold 等父级消费 insets 后导致 Compose 侧拿到 0。
 */
@Composable
fun mainTabContentPadding(extraDp: Int = 16): PaddingValues {
    val view: View = LocalView.current
    val density = LocalDensity.current
    val rootNavBottom = remember(view) {
        val px = ViewCompat.getRootWindowInsets(view)
            ?.getInsets(WindowInsetsCompat.Type.navigationBars())
            ?.bottom ?: 0
        with(density) { px.toDp() }
    }
    val composeNavBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val navigationBarBottom = rootNavBottom.coerceAtLeast(composeNavBottom)
    return PaddingValues(bottom = navigationBarBottom + BottomNavHeightDp + extraDp.dp)
}

private val BottomNavHeightDp = 80.dp
