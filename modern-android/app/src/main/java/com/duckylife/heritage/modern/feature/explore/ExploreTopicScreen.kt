package com.duckylife.heritage.modern.feature.explore

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.duckylife.heritage.modern.R
import androidx.compose.ui.tooling.preview.Preview
import com.duckylife.heritage.modern.ui.theme.HeritageTheme
import com.duckylife.heritage.modern.core.network.dto.ExploreTopicItemDto
import com.duckylife.heritage.modern.core.network.dto.ExploreTopicSectionDto
import com.duckylife.heritage.modern.core.network.dto.ExploreTopicStatDto
import com.duckylife.heritage.modern.core.network.dto.ExploreTopicV2Dto
import com.duckylife.heritage.modern.core.network.dto.ExploreTopicLinkDto
import com.duckylife.heritage.modern.ui.component.HeritageMetaChip
import com.duckylife.heritage.modern.ui.component.HeritagePageBackground
import com.duckylife.heritage.modern.ui.component.HeritagePageHeader
import com.duckylife.heritage.modern.ui.component.HeritageSectionHeader
import com.duckylife.heritage.modern.ui.error.ErrorKind
import com.duckylife.heritage.modern.ui.error.fallbackResId
import com.duckylife.heritage.modern.ui.text.localizedArticleCategory

