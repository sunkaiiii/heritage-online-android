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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.ImageLoader
import com.duckylife.heritage.modern.R
import com.duckylife.heritage.modern.core.network.dto.DiscoveryItemDto
import com.duckylife.heritage.modern.core.network.dto.MediaAssetDto
import com.duckylife.heritage.modern.core.image.rememberHeritageImageLoader
import com.duckylife.heritage.modern.core.image.rememberHeritageUrlResolver
import com.duckylife.heritage.modern.core.network.HeritageUrlResolver
import com.duckylife.heritage.modern.core.network.resolvedBestUrl

@Composable
fun DiscoveryItemCard(
    item: DiscoveryItemDto,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showImageSlot: Boolean = true,
    imageLoader: ImageLoader = rememberHeritageImageLoader(),
    resolver: HeritageUrlResolver = rememberHeritageUrlResolver(),
) {
    val imageUrl = item.coverImage.resolvedBestUrl(resolver)

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
                        HeritageMetaChip(text = com.duckylife.heritage.modern.ui.text.localizedContentType(item.type))
                    }
                    val localizedCat = com.duckylife.heritage.modern.ui.text.localizedArticleCategory(item.category)
                    if (localizedCat != null) {
                        Spacer(modifier = Modifier.width(4.dp))
                        HeritageMetaChip(text = localizedCat)
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
    resolver: HeritageUrlResolver = rememberHeritageUrlResolver(),
) {
    val imageUrl = item.coverImage.resolvedBestUrl(resolver)

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
                        HeritageMetaChip(text = com.duckylife.heritage.modern.ui.text.localizedContentType(item.type))
                    }
                    val localizedCat = com.duckylife.heritage.modern.ui.text.localizedArticleCategory(item.category)
                    if (localizedCat != null) {
                        Spacer(modifier = Modifier.width(4.dp))
                        HeritageMetaChip(text = localizedCat)
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

