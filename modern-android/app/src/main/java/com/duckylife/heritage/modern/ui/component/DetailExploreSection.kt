package com.duckylife.heritage.modern.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.duckylife.heritage.modern.R
import com.duckylife.heritage.modern.core.network.dto.BlendedRecommendationResponseDto
import com.duckylife.heritage.modern.core.network.dto.ContentDigestDto
import com.duckylife.heritage.modern.core.network.dto.DetailContextDto
import com.duckylife.heritage.modern.feature.detail.DetailContextTarget
import com.duckylife.heritage.modern.feature.detail.DetailExploreSource
import com.duckylife.heritage.modern.feature.detail.DetailExploreTargetClick
import com.duckylife.heritage.modern.feature.detail.contextItemTarget
import com.duckylife.heritage.modern.ui.error.ErrorKind
import com.duckylife.heritage.modern.ui.error.fallbackResId

/**
 * 统一的详情页底部探索组件。
 *
 * 顺序：Digest -> Blended -> Related -> Recommendations -> Semantic -> Graph -> Collections -> Topics
 *
 * 三类详情页共用此组件，减少重复。
 */
@Composable
fun DetailExploreSection(
    // Digest
    digest: ContentDigestDto?,
    digestLoading: Boolean,
    digestErrorKind: ErrorKind?,
    onDigestRetry: () -> Unit,
    // Blended Recommendations
    blendedRecommendations: BlendedRecommendationResponseDto?,
    // Context
    context: DetailContextDto?,
    contextLoading: Boolean,
    contextErrorKind: ErrorKind?,
    onContextRetry: () -> Unit,
    // Navigation
    onExploreTargetClick: (DetailExploreTargetClick) -> Unit,
    modifier: Modifier = Modifier,
) {
    // 空壳隐藏：如果所有区块都没有数据、没有 loading、没有 error，则不显示
    val hasDigestBlock = digest != null || digestLoading || digestErrorKind != null
    val validBlendedItems = blendedRecommendations?.items.orEmpty()
        .filter { contextItemTarget(it.id, it.type) != null }
    val hasBlendedBlock = validBlendedItems.isNotEmpty()
    val hasContextBlock = context != null || contextLoading || contextErrorKind != null
    if (!hasDigestBlock && !hasBlendedBlock && !hasContextBlock) return

    Column(modifier = modifier.fillMaxWidth()) {
        // 总标题
        HeritageSectionHeader(
            title = stringResource(R.string.detail_explore_title),
            modifier = Modifier.padding(bottom = 4.dp),
        )
        Text(
            text = stringResource(R.string.detail_explore_subtitle),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        // 1. Digest 速览
        if (digest != null) {
            DigestCard(digest = digest)
            Spacer(modifier = Modifier.height(16.dp))
        } else if (digestLoading) {
            LoadingPlaceholder()
            Spacer(modifier = Modifier.height(16.dp))
        } else if (digestErrorKind != null) {
            ErrorRetryRow(
                errorKind = digestErrorKind,
                onRetry = onDigestRetry,
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 2. 综合推荐
        if (validBlendedItems.isNotEmpty()) {
            BlendedRecommendationsSection(
                recommendations = blendedRecommendations,
                onItemClick = { item ->
                    contextItemTarget(item.id, item.type)?.let { target ->
                        onExploreTargetClick(
                            DetailExploreTargetClick(
                                target = target,
                                source = DetailExploreSource.BlendedRecommendation,
                                title = item.title,
                                subtitle = item.subtitle,
                                category = item.category,
                                region = item.region,
                                imageUrl = item.coverImage?.displayUrl,
                            ),
                        )
                    }
                },
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 3-8. Context 区块
        DetailContextSection(
            context = context,
            isLoading = contextLoading,
            errorKind = contextErrorKind,
            onRetry = onContextRetry,
            onItemClick = { id, type, source ->
                contextItemTarget(id, type)?.let { target ->
                    val exploreSource = when (source) {
                        "recommendation" -> DetailExploreSource.Recommendation
                        "semanticRecommendation" -> DetailExploreSource.SemanticRecommendation
                        "graph" -> DetailExploreSource.Graph
                        else -> DetailExploreSource.Related
                    }
                    onExploreTargetClick(
                        DetailExploreTargetClick(
                            target = target,
                            source = exploreSource,
                        ),
                    )
                }
            },
            onCollectionClick = { collectionId ->
                onExploreTargetClick(
                    DetailExploreTargetClick(
                        target = DetailContextTarget.Collection(collectionId),
                        source = DetailExploreSource.Collection,
                    ),
                )
            },
            onTopicClick = { type, key ->
                onExploreTargetClick(
                    DetailExploreTargetClick(
                        target = DetailContextTarget.Topic(type, key),
                        source = DetailExploreSource.ExploreTopic,
                    ),
                )
            },
        )
    }
}

@Composable
private fun LoadingPlaceholder(modifier: Modifier = Modifier) {
    HeritageContentCard(modifier = modifier) {
        Text(
            text = stringResource(R.string.context_loading),
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ErrorRetryRow(
    errorKind: ErrorKind,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    HeritageContentCard(modifier = modifier) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(errorKind.fallbackResId()),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f),
            )
            TextButton(onClick = onRetry) {
                Text(stringResource(R.string.action_retry))
            }
        }
    }
}
