package com.duckylife.heritage.modern.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
    onContextTargetSelected: (DetailContextTarget) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
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
        if (blendedRecommendations != null && blendedRecommendations.items.isNotEmpty()) {
            BlendedRecommendationsSection(
                recommendations = blendedRecommendations,
                onItemClick = { item ->
                    contextItemTarget(item.id, item.type)?.let(onContextTargetSelected)
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
            onItemClick = { id, type ->
                contextItemTarget(id, type)?.let(onContextTargetSelected)
            },
            onCollectionClick = { collectionId ->
                onContextTargetSelected(DetailContextTarget.Collection(collectionId))
            },
            onTopicClick = { type, key ->
                onContextTargetSelected(DetailContextTarget.Topic(type, key))
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
