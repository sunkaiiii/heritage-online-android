package com.duckylife.heritage.modern.feature.discovery.graphexplore

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.duckylife.heritage.modern.R
import com.duckylife.heritage.modern.core.network.dto.DiscoveryItemDto
import com.duckylife.heritage.modern.core.network.dto.MediaAssetDto
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphNodeType
import com.duckylife.heritage.modern.feature.discovery.DiscoverySectionState
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
import com.duckylife.heritage.modern.ui.theme.HeritageTheme

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

    GraphExploreScreen(
        uiState = uiState,
        onBack = onBack,
        onTabSelected = viewModel::selectTab,
        onRetry = viewModel::retry,
        onRefresh = viewModel::refresh,
        onToggleAiInferred = viewModel::toggleAiInferred,
        onCenterNodeClick = { node ->
            node.toDiscoveryItemDto()?.let(onItemClick)
        },
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun GraphExploreScreen(
    uiState: GraphExploreUiState,
    onBack: () -> Unit,
    onTabSelected: (GraphTab) -> Unit,
    onRetry: () -> Unit,
    onRefresh: () -> Unit,
    onToggleAiInferred: () -> Unit,
    onCenterNodeClick: (GraphNodeUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    var menuExpanded by remember { mutableStateOf(false) }
    var showExplanation by remember { mutableStateOf(false) }
    val sheetState: SheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false,
    )

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
                actions = {
                    val moreOptionsContentDescription = stringResource(R.string.graph_explore_menu_more)
                    IconButton(
                        onClick = { menuExpanded = true },
                        modifier = Modifier.semantics {
                            contentDescription = moreOptionsContentDescription
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = null,
                        )
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.graph_explore_menu_refresh)) },
                            onClick = {
                                menuExpanded = false
                                onRefresh()
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                )
                            },
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.graph_explore_menu_relation_explanation)) },
                            onClick = {
                                menuExpanded = false
                                showExplanation = true
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Outlined.HelpOutline,
                                    contentDescription = null,
                                )
                            },
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
                else -> GraphExploreBody(
                    uiState = uiState,
                    onTabSelected = onTabSelected,
                    onRetry = onRetry,
                    onToggleAiInferred = onToggleAiInferred,
                    onCenterNodeClick = onCenterNodeClick,
                )
            }
        }
    }

    if (showExplanation) {
        RelationExplanationBottomSheet(
            sheetState = sheetState,
            onDismiss = { showExplanation = false },
        )
    }
}

