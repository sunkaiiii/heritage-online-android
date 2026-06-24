package com.duckylife.heritage.modern.feature.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.CompareArrows
import androidx.compose.material.icons.outlined.AccountTree
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.duckylife.heritage.modern.R
import com.duckylife.heritage.modern.core.network.dto.advanced.SectionStatus
import com.duckylife.heritage.modern.feature.detail.intelligence.ContentIntelligenceUiState
import com.duckylife.heritage.modern.ui.component.HeritageContentCard
import com.duckylife.heritage.modern.ui.component.HeritageSectionHeader

/**
 * 详情页“关系与推荐”摘要条（“继续探索” action bar）。
 *
 * 只根据 V3 content page 已经返回的图谱/推荐/学习路线可用性展示入口，不额外发请求。
 * 某个增强服务不可用时隐藏对应 action，而不是显示禁用态。
 */
@Composable
fun DetailContinueExploreSection(
    uiState: ContentIntelligenceUiState,
    onGraphClick: () -> Unit,
    onSimilarClick: () -> Unit,
    onLearningRoutesClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val graphCount = (uiState.graphSection.data?.nodes?.size ?: 0)
        .takeIf { uiState.graphSection.status == SectionStatus.Ready && it > 0 }
    val recommendationCount = (uiState.recommendationSection.data?.size ?: 0)
        .takeIf { uiState.recommendationSection.status == SectionStatus.Ready && it > 0 }
    val learningRoutesAvailable = uiState.learningRoutesAvailable

    if (graphCount == null && recommendationCount == null && !learningRoutesAvailable) return

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        HeritageSectionHeader(title = stringResource(R.string.explore_actions_title))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (graphCount != null) {
                ContinueActionCard(
                    icon = Icons.Outlined.AccountTree,
                    label = stringResource(R.string.explore_action_graph),
                    subtitle = stringResource(R.string.explore_action_graph_subtitle, graphCount),
                    onClick = onGraphClick,
                    modifier = Modifier.weight(1f),
                )
            }
            if (recommendationCount != null) {
                ContinueActionCard(
                    icon = Icons.AutoMirrored.Outlined.CompareArrows,
                    label = stringResource(R.string.explore_action_similar),
                    subtitle = stringResource(
                        R.string.explore_action_similar_subtitle,
                        recommendationCount,
                    ),
                    onClick = onSimilarClick,
                    modifier = Modifier.weight(1f),
                )
            }
            if (learningRoutesAvailable) {
                ContinueActionCard(
                    icon = Icons.Outlined.School,
                    label = stringResource(R.string.explore_action_learning_routes),
                    subtitle = stringResource(R.string.explore_action_learning_routes_subtitle),
                    onClick = onLearningRoutesClick,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun ContinueActionCard(
    icon: ImageVector,
    label: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    HeritageContentCard(
        onClick = onClick,
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
