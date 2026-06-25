package com.duckylife.heritage.modern.feature.discovery.graphexplore

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.duckylife.heritage.modern.R
import com.duckylife.heritage.modern.core.network.dto.DiscoveryItemDto
import com.duckylife.heritage.modern.core.network.dto.MediaAssetDto
import com.duckylife.heritage.modern.core.network.dto.SearchResultType
import com.duckylife.heritage.modern.feature.discovery.GraphTab
import com.duckylife.heritage.modern.feature.graph.model.GraphEvidenceResult
import com.duckylife.heritage.modern.feature.graph.model.GraphExploreResult
import com.duckylife.heritage.modern.feature.graph.model.GraphNeighborsResult
import com.duckylife.heritage.modern.feature.graph.model.GraphNodeUiModel
import com.duckylife.heritage.modern.feature.graph.model.GraphSimilarResult
import com.duckylife.heritage.modern.ui.component.HeritageContentCard
import com.duckylife.heritage.modern.ui.component.HeritageMetaChip
import com.duckylife.heritage.modern.ui.error.ErrorKind
import com.duckylife.heritage.modern.ui.error.fallbackResId
import com.duckylife.heritage.modern.ui.text.localizedContentType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GraphExploreRoute(
    contentType: String,
    contentId: String,
    initialTab: GraphTab,
    onBack: () -> Unit,
    onItemClick: (DiscoveryItemDto) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: GraphExploreViewModel = hiltViewModel<GraphExploreViewModel, GraphExploreViewModel.Factory>(
        key = "graph-explore-$contentType-$contentId-${initialTab.name}",
        creationCallback = { factory ->
            factory.create(
                contentType = contentType,
                contentId = contentId,
                initialTab = initialTab,
            )
        },
    )
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.graph_explore_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.action_back),
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
            when {
                uiState.isInvalidRoute -> InvalidRouteContent(onBack = onBack)
                else -> GraphExploreContent(
                    uiState = uiState,
                    onTabSelected = viewModel::selectTab,
                    onRetry = viewModel::retry,
                    onToggleAiInferred = viewModel::toggleAiInferred,
                    onCenterNodeClick = { node ->
                        node.toDiscoveryItemDto()?.let(onItemClick)
                    },
                )
            }
        }
    }
}

@Composable
private fun GraphExploreContent(
    uiState: GraphExploreUiState,
    onTabSelected: (GraphTab) -> Unit,
    onRetry: () -> Unit,
    onToggleAiInferred: () -> Unit,
    onCenterNodeClick: (GraphNodeUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tabs = GraphTab.entries
    Column(modifier = modifier.fillMaxSize()) {
        CenterNodeCard(
            node = uiState.centerNode,
            onClick = onCenterNodeClick,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        )

        PrimaryTabRow(selectedTabIndex = tabs.indexOf(uiState.selectedTab)) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    selected = uiState.selectedTab == tab,
                    onClick = { onTabSelected(tab) },
                    text = { Text(stringResource(tab.labelResId())) },
                )
            }
        }

        when (uiState.selectedTab) {
            GraphTab.Neighbors -> NeighborsTab(
                section = uiState.neighbors,
                onRetry = onRetry,
            )
            GraphTab.Similar -> SimilarTab(
                section = uiState.similar,
                onRetry = onRetry,
            )
            GraphTab.Explore -> ExploreTab(
                section = uiState.explore,
                onRetry = onRetry,
            )
            GraphTab.Evidence -> EvidenceTab(
                section = uiState.evidence,
                includeAiInferred = uiState.includeAiInferred,
                onRetry = onRetry,
                onToggleAiInferred = onToggleAiInferred,
            )
        }
    }
}

@Composable
private fun CenterNodeCard(
    node: GraphNodeUiModel?,
    onClick: (GraphNodeUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val currentNode = node ?: return
    val title = currentNode.displayTitle
    val subtitle = currentNode.subtitle
    val typeLabel = localizedContentType(currentNode.type.wireName)
    val enabled = currentNode.isContentNode

    HeritageContentCard(
        onClick = if (enabled) {{ onClick(currentNode) }} else null,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                typeLabel?.let { label ->
                    HeritageMetaChip(text = label)
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
            }
            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                node?.category?.let { HeritageMetaChip(text = it) }
                node?.region?.let { HeritageMetaChip(text = it) }
            }
        }
    }
}

