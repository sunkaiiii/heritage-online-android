package com.duckylife.heritage.modern.feature.discovery.graphexplore

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.AccountTree
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
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
import com.duckylife.heritage.modern.core.image.rememberHeritageImageLoader
import com.duckylife.heritage.modern.feature.detail.DetailContextTarget
import com.duckylife.heritage.modern.feature.detail.toDetailContextTarget
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphEvidenceSource
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphNodeType
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphRelationType
import com.duckylife.heritage.modern.feature.discovery.DiscoverySectionState
import com.duckylife.heritage.modern.feature.discovery.GraphTab
import com.duckylife.heritage.modern.feature.graph.format.GraphRelationFormatter
import com.duckylife.heritage.modern.feature.graph.model.AiInferredEdgeUiModel
import com.duckylife.heritage.modern.feature.graph.model.AiInferredEdgesResult
import com.duckylife.heritage.modern.feature.graph.model.BridgeItemUiModel
import com.duckylife.heritage.modern.feature.graph.model.BridgeResult
import com.duckylife.heritage.modern.feature.graph.model.GraphEdgeUiModel
import com.duckylife.heritage.modern.feature.graph.model.PathExplainResult
import com.duckylife.heritage.modern.feature.graph.model.PathStepUiModel
import com.duckylife.heritage.modern.feature.graph.model.GraphEvidenceResult
import com.duckylife.heritage.modern.feature.graph.model.GraphEvidenceUiModel
import com.duckylife.heritage.modern.feature.graph.model.GraphExploreResult
import com.duckylife.heritage.modern.feature.graph.model.GraphNeighborsResult
import com.duckylife.heritage.modern.feature.graph.model.GraphNodeUiModel
import com.duckylife.heritage.modern.feature.graph.model.GraphSimilarItemUiModel
import com.duckylife.heritage.modern.feature.graph.model.GraphSimilarResult
import com.duckylife.heritage.modern.ui.component.HeritageContentCard
import com.duckylife.heritage.modern.ui.component.HeritageListImage
import com.duckylife.heritage.modern.ui.component.HeritageMetaChip
import com.duckylife.heritage.modern.ui.error.ErrorKind
import com.duckylife.heritage.modern.ui.error.fallbackResId
import com.duckylife.heritage.modern.ui.text.localizedContentType
import com.duckylife.heritage.modern.ui.theme.HeritageTheme
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private const val MAX_BRIDGE_NODES = 20
private const val MAX_SIMILAR_REASONS = 2

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GraphExploreRoute(
    contentType: String,
    contentId: String,
    initialTab: GraphTab,
    onBack: () -> Unit,
    onContentClick: (DetailContextTarget) -> Unit,
    onTopicClick: (String, String) -> Unit,
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
        onExploreDepthSelected = viewModel::selectExploreDepth,
        onPathClick = { item -> viewModel.openPathExplain(item.node) },
        onPathExplainDismiss = viewModel::dismissPathExplain,
        onPathExplainRetry = viewModel::retryPathExplain,
        onPathExplainLoadBridge = viewModel::loadBridge,
        onNodeClick = { node ->
            when {
                node.isContentNode -> node.toDetailContextTarget()?.let(onContentClick)
                node.isTopicNode -> onTopicClick(node.type.wireName, node.topicKey)
            }
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
    onExploreDepthSelected: (Int) -> Unit,
    onPathClick: (GraphSimilarItemUiModel) -> Unit,
    onPathExplainDismiss: () -> Unit,
    onPathExplainRetry: () -> Unit,
    onPathExplainLoadBridge: () -> Unit,
    onNodeClick: (GraphNodeUiModel) -> Unit,
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
                    onExploreDepthSelected = onExploreDepthSelected,
                    onPathClick = onPathClick,
                    onNodeClick = onNodeClick,
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
    val pathSheet = uiState.pathExplainSheet
    if (pathSheet.targetNode != null) {
        PathExplainBottomSheet(
            centerNode = uiState.centerNode,
            sheetState = pathSheet,
            bottomSheetState = sheetState,
            onDismiss = onPathExplainDismiss,
            onNodeClick = onNodeClick,
            onRetry = onPathExplainRetry,
            onLoadBridge = onPathExplainLoadBridge,
        )
    }
}

@Composable
private fun GraphExploreBody(
    uiState: GraphExploreUiState,
    onTabSelected: (GraphTab) -> Unit,
    onRetry: () -> Unit,
    onToggleAiInferred: () -> Unit,
    onExploreDepthSelected: (Int) -> Unit,
    onPathClick: (GraphSimilarItemUiModel) -> Unit,
    onNodeClick: (GraphNodeUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tabs = GraphTab.entries
    Column(modifier = modifier.fillMaxSize()) {
        val centerNode = uiState.centerNode
        if (centerNode != null) {
            CenterNodeCard(
                node = centerNode,
                onClick = onNodeClick,
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
                    modifier = Modifier.semantics {
                        contentDescription = label
                    },
                )
            }
        }

        when (uiState.selectedTab) {
            GraphTab.Neighbors -> NeighborsTab(
                section = uiState.neighbors,
                onRetry = onRetry,
                onNodeClick = onNodeClick,
                onSimilarClick = { onTabSelected(GraphTab.Similar) },
                modifier = Modifier.weight(1f),
            )
            GraphTab.Similar -> SimilarTab(
                section = uiState.similar,
                onRetry = onRetry,
                onNodeClick = onNodeClick,
                onPathClick = onPathClick,
                onExploreClick = { onTabSelected(GraphTab.Explore) },
                modifier = Modifier.weight(1f),
            )
            GraphTab.Explore -> ExploreTab(
                section = uiState.explore,
                selectedDepth = uiState.exploreDepth,
                onDepthSelected = onExploreDepthSelected,
                onRetry = onRetry,
                onNodeClick = onNodeClick,
                modifier = Modifier.weight(1f),
            )
            GraphTab.Evidence -> EvidenceTab(
                section = uiState.evidence,
                aiInferredSection = uiState.aiInferredEdges,
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
    val cardClick: (() -> Unit)? = if (enabled) {
        { onClick(node) }
    } else {
        null
    }

    HeritageContentCard(
        onClick = cardClick,
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
    onNodeClick: (GraphNodeUiModel) -> Unit,
    onSimilarClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SectionContent(
        section = section,
        onRetry = onRetry,
        skeleton = { NeighborsSkeleton() },
        modifier = modifier,
    ) { result ->
        val relationRows = result.relatedRows()
        if (relationRows.isEmpty()) {
            EmptyTabState(
                title = stringResource(R.string.graph_neighbors_empty_title),
                message = stringResource(R.string.graph_neighbors_empty_message),
                actionLabel = stringResource(R.string.graph_neighbors_empty_action),
                onActionClick = onSimilarClick,
                modifier = Modifier.fillMaxSize(),
            )
            return@SectionContent
        }
        var expandedGroups by remember(result) { mutableStateOf<Set<String>>(emptySet()) }
        val groups = relationRows.groupBy { it.groupKey }
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = stringResource(
                            R.string.graph_explore_neighbors_summary,
                            relationRows.distinctBy { it.node.nodeKey }.size,
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = stringResource(R.string.graph_neighbors_content_only_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            groups.forEach { (groupKey, rows) ->
                item(key = "group-$groupKey") {
                    val groupLabel = rows.firstOrNull()?.edge?.localizedRelationLabel()
                        ?: stringResource(R.string.graph_relation_other_node)
                    RelationGroupHeader(
                        title = groupLabel,
                        count = rows.size,
                    )
                }
                val expanded = groupKey in expandedGroups
                val visibleRows = if (expanded) rows else rows.take(6)
                items(
                    items = visibleRows,
                    key = { "${it.node.nodeKey}-${it.edge.fromNodeKey}-${it.edge.toNodeKey}-${it.edge.relationType}" },
                ) { row ->
                    RelationNodeRow(
                        node = row.node,
                        relationLabel = row.edge.localizedRelationLabel(),
                        reason = row.edge.reason,
                        isAiInferred = row.edge.isAiInferred,
                        onNodeClick = onNodeClick,
                    )
                }
                if (!expanded && rows.size > 6) {
                    item(key = "expand-$groupKey") {
                        TextButton(
                            onClick = { expandedGroups = expandedGroups + groupKey },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(stringResource(R.string.graph_neighbors_show_all, rows.size))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SimilarTab(
    section: DiscoverySectionState<GraphSimilarResult>,
    onRetry: () -> Unit,
    onNodeClick: (GraphNodeUiModel) -> Unit,
    onPathClick: (GraphSimilarItemUiModel) -> Unit,
    onExploreClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SectionContent(
        section = section,
        onRetry = onRetry,
        skeleton = { SimilarSkeleton() },
        modifier = modifier,
    ) { result ->
        if (result.items.isEmpty()) {
            EmptyTabState(
                title = stringResource(R.string.graph_similar_empty_title),
                message = stringResource(R.string.graph_similar_empty_message),
                actionLabel = stringResource(R.string.graph_similar_empty_action),
                onActionClick = onExploreClick,
                modifier = Modifier.fillMaxSize(),
            )
            return@SectionContent
        }
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Text(
                    text = stringResource(R.string.graph_similar_intro),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            itemsIndexed(
                items = result.items,
                key = { _, item -> item.node.nodeKey },
            ) { index, item ->
                SimilarResultCard(
                    rank = index + 1,
                    item = item,
                    onNodeClick = onNodeClick,
                    onPathClick = onPathClick,
                )
            }
        }
    }
}

@Composable
private fun ExploreTab(
    section: DiscoverySectionState<GraphExploreResult>,
    selectedDepth: Int,
    onDepthSelected: (Int) -> Unit,
    onRetry: () -> Unit,
    onNodeClick: (GraphNodeUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    SectionContent(
        section = section,
        onRetry = onRetry,
        skeleton = { ExploreSkeleton() },
        modifier = modifier,
    ) { result ->
        var showOverview by remember(result) { mutableStateOf(false) }
        val rows = result.exploreRows()
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                ExploreTabHeader(
                    selectedDepth = selectedDepth,
                    nodeCount = result.nodes.size,
                    edgeCount = result.edges.size,
                    showOverview = showOverview,
                    onDepthSelected = onDepthSelected,
                    onViewModeSelected = { showOverview = it },
                )
            }
            when {
                result.nodes.isEmpty() -> item {
                    EmptyTabState(
                        title = stringResource(R.string.graph_explore_empty_title),
                        message = stringResource(R.string.graph_explore_empty_message),
                        actionLabel = null,
                        onActionClick = null,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                showOverview && result.canRenderOverview -> {
                    item {
                        GraphOverviewCanvas(
                            result = result,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .testTag("GraphOverviewCanvas"),
                        )
                    }
                    item {
                        Text(
                            text = stringResource(R.string.graph_overview_accessible_list_hint),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    items(
                        items = rows,
                        key = { "overview-${it.node.nodeKey}" },
                    ) { row ->
                        RelationNodeRow(
                            node = row.node,
                            relationLabel = row.edge?.localizedRelationLabel()
                                ?: stringResource(R.string.graph_relation_other_node),
                            reason = row.edge?.reason,
                            isAiInferred = row.edge?.isAiInferred == true,
                            onNodeClick = onNodeClick,
                        )
                    }
                }
                showOverview -> item {
                    EmptyTabState(
                        title = if (result.edges.isEmpty()) {
                            stringResource(R.string.graph_overview_no_edges_title)
                        } else {
                            stringResource(R.string.graph_overview_too_large_title)
                        },
                        message = if (result.edges.isEmpty()) {
                            stringResource(R.string.graph_overview_no_edges_message)
                        } else {
                            stringResource(R.string.graph_overview_too_large_message)
                        },
                        actionLabel = stringResource(R.string.graph_explore_list_view),
                        onActionClick = { showOverview = false },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                else -> {
                    val groupedRows = rows.groupBy { it.distance }
                    groupedRows.forEach { (distance, distanceRows) ->
                        item(key = "distance-$distance") {
                            RelationGroupHeader(
                                title = stringResource(R.string.graph_explore_distance_group, distance),
                                count = distanceRows.size,
                            )
                        }
                        items(
                            items = distanceRows,
                            key = { "distance-$distance-${it.node.nodeKey}" },
                        ) { row ->
                            RelationNodeRow(
                                node = row.node,
                                relationLabel = row.edge?.localizedRelationLabel()
                                    ?: stringResource(R.string.graph_relation_other_node),
                                reason = row.edge?.reason,
                                isAiInferred = row.edge?.isAiInferred == true,
                                onNodeClick = onNodeClick,
                            )
                        }
                    }
                    if (result.edges.size >= 50) {
                        item {
                            Text(
                                text = stringResource(R.string.graph_explore_truncated_hint, result.edges.size),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EvidenceTab(
    section: DiscoverySectionState<GraphEvidenceResult>,
    aiInferredSection: DiscoverySectionState<AiInferredEdgesResult>,
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
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    Text(
                        text = stringResource(R.string.graph_evidence_intro),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                if (result.evidence.isEmpty()) {
                    item {
                        EmptyTabState(
                            title = stringResource(R.string.graph_evidence_empty_title),
                            message = stringResource(R.string.graph_evidence_empty_message),
                            actionLabel = null,
                            onActionClick = null,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                } else {
                    items(
                        items = result.evidence,
                        key = { it.evidenceId ?: it.relationLabel.orEmpty() + it.sourceContentTitle.orEmpty() },
                    ) { item ->
                        EvidenceCard(item)
                    }
                }
                if (includeAiInferred) {
                    item {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    }
                    item {
                        Text(
                            text = stringResource(R.string.graph_ai_inferred_section_title),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                    aiInferredSectionContent(
                        section = aiInferredSection,
                        onRetry = onRetry,
                    )
                }
            }
        }
    }
}

private fun LazyListScope.aiInferredSectionContent(
    section: DiscoverySectionState<AiInferredEdgesResult>,
    onRetry: () -> Unit,
) {
    when {
        section.isLoading && !section.hasData -> item(key = "ai-loading") {
            GraphListSkeleton(itemCount = 2, modifier = Modifier.height(140.dp))
        }
        section.hasFatalError -> item(key = "ai-error") {
            SectionErrorCard(
                errorKind = section.errorKind ?: ErrorKind.Unknown,
                onRetry = onRetry,
                modifier = Modifier.padding(0.dp),
            )
        }
        section.hasData && section.data!!.edges.isEmpty() -> item(key = "ai-empty") {
            EmptyTabState(
                title = stringResource(R.string.graph_ai_inferred_empty_title),
                message = stringResource(R.string.graph_ai_inferred_empty_message),
                actionLabel = null,
                onActionClick = null,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        section.hasData -> items(
            items = section.data!!.edges,
            key = { "${it.fromNodeKey}-${it.toNodeKey}-${it.relationType}-${it.entityName}" },
        ) { edge ->
            AiInferredEdgeCard(edge)
        }
    }
}

@Composable
private fun RelationGroupHeader(
    title: String,
    count: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f),
        ) {
            Icon(
                imageVector = Icons.Outlined.AccountTree,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
            )
        }
        HeritageMetaChip(text = count.toString())
    }
}

@Composable
private fun RelationNodeRow(
    node: GraphNodeUiModel,
    relationLabel: String,
    reason: String?,
    isAiInferred: Boolean,
    onNodeClick: (GraphNodeUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val imageLoader = rememberHeritageImageLoader()
    val canNavigate = node.isContentNode || node.isTopicNode
    val cardClick: (() -> Unit)? = if (canNavigate) {
        { onNodeClick(node) }
    } else {
        null
    }
    HeritageContentCard(
        onClick = cardClick,
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            HeritageListImage(
                imageUrl = node.coverImageUrl,
                imageLoader = imageLoader,
                fallbackText = stringResource(node.type.placeholderResId()),
                contentDescription = node.displayTitle,
                modifier = Modifier.size(56.dp),
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    HeritageMetaChip(
                        text = localizedContentType(node.type.wireName)
                            .ifBlank { stringResource(node.type.fallbackLabelResId()) },
                    )
                    if (isAiInferred) {
                        AiInferredBadge()
                    }
                }
                Text(
                    text = node.displayTitle,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                val subtitle = node.subtitle?.takeIf { it.isNotBlank() }
                    ?: relationLabel
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (!reason.isNullOrBlank()) {
                    Text(
                        text = reason,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            if (canNavigate) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun SimilarResultCard(
    rank: Int,
    item: GraphSimilarItemUiModel,
    onNodeClick: (GraphNodeUiModel) -> Unit,
    onPathClick: (GraphSimilarItemUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val cardClick: (() -> Unit)? = if (item.node.isContentNode || item.node.isTopicNode) {
        { onNodeClick(item.node) }
    } else {
        null
    }
    HeritageContentCard(
        onClick = cardClick,
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RankBadge(rank)
                Text(
                    text = item.node.displayTitle,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                HeritageMetaChip(
                    text = localizedContentType(item.node.type.wireName)
                        .ifBlank { stringResource(item.node.type.fallbackLabelResId()) },
                )
                HeritageMetaChip(text = stringResource(GraphRelationFormatter.associationLevelLabelResId(item.associationLevel)))
                HeritageMetaChip(text = stringResource(R.string.graph_similar_shared_neighbors, item.sharedNeighborCount))
            }
            if (item.sharedTopics.isNotEmpty()) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    item.sharedTopics.take(2).forEach { topic ->
                        HeritageMetaChip(text = topic)
                    }
                }
            }
            item.reasons.take(MAX_SIMILAR_REASONS).forEach { reason ->
                Text(
                    text = "• $reason",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (item.node.isContentNode && !item.node.id.isNullOrBlank()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(onClick = { onPathClick(item) }) {
                        Text(stringResource(R.string.graph_similar_view_path))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExploreTabHeader(
    selectedDepth: Int,
    nodeCount: Int,
    edgeCount: Int,
    showOverview: Boolean,
    onDepthSelected: (Int) -> Unit,
    onViewModeSelected: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SingleChoiceSegmentedButtonRow {
                listOf(1, 2).forEachIndexed { index, depth ->
                    SegmentedButton(
                        selected = selectedDepth == depth,
                        onClick = { onDepthSelected(depth) },
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = 2),
                    ) {
                        Text(stringResource(if (depth == 1) R.string.graph_explore_depth_one else R.string.graph_explore_depth_two))
                    }
                }
            }
            Text(
                text = stringResource(R.string.graph_explore_explore_summary, selectedDepth, nodeCount, edgeCount),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = !showOverview,
                onClick = { onViewModeSelected(false) },
                label = { Text(stringResource(R.string.graph_explore_list_view)) },
            )
            FilterChip(
                selected = showOverview,
                onClick = { onViewModeSelected(true) },
                label = { Text(stringResource(R.string.graph_explore_overview_view)) },
            )
        }
    }
}

@Composable
private fun GraphOverviewCanvas(
    result: GraphExploreResult,
    modifier: Modifier = Modifier,
) {
    val lineColor = MaterialTheme.colorScheme.outlineVariant
    val contentColor = MaterialTheme.colorScheme.primary
    val topicColor = MaterialTheme.colorScheme.tertiary
    val nodePositions = remember(result) { result.circularNodePositions() }

    Canvas(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp)),
    ) {
        val scaled = nodePositions.mapValues { (_, unitPoint) ->
            Offset(unitPoint.x * size.width, unitPoint.y * size.height)
        }
        result.edges.forEach { edge ->
            val from = scaled[edge.fromNodeKey]
            val to = scaled[edge.toNodeKey]
            if (from != null && to != null) {
                drawLine(
                    color = lineColor,
                    start = from,
                    end = to,
                    strokeWidth = if (edge.isAiInferred) 3f else 2f,
                )
            }
        }
        result.nodes.forEach { node ->
            val offset = scaled[node.nodeKey] ?: return@forEach
            drawCircle(
                color = if (node.isContentNode) contentColor else topicColor,
                radius = if (node.nodeKey == result.centerNodeKey) 9f else 6f,
                center = offset,
            )
        }
    }
}

@Composable
private fun EvidenceCard(
    item: GraphEvidenceUiModel,
    modifier: Modifier = Modifier,
) {
    HeritageContentCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = item.relationLabel ?: stringResource(GraphRelationFormatter.labelResId(item.relationType)),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                )
                if (item.isAiInferred) {
                    AiInferredBadge()
                }
            }
            HeritageMetaChip(text = stringResource(item.source.labelResId()))
            if (!item.reason.isNullOrBlank()) {
                Text(
                    text = item.reason,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            item.sourceContentTitle?.let { title ->
                Text(
                    text = stringResource(R.string.graph_evidence_source_content, title),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun AiInferredEdgeCard(
    item: AiInferredEdgeUiModel,
    modifier: Modifier = Modifier,
) {
    HeritageContentCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(GraphRelationFormatter.labelResId(item.relationType)),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                )
                AiInferredBadge()
            }
            Text(
                text = "${item.fromNodeKey} → ${item.toNodeKey}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                HeritageMetaChip(text = stringResource(GraphRelationFormatter.confidenceLabelResId(item.confidence)))
                item.entityName?.let { HeritageMetaChip(text = it) }
            }
            if (!item.reason.isNullOrBlank()) {
                Text(
                    text = item.reason,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun EmptyTabState(
    title: String,
    message: String,
    actionLabel: String?,
    onActionClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        ) {
            Icon(
                imageVector = Icons.Outlined.AccountTree,
                contentDescription = null,
                modifier = Modifier
                    .padding(12.dp)
                    .size(28.dp),
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        if (actionLabel != null && onActionClick != null) {
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(onClick = onActionClick) {
                Text(actionLabel)
            }
        }
    }
}

@Composable
private fun RankBadge(rank: Int) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    ) {
        Text(
            text = "#$rank",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        )
    }
}

@Composable
private fun AiInferredBadge() {
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.tertiary,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary),
    ) {
        Text(
            text = stringResource(R.string.graph_ai_inferred_badge),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
        )
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
            section.hasFatalError -> SectionErrorCard(
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PathExplainBottomSheet(
    centerNode: GraphNodeUiModel?,
    sheetState: PathExplainSheetState,
    bottomSheetState: SheetState,
    onDismiss: () -> Unit,
    onNodeClick: (GraphNodeUiModel) -> Unit,
    onRetry: () -> Unit,
    onLoadBridge: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = bottomSheetState,
        modifier = modifier,
    ) {
        PathExplainSheetContent(
            centerNode = centerNode,
            sheetState = sheetState,
            onNodeClick = onNodeClick,
            onRetry = onRetry,
            onLoadBridge = onLoadBridge,
            onDismiss = onDismiss,
            modifier = Modifier.padding(bottom = 32.dp),
        )
    }
}

@Composable
private fun PathExplainSheetContent(
    centerNode: GraphNodeUiModel?,
    sheetState: PathExplainSheetState,
    onNodeClick: (GraphNodeUiModel) -> Unit,
    onRetry: () -> Unit,
    onLoadBridge: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(R.string.graph_path_explain_title),
            style = MaterialTheme.typography.headlineSmall,
        )
        val targetTitle = sheetState.targetNode?.displayTitle.orEmpty()
        val centerTitle = centerNode?.displayTitle.orEmpty()
        Text(
            text = if (centerTitle.isNotBlank() && targetTitle.isNotBlank()) {
                stringResource(R.string.graph_path_explain_subtitle, centerTitle, targetTitle)
            } else if (targetTitle.isNotBlank()) {
                stringResource(R.string.graph_path_explain_target_only, targetTitle)
            } else {
                stringResource(R.string.graph_path_explain_target_unknown)
            },
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )

        when {
            sheetState.isLoading && sheetState.result == null -> PathExplainSkeleton()
            sheetState.errorKind != null && sheetState.result == null -> {
                val targetNode = sheetState.targetNode
                if (targetNode != null && !targetNode.isContentNode) {
                    Text(
                        text = stringResource(R.string.graph_path_explain_unsupported_node),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                } else {
                    SectionErrorCard(
                        errorKind = sheetState.errorKind,
                        onRetry = onRetry,
                        modifier = Modifier.padding(0.dp),
                    )
                }
            }
            sheetState.result != null -> PathExplainResultContent(
                result = sheetState.result,
                centerNode = centerNode,
                targetNode = sheetState.targetNode,
                bridgeSection = sheetState.bridge,
                onNodeClick = onNodeClick,
                onLoadBridge = onLoadBridge,
                onRetryBridge = onLoadBridge,
            )
            sheetState.errorKind != null -> SectionErrorCard(
                errorKind = sheetState.errorKind,
                onRetry = onRetry,
                modifier = Modifier.padding(0.dp),
            )
        }

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
private fun PathExplainResultContent(
    result: PathExplainResult,
    centerNode: GraphNodeUiModel?,
    targetNode: GraphNodeUiModel?,
    bridgeSection: DiscoverySectionState<BridgeResult>,
    onNodeClick: (GraphNodeUiModel) -> Unit,
    onLoadBridge: () -> Unit,
    onRetryBridge: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (!result.found) {
            EmptyTabState(
                title = stringResource(R.string.graph_path_explain_not_found_title),
                message = stringResource(R.string.graph_path_explain_not_found_message),
                actionLabel = null,
                onActionClick = null,
                modifier = Modifier.fillMaxWidth(),
            )
            return@Column
        }

        if (result.warnings.isNotEmpty()) {
            Text(
                text = result.warnings.joinToString("\n"),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
            )
        }

        result.narrative.firstOrNull { it.isNotBlank() }?.let { narrative ->
            Text(
                text = narrative,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        result.steps.forEachIndexed { index, step ->
            PathStepItem(
                step = step,
                isLast = index == result.steps.lastIndex,
                onNodeClick = onNodeClick,
            )
        }

        if (centerNode?.isContentNode == true && targetNode?.isContentNode == true) {
            when {
                bridgeSection.isLoading && !bridgeSection.hasData -> PathExplainSkeleton(itemCount = 2)
                bridgeSection.hasFatalError -> SectionErrorCard(
                    errorKind = bridgeSection.errorKind ?: ErrorKind.Unknown,
                    onRetry = onRetryBridge,
                    modifier = Modifier.padding(0.dp),
                )
                bridgeSection.hasData -> Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    if (bridgeSection.errorKind != null) {
                        SectionErrorCard(
                            errorKind = bridgeSection.errorKind,
                            onRetry = onRetryBridge,
                            modifier = Modifier.padding(0.dp),
                        )
                    }
                    BridgeSection(
                        bridges = bridgeSection.data!!.bridges,
                        onNodeClick = onNodeClick,
                    )
                }
                else -> TextButton(onClick = onLoadBridge) {
                    Text(stringResource(R.string.graph_path_explain_show_bridge))
                }
            }
        }
    }
}

@Composable
private fun PathStepItem(
    step: PathStepUiModel,
    isLast: Boolean,
    onNodeClick: (GraphNodeUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ) {
                Text(
                    text = "${step.order + 1}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                )
            }
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .weight(1f)
                        .padding(vertical = 4.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant),
                )
            }
        }
        RelationNodeRow(
            node = step.node,
            relationLabel = step.edge?.localizedRelationLabel()
                ?: stringResource(R.string.graph_relation_other_node),
            reason = step.explanation ?: step.edge?.reason,
            isAiInferred = step.edge?.isAiInferred == true,
            onNodeClick = onNodeClick,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun PathExplainSkeleton(
    itemCount: Int = 3,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        repeat(itemCount) {
            HeritageContentCard {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                )
            }
        }
    }
}

@Composable
private fun BridgeSection(
    bridges: List<BridgeItemUiModel>,
    onNodeClick: (GraphNodeUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = stringResource(R.string.graph_path_explain_bridge_title),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
        )
        if (bridges.isEmpty()) {
            Text(
                text = stringResource(R.string.graph_path_explain_bridge_empty),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            bridges.take(MAX_BRIDGE_NODES).forEach { bridge ->
                BridgeNodeRow(bridge = bridge, onNodeClick = onNodeClick)
            }
            if (bridges.size > MAX_BRIDGE_NODES) {
                Text(
                    text = stringResource(
                        R.string.and_more_format,
                        bridges.size - MAX_BRIDGE_NODES,
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun BridgeNodeRow(
    bridge: BridgeItemUiModel,
    onNodeClick: (GraphNodeUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    HeritageContentCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            RelationNodeRow(
                node = bridge.node,
                relationLabel = stringResource(R.string.graph_path_explain_bridge_node),
                reason = bridge.reason,
                isAiInferred = false,
                onNodeClick = onNodeClick,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                HeritageMetaChip(
                    text = stringResource(R.string.graph_path_explain_bridge_score, bridge.score),
                )
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

private data class RelatedNodeRow(
    val node: GraphNodeUiModel,
    val edge: GraphEdgeUiModel,
    val groupKey: String,
)

private data class ExploreNodeRow(
    val node: GraphNodeUiModel,
    val distance: Int,
    val edge: GraphEdgeUiModel?,
)

private fun GraphNeighborsResult.relatedRows(): List<RelatedNodeRow> {
    val centerKey = centerNodeKey ?: nodes.firstOrNull()?.nodeKey
    val nodeByKey = nodes.associateBy { it.nodeKey }
    return edges.mapNotNull { edge ->
        val relatedKey = when {
            centerKey == null -> edge.toNodeKey
            edge.fromNodeKey == centerKey -> edge.toNodeKey
            edge.toNodeKey == centerKey -> edge.fromNodeKey
            else -> null
        }
        val node = relatedKey?.let(nodeByKey::get) ?: return@mapNotNull null
        if (node.nodeKey == centerKey) return@mapNotNull null
        RelatedNodeRow(
            node = node,
            edge = edge,
            groupKey = edge.label?.takeIf { it.isNotBlank() } ?: edge.relationType.wireName,
        )
    }.distinctBy { "${it.node.nodeKey}-${it.edge.relationType}-${it.edge.fromNodeKey}-${it.edge.toNodeKey}" }
}

private fun GraphExploreResult.exploreRows(): List<ExploreNodeRow> {
    if (nodes.isEmpty()) return emptyList()
    val centerKey = centerNodeKey ?: nodes.firstOrNull()?.nodeKey
    val distances = mutableMapOf<String, Int>()
    if (centerKey != null) {
        distances[centerKey] = 0
        repeat(depth.coerceIn(1, 2)) {
            val snapshot = distances.toMap()
            edges.forEach { edge ->
                val fromDistance = snapshot[edge.fromNodeKey]
                val toDistance = snapshot[edge.toNodeKey]
                when {
                    fromDistance != null && fromDistance < 2 ->
                        distances.putIfAbsent(edge.toNodeKey, fromDistance + 1)
                    toDistance != null && toDistance < 2 ->
                        distances.putIfAbsent(edge.fromNodeKey, toDistance + 1)
                }
            }
        }
    }
    return nodes
        .filterNot { it.nodeKey == centerKey }
        .map { node ->
            ExploreNodeRow(
                node = node,
                distance = distances[node.nodeKey]?.coerceIn(1, 2) ?: 1,
                edge = edges.firstOrNull { it.fromNodeKey == node.nodeKey || it.toNodeKey == node.nodeKey },
            )
        }
        .sortedWith(compareBy<ExploreNodeRow> { it.distance }.thenBy { it.node.displayTitle })
}

private val GraphExploreResult.canRenderOverview: Boolean
    get() = nodes.isNotEmpty() && edges.isNotEmpty() && nodes.size <= 36 && edges.size <= 72

private fun GraphExploreResult.circularNodePositions(): Map<String, Offset> {
    if (nodes.isEmpty()) return emptyMap()
    val centerKey = centerNodeKey ?: nodes.first().nodeKey
    val outerNodes = nodes.filterNot { it.nodeKey == centerKey }
    val result = mutableMapOf(centerKey to Offset(0.5f, 0.5f))
    val radius = 0.38f
    outerNodes.forEachIndexed { index, node ->
        val angle = 2.0 * PI * index / outerNodes.size.coerceAtLeast(1)
        result[node.nodeKey] = Offset(
            x = (0.5f + radius * cos(angle)).toFloat().coerceIn(0.08f, 0.92f),
            y = (0.5f + radius * sin(angle)).toFloat().coerceIn(0.08f, 0.92f),
        )
    }
    return result
}

private fun GraphNodeType.placeholderResId(): Int = when (this) {
    GraphNodeType.Article -> R.string.graph_node_placeholder_article
    GraphNodeType.DirectoryItem -> R.string.graph_node_placeholder_directory
    GraphNodeType.Inheritor -> R.string.graph_node_placeholder_inheritor
    GraphNodeType.Category,
    GraphNodeType.Region,
    GraphNodeType.Year,
    GraphNodeType.Kind,
    GraphNodeType.ProjectCode,
    -> R.string.graph_node_placeholder_topic
    GraphNodeType.Unknown -> R.string.graph_node_placeholder_unknown
}

private fun GraphNodeType.fallbackLabelResId(): Int = when (this) {
    GraphNodeType.Article -> R.string.graph_node_type_article
    GraphNodeType.DirectoryItem -> R.string.graph_node_type_directory
    GraphNodeType.Inheritor -> R.string.graph_node_type_inheritor
    GraphNodeType.Category,
    GraphNodeType.Region,
    GraphNodeType.Year,
    GraphNodeType.Kind,
    GraphNodeType.ProjectCode,
    -> R.string.graph_node_type_topic
    GraphNodeType.Unknown -> R.string.graph_node_type_unknown
}

private fun GraphEvidenceSource.labelResId(): Int = when (this) {
    GraphEvidenceSource.Explicit -> R.string.graph_evidence_source_explicit
    GraphEvidenceSource.Inferred -> R.string.graph_evidence_source_inferred
    GraphEvidenceSource.Embedding -> R.string.graph_evidence_source_embedding
    GraphEvidenceSource.Ai -> R.string.graph_evidence_source_ai
    GraphEvidenceSource.Unknown -> R.string.graph_evidence_source_unknown
}

@Composable
private fun GraphEdgeUiModel.localizedRelationLabel(): String =
    label?.takeIf { it.isNotBlank() }
        ?: stringResource(GraphRelationFormatter.labelResId(relationType))

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
            onExploreDepthSelected = {},
            onPathClick = {},
            onPathExplainDismiss = {},
            onPathExplainRetry = {},
            onPathExplainLoadBridge = {},
            onNodeClick = {},
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
            onExploreDepthSelected = {},
            onPathClick = {},
            onPathExplainDismiss = {},
            onPathExplainRetry = {},
            onPathExplainLoadBridge = {},
            onNodeClick = {},
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
            onExploreDepthSelected = {},
            onPathClick = {},
            onPathExplainDismiss = {},
            onPathExplainRetry = {},
            onPathExplainLoadBridge = {},
            onNodeClick = {},
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
            onExploreDepthSelected = {},
            onPathClick = {},
            onPathExplainDismiss = {},
            onPathExplainRetry = {},
            onPathExplainLoadBridge = {},
            onNodeClick = {},
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
