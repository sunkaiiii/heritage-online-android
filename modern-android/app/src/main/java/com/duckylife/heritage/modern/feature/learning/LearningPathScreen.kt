package com.duckylife.heritage.modern.feature.learning

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Circle
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
import com.duckylife.heritage.modern.core.network.dto.ExploreTopicLinkDto
import com.duckylife.heritage.modern.core.network.dto.LearningPathDetailDto
import com.duckylife.heritage.modern.core.network.dto.LearningPathStepDto
import com.duckylife.heritage.modern.ui.component.HeritageMetaChip
import com.duckylife.heritage.modern.ui.component.HeritagePageBackground
import com.duckylife.heritage.modern.ui.component.HeritagePageHeader
import com.duckylife.heritage.modern.ui.component.HeritageSectionHeader
import com.duckylife.heritage.modern.ui.error.ErrorKind
import com.duckylife.heritage.modern.ui.error.fallbackResId

@Composable
fun LearningPathRoute(
    id: String,
    onBack: () -> Unit,
    onArticleSelected: (String) -> Unit,
    onDirectoryItemSelected: (String) -> Unit,
    onInheritorSelected: (String) -> Unit,
    onRelatedTopicSelected: (String, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LearningPathViewModel = hiltViewModel<LearningPathViewModel, LearningPathViewModel.Factory>(
        key = "learning-path-$id",
        creationCallback = { factory -> factory.create(id = id) },
    ),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    LearningPathScreen(
        uiState = uiState,
        onBack = onBack,
        onRetry = viewModel::loadPath,
        onItemClick = { item ->
            val itemId = item.id ?: return@LearningPathScreen
            when (item.type) {
                "article" -> onArticleSelected(itemId)
                "directoryItem" -> onDirectoryItemSelected(itemId)
                "inheritor" -> onInheritorSelected(itemId)
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
fun LearningPathScreen(
    uiState: LearningPathUiState,
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
                    LearningErrorContent(
                        errorKind = uiState.errorKind,
                        onRetry = onRetry,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
                uiState.path != null -> {
                    LearningPathContent(
                        path = uiState.path,
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
private fun LearningPathContent(
    path: LearningPathDetailDto,
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
                    title = path.title.orEmpty(),
                    subtitle = path.subtitle,
                )
            }
        }

        if (!path.description.isNullOrBlank()) {
            item {
                Text(
                    text = path.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
        }

        if (path.tags.isNotEmpty()) {
            item {
                FlowRow(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    path.tags.forEach { tag ->
                        HeritageMetaChip(text = tag)
                    }
                }
            }
        }

        if (path.steps.isNotEmpty()) {
            item {
                HeritageSectionHeader(
                    title = stringResource(R.string.learning_steps_title),
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
            itemsIndexed(path.steps) { index, step ->
                LearningStepCard(
                    stepNumber = index + 1,
                    step = step,
                    onItemClick = onItemClick,
                )
            }
        }

        if (path.featuredItems.isNotEmpty()) {
            item {
                HeritageSectionHeader(
                    title = stringResource(R.string.learning_featured_items),
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(path.featuredItems) { item ->
                        FeaturedItemCard(
                            item = item,
                            onClick = { onItemClick(item) },
                        )
                    }
                }
            }
        }

        if (path.relatedTopics.isNotEmpty()) {
            item {
                HeritageSectionHeader(
                    title = stringResource(R.string.explore_related_topics),
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
            item {
                FlowRow(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    path.relatedTopics.forEach { topic ->
                        FilterChip(
                            selected = false,
                            onClick = { onRelatedTopicClick(topic) },
                            label = { Text(topic.title.orEmpty()) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LearningStepCard(
    stepNumber: Int,
    step: LearningPathStepDto,
    onItemClick: (ExploreTopicItemDto) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stepNumber.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
                Column {
                    Text(
                        text = step.title.orEmpty(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    if (!step.subtitle.isNullOrBlank()) {
                        Text(
                            text = step.subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
            if (step.items.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                step.items.forEach { item ->
                    StepItemRow(
                        item = item,
                        onClick = { onItemClick(item) },
                    )
                }
            }
        }
    }
}

@Composable
private fun StepItemRow(
    item: ExploreTopicItemDto,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Outlined.Circle,
            contentDescription = null,
            modifier = Modifier.height(8.dp).width(8.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = item.title.orEmpty(),
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        if (!item.category.isNullOrBlank()) {
            HeritageMetaChip(text = item.category)
        }
    }
}

@Composable
private fun FeaturedItemCard(
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
        }
    }
}

@Preview
@Composable
private fun LearningPathScreenPreview() {
    HeritageTheme {
        LearningPathScreen(
            uiState = LearningPathUiState(
                isLoading = false,
                path = LearningPathDetailDto(
                    id = "preview-path",
                    title = "非遗入门之旅",
                    subtitle = "从传统技艺到民间文学",
                    description = "按照由浅入深的顺序，带你了解非物质文化遗产的核心门类。",
                    tags = listOf("入门", "系统", "推荐"),
                    steps = listOf(
                        LearningPathStepDto(
                            id = "s1",
                            title = "认识非遗",
                            subtitle = "什么是非物质文化遗产",
                            items = listOf(
                                ExploreTopicItemDto(
                                    id = "1",
                                    type = "article",
                                    title = "非遗保护公约",
                                ),
                                ExploreTopicItemDto(
                                    id = "2",
                                    type = "directoryItem",
                                    title = "中国非遗名录体系",
                                ),
                            ),
                        ),
                        LearningPathStepDto(
                            id = "s2",
                            title = "传统技艺",
                            subtitle = "动手体验匠心",
                            items = listOf(
                                ExploreTopicItemDto(
                                    id = "3",
                                    type = "directoryItem",
                                    title = "景泰蓝制作技艺",
                                ),
                            ),
                        ),
                    ),
                    featuredItems = listOf(
                        ExploreTopicItemDto(
                            id = "f1",
                            type = "directoryItem",
                            title = "苏州缂丝",
                            summary = "织中之圣",
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
private fun LearningErrorContent(
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
