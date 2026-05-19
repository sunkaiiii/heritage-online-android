package com.duckylife.heritage.modern.feature.articles

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.annotation.StringRes
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil3.ImageLoader
import com.duckylife.heritage.modern.R
import com.duckylife.heritage.modern.core.image.rememberHeritageImageLoader
import com.duckylife.heritage.modern.core.network.dto.ArticleCategory
import com.duckylife.heritage.modern.core.network.dto.ArticleSummaryDto
import com.duckylife.heritage.modern.core.network.dto.HomeBannerDto
import com.duckylife.heritage.modern.ui.component.HeritageListCard
import com.duckylife.heritage.modern.ui.component.HeritageListImage
import com.duckylife.heritage.modern.ui.component.HeritageMetaChip
import com.duckylife.heritage.modern.ui.component.HeritagePageBackground
import com.duckylife.heritage.modern.ui.component.HeritagePageHeader
import com.duckylife.heritage.modern.ui.component.HeritageSectionHeader
import com.duckylife.heritage.modern.ui.theme.HeritageTheme

@Composable
fun ArticlesRoute(
    onSettingsSelected: () -> Unit,
    onArticleSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ArticlesViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    val articles = viewModel.articles.collectAsLazyPagingItems()
    ArticlesScreen(
        uiState = uiState,
        articles = articles,
        onRefreshBanners = viewModel::refreshBanners,
        onCategorySelected = viewModel::selectCategory,
        onSettingsSelected = onSettingsSelected,
        onArticleSelected = onArticleSelected,
        modifier = modifier,
    )
}

@Composable
fun ArticlesScreen(
    uiState: ArticlesUiState,
    articles: LazyPagingItems<ArticleSummaryDto>,
    onRefreshBanners: () -> Unit,
    onCategorySelected: (ArticleCategory) -> Unit,
    onSettingsSelected: () -> Unit,
    onArticleSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val imageLoader = rememberHeritageImageLoader()
    ArticlesContent(
        uiState = uiState,
        articles = articles,
        onRefreshBanners = onRefreshBanners,
        onCategorySelected = onCategorySelected,
        onSettingsSelected = onSettingsSelected,
        onArticleSelected = onArticleSelected,
        imageLoader = imageLoader,
        modifier = modifier,
    )
}

@Composable
private fun ArticlesContent(
    uiState: ArticlesUiState,
    articles: LazyPagingItems<ArticleSummaryDto>,
    onRefreshBanners: () -> Unit,
    onCategorySelected: (ArticleCategory) -> Unit,
    onSettingsSelected: () -> Unit,
    onArticleSelected: (String) -> Unit,
    imageLoader: ImageLoader,
    modifier: Modifier = Modifier,
) {
    HeritagePageBackground(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                ArticlesHeader(
                    onSettingsSelected = onSettingsSelected,
                    onRetry = {
                        onRefreshBanners()
                        articles.refresh()
                    },
                )
            }

            item {
                when {
                    uiState.banners.isNotEmpty() -> BannerStrip(
                        banners = uiState.banners,
                        imageLoader = imageLoader,
                    )

                    uiState.isLoadingBanners -> BannerLoadingStrip()

                    uiState.bannerErrorMessage != null -> InlineRetryMessage(
                        message = uiState.bannerErrorMessage
                            .takeUnless { it.isBlank() }
                            ?: stringResource(R.string.banner_load_failed),
                        onRetry = onRefreshBanners,
                        modifier = Modifier.padding(horizontal = 20.dp),
                    )
                }
            }

            item {
                HeritageSectionHeader(
                    title = stringResource(R.string.articles_latest_title),
                    modifier = Modifier.padding(horizontal = 20.dp),
                )
            }

            item {
                ArticleCategoryTabs(
                    selectedCategory = uiState.selectedCategory,
                    onCategorySelected = onCategorySelected,
                )
            }

            when (val refreshState = articles.loadState.refresh) {
                is LoadState.Loading -> item {
                    LoadingContent(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                    )
                }

                is LoadState.Error -> item {
                    ErrorContent(
                        message = refreshState.error.message ?: stringResource(R.string.article_load_failed),
                        onRetry = articles::retry,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                    )
                }

                is LoadState.NotLoading -> {
                    if (articles.itemCount == 0) {
                        item {
                            EmptyContent(
                                onRetry = articles::refresh,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(220.dp),
                            )
                        }
                    }
                }
            }

            items(
                count = articles.itemCount,
                key = articles.itemKey { it.id ?: it.sourceUrl ?: it.title.orEmpty() },
            ) { index ->
                val article = articles[index]
                if (article != null) {
                    ArticleRow(
                        article = article,
                        imageLoader = imageLoader,
                        prominent = index == 0,
                        onClick = onArticleSelected,
                        modifier = Modifier.padding(horizontal = 20.dp),
                    )
                }
            }

            when (val appendState = articles.loadState.append) {
                is LoadState.Loading -> item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is LoadState.Error -> item {
                    InlineRetryMessage(
                        message = appendState.error.message ?: stringResource(R.string.article_append_failed),
                        onRetry = articles::retry,
                        modifier = Modifier.padding(horizontal = 20.dp),
                    )
                }

                is LoadState.NotLoading -> Unit
            }
        }
    }
}