@Composable
fun ExploreTopicRoute(
    type: String,
    key: String,
    onBack: () -> Unit,
    onArticleSelected: (String) -> Unit,
    onDirectoryItemSelected: (String) -> Unit,
    onInheritorSelected: (String) -> Unit,
    onRelatedTopicSelected: (String, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ExploreTopicViewModel = hiltViewModel<ExploreTopicViewModel, ExploreTopicViewModel.Factory>(
        key = "explore-topic-$type-$key",
        creationCallback = { factory -> factory.create(type = type, key = key) },
    ),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    ExploreTopicScreen(
        uiState = uiState,
        onBack = onBack,
        onRetry = viewModel::loadTopic,
        onItemClick = { item ->
            val id = item.id ?: return@ExploreTopicScreen
            when (item.type) {
                "article" -> onArticleSelected(id)
                "directoryItem" -> onDirectoryItemSelected(id)
                "inheritor" -> onInheritorSelected(id)
            }
        },
        onRelatedTopicClick = { topic ->
            if (!topic.type.isNullOrBlank() && !topic.key.isNullOrBlank()) {
                onRelatedTopicSelected(topic.type, topic.key)
            }
        },
        modifier = modifier,
    )
}

@Composable
fun ExploreTopicScreen(
    uiState: ExploreTopicUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onItemClick: (ExploreTopicItemDto) -> Unit,
    onRelatedTopicClick: (ExploreTopicLinkDto) -> Unit,
    modifier: Modifier = Modifier,
) {
    HeritagePageBackground(modifier = modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.errorKind != null -> {
                    ExploreErrorContent(
                        errorKind = uiState.errorKind,
                        onRetry = onRetry,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
                uiState.topic != null -> {
                    ExploreTopicContent(
                        topic = uiState.topic,
                        onBack = onBack,
                        onItemClick = onItemClick,
                        onRelatedTopicClick = onRelatedTopicClick,
                    )
                }
            }
        }
    }
}

@Composable
private fun ExploreTopicContent(
    topic: ExploreTopicV2Dto,
    onBack: () -> Unit,
    onItemClick: (ExploreTopicItemDto) -> Unit,
    onRelatedTopicClick: (ExploreTopicLinkDto) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = stringResource(R.string.action_back),
                    )
                }
                HeritagePageHeader(
                    title = topic.topic?.title.orEmpty(),
                    subtitle = topic.topic?.subtitle,
                )
            }
        }

        if (topic.stats.isNotEmpty()) {
            item {
                StatsRow(
                    stats = topic.stats,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
        }

        items(topic.sections) { section ->
            TopicSection(
                section = section,
                onItemClick = onItemClick,
            )
        }

        if (topic.timeline.isNotEmpty()) {
            item {
                HeritageSectionHeader(
                    title = stringResource(R.string.discovery_timeline),
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
            items(topic.timeline) { item ->
                TopicTimelineRow(
                    item = item,
                    onClick = { onItemClick(item) },
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
        }

        if (topic.relatedTopics.isNotEmpty()) {
            item {
                HeritageSectionHeader(
                    title = stringResource(R.string.explore_related_topics),
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
            item {
                RelatedTopicsRow(
                    topics = topic.relatedTopics,
                    onTopicClick = onRelatedTopicClick,
                )
            }
        }
    }
}

@Composable
private fun StatsRow(
    stats: List<ExploreTopicStatDto>,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        stats.forEach { stat ->
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                ),
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = stat.value.toString(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = stat.name.orEmpty(),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun TopicSection(
    section: ExploreTopicSectionDto,
    onItemClick: (ExploreTopicItemDto) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        if (!section.title.isNullOrBlank()) {
            HeritageSectionHeader(
                title = section.title,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(section.items) { item ->
                TopicItemCard(
                    item = item,
                    onClick = { onItemClick(item) },
                )
            }
        }
    }
}

@Composable
private fun TopicItemCard(
    item: ExploreTopicItemDto,
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
                text = item.title.orEmpty(),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            if (!item.summary.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.summary,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                localizedArticleCategory(item.category)?.let { categoryLabel ->
                    HeritageMetaChip(text = categoryLabel)
                }
                if (!item.region.isNullOrBlank()) {
                    HeritageMetaChip(text = item.region)
                }
            }
        }
    }
}

@Composable
private fun TopicTimelineRow(
    item: ExploreTopicItemDto,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title.orEmpty(),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                if (!item.summary.isNullOrBlank()) {
                    Text(
                        text = item.summary,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            if (item.year != null) {
                HeritageMetaChip(text = item.year.toString())
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RelatedTopicsRow(
    topics: List<ExploreTopicLinkDto>,
    onTopicClick: (ExploreTopicLinkDto) -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        topics.forEach { topic ->
            FilterChip(
                selected = false,
                onClick = { onTopicClick(topic) },
                label = { Text(topic.title.orEmpty()) },
            )
        }
    }
}

@Preview
@Composable
private fun ExploreTopicScreenPreview() {
    HeritageTheme {
        ExploreTopicScreen(
            uiState = ExploreTopicUiState(
                isLoading = false,
                topic = ExploreTopicV2Dto(
                    topic = com.duckylife.heritage.modern.core.network.dto.ExploreTopicInfoDto(
                        type = "category",
                        key = "traditionalCraft",
                        title = "传统技艺",
                        subtitle = "匠心传承",
                    ),
                    stats = listOf(
                        ExploreTopicStatDto(name = "名录", value = 128),
                        ExploreTopicStatDto(name = "文章", value = 56),
                        ExploreTopicStatDto(name = "传承人", value = 312),
                    ),
                    sections = listOf(
                        ExploreTopicSectionDto(
                            title = "精选名录",
                            items = listOf(
                                ExploreTopicItemDto(
                                    id = "1",
                                    type = "directoryItem",
                                    title = "景泰蓝制作技艺",
                                    summary = "北京传统手工艺",
                                    category = "traditionalCraft",
                                    region = "北京",
                                ),
                                ExploreTopicItemDto(
                                    id = "2",
                                    type = "directoryItem",
                                    title = "苏绣",
                                    summary = "苏州刺绣技艺",
                                    category = "traditionalCraft",
                                    region = "江苏",
                                ),
                            ),
                        ),
                    ),
                    timeline = listOf(
                        ExploreTopicItemDto(
                            id = "3",
                            type = "article",
                            title = "传统技艺保护元年",
                            year = 2006,
                        ),
                    ),
                    relatedTopics = listOf(
                        ExploreTopicLinkDto(
                            type = "category",
                            key = "folkArt",
                            title = "民间美术",
                        ),
                    ),
                ),
            ),
            onBack = {},
            onRetry = {},
            onItemClick = {},
            onRelatedTopicClick = {},
        )
    }
}

@Composable
private fun ExploreErrorContent(
    errorKind: ErrorKind,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(errorKind.fallbackResId()),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text(text = stringResource(R.string.action_retry))
        }
    }
}
