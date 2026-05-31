package com.duckylife.heritage.modern.ui.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.duckylife.heritage.modern.R
import com.duckylife.heritage.modern.core.network.dto.BlendedRecommendationItemDto
import com.duckylife.heritage.modern.core.network.dto.BlendedRecommendationResponseDto

@Composable
fun BlendedRecommendationsSection(
    recommendations: BlendedRecommendationResponseDto?,
    onItemClick: (BlendedRecommendationItemDto) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (recommendations == null || recommendations.items.isEmpty()) return

    Column(modifier = modifier.fillMaxWidth()) {
        HeritageSectionHeader(
            title = stringResource(R.string.blended_title),
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(recommendations.items) { item ->
                BlendedRecommendationCard(
                    item = item,
                    onClick = { onItemClick(item) },
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BlendedRecommendationCard(
    item: BlendedRecommendationItemDto,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showAllReasons by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .width(220.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        ),
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .animateContentSize(),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            // 标题
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            // 副标题
            if (!item.subtitle.isNullOrBlank()) {
                Text(
                    text = item.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            // 分类/地区 chips
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                if (!item.category.isNullOrBlank()) {
                    HeritageMetaChip(text = item.category)
                }
                if (!item.region.isNullOrBlank()) {
                    Spacer(modifier = Modifier.width(4.dp))
                    HeritageMetaChip(text = item.region)
                }
            }

            // 推荐理由
            if (item.reasons.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.blended_reasons),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                )
                val displayReasons = if (showAllReasons) {
                    item.reasons
                } else {
                    item.reasons.take(2)
                }
                displayReasons.forEach { reason ->
                    Text(
                        text = "• $reason",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                if (item.reasons.size > 2 && !showAllReasons) {
                    TextButton(
                        onClick = { showAllReasons = true },
                        modifier = Modifier.padding(0.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.blended_show_more_reasons),
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                }
            }

            // Score breakdown 小条
            ScoreBreakdownBar(item = item)
        }
    }
}

@Composable
private fun ScoreBreakdownBar(
    item: BlendedRecommendationItemDto,
    modifier: Modifier = Modifier,
) {
    val breakdown = item.scoreBreakdown
    val total = breakdown.explicit + breakdown.inferred + breakdown.embedding +
        breakdown.sameCategory + breakdown.sameRegion
    if (total <= 0.0) return

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = stringResource(R.string.blended_score),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
        ) {
            if (breakdown.explicit > 0) {
                Box(
                    modifier = Modifier
                        .weight((breakdown.explicit / total).toFloat())
                        .height(6.dp)
                        .background(MaterialTheme.colorScheme.primary),
                )
            }
            if (breakdown.inferred > 0) {
                Box(
                    modifier = Modifier
                        .weight((breakdown.inferred / total).toFloat())
                        .height(6.dp)
                        .background(MaterialTheme.colorScheme.secondary),
                )
            }
            if (breakdown.embedding > 0) {
                Box(
                    modifier = Modifier
                        .weight((breakdown.embedding / total).toFloat())
                        .height(6.dp)
                        .background(MaterialTheme.colorScheme.tertiary),
                )
            }
            if (breakdown.sameCategory > 0) {
                Box(
                    modifier = Modifier
                        .weight((breakdown.sameCategory / total).toFloat())
                        .height(6.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                )
            }
            if (breakdown.sameRegion > 0) {
                Box(
                    modifier = Modifier
                        .weight((breakdown.sameRegion / total).toFloat())
                        .height(6.dp)
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                )
            }
        }
    }
}
