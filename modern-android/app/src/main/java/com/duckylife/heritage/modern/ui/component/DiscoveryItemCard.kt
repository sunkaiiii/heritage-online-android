package com.duckylife.heritage.modern.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.ImageLoader
import com.duckylife.heritage.modern.core.network.dto.DiscoveryItemDto
import com.duckylife.heritage.modern.core.network.dto.MediaAssetDto
import com.duckylife.heritage.modern.core.image.rememberHeritageImageLoader

private fun MediaAssetDto?.bestUrl(): String? =
    this?.let { displayUrl ?: thumbnailUrl ?: originalUrl ?: sourceUrl }

@Composable
fun DiscoveryItemCard(
    item: DiscoveryItemDto,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showImageSlot: Boolean = true,
    imageLoader: ImageLoader = rememberHeritageImageLoader(),
) {
    val imageUrl = item.coverImage.bestUrl()

    HeritageContentCard(
        onClick = onClick,
        modifier = modifier.width(220.dp),
    ) {
        Column {
            // 图片区域（有图显示图，无图显示占位）
            if (showImageSlot) {
                if (imageUrl != null) {
                    HeritageListImage(
                        imageUrl = imageUrl,
                        imageLoader = imageLoader,
                        contentDescription = item.title,
                        fallbackText = item.title.take(1),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(118.dp)
                            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(118.dp)
                            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = item.title.take(1),
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                if (!item.summary.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.summary,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row {
                    if (item.type.isNotBlank()) {
                        HeritageMetaChip(text = localizedTypeChip(item.type))
                    }
                    if (!item.category.isNullOrBlank()) {
                        Spacer(modifier = Modifier.width(4.dp))
                        HeritageMetaChip(text = item.category)
                    }
                    if (!item.region.isNullOrBlank()) {
                        Spacer(modifier = Modifier.width(4.dp))
                        HeritageMetaChip(text = item.region)
                    }
                }
            }
        }
    }
}

@Composable
fun DiscoveryItemRow(
    item: DiscoveryItemDto,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showImageSlot: Boolean = true,
    imageLoader: ImageLoader = rememberHeritageImageLoader(),
) {
    val imageUrl = item.coverImage.bestUrl()

    HeritageContentCard(
        onClick = onClick,
        modifier = modifier,
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            // 左侧缩略图（有图显示图，无图显示占位）
            if (showImageSlot) {
                if (imageUrl != null) {
                    HeritageListImage(
                        imageUrl = imageUrl,
                        imageLoader = imageLoader,
                        contentDescription = item.title,
                        fallbackText = item.title.take(1),
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(8.dp)),
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(
                                MaterialTheme.colorScheme.surfaceContainerHighest,
                                RoundedCornerShape(8.dp),
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = item.title.take(1),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                if (!item.summary.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.summary,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row {
                    if (item.type.isNotBlank()) {
                        HeritageMetaChip(text = localizedTypeChip(item.type))
                    }
                    if (!item.category.isNullOrBlank()) {
                        Spacer(modifier = Modifier.width(4.dp))
                        HeritageMetaChip(text = item.category)
                    }
                    if (!item.region.isNullOrBlank()) {
                        Spacer(modifier = Modifier.width(4.dp))
                        HeritageMetaChip(text = item.region)
                    }
                }
            }
        }
    }
}

// 本地化 type chip 显示文本
@Composable
private fun localizedTypeChip(type: String): String =
    when (type) {
        "article" -> "文章"
        "directoryItem" -> "名录"
        "inheritor" -> "传承人"
        else -> type
    }
