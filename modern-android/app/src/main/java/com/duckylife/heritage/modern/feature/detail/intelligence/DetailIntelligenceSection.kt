package com.duckylife.heritage.modern.feature.detail.intelligence

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.duckylife.heritage.modern.R
import com.duckylife.heritage.modern.core.data.IntelligenceSection
import com.duckylife.heritage.modern.core.network.dto.advanced.AiCardDto
import com.duckylife.heritage.modern.core.network.dto.advanced.SectionStatus
import com.duckylife.heritage.modern.ui.component.HeritageContentCard

private const val HIGHLIGHT_PREVIEW_COUNT = 3
private const val KEYWORD_MAX_COUNT = 6

/**
 * 详情页“智能解读”区块。
 *
 * 只处理 V3 content page 的 AI 板块；digest、graph、recommendations 仍由现有
 * [com.duckylife.heritage.modern.ui.component.DetailExploreSection] 消费，避免步骤 23 之前重复请求。
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DetailIntelligenceSection(
    uiState: ContentIntelligenceUiState,
    onKeywordClick: (String) -> Unit,
    onRetry: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    when {
        uiState.isLoading -> IntelligenceSkeleton(modifier = modifier)
        uiState.aiSection.status == SectionStatus.Ready -> {
            val card = uiState.aiSection.data
            if (card != null) {
                AiCard(
                    card = card,
                    onKeywordClick = onKeywordClick,
                    modifier = modifier,
                )
            }
        }

        uiState.aiSection.status == SectionStatus.Missing ||
            uiState.aiSection.status == SectionStatus.Empty -> {
            LowKeyMessage(
                text = stringResource(R.string.intelligence_not_available),
                modifier = modifier,
            )
        }

        uiState.loadError != null ||
            uiState.aiSection.status == SectionStatus.Unavailable -> {
            IntelligenceUnavailable(
                onRetry = onRetry,
                modifier = modifier,
            )
        }

        else -> Unit
    }
}

@Composable
private fun IntelligenceSkeleton(modifier: Modifier = Modifier) {
    HeritageContentCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Header skeleton
            Row(verticalAlignment = Alignment.CenterVertically) {
            SkeletonBox(width = 24.dp, height = 24.dp)
                Spacer(modifier = Modifier.size(8.dp))
                SkeletonBox(width = 96.dp, height = 18.dp)
            }
            SkeletonBox(width = 220.dp, height = 14.dp)
            SkeletonBox(width = 180.dp, height = 14.dp)
        }
    }
}

@Composable
private fun SkeletonBox(width: androidx.compose.ui.unit.Dp, height: androidx.compose.ui.unit.Dp) {
    val placeholderColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .size(width, height)
            .shimmerPlaceholder(placeholderColor),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AiCard(
    card: AiCardDto,
    onKeywordClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    val hasSummary = !card.summary.isNullOrBlank()
    val hasShortSummary = !card.shortSummary.isNullOrBlank()
    val summaryText = when {
        expanded || !hasShortSummary -> card.summary
        else -> card.shortSummary
    }

    HeritageContentCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = stringResource(R.string.intelligence_section_title),
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
                if (card.isStale) {
                    Text(
                        text = stringResource(R.string.intelligence_stale_badge),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            // Summary
            if (!summaryText.isNullOrBlank()) {
                Column {
                    Text(
                        text = summaryText,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = if (expanded || !hasShortSummary) Int.MAX_VALUE else 3,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (hasSummary && hasShortSummary) {
                        TextButton(
                            onClick = { expanded = !expanded },
                            modifier = Modifier.padding(top = 2.dp),
                        ) {
                            Text(
                                text = stringResource(
                                    if (expanded) R.string.intelligence_collapse
                                    else R.string.intelligence_expand,
                                ),
                            )
                        }
                    }
                }
            }

            // Highlights
            val highlights = card.highlights
            if (highlights.isNotEmpty()) {
                var showAllHighlights by remember { mutableStateOf(false) }
                val visibleHighlights = if (showAllHighlights) {
                    highlights
                } else {
                    highlights.take(HIGHLIGHT_PREVIEW_COUNT)
                }
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    visibleHighlights.forEach { highlight ->
                        Row {
                            Text(
                                text = "•",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(end = 8.dp),
                            )
                            Text(
                                text = highlight,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                    if (highlights.size > HIGHLIGHT_PREVIEW_COUNT) {
                        TextButton(
                            onClick = { showAllHighlights = !showAllHighlights },
                        ) {
                            Text(
                                text = if (showAllHighlights) {
                                    stringResource(R.string.intelligence_collapse)
                                } else {
                                    stringResource(
                                        R.string.intelligence_show_all_n,
                                        highlights.size,
                                    )
                                },
                            )
                        }
                    }
                }
            }

            // Keywords
            if (card.keywords.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    card.keywords.take(KEYWORD_MAX_COUNT).forEach { keyword ->
                        AssistChip(
                            onClick = { onKeywordClick(keyword) },
                            label = { Text(keyword) },
                        )
                    }
                }
            }

            // Source note
            if (card.hasAi && !card.isStale) {
                Text(
                    text = stringResource(R.string.intelligence_based_on_local_ai),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun LowKeyMessage(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
    )
}

@Composable
private fun IntelligenceUnavailable(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    HeritageContentCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = stringResource(R.string.intelligence_unavailable),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            TextButton(onClick = onRetry) {
                Text(text = stringResource(R.string.intelligence_retry))
            }
        }
    }
}

private fun Modifier.shimmerPlaceholder(color: Color): Modifier =
    this.then(
        drawBehind {
            drawRect(color = color)
        },
    )
