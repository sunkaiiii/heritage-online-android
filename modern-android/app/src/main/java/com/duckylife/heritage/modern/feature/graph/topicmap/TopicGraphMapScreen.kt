package com.duckylife.heritage.modern.feature.graph.topicmap

import android.content.res.Configuration
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.AccountTree
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.duckylife.heritage.modern.R
import com.duckylife.heritage.modern.core.image.rememberHeritageImageLoader
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphNodeType
import com.duckylife.heritage.modern.feature.graph.model.GraphEdgeUiModel
import com.duckylife.heritage.modern.feature.graph.model.GraphNodeUiModel
import com.duckylife.heritage.modern.feature.graph.model.TopicGraphMapResult
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicGraphMapRoute(
    topicType: String,
    topicKey: String,
    onBack: () -> Unit,
    onContentClick: (type: String, id: String) -> Unit,
    onTopicClick: (type: String, topicKey: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: TopicGraphMapViewModel = hiltViewModel<TopicGraphMapViewModel, TopicGraphMapViewModel.Factory>(
        key = "topic-map-$topicType-$topicKey",
        creationCallback = { factory ->
            factory.create(
                topicType = topicType,
                topicKey = topicKey,
            )
        },
    )
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    TopicGraphMapScreen(
        uiState = uiState,
        onBack = onBack,
        onRetry = viewModel::retry,
        onViewModeSelected = viewModel::selectViewMode,
        onNodeClick = { node ->
            when {
                node.isContentNode && !node.id.isNullOrBlank() ->
                    onContentClick(node.type.wireName, node.id)

                node.isTopicNode -> onTopicClick(node.type.wireName, node.topicKey)
            }
        },
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TopicGraphMapScreen(
    uiState: TopicGraphMapUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onViewModeSelected: (TopicGraphMapViewMode) -> Unit,
    onNodeClick: (GraphNodeUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.topic_graph_map_title)) },
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
                uiState.isLoading && uiState.result == null -> TopicGraphMapSkeleton()
                uiState.errorKind != null && uiState.result == null -> ErrorState(
                    errorKind = uiState.errorKind,
                    onRetry = onRetry,
                )
                uiState.result != null -> TopicGraphMapContent(
                    result = uiState.result,
                    selectedViewMode = uiState.selectedViewMode,
                    onViewModeSelected = onViewModeSelected,
                    onNodeClick = onNodeClick,
                )
            }
        }
    }
}

