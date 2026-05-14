package com.duckylife.heritage.modern.feature.articles

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.ImageLoader
import coil3.compose.AsyncImage
import com.duckylife.heritage.modern.core.image.rememberHeritageImageLoader
import com.duckylife.heritage.modern.core.network.dto.ArticleCategory
import com.duckylife.heritage.modern.core.network.dto.ArticleSummaryDto
import com.duckylife.heritage.modern.core.network.dto.HomeBannerDto
import com.duckylife.heritage.modern.core.network.dto.MediaAssetDto
import com.duckylife.heritage.modern.ui.theme.HeritageTheme

@Composable
fun ArticlesRoute(
    onArticleSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ArticlesViewModel = viewModel(),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    ArticlesScreen(
        uiState = uiState,
        onRetry = viewModel::refresh,
        onArticleSelected = onArticleSelected,
        modifier = modifier,
    )
}

@Composable
fun ArticlesScreen(
    uiState: ArticlesUiState,
    onRetry: () -> Unit,
    onArticleSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val imageLoader = rememberHeritageImageLoader()
    when {
        uiState.isLoading -> LoadingContent(modifier)
        uiState.errorMessage != null -> ErrorContent(
            message = uiState.errorMessage,
            onRetry = onRetry,
            modifier = modifier,
        )

        uiState.isEmpty -> EmptyContent(
            onRetry = onRetry,
            modifier = modifier,
        )

        else -> ArticlesContent(
            uiState = uiState,
            onRetry = onRetry,
            onArticleSelected = onArticleSelected,
            imageLoader = imageLoader,
            modifier = modifier,
        )
    }
}

@Composable
private fun ArticlesContent(
    uiState: ArticlesUiState,
    onRetry: () -> Unit,
    onArticleSelected: (String) -> Unit,
    imageLoader: ImageLoader,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            ArticlesHeader(onRetry)
        }

        if (uiState.banners.isNotEmpty()) {
            item {
                BannerStrip(
                    banners = uiState.banners,
                    imageLoader = imageLoader,
                )
            }
        }

        item {
            Text(
                text = "最新文章",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 20.dp),
            )
        }

        items(
            items = uiState.articles,
            key = { it.id ?: it.sourceUrl ?: it.title.orEmpty() },
        ) { article ->
            ArticleRow(
                article = article,
                imageLoader = imageLoader,
                onClick = onArticleSelected,
                modifier = Modifier.padding(horizontal = 20.dp),
            )
        }
    }
}

@Composable
private fun ArticlesHeader(onRetry: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 18.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text = "E迹",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "非遗新闻、论坛与专题",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        IconButton(onClick = onRetry) {
            Icon(
                imageVector = Icons.Outlined.Refresh,
                contentDescription = "刷新",
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
    Card(
        modifier = Modifier
            .size(width = 300.dp, height = 156.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
    ) {
        ImageOrFallback(
            image = banner.mobileImage ?: banner.displayImage ?: banner.desktopImage,
            imageLoader = imageLoader,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun ArticleRow(
    article: ArticleSummaryDto,
    imageLoader: ImageLoader,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val articleId = article.id
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = !articleId.isNullOrBlank()) {
                if (articleId != null) {
                    onClick(articleId)
                }
            },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            ImageOrFallback(
                image = article.coverImage,
                imageLoader = imageLoader,
                modifier = Modifier
                    .size(width = 104.dp, height = 82.dp)
                    .clip(RoundedCornerShape(6.dp)),
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = article.category.label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = article.title.orEmpty().ifBlank { "未命名文章" },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = article.summary.orEmpty().ifBlank { article.publishedAt.orEmpty() },
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
private fun ImageOrFallback(
    image: MediaAssetDto?,
    imageLoader: ImageLoader,
    modifier: Modifier = Modifier,
) {
    val imageUrl = image?.displayUrl ?: image?.thumbnailUrl ?: image?.originalUrl ?: image?.sourceUrl
    if (imageUrl.isNullOrBlank()) {
        Box(
            modifier = modifier
                .background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "E迹",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
            )
        }
    } else {
        AsyncImage(
            model = imageUrl,
            contentDescription = image?.altText,
            imageLoader = imageLoader,
            modifier = modifier,
            contentScale = ContentScale.Crop,
        )
    }
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
        title = "加载失败",
        message = message,
        actionLabel = "重试",
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
        title = "暂无内容",
        message = "后端暂时没有返回首页内容。",
        actionLabel = "刷新",
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

private val ArticleCategory.label: String
    get() = when (this) {
        ArticleCategory.News -> "新闻"
        ArticleCategory.Forum -> "论坛"
        ArticleCategory.SpecialTopic -> "专题"
    }

@Preview(showBackground = true)
@Composable
private fun ArticlesScreenPreview() {
    HeritageTheme {
        ArticlesScreen(
            uiState = ArticlesUiState(
                isLoading = false,
                banners = listOf(
                    HomeBannerDto(id = "banner", sortOrder = 1),
                ),
                articles = listOf(
                    ArticleSummaryDto(
                        id = "1",
                        category = ArticleCategory.News,
                        title = "传统技艺在城市街巷里重新发芽",
                        summary = "一个用于预览首页列表的文章摘要。",
                    ),
                ),
            ),
            onRetry = {},
            onArticleSelected = {},
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
        )
    }
}