@Composable
private fun NeighborsTab(
    section: com.duckylife.heritage.modern.feature.discovery.DiscoverySectionState<GraphNeighborsResult>,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SectionContent(
        section = section,
        onRetry = onRetry,
        modifier = modifier,
    ) { result ->
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                Text(
                    text = stringResource(
                        R.string.graph_explore_neighbors_summary,
                        result.nodes.size,
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            items(
                items = result.nodes,
                key = { it.nodeKey },
            ) { node ->
                Text(
                    text = node.displayTitle,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 4.dp),
                )
            }
        }
    }
}

@Composable
private fun SimilarTab(
    section: com.duckylife.heritage.modern.feature.discovery.DiscoverySectionState<GraphSimilarResult>,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SectionContent(
        section = section,
        onRetry = onRetry,
        modifier = modifier,
    ) { result ->
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                Text(
                    text = stringResource(
                        R.string.graph_explore_similar_summary,
                        result.items.size,
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            items(
                items = result.items,
                key = { it.node.nodeKey },
            ) { item ->
                Text(
                    text = item.node.displayTitle,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 4.dp),
                )
            }
        }
    }
}

@Composable
private fun ExploreTab(
    section: com.duckylife.heritage.modern.feature.discovery.DiscoverySectionState<GraphExploreResult>,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SectionContent(
        section = section,
        onRetry = onRetry,
        modifier = modifier,
    ) { result ->
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = stringResource(
                    R.string.graph_explore_explore_summary,
                    result.depth,
                    result.nodes.size,
                    result.edges.size,
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun EvidenceTab(
    section: com.duckylife.heritage.modern.feature.discovery.DiscoverySectionState<GraphEvidenceResult>,
    includeAiInferred: Boolean,
    onRetry: () -> Unit,
    onToggleAiInferred: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        FilterChip(
            selected = includeAiInferred,
            onClick = onToggleAiInferred,
            label = { Text(stringResource(R.string.graph_explore_include_ai_inferred)) },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )
        SectionContent(
            section = section,
            onRetry = onRetry,
        ) { result ->
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(
                    items = result.evidence,
                    key = { it.evidenceId ?: it.relationLabel + it.sourceContentTitle },
                ) { item ->
                    Text(
                        text = item.relationLabel,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(vertical = 4.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun <T> SectionContent(
    section: com.duckylife.heritage.modern.feature.discovery.DiscoverySectionState<T>,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (T) -> Unit,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        when {
            section.isLoading && !section.hasData -> SectionSkeleton()
            section.hasError -> SectionErrorCard(
                errorKind = section.errorKind ?: ErrorKind.Unknown,
                onRetry = onRetry,
            )
            section.hasData -> content(section.data!!)
            else -> { /* initial idle; will be loading soon */ }
        }
    }
}

@Composable
private fun SectionSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        repeat(4) {
            HeritageContentCard {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                )
            }
        }
    }
}

@Composable
private fun SectionErrorCard(
    errorKind: ErrorKind,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    HeritageContentCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
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
            TextButton(onClick = onRetry) {
                Text(stringResource(R.string.action_retry))
            }
        }
    }
}

@Composable
private fun InvalidRouteContent(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(R.string.graph_explore_invalid_route_title),
            style = MaterialTheme.typography.headlineSmall,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.graph_explore_invalid_route_message),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onBack) {
            Text(stringResource(R.string.action_back))
        }
    }
}

private fun GraphNodeUiModel.toDiscoveryItemDto(): DiscoveryItemDto? {
    if (!isContentNode || id.isNullOrBlank()) return null
    return DiscoveryItemDto(
        id = id,
        type = type.wireName,
        title = title.orEmpty(),
        summary = subtitle,
        category = category,
        region = region,
        coverImage = coverImageUrl?.let { MediaAssetDto(displayUrl = it) },
    )
}


private fun GraphTab.labelResId(): Int = when (this) {
    GraphTab.Neighbors -> R.string.graph_tab_neighbors
    GraphTab.Similar -> R.string.graph_tab_similar
    GraphTab.Explore -> R.string.graph_tab_explore
    GraphTab.Evidence -> R.string.graph_tab_evidence
}
