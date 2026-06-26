package com.duckylife.heritage.modern.feature.graph.trail

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphRelationType
import com.duckylife.heritage.modern.core.network.dto.advanced.TrailStrategy
import com.duckylife.heritage.modern.feature.discovery.GraphTrailSource
import com.duckylife.heritage.modern.feature.graph.format.GraphRelationFormatter
import com.duckylife.heritage.modern.feature.graph.model.GraphNodeUiModel
import com.duckylife.heritage.modern.feature.graph.model.GraphTrailResult
import com.duckylife.heritage.modern.feature.graph.model.GraphTrailStepUiModel
import com.duckylife.heritage.modern.ui.component.HeritageContentCard
import com.duckylife.heritage.modern.ui.component.HeritageListImage
import com.duckylife.heritage.modern.ui.component.HeritageMetaChip
import com.duckylife.heritage.modern.ui.error.ErrorKind
import com.duckylife.heritage.modern.ui.error.fallbackResId
import com.duckylife.heritage.modern.ui.text.localizedContentType
import com.duckylife.heritage.modern.ui.theme.HeritageTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GraphTrailRoute(
    source: GraphTrailSource,
    onBack: () -> Unit,
    onContentClick: (type: String, id: String) -> Unit,
    onTopicClick: (type: String, topicKey: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: GraphTrailViewModel = hiltViewModel<GraphTrailViewModel, GraphTrailViewModel.Factory>(
        key = "graph-trail-${source.hashCode()}",
        creationCallback = { factory -> factory.create(source) },
    )
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    GraphTrailScreen(
        uiState = uiState,
        onBack = onBack,
        onRetry = viewModel::retry,
        onResample = viewModel::resample,
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
internal fun GraphTrailScreen(
    uiState: GraphTrailUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onResample: () -> Unit,
    onNodeClick: (GraphNodeUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.graph_trail_title)) },
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
        bottomBar = {
            if (uiState.canResample) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                ) {
                    Button(
                        onClick = onResample,
                        enabled = !uiState.isLoading,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(stringResource(R.string.graph_trail_resample))
                    }
                }
            }
        },
        modifier = modifier,
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when {
                uiState.isLoading && uiState.trail == null -> GraphTrailSkeleton()
                uiState.errorKind != null && uiState.trail == null -> ErrorState(
                    errorKind = uiState.errorKind,
                    onRetry = onRetry,
                )
                uiState.trail != null -> TrailContent(
                    trail = uiState.trail,
                    recommendedStepOrder = 0,
                    onNodeClick = onNodeClick,
                )
                else -> EmptyState(
                    title = stringResource(R.string.graph_trail_empty_title),
                    message = stringResource(R.string.graph_trail_empty_message),
                )
            }
        }
    }
}

@Composable
private fun TrailContent(
    trail: GraphTrailResult,
    recommendedStepOrder: Int,
    onNodeClick: (GraphNodeUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            TrailHeader(trail = trail)
        }

        if (trail.steps.isEmpty()) {
            item {
                EmptyState(
                    title = stringResource(R.string.graph_trail_empty_title),
                    message = stringResource(R.string.graph_trail_empty_message),
                )
            }
        } else {
            itemsIndexed(
                items = trail.steps,
                key = { _, step -> step.node.nodeKey },
            ) { index, step ->
                TrailStepItem(
                    step = step,
                    isRecommended = step.order == recommendedStepOrder,
                    isLast = index == trail.steps.lastIndex,
                    onNodeClick = onNodeClick,
                )
            }
        }
    }
}

@Composable
private fun TrailHeader(
    trail: GraphTrailResult,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = trail.title ?: trail.startNode?.displayTitle ?: stringResource(R.string.graph_trail_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
        )
        if (!trail.subtitle.isNullOrBlank()) {
            Text(
                text = trail.subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            HeritageMetaChip(
                text = stringResource(
                    R.string.graph_trail_strategy_label,
                ) + ": " + trail.strategy.wireName,
            )
            HeritageMetaChip(
                text = stringResource(R.string.graph_trail_steps_format, trail.steps.size),
            )
        }
        if (trail.topicLabels.isNotEmpty()) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                trail.topicLabels.take(4).forEach { label ->
                    HeritageMetaChip(text = label)
                }
            }
        }
    }
}

@Composable
private fun TrailStepItem(
    step: GraphTrailStepUiModel,
    isRecommended: Boolean,
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
                shape = MaterialTheme.shapes.medium,
                color = if (isRecommended) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surfaceContainerHighest
                },
                contentColor = if (isRecommended) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
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
                        .size(width = 2.dp, height = 24.dp)
                        .padding(vertical = 4.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant),
                )
            }
        }

        HeritageContentCard(
            onClick = { onNodeClick(step.node) },
            modifier = Modifier.weight(1f),
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    HeritageMetaChip(
                        text = localizedContentType(step.node.type.wireName)
                            .ifBlank { stringResource(step.node.type.fallbackLabelResId()) },
                    )
                    if (isRecommended) {
                        HeritageMetaChip(
                            text = stringResource(R.string.graph_trail_step_recommended),
                        )
                    } else if (step.order == 0) {
                        HeritageMetaChip(
                            text = stringResource(R.string.graph_trail_step_start),
                        )
                    }
                }
                Text(
                    text = step.node.displayTitle,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                if (!step.reason.isNullOrBlank()) {
                    Text(
                        text = step.reason,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                if (step.viaRelationType != GraphRelationType.Unknown) {
                    Text(
                        text = stringResource(
                            R.string.graph_trail_step_via,
                            stringResource(GraphRelationFormatter.labelResId(step.viaRelationType)),
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun GraphTrailSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        repeat(5) {
            HeritageContentCard {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(96.dp)
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
        Button(onClick = onRetry) {
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
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
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

@Preview(name = "Graph Trail - Light")
@Composable
private fun GraphTrailScreenPreview() {
    HeritageTheme {
        GraphTrailScreen(
            uiState = GraphTrailUiState(
                trail = GraphTrailResult(
                    trailId = "preview",
                    strategy = TrailStrategy.Mixed,
                    title = "从民间美术到传承人",
                    subtitle = "一条跨内容类型的探索路线",
                    startNode = GraphNodeUiModel(
                        nodeKey = "article-1",
                        type = GraphNodeType.Article,
                        id = "a1",
                        title = "Article 1",
                    ),
                    endNode = null,
                    steps = listOf(
                        GraphTrailStepUiModel(
                            order = 0,
                            node = GraphNodeUiModel(
                                nodeKey = "article-1",
                                type = GraphNodeType.Article,
                                id = "a1",
                                title = "Article 1",
                            ),
                            stepType = "start",
                            reason = "起点",
                            viaRelationType = GraphRelationType.RelatedTo,
                        ),
                    ),
                    nodes = emptyList(),
                    edges = emptyList(),
                    topicLabels = listOf("民间美术"),
                    score = 0.8,
                ),
                canResample = true,
            ),
            onBack = {},
            onRetry = {},
            onResample = {},
            onNodeClick = {},
        )
    }
}

@Preview(name = "Graph Trail - Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun GraphTrailScreenDarkPreview() {
    HeritageTheme {
        GraphTrailScreen(
            uiState = GraphTrailUiState(
                trail = null,
                errorKind = ErrorKind.ServerError,
            ),
            onBack = {},
            onRetry = {},
            onResample = {},
            onNodeClick = {},
        )
    }
}
