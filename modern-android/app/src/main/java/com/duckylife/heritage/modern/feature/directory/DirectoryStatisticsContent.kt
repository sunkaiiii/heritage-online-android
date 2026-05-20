package com.duckylife.heritage.modern.feature.directory

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.DirectionsRun
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Festival
import androidx.compose.material.icons.outlined.LocalPharmacy
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.TheaterComedy
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.duckylife.heritage.modern.R
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemKind
import com.duckylife.heritage.modern.core.network.dto.DirectoryStatisticItemDto
import com.duckylife.heritage.modern.ui.component.HeritageContentCard
import com.duckylife.heritage.modern.ui.component.HeritageSectionHeader
import com.duckylife.heritage.modern.ui.error.fallbackResId

@Composable
fun DirectoryStatisticsContent(
    state: DirectoryStatisticsState,
    selectedKind: DirectoryItemKind,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when {
        state.isLoading -> StatisticsLoadingContent(modifier)
        state.errorKind != null -> StatisticsErrorContent(
            errorResId = state.errorKind.fallbackResId(),
            onRetry = onRetry,
            modifier = modifier,
        )
        state.overview == null -> StatisticsLoadingContent(modifier)
        else -> {
            val overview = state.overview
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                StatisticsOverviewCard(
                    total = overview.total,
                    kindLabel = stringResource(selectedKind.labelRes),
                    generatedAt = overview.generatedAt,
                    dimensionCount = overview.dimensions.size,
                )

                val yearItems = state.yearBreakdown?.items.orEmpty()
                if (yearItems.isNotEmpty()) {
                    HeritageSectionHeader(title = stringResource(R.string.statistics_year_title))
                    YearBarChart(
                        items = yearItems,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                val categoryItems = state.categoryBreakdown?.items.orEmpty()
                if (categoryItems.isNotEmpty()) {
                    HeritageSectionHeader(title = stringResource(R.string.statistics_category_title))
                    CategoryCardGrid(
                        items = categoryItems,
                        total = overview.total,
                    )
                }

                val regionItems = state.regionBreakdown?.items.orEmpty()
                if (regionItems.isNotEmpty()) {
                    HeritageSectionHeader(title = stringResource(R.string.statistics_region_title))
                    RegionRankingList(
                        items = regionItems.take(20),
                        maxValue = regionItems.firstOrNull()?.value ?: 1L,
                    )
                }

                if (yearItems.isEmpty() && categoryItems.isEmpty() && regionItems.isEmpty()) {
                    StatisticsEmptyContent()
                }

                Spacer(modifier = Modifier.height(18.dp))
            }
        }
    }
}

@Composable
private fun StatisticsOverviewCard(
    total: Long,
    kindLabel: String,
    generatedAt: String?,
    dimensionCount: Int,
) {
    HeritageContentCard {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = total.toString(),
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = kindLabel,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (!generatedAt.isNullOrBlank()) {
                Text(
                    text = stringResource(R.string.statistics_generated_at, generatedAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (dimensionCount > 0) {
                Text(
                    text = stringResource(R.string.statistics_dimensions_count, dimensionCount),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

private val BarColors = listOf(
    Color(0xFFE8B86D),
    Color(0xFFC74B4B),
    Color(0xFFA67C52),
    Color(0xFF6B8E6B),
    Color(0xFFD4956B),
    Color(0xFF8B6B5A),
    Color(0xFFB8865A),
    Color(0xFFCD7F5A),
)

@Composable
private fun YearBarChart(
    items: List<DirectoryStatisticItemDto>,
    modifier: Modifier = Modifier,
) {
    val maxValue = items.maxOf { it.value }
    val barWidth = 40.dp
    val barSpacing = 12.dp
    val labelHeight = 40.dp
    val countLabelHeight = 20.dp
    val chartHeight = 180.dp
    val totalBars = items.size
    val primaryColor = MaterialTheme.colorScheme.primary

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(barSpacing),
    ) {
        items.forEachIndexed { index, item ->
            val barRatio = if (maxValue > 0) item.value.toFloat() / maxValue else 0f
            val color = if (item.value == maxValue) primaryColor else BarColors[index % BarColors.size]
            val yearLabel = item.name ?: item.key ?: ""
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = item.value.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Canvas(
                    modifier = Modifier
                        .width(barWidth)
                        .height(chartHeight),
                ) {
                    val barHeight = size.height * barRatio.coerceAtLeast(0.02f)
                    drawRoundRect(
                        color = color,
                        topLeft = Offset(0f, size.height - barHeight),
                        size = Size(size.width, barHeight),
                        cornerRadius = CornerRadius(4f, 4f),
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = yearLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.width(barWidth),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

private val CategoryColors = listOf(
    Color(0xFFE8B86D),
    Color(0xFFC74B4B),
    Color(0xFF6B8E6B),
    Color(0xFFD4956B),
    Color(0xFF8B7355),
    Color(0xFFCD7F5A),
    Color(0xFF5B8C85),
    Color(0xFFB8865A),
    Color(0xFF9B6B5A),
    Color(0xFF7B8B6B),
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CategoryCardGrid(
    items: List<DirectoryStatisticItemDto>,
    total: Long,
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items.forEachIndexed { index, item ->
            val color = CategoryColors[index % CategoryColors.size]
            val icon = categoryIcon(item.name, item.key)
            val name = item.name ?: item.key ?: ""
            val percentage = if (total > 0) (item.value.toDouble() / total * 100.0) else 0.0
            CategoryCard(
                name = name,
                count = item.value,
                percentage = percentage,
                icon = icon,
                color = color,
            )
        }
    }
}

@Composable
private fun CategoryCard(
    name: String,
    count: Long,
    percentage: Double,
    icon: ImageVector,
    color: Color,
) {
    HeritageContentCard(
        modifier = Modifier.width(152.dp),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = name,
                tint = color,
                modifier = Modifier.size(28.dp),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = stringResource(R.string.statistics_percentage_format, percentage),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun RegionRankingList(
    items: List<DirectoryStatisticItemDto>,
    maxValue: Long,
) {
    HeritageContentCard {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items.forEachIndexed { index, item ->
                val name = item.name ?: item.key ?: ""
                val ratio = if (maxValue > 0) item.value.toFloat() / maxValue else 0f
                RegionRankRow(
                    rank = index + 1,
                    name = name,
                    count = item.value,
                    ratio = ratio,
                )
            }
        }
    }
}

@Composable
private fun RegionRankRow(
    rank: Int,
    name: String,
    count: Long,
    ratio: Float,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        when (rank) {
            1 -> Badge(containerColor = Color(0xFFFFD700)) { Text("1") }
            2 -> Badge(containerColor = Color(0xFFC0C0C0)) { Text("2") }
            3 -> Badge(containerColor = Color(0xFFCD7F32)) { Text("3") }
            else -> Text(
                text = rank.toString(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.width(20.dp),
                textAlign = TextAlign.Center,
            )
        }

        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(0.32f),
        )

        LinearProgressIndicator(
            progress = { ratio },
            modifier = Modifier.weight(0.52f).height(8.dp),
            color = if (rank <= 3) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHighest,
            trackColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        )

        Text(
            text = count.toString(),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.width(36.dp),
            textAlign = TextAlign.End,
        )
    }
}

@Composable
private fun StatisticsLoadingContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        CircularProgressIndicator()
        Text(
            text = stringResource(R.string.statistics_loading),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun StatisticsErrorContent(
    errorResId: Int,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(28.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.statistics_load_failed),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(errorResId),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(18.dp))
        Button(onClick = onRetry) {
            Text(stringResource(R.string.action_retry))
        }
    }
}

@Composable
private fun StatisticsEmptyContent() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.statistics_empty_message),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private fun categoryIcon(name: String?, key: String?): ImageVector {
    val text = (name ?: key ?: "").trim()
    return when {
        text.contains("音乐") -> Icons.Outlined.MusicNote
        text.contains("舞蹈") -> Icons.AutoMirrored.Outlined.DirectionsRun
        text.contains("戏剧") || text.contains("戏曲") -> Icons.Outlined.TheaterComedy
        text.contains("曲艺") -> Icons.Outlined.Mic
        text.contains("体育") || text.contains("杂技") || text.contains("游艺") -> Icons.Outlined.EmojiEvents
        text.contains("美术") -> Icons.Outlined.Palette
        text.contains("技艺") -> Icons.Outlined.Build
        text.contains("医药") || text.contains("医学") -> Icons.Outlined.LocalPharmacy
        text.contains("民俗") -> Icons.Outlined.Festival
        text.contains("文学") || text.contains("传说") || text.contains("故事") -> Icons.AutoMirrored.Outlined.MenuBook
        else -> Icons.Outlined.EmojiEvents
    }
}
