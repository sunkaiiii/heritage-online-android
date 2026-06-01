package com.duckylife.heritage.modern.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.duckylife.heritage.modern.R
import com.duckylife.heritage.modern.core.network.dto.ContextCollectionDto
import com.duckylife.heritage.modern.core.network.dto.DetailContextDto
import com.duckylife.heritage.modern.core.network.dto.ExploreTopicInfoDto
import com.duckylife.heritage.modern.core.network.dto.GraphDto
import com.duckylife.heritage.modern.core.network.dto.RecommendationDto
import com.duckylife.heritage.modern.core.network.dto.RelatedSummaryDto
import com.duckylife.heritage.modern.ui.error.ErrorKind
import com.duckylife.heritage.modern.ui.error.fallbackResId

@Composable
fun DetailContextSection(
    context: DetailContextDto?,
    isLoading: Boolean,
    errorKind: ErrorKind?,
    onRetry: () -> Unit,
    onItemClick: (id: String, type: String?, source: String) -> Unit, // (id, type, source)
    onCollectionClick: (String) -> Unit,
    onTopicClick: (String, String) -> Unit, // (type, key)
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (isLoading) {
            ContextLoadingPlaceholder()
            return
        }

        if (errorKind != null) {
            ContextErrorRow(
                errorKind = errorKind,
                onRetry = onRetry,
            )
            return
        }

        if (context == null) return

        if (context.related.isNotEmpty()) {
            ContextRelatedSection(
                related = context.related,
                onItemClick = { id, type -> onItemClick(id, type, "related") },
            )
        }

        if (context.recommendations.isNotEmpty()) {
            ContextRecommendationsSection(
                title = stringResource(R.string.context_recommendations),
                recommendations = context.recommendations,
                onItemClick = { id, type -> onItemClick(id, type, "recommendation") },
            )
        }

        if (context.semanticRecommendations.isNotEmpty()) {
            ContextRecommendationsSection(
                title = stringResource(R.string.context_semantic_recommendations),
                recommendations = context.semanticRecommendations,
                onItemClick = { id, type -> onItemClick(id, type, "semanticRecommendation") },
            )
        }

        if (context.collections.isNotEmpty()) {
            ContextCollectionsSection(
                collections = context.collections,
                onCollectionClick = onCollectionClick,
            )
        }

        if (context.exploreTopics.isNotEmpty()) {
            ContextTopicsSection(
                topics = context.exploreTopics,
                onTopicClick = onTopicClick,
            )
        }

        if (context.graph != null && (context.graph.nodes.isNotEmpty() || context.graph.edges.isNotEmpty())) {
            ContextGraphSection(
                graph = context.graph,
                onItemClick = onItemClick,
            )
        }
    }
}