@Composable
private fun ArticleCategoryTabs(
    selectedCategory: ArticleCategory,
    onCategorySelected: (ArticleCategory) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(ArticleCategory.entries) { category ->
            FilterChip(
                selected = category == selectedCategory,
                onClick = { onCategorySelected(category) },
                label = { Text(stringResource(category.labelRes)) },
            )
        }
    }
}

@Composable
private fun ArticlesHeader(
    onSettingsSelected: () -> Unit,
    onRetry: () -> Unit,
) {
    HeritagePageHeader(
        title = stringResource(R.string.articles_header_title),
        subtitle = stringResource(R.string.articles_header_subtitle),
    ) {
        IconButton(onClick = onSettingsSelected) {
            Icon(
                imageVector = Icons.Outlined.Settings,
                contentDescription = stringResource(R.string.nav_settings),
            )
        }
        IconButton(onClick = onRetry) {
            Icon(
                imageVector = Icons.Outlined.Refresh,
                contentDescription = stringResource(R.string.action_refresh),
            )
        }
    }
}

@Composable
private fun BannerStrip(
    banners: List<HomeBannerDto>,
    imageLoader: ImageLoader,
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(
            items = banners,
            key = { it.id ?: it.targetUrl.orEmpty() },
        ) { banner ->
            BannerCard(
                banner = banner,
                imageLoader = imageLoader,
            )
        }
    }
}

@Composable
private fun BannerCard(
    banner: HomeBannerDto,
    imageLoader: ImageLoader,
) {
    val bannerImageUrl = banner.mobileImage?.thumbnailUrl
        ?: banner.mobileImage?.displayUrl
        ?: banner.displayImage?.thumbnailUrl
        ?: banner.displayImage?.displayUrl
        ?: banner.desktopImage?.thumbnailUrl
        ?: banner.desktopImage?.displayUrl
    Card(
        modifier = Modifier
            .size(width = 300.dp, height = 156.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        HeritageListImage(
            imageUrl = bannerImageUrl,
            imageLoader = imageLoader,
            fallbackText = stringResource(R.string.brand_fallback),
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun BannerLoadingStrip() {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(2) {
            Box(
                modifier = Modifier
                    .size(width = 300.dp, height = 156.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh),
            )
        }
    }
}

@Composable
private fun InlineRetryMessage(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.weight(1f),
            )
            Button(onClick = onRetry) {
                Text(stringResource(R.string.action_retry))
            }
        }
    }
}

@Composable
private fun ArticleRow(
    article: ArticleSummaryDto,
    imageLoader: ImageLoader,
    prominent: Boolean,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val articleId = article.id
    val unnamedArticle = stringResource(R.string.unnamed_article)
    val fallbackText = stringResource(R.string.brand_fallback)
    val imageUrl = article.coverImage?.displayUrl
        ?: article.coverImage?.thumbnailUrl
        ?: article.coverImage?.originalUrl
        ?: article.coverImage?.sourceUrl
    val titleMaxLines = if (prominent) 3 else 2
    HeritageListCard(
        onClick = articleId?.let { id -> { onClick(id) } },
        modifier = modifier,
        prominent = prominent,
        image = {
            HeritageListImage(
                imageUrl = imageUrl,
                imageLoader = imageLoader,
                fallbackText = fallbackText,
                modifier = if (prominent) {
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .clip(RoundedCornerShape(6.dp))
                } else {
                    Modifier
                        .size(width = 104.dp, height = 82.dp)
                        .clip(RoundedCornerShape(6.dp))
                },
            )
        },
        text = {
            HeritageMetaChip(text = stringResource(article.category.labelRes))
            Text(
                text = article.title.orEmpty().ifBlank { unnamedArticle },
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = titleMaxLines,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = article.summary.orEmpty().ifBlank { article.publishedAt.orEmpty() },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        },
    )
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    StatusContent(
        title = stringResource(R.string.content_load_failed),
        message = message,
        actionLabel = stringResource(R.string.action_retry),
        onAction = onRetry,
        modifier = modifier,
    )
}

@Composable
private fun EmptyContent(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    StatusContent(
        title = stringResource(R.string.content_empty_title),
        message = stringResource(R.string.home_empty_message),
        actionLabel = stringResource(R.string.action_refresh),
        onAction = onRetry,
        modifier = modifier,
    )
}

@Composable
private fun StatusContent(
    title: String,
    message: String,
    actionLabel: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(28.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(18.dp))
        Button(onClick = onAction) {
            Text(actionLabel)
        }
    }
}

@get:StringRes
private val ArticleCategory.labelRes: Int
    get() = when (this) {
        ArticleCategory.News -> R.string.category_news
        ArticleCategory.Forum -> R.string.category_forum
        ArticleCategory.SpecialTopic -> R.string.category_special_topic
    }

@Preview(showBackground = true)
@Composable
private fun ArticlesScreenPreview() {
    HeritageTheme {}
}
