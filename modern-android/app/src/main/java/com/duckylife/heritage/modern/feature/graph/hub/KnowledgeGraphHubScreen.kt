package com.duckylife.heritage.modern.feature.graph.hub

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.duckylife.heritage.modern.R
import com.duckylife.heritage.modern.core.data.RecentContentRef
import com.duckylife.heritage.modern.ui.component.HeritageContentCard
import com.duckylife.heritage.modern.ui.component.HeritageSectionHeader
import com.duckylife.heritage.modern.ui.error.ErrorKind
import com.duckylife.heritage.modern.ui.error.fallbackResId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KnowledgeGraphHubRoute(
    onBack: () -> Unit,
    onTopicClick: (topicType: String, topicKey: String) -> Unit,
    onRandomTrailClick: () -> Unit,
    onRecentTrailClick: (type: String, id: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: KnowledgeGraphHubViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.knowledge_graph_hub_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.action_back),
                        )
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::showInfoSheet) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.HelpOutline,
                            contentDescription = stringResource(R.string.knowledge_graph_hub_info_icon_description),
                        )
                    }
                },
            )
        },
        modifier = modifier,
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            KnowledgeGraphHubContent(
                uiState = uiState,
                onTopicClick = onTopicClick,
                onRandomTrailClick = onRandomTrailClick,
                onRecentTrailClick = onRecentTrailClick,
                onRetry = viewModel::retry,
            )

            if (uiState.isInfoSheetVisible) {
                ModalBottomSheet(
                    onDismissRequest = viewModel::dismissInfoSheet,
                    sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                ) {
                    KnowledgeGraphInfoSheetContent(
                        onDismiss = viewModel::dismissInfoSheet,
                        modifier = Modifier.padding(bottom = 32.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun KnowledgeGraphHubContent(
    uiState: KnowledgeGraphHubUiState,
    onTopicClick: (topicType: String, topicKey: String) -> Unit,
    onRandomTrailClick: () -> Unit,
    onRecentTrailClick: (type: String, id: String) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 160.dp),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Text(
                text = stringResource(R.string.knowledge_graph_hub_intro),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp),
            )
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            HeritageSectionHeader(
                title = stringResource(R.string.knowledge_graph_hub_communities_title),
                modifier = Modifier.padding(top = 4.dp),
            )
        }

        when {
            uiState.communities.isLoading && !uiState.communities.hasData -> {
                items(count = 6, key = { "skeleton-$it" }) {
                    CommunityCardSkeleton()
                }
            }

            uiState.communities.hasFatalError -> {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    HubErrorCard(
                        errorKind = uiState.communities.errorKind ?: ErrorKind.Unknown,
                        onRetry = onRetry,
                    )
                }
            }

            uiState.hasCommunities -> {
                items(
                    items = uiState.communities.data.orEmpty(),
                    key = { it.communityKey },
                ) { community ->
                    CommunityCard(
                        community = community,
                        onClick = {
                            community.primaryTopicKey?.let { topicKey ->
                                onTopicClick(community.topicType, topicKey)
                            }
                        },
                    )
                }
            }

            else -> {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    CommunitiesEmptyState(
                        onDiscoverClick = { /* discovery index is behind this page; back is enough */ },
                    )
                }
            }
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            HeritageSectionHeader(
                title = stringResource(R.string.knowledge_graph_hub_trails_title),
                modifier = Modifier.padding(top = 8.dp),
            )
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            TrailCardsRow(
                recentContent = uiState.recentContent,
                onRandomTrailClick = onRandomTrailClick,
                onRecentTrailClick = onRecentTrailClick,
            )
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            Text(
                text = stringResource(R.string.knowledge_graph_hub_footer),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CommunityCard(
    community: com.duckylife.heritage.modern.feature.graph.model.GraphCommunityUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val enabled = community.primaryTopicKey != null
    HeritageContentCard(
        onClick = onClick.takeIf { enabled },
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = community.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                community.topicChips.forEach { chip ->
                    androidx.compose.material3.SuggestionChip(
                        onClick = {},
                        label = { Text(chip) },
                        enabled = false,
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                            disabledLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        ),
                    )
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = stringResource(
                        R.string.knowledge_graph_hub_community_content_count,
                        community.contentCount,
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = stringResource(
                        R.string.knowledge_graph_hub_community_relation_count,
                        community.relationCount,
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun CommunityCardSkeleton(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.aspectRatio(1f),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        ),
    ) {
        Box(modifier = Modifier.fillMaxSize())
    }
}

@Composable
private fun CommunitiesEmptyState(
    onDiscoverClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = stringResource(R.string.knowledge_graph_hub_empty_title),
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = stringResource(R.string.knowledge_graph_hub_empty_message),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        TextButton(onClick = onDiscoverClick) {
            Text(stringResource(R.string.knowledge_graph_hub_empty_action))
        }
    }
}

@Composable
private fun HubErrorCard(
    errorKind: ErrorKind,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    HeritageContentCard(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(errorKind.fallbackResId()),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f),
            )
            Spacer(modifier = Modifier.width(12.dp))
            TextButton(onClick = onRetry) {
                Text(stringResource(R.string.action_retry))
            }
        }
    }
}

@Composable
private fun TrailCardsRow(
    recentContent: RecentContentRef?,
    onRandomTrailClick: () -> Unit,
    onRecentTrailClick: (type: String, id: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        TrailEntryCard(
            title = stringResource(R.string.knowledge_graph_hub_random_trail_title),
            subtitle = stringResource(R.string.knowledge_graph_hub_random_trail_subtitle),
            onClick = onRandomTrailClick,
            modifier = Modifier.weight(1f),
        )
        TrailEntryCard(
            title = stringResource(R.string.knowledge_graph_hub_recent_trail_title),
            subtitle = recentContent?.title
                ?: stringResource(R.string.knowledge_graph_hub_recent_trail_disabled_subtitle),
            onClick = recentContent?.let { { onRecentTrailClick(it.type.wireName, it.id) } },
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun TrailEntryCard(
    title: String,
    subtitle: String,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    HeritageContentCard(
        onClick = onClick,
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (onClick != null) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun KnowledgeGraphInfoSheetContent(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(R.string.knowledge_graph_hub_info_title),
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            text = stringResource(R.string.knowledge_graph_hub_info_body),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        TextButton(
            onClick = onDismiss,
            modifier = Modifier.align(Alignment.End),
        ) {
            Text(stringResource(R.string.action_close))
        }
    }
}
