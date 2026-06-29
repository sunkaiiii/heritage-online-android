package com.duckylife.heritage.modern.ui.component

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 在可横向滚动组件的水平两端绘制渐隐遮罩，提示用户左右仍有内容。
 *
 * @param edgeWidth 渐隐边距宽度。
 * @param color 遮罩颜色，必须与组件背景色一致才能形成“淡出”效果。
 */
fun Modifier.horizontalFadingEdge(
    edgeWidth: Dp = 16.dp,
    color: Color,
): Modifier = if (edgeWidth.value <= 0f) {
    this
} else {
    this.drawWithContent {
        val width = edgeWidth.toPx()
        drawContent()
        // 左侧淡出
        drawRect(
            brush = Brush.horizontalGradient(
                0f to color,
                1f to Color.Transparent,
                startX = 0f,
                endX = width,
            ),
            size = Size(width, size.height),
        )
        // 右侧淡出
        drawRect(
            brush = Brush.horizontalGradient(
                0f to Color.Transparent,
                1f to color,
                startX = size.width - width,
                endX = size.width,
            ),
            topLeft = Offset(size.width - width, 0f),
            size = Size(width, size.height),
        )
    }
}
