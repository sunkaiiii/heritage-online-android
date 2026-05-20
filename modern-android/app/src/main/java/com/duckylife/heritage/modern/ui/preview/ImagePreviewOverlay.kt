package com.duckylife.heritage.modern.ui.preview

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil3.ImageLoader
import com.duckylife.heritage.modern.R
import com.github.panpf.zoomimage.CoilZoomAsyncImage

@Composable
fun ImagePreviewOverlay(
    imageUrls: List<String>,
    initialIndex: Int,
    imageLoader: ImageLoader,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (imageUrls.isEmpty()) return

    BackHandler(onBack = onDismiss)

    val pagerState = rememberPagerState(initialPage = initialIndex.coerceIn(0, (imageUrls.size - 1).coerceAtLeast(0))) {
        imageUrls.size
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.96f)),
    ) {
        // 图片分页 ��� 先绘制，在底层
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
        ) { page ->
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CoilZoomAsyncImage(
                    model = imageUrls[page],
                    imageLoader = imageLoader,
                    contentDescription = stringResource(R.string.preview_image),
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }

        // 顶部操作栏 — 后绘制，在上层，zIndex 确保点击不被 Pager 拦截
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .zIndex(1f)
                .statusBarsPadding()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = stringResource(R.string.preview_close),
                    tint = Color.White,
                )
            }
            Text(
                text = stringResource(
                    R.string.preview_page_indicator,
                    pagerState.currentPage + 1,
                    imageUrls.size,
                ),
                style = MaterialTheme.typography.labelLarge,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}
