package com.duckylife.heritage.modern.feature.stories

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.duckylife.heritage.modern.R
import androidx.compose.ui.tooling.preview.Preview
import com.duckylife.heritage.modern.ui.theme.HeritageTheme
import com.duckylife.heritage.modern.core.image.rememberHeritageImageLoader
import com.duckylife.heritage.modern.core.network.dto.DataStoryDto
import com.duckylife.heritage.modern.core.network.dto.DataStoryItemDto
import com.duckylife.heritage.modern.core.network.dto.DataStorySectionDto
import com.duckylife.heritage.modern.core.network.dto.DiscoveryItemDto
import com.duckylife.heritage.modern.core.network.dto.ExploreTopicInfoDto
import com.duckylife.heritage.modern.ui.component.DiscoveryItemCard
import com.duckylife.heritage.modern.ui.component.HeritageDetailImage
import com.duckylife.heritage.modern.ui.component.HeritagePageBackground
import com.duckylife.heritage.modern.ui.component.HeritagePageHeader
import com.duckylife.heritage.modern.ui.component.HeritageSectionHeader
import com.duckylife.heritage.modern.ui.error.ErrorKind
import com.duckylife.heritage.modern.ui.error.fallbackResId

@Composable
fun StoryRoute(
    region: String?,
    category: String?,
    year: Int?,
    onBack: () -> Unit,
    onItemClick: (DiscoveryItemDto) -> Unit = {},
    onTopicClick: (String, String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier,
    viewModel: StoryViewModel = hiltViewModel<StoryViewModel, StoryViewModel.Factory>(
        key = "story-${region.orEmpty()}-${category.orEmpty()}-${year ?: ""}",
        creationCallback = { factory -> factory.create(region = region, category = category, year = year) },
    ),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    StoryScreen(
        uiState = uiState,
        onBack = onBack,
        onRetry = viewModel::load,
        onItemClick = onItemClick,
        onTopicClick = onTopicClick,
        modifier = modifier,
    )
}

@Composable
fun StoryScreen(
    uiState: StoryUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onItemClick: (DiscoveryItemDto) -> Unit = {},
    onTopicClick: (String, String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier,
) {
    HeritagePageBackground(modifier = modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.errorKind != null -> {
                    StoryErrorContent(
                        errorKind = uiState.errorKind,
                        onRetry = onRetry,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
                uiState.story != null -> {
                    StoryContent(
                        story = uiState.story,
                        onBack = onBack,
                        onItemClick = onItemClick,
                        onTopicClick = onTopicClick,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StoryContent(
    story: DataStoryDto,
    onBack: () -> Unit,
    onItemClick: (DiscoveryItemDto) -> Unit = {},
    onTopicClick: (String, String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier,
) {
    val imageLoader = rememberHeritageImageLoader()

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // 顶部：返回按钮 + 标题
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
                    title = story.title,
                    subtitle = story.subtitle,
                )
            }
        }

        // Hero 图片
        val heroImageUrl = story.heroImage?.displayUrl
            ?: story.heroImage?.sourceUrl
            ?: story.heroImage?.originalUrl
        if (heroImageUrl != null) {
            item {
                HeritageDetailImage(
                    imageUrl = heroImageUrl,
                    imageLoader = imageLoader,
                    contentDescription = story.heroImage?.altText,
                    fallbackText = story.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .padding(horizontal = 16.dp),
                )
            }
        }

        // 各个 section
        items(story.sections) { section ->
            StorySection(
                section = section,
                onItemClick = onItemClick,
            )
        }

        // 底部相关主题
        if (story.relatedTopics.isNotEmpty()) {
            item {
                HeritageSectionHeader(
                    title = stringResource(R.string.story_related_topics),
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
            item {
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    story.relatedTopics.forEach { topic ->
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
    }
}

@Composable
private fun StorySection(
    section: DataStorySectionDto,
    onItemClick: (DiscoveryItemDto) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        if (section.title.isNotBlank()) {
            HeritageSectionHeader(
                title = section.title,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (!section.body.isNullOrBlank()) {
            Text(
                text = section.body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (section.items.isNotEmpty()) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(section.items) { item ->
                    val discoveryItem = item.toDiscoveryItemDto()
                    DiscoveryItemCard(
                        item = discoveryItem,
                        onClick = { onItemClick(discoveryItem) },
                    )
                }
            }
        }
    }
}

private fun DataStoryItemDto.toDiscoveryItemDto(): DiscoveryItemDto = DiscoveryItemDto(
    id = id,
    type = type,
    title = title,
    summary = summary,
    coverImage = coverImage,
    sourceUrl = sourceUrl,
)

@Preview
@Composable
private fun StoryScreenPreview() {
    HeritageTheme {
        StoryScreen(
            uiState = StoryUiState(
                isLoading = false,
                story = DataStoryDto(
                    id = "preview-story",
                    title = "北京非遗故事",
                    subtitle = "从历史走向未来",
                    heroImage = com.duckylife.heritage.modern.core.network.dto.MediaAssetDto(
                        displayUrl = "https://example.com/beijing.jpg",
                        altText = "北京非遗",
                    ),
                    sections = listOf(
                        DataStorySectionDto(
                            id = "sec1",
                            title = "京剧的诞生",
                            type = "text",
                            body = "京剧形成于北京，是中国影响最大的戏曲剧种。",
                            items = listOf(
                                DataStoryItemDto(
                                    type = "directoryItem",
                                    id = "d1",
                                    title = "京剧",
                                    summary = "中国国粹",
                                ),
                            ),
                        ),
                        DataStorySectionDto(
                            id = "sec2",
                            title = "传统技艺",
                            type = "text",
                            body = "景泰蓝、雕漆等传统技艺在北京世代传承。",
                        ),
                    ),
                    relatedTopics = listOf(
                        ExploreTopicInfoDto(
                            type = "region",
                            key = "北京",
                            title = "北京",
                        ),
                    ),
                ),
            ),
            onBack = {},
            onRetry = {},
            onItemClick = {},
            onTopicClick = { _, _ -> },
        )
    }
}

@Composable
private fun StoryErrorContent(
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