@Composable
private fun TopicGraphMapContent(
    result: TopicGraphMapResult,
    selectedViewMode: TopicGraphMapViewMode,
    onViewModeSelected: (TopicGraphMapViewMode) -> Unit,
    onNodeClick: (GraphNodeUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val topicNode = result.topicNode
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = topicNode?.displayTitle ?: result.topicKey,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    HeritageMetaChip(
                        text = localizedContentType(result.topicType)
                            .ifBlank { stringResource(R.string.graph_node_type_topic) },
                    )
                    HeritageMetaChip(
                        text = stringResource(
                            R.string.topic_graph_map_node_count,
                            result.nodes.size,
                            result.edges.size,
                        ),
                    )
                }
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = selectedViewMode == TopicGraphMapViewMode.List,
                    onClick = { onViewModeSelected(TopicGraphMapViewMode.List) },
                    label = { Text(stringResource(R.string.topic_graph_map_list_view)) },
                )
                FilterChip(
                    selected = selectedViewMode == TopicGraphMapViewMode.Overview,
                    onClick = { onViewModeSelected(TopicGraphMapViewMode.Overview) },
                    label = { Text(stringResource(R.string.topic_graph_map_overview_view)) },
                )
            }
        }

        when {
            result.nodes.isEmpty() -> item {
                EmptyState(
                    title = stringResource(R.string.topic_graph_map_empty_title),
                    message = stringResource(R.string.topic_graph_map_empty_message),
                )
            }

            selectedViewMode == TopicGraphMapViewMode.Overview && result.canRenderOverview -> {
                item {
                    TopicGraphOverviewCanvas(
                        result = result,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp),
                    )
                }
                item {
                    Text(
                        text = stringResource(R.string.graph_overview_accessible_list_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            selectedViewMode == TopicGraphMapViewMode.Overview -> item {
                EmptyState(
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
                )
            }

            else -> {
                val contentNodes = result.nodes.filter { it.isContentNode }
                val topicNodes = result.nodes.filter { it.isTopicNode }

                if (contentNodes.isNotEmpty()) {
                    item(key = "content-group") {
                        TopicGroupHeader(
                            title = stringResource(R.string.topic_graph_map_content_nodes),
                            count = contentNodes.size,
                        )
                    }
                    items(
                        items = contentNodes,
                        key = { "content-${it.nodeKey}" },
                    ) { node ->
                        TopicNodeRow(node = node, onNodeClick = onNodeClick)
                    }
                }

                if (topicNodes.isNotEmpty()) {
                    item(key = "topic-group") {
                        TopicGroupHeader(
                            title = stringResource(R.string.topic_graph_map_topic_nodes),
                            count = topicNodes.size,
                        )
                    }
                    items(
                        items = topicNodes,
                        key = { "topic-${it.nodeKey}" },
                    ) { node ->
                        TopicNodeRow(node = node, onNodeClick = onNodeClick)
                    }
                }
            }
        }
    }
}

@Composable
private fun TopicNodeRow(
    node: GraphNodeUiModel,
    onNodeClick: (GraphNodeUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val imageLoader = rememberHeritageImageLoader()
    val canNavigate = node.isContentNode || node.isTopicNode
    HeritageContentCard(
        onClick = if (canNavigate) {{ onNodeClick(node) }} else null,
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
                }
                Text(
                    text = node.displayTitle,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                if (!node.subtitle.isNullOrBlank()) {
                    Text(
                        text = node.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    node.category?.takeIf { it.isNotBlank() }?.let { HeritageMetaChip(text = it) }
                    node.region?.takeIf { it.isNotBlank() }?.let { HeritageMetaChip(text = it) }
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
private fun TopicGroupHeader(
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
private fun TopicGraphMapSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        repeat(6) {
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
private fun ErrorState(
    errorKind: ErrorKind,
    onRetry: () -> Unit,
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
            text = stringResource(errorKind.fallbackResId()),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.height(12.dp))
        androidx.compose.material3.Button(onClick = onRetry) {
            Text(stringResource(R.string.action_retry))
        }
    }
}

@Composable
private fun EmptyState(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun TopicGraphOverviewCanvas(
    result: TopicGraphMapResult,
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
                drawLine(color = lineColor, start = from, end = to, strokeWidth = 2f)
            }
        }
        result.nodes.forEach { node ->
            val offset = scaled[node.nodeKey] ?: return@forEach
            drawCircle(
                color = if (node.isContentNode) contentColor else topicColor,
                radius = if (node.nodeKey == result.topicNode?.nodeKey) 9f else 6f,
                center = offset,
            )
        }
    }
}

private val TopicGraphMapResult.canRenderOverview: Boolean
    get() = nodes.isNotEmpty() && edges.isNotEmpty() && nodes.size <= 36 && edges.size <= 72

private fun TopicGraphMapResult.circularNodePositions(): Map<String, Offset> {
    if (nodes.isEmpty()) return emptyMap()
    val centerKey = topicNode?.nodeKey ?: nodes.first().nodeKey
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

@Preview(name = "Topic Graph Map - Light")
@Composable
private fun TopicGraphMapScreenPreview() {
    HeritageTheme {
        TopicGraphMapScreen(
            uiState = TopicGraphMapUiState(
                result = TopicGraphMapResult(
                    topicType = "category",
                    topicKey = "folk-art",
                    topicNode = GraphNodeUiModel(
                        nodeKey = "category-folk-art",
                        type = GraphNodeType.Category,
                        id = "folk-art",
                        title = "民间美术",
                    ),
                    nodes = listOf(
                        GraphNodeUiModel(
                            nodeKey = "category-folk-art",
                            type = GraphNodeType.Category,
                            id = "folk-art",
                            title = "民间美术",
                        ),
                        GraphNodeUiModel(
                            nodeKey = "article-1",
                            type = GraphNodeType.Article,
                            id = "a1",
                            title = "Article 1",
                        ),
                    ),
                    edges = emptyList(),
                ),
            ),
            onBack = {},
            onRetry = {},
            onViewModeSelected = {},
            onNodeClick = {},
        )
    }
}

@Preview(name = "Topic Graph Map - Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TopicGraphMapScreenDarkPreview() {
    HeritageTheme {
        TopicGraphMapScreen(
            uiState = TopicGraphMapUiState(
                result = TopicGraphMapResult(
                    topicType = "category",
                    topicKey = "folk-art",
                    topicNode = GraphNodeUiModel(
                        nodeKey = "category-folk-art",
                        type = GraphNodeType.Category,
                        id = "folk-art",
                        title = "民间美术",
                    ),
                    nodes = emptyList(),
                    edges = emptyList(),
                ),
            ),
            onBack = {},
            onRetry = {},
            onViewModeSelected = {},
            onNodeClick = {},
        )
    }
}