@Composable
private fun ContextLoadingPlaceholder(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Text(
            text = stringResource(R.string.context_loading),
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ContextErrorRow(
    errorKind: ErrorKind,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
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

@Composable
private fun ContextRelatedSection(
    related: List<RelatedSummaryDto>,
    onItemClick: (String, String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(vertical = 8.dp)) {
        HeritageSectionHeader(title = stringResource(R.string.context_related))
        Spacer(modifier = Modifier.height(8.dp))
        related.forEach { item ->
            ContextRelatedRow(
                item = item,
                onClick = {
                    if (!item.id.isNullOrBlank()) {
                        onItemClick(item.id, item.type)
                    }
                },
            )
        }
    }
}

@Composable
private fun ContextRelatedRow(
    item: RelatedSummaryDto,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val metaParts = listOfNotNull(
        com.duckylife.heritage.modern.ui.text.localizedArticleCategory(item.category),
        item.region,
        com.duckylife.heritage.modern.ui.text.localizedDirectoryKind(item.kind),
    )
    HeritageReferenceCard(
        title = item.title.orEmpty(),
        meta = metaParts.joinToString(" · "),
        onClick = onClick,
        modifier = modifier.padding(vertical = 4.dp),
    )
}

@Composable
private fun ContextRecommendationsSection(
    title: String,
    recommendations: List<RecommendationDto>,
    onItemClick: (String, String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(vertical = 8.dp)) {
        HeritageSectionHeader(title = title)
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 0.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(recommendations) { rec ->
                RecommendationCard(
                    recommendation = rec,
                    onClick = {
                        if (!rec.id.isNullOrBlank()) {
                            onItemClick(rec.id, rec.type)
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun RecommendationCard(
    recommendation: RecommendationDto,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .width(180.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        ),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = recommendation.title.orEmpty(),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            if (!recommendation.reason.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = recommendation.reason,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            val localizedCat = com.duckylife.heritage.modern.ui.text.localizedArticleCategory(recommendation.category)
            if (localizedCat != null) {
                Spacer(modifier = Modifier.height(6.dp))
                HeritageMetaChip(text = localizedCat)
            }
        }
    }
}

@Composable
private fun ContextCollectionsSection(
    collections: List<ContextCollectionDto>,
    onCollectionClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(vertical = 8.dp)) {
        HeritageSectionHeader(title = stringResource(R.string.context_collections))
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 0.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(collections) { collection ->
                ContextCollectionCard(
                    collection = collection,
                    onClick = {
                        if (!collection.id.isNullOrBlank()) {
                            onCollectionClick(collection.id)
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun ContextCollectionCard(
    collection: ContextCollectionDto,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .width(160.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        ),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = collection.title.orEmpty(),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.discovery_items_count, collection.items.size),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ContextTopicsSection(
    topics: List<ExploreTopicInfoDto>,
    onTopicClick: (String, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(vertical = 8.dp)) {
        HeritageSectionHeader(title = stringResource(R.string.context_explore_topics))
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            topics.forEach { topic ->
                FilterChip(
                    selected = false,
                    onClick = {
                        if (!topic.type.isNullOrBlank() && !topic.key.isNullOrBlank()) {
                            onTopicClick(topic.type, topic.key)
                        }
                    },
                    label = { Text(topic.title.orEmpty()) },
                )
            }
        }
    }
}

@Composable
private fun ContextGraphSection(
    graph: GraphDto,
    onItemClick: (id: String, type: String?, source: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val nodeMap = graph.nodes.associateBy { it.id }
    Column(modifier = modifier.padding(vertical = 8.dp)) {
        HeritageSectionHeader(title = stringResource(R.string.context_graph))
        Spacer(modifier = Modifier.height(8.dp))
        graph.edges.forEach { edge ->
            val fromNode = nodeMap[edge.fromId]
            val toNode = nodeMap[edge.toId]
            if (fromNode != null && toNode != null) {
                GraphEdgeRow(
                    fromTitle = fromNode.title.orEmpty(),
                    toTitle = toNode.title.orEmpty(),
                    label = edge.label,
                    reason = edge.reason,
                    source = edge.source,
                    onFromClick = {
                        if (!fromNode.id.isNullOrBlank()) {
                            onItemClick(fromNode.id, fromNode.type, "graph")
                        }
                    },
                    onToClick = {
                        if (!toNode.id.isNullOrBlank()) {
                            onItemClick(toNode.id, toNode.type, "graph")
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun GraphEdgeRow(
    fromTitle: String,
    toTitle: String,
    label: String?,
    reason: String?,
    source: String?,
    onFromClick: () -> Unit,
    onToClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // from -> to 行（分别可点击，整行不 clickable）
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = fromTitle,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clickable(onClick = onFromClick)
                        .weight(1f, fill = false),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = " → ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = toTitle,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clickable(onClick = onToClick)
                        .weight(1f, fill = false),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            // 关系标签
            if (!label.isNullOrBlank()) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            // 关系原因
            if (!reason.isNullOrBlank()) {
                Text(
                    text = reason,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            // 来源
            if (!source.isNullOrBlank()) {
                Text(
                    text = stringResource(R.string.context_graph_source_format, source),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