@Composable
private fun GraphExploreBody(
    uiState: GraphExploreUiState,
    onTabSelected: (GraphTab) -> Unit,
    onRetry: () -> Unit,
    onToggleAiInferred: () -> Unit,
    onCenterNodeClick: (GraphNodeUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tabs = GraphTab.entries
    Column(modifier = modifier.fillMaxSize()) {
        val centerNode = uiState.centerNode
        if (centerNode != null) {
            CenterNodeCard(
                node = centerNode,
                onClick = onCenterNodeClick,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            )
        } else {
            CenterNodeSkeleton(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            )
        }

        PrimaryTabRow(selectedTabIndex = tabs.indexOf(uiState.selectedTab)) {
            tabs.forEach { tab ->
                val label = stringResource(tab.labelResId())
                Tab(
                    selected = uiState.selectedTab == tab,
                    onClick = { onTabSelected(tab) },
                    text = { Text(label) },
                )
            }
        }

        when (uiState.selectedTab) {
            GraphTab.Neighbors -> NeighborsTab(
                section = uiState.neighbors,
                onRetry = onRetry,
                modifier = Modifier.weight(1f),
            )
            GraphTab.Similar -> SimilarTab(
                section = uiState.similar,
                onRetry = onRetry,
                modifier = Modifier.weight(1f),
            )
            GraphTab.Explore -> ExploreTab(
                section = uiState.explore,
                onRetry = onRetry,
                modifier = Modifier.weight(1f),
            )
            GraphTab.Evidence -> EvidenceTab(
                section = uiState.evidence,
                includeAiInferred = uiState.includeAiInferred,
                onRetry = onRetry,
                onToggleAiInferred = onToggleAiInferred,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun CenterNodeCard(
    node: GraphNodeUiModel,
    onClick: (GraphNodeUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val title = node.displayTitle
    val subtitle = node.subtitle
    val typeLabel = localizedContentType(node.type.wireName)
    val enabled = node.isContentNode

    HeritageContentCard(
        onClick = if (enabled) {{ onClick(node) }} else null,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 64.dp)
            .testTag("CenterNodeCard"),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (typeLabel.isNotBlank()) {
                    HeritageMetaChip(text = typeLabel)
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
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
                verticalAlignment = Alignment.CenterVertically,
            ) {
                node.category?.takeIf { it.isNotBlank() }?.let {
                    HeritageMetaChip(text = it)
                }
                node.region?.takeIf { it.isNotBlank() }?.let {
                    HeritageMetaChip(text = it)
                }
            }
        }
    }
}

@Composable
private fun CenterNodeSkeleton(
    modifier: Modifier = Modifier,
) {
    HeritageContentCard(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(16.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        shape = RoundedCornerShape(4.dp),
                    ),
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.35f)
                    .height(12.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        shape = RoundedCornerShape(4.dp),
                    ),
            )
        }
    }
}

@Composable
private fun NeighborsTab(
    section: DiscoverySectionState<GraphNeighborsResult>,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SectionContent(
        section = section,
        onRetry = onRetry,
        skeleton = { NeighborsSkeleton() },
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
    section: DiscoverySectionState<GraphSimilarResult>,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SectionContent(
        section = section,
        onRetry = onRetry,
        skeleton = { SimilarSkeleton() },
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
    section: DiscoverySectionState<GraphExploreResult>,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SectionContent(
        section = section,
        onRetry = onRetry,
        skeleton = { ExploreSkeleton() },
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
    section: DiscoverySectionState<GraphEvidenceResult>,
    includeAiInferred: Boolean,
    onRetry: () -> Unit,
    onToggleAiInferred: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        val aiInferredContentDescription = stringResource(R.string.graph_explore_include_ai_inferred)
        FilterChip(
            selected = includeAiInferred,
            onClick = onToggleAiInferred,
            label = { Text(stringResource(R.string.graph_explore_include_ai_inferred)) },
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .semantics {
                    contentDescription = aiInferredContentDescription
                },
        )
        if (includeAiInferred) {
            Text(
                text = stringResource(R.string.graph_explore_ai_inferred_hint),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }
        SectionContent(
            section = section,
            onRetry = onRetry,
            skeleton = { EvidenceSkeleton() },
            modifier = Modifier.weight(1f),
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
    section: DiscoverySectionState<T>,
    onRetry: () -> Unit,
    skeleton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (T) -> Unit,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        when {
            section.isLoading && !section.hasData -> skeleton()
            section.hasData -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    if (section.errorKind != null) {
                        SectionErrorCard(
                            errorKind = section.errorKind,
                            onRetry = onRetry,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        )
                    }
                    content(section.data!!)
                }
            }
            section.hasError -> SectionErrorCard(
                errorKind = section.errorKind ?: ErrorKind.Unknown,
                onRetry = onRetry,
            )
            else -> { /* initial idle; will be loading soon */ }
        }
    }
}

@Composable
private fun NeighborsSkeleton(modifier: Modifier = Modifier) {
    GraphListSkeleton(
        itemCount = 5,
        modifier = modifier.testTag("NeighborsSkeleton"),
    )
}

@Composable
private fun SimilarSkeleton(modifier: Modifier = Modifier) {
    GraphListSkeleton(
        itemCount = 4,
        modifier = modifier.testTag("SimilarSkeleton"),
    )
}

@Composable
private fun ExploreSkeleton(modifier: Modifier = Modifier) {
    GraphListSkeleton(
        itemCount = 4,
        modifier = modifier.testTag("ExploreSkeleton"),
    )
}

@Composable
private fun EvidenceSkeleton(modifier: Modifier = Modifier) {
    GraphListSkeleton(
        itemCount = 4,
        modifier = modifier.testTag("EvidenceSkeleton"),
    )
}

@Composable
private fun GraphListSkeleton(
    itemCount: Int,
    modifier: Modifier = Modifier,
) {
    val loadingContentDescription = stringResource(R.string.graph_explore_loading)
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .semantics {
                contentDescription = loadingContentDescription
            },
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        repeat(itemCount) {
            HeritageContentCard {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh),
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RelationExplanationBottomSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = modifier,
    ) {
        RelationExplanationSheetContent(
            onDismiss = onDismiss,
            modifier = Modifier.padding(bottom = 32.dp),
        )
    }
}

@Composable
private fun RelationExplanationSheetContent(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(R.string.graph_explore_relation_explanation_title),
            style = MaterialTheme.typography.headlineSmall,
        )
        Text(
            text = stringResource(R.string.graph_explore_relation_explanation_body),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_close))
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

// ---------------------------------------------------------------------------
// Previews
// ---------------------------------------------------------------------------

@Preview(name = "Graph Explore - Light")
@Composable
private fun GraphExploreScreenLightPreview() {
    HeritageTheme {
        GraphExploreScreen(
            uiState = previewUiState(),
            onBack = {},
            onTabSelected = {},
            onRetry = {},
            onRefresh = {},
            onToggleAiInferred = {},
            onCenterNodeClick = {},
        )
    }
}

@Preview(name = "Graph Explore - Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun GraphExploreScreenDarkPreview() {
    HeritageTheme {
        GraphExploreScreen(
            uiState = previewUiState(),
            onBack = {},
            onTabSelected = {},
            onRetry = {},
            onRefresh = {},
            onToggleAiInferred = {},
            onCenterNodeClick = {},
        )
    }
}

@Preview(name = "Graph Explore - Loading")
@Composable
private fun GraphExploreScreenLoadingPreview() {
    HeritageTheme {
        GraphExploreScreen(
            uiState = GraphExploreUiState(
                centerNode = null,
                neighbors = DiscoverySectionState(isLoading = true),
            ),
            onBack = {},
            onTabSelected = {},
            onRetry = {},
            onRefresh = {},
            onToggleAiInferred = {},
            onCenterNodeClick = {},
        )
    }
}

@Preview(name = "Graph Explore - Invalid Route")
@Composable
private fun GraphExploreScreenInvalidRoutePreview() {
    HeritageTheme {
        GraphExploreScreen(
            uiState = GraphExploreUiState(isInvalidRoute = true),
            onBack = {},
            onTabSelected = {},
            onRetry = {},
            onRefresh = {},
            onToggleAiInferred = {},
            onCenterNodeClick = {},
        )
    }
}

@Composable
private fun previewUiState(): GraphExploreUiState {
    val centerNode = GraphNodeUiModel(
        nodeKey = "article-preview",
        type = GraphNodeType.Article,
        id = "preview-id",
        title = "Preview Article Title",
        subtitle = "A short subtitle for the preview",
        category = "Folk Art",
        region = "Zhejiang",
    )
    return GraphExploreUiState(
        centerNode = centerNode,
        neighbors = DiscoverySectionState(
            data = GraphNeighborsResult(
                centerNodeKey = centerNode.nodeKey,
                nodes = listOf(centerNode),
                edges = emptyList(),
            ),
        ),
    )
}
