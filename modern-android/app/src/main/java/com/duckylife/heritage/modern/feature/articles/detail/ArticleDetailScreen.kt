package com.duckylife.heritage.modern.feature.articles.detail

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.annotation.StringRes
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.ImageLoader
import coil3.compose.AsyncImage
import com.duckylife.heritage.modern.R
import com.duckylife.heritage.modern.core.image.rememberHeritageImageLoader
import com.duckylife.heritage.modern.core.network.dto.ArticleCategory
import com.duckylife.heritage.modern.core.network.dto.ArticleContentBlockDto
import com.duckylife.heritage.modern.core.network.dto.ArticleContentBlockType
import com.duckylife.heritage.modern.core.network.dto.ArticleDetailDto
import com.duckylife.heritage.modern.core.network.dto.ArticleReferenceDto
import com.duckylife.heritage.modern.core.network.dto.MediaAssetDto
import com.duckylife.heritage.modern.ui.theme.HeritageTheme
import kotlinx.coroutines.launch

@Composable
fun ArticleDetailRoute(
    articleId: String?,
    sourceId: String?,
    sourceUrl: String?,
    category: ArticleCategory,
    onBack: () -> Unit,
    onRelatedArticleSelected: (ArticleReferenceDto, ArticleCategory) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: ArticleDetailViewModel = hiltViewModel<ArticleDetailViewModel, ArticleDetailViewModel.Factory>(
        key = "article-detail-${articleId.orEmpty()}-${sourceId.orEmpty()}-${sourceUrl.orEmpty()}-${category.wireName}",
        creationCallback = { factory ->
            factory.create(
                articleId = articleId,
                sourceId = sourceId,
                sourceUrl = sourceUrl,
                category = category,
            )
        },
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ArticleDetailScreen(
        uiState = uiState,
        onBack = onBack,
        onRetry = viewModel::refresh,
        onRelatedArticleSelected = onRelatedArticleSelected,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleDetailScreen(
    uiState: ArticleDetailUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onRelatedArticleSelected: (ArticleReferenceDto, ArticleCategory) -> Unit,
    modifier: Modifier = Modifier,
) {
    val imageLoader = rememberHeritageImageLoader()
    val uriHandler = LocalUriHandler.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val sourceOpenFailedMessage = stringResource(R.string.source_open_failed)
    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.article_detail_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = stringResource(R.string.action_back),
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onRetry) {
                        Icon(
                            imageVector = Icons.Outlined.Refresh,
                            contentDescription = stringResource(R.string.action_refresh),
                        )
                    }
                },
            )
        },
    ) { contentPadding ->
        when {
            uiState.isLoading -> LoadingDetailContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
            )

            uiState.errorMessage != null -> ErrorDetailContent(
                message = uiState.errorMessage
                    .takeUnless { it.isBlank() }
                    ?: stringResource(R.string.article_detail_load_failed),
                onRetry = onRetry,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
            )

            uiState.article != null -> ArticleDetailContent(
                article = uiState.article,
                imageLoader = imageLoader,
                onOpenSource = { sourceUrl ->
                    runCatching {
                        uriHandler.openUri(sourceUrl)
                    }.onFailure {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(sourceOpenFailedMessage)
                        }
                    }
                },
                onRelatedArticleSelected = onRelatedArticleSelected,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
            )
        }
    }
}

@Composable
private fun ArticleDetailContent(
    article: ArticleDetailDto,
    imageLoader: ImageLoader,
    onOpenSource: (String) -> Unit,
    onRelatedArticleSelected: (ArticleReferenceDto, ArticleCategory) -> Unit,
    modifier: Modifier = Modifier,
) {
    val unnamedArticle = stringResource(R.string.unnamed_article)
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = stringResource(article.category.labelRes),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = article.title.orEmpty().ifBlank { unnamedArticle },
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                )
                val meta = article.metaText()
                if (meta.isNotBlank()) {
                    Text(
                        text = meta,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                if (!article.sourceUrl.isNullOrBlank()) {
                    TextButton(onClick = { onOpenSource(article.sourceUrl) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.OpenInNew,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(modifier = Modifier.size(6.dp))
                        Text(stringResource(R.string.action_view_source))
                    }
                }
            }
        }

        article.coverImage?.let { coverImage ->
            item {
                DetailImage(
                    image = coverImage,
                    imageLoader = imageLoader,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .clip(RoundedCornerShape(8.dp)),
                )
            }
        }

        if (!article.summary.isNullOrBlank()) {
            item {
                Text(
                    text = article.summary,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        items(article.contentBlocks) { block ->
            ContentBlock(
                block = block,
                imageLoader = imageLoader,
            )
        }

        if (article.contentBlocks.isEmpty() && article.summary.isNullOrBlank()) {
            item {
                Text(
                    text = stringResource(R.string.article_body_empty),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        if (article.relatedArticles.isNotEmpty()) {
            item {
                HorizontalDivider()
            }
            item {
                Text(
                    text = stringResource(R.string.related_articles_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            items(article.relatedArticles) { reference ->
                RelatedArticleRow(
                    reference = reference,
                    onClick = {
                        onRelatedArticleSelected(reference, article.category)
                    },
                )
            }
        }
    }
}

@Composable
private fun ContentBlock(
    block: ArticleContentBlockDto,
    imageLoader: ImageLoader,
) {
    when (block.type) {
        ArticleContentBlockType.Heading -> {
            Text(
                text = block.text.orEmpty(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
        }

        ArticleContentBlockType.Text -> {
            if (!block.text.isNullOrBlank()) {
                if (isStandaloneSectionTitle(block.text)) {
                    Text(
                        text = block.text.trim(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                } else {
                    Text(
                        text = block.text,
                        style = MaterialTheme.typography.bodyLarge,
                        lineHeight = 28.sp,
                    )
                }
            }
        }

        ArticleContentBlockType.Image -> {
            DetailImage(
                image = block.image,
                imageLoader = imageLoader,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f / 3f)
                    .clip(RoundedCornerShape(8.dp)),
            )
        }
    }
}

@Composable
private fun DetailImage(
    image: MediaAssetDto?,
    imageLoader: ImageLoader,
    contentScale: ContentScale,
    modifier: Modifier = Modifier,
) {
    val imageUrl = image?.displayUrl ?: image?.thumbnailUrl ?: image?.originalUrl ?: image?.sourceUrl
    if (imageUrl.isNullOrBlank()) {
        Box(
            modifier = modifier.background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.brand_fallback),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
            )
        }
    } else {
        AsyncImage(
            model = imageUrl,
            contentDescription = image?.altText,
            imageLoader = imageLoader,
            modifier = modifier.background(MaterialTheme.colorScheme.surfaceContainerHigh),
            contentScale = contentScale,
        )
    }
}

@Composable
private fun RelatedArticleRow(
    reference: ArticleReferenceDto,
    onClick: () -> Unit,
) {
    val unnamedArticle = stringResource(R.string.unnamed_article)
    val canOpen = !reference.sourceId.isNullOrBlank() || !reference.detailUrl.isNullOrBlank()
    Card(
        modifier = Modifier.clickable(
            enabled = canOpen,
            onClick = onClick,
        ),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = reference.title.orEmpty().ifBlank { unnamedArticle },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            val publishedAt = formatArticleDate(reference.publishedAt)
            if (!publishedAt.isNullOrBlank()) {
                Text(
                    text = publishedAt,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun LoadingDetailContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorDetailContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(28.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.content_load_failed),
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
        Button(onClick = onRetry) {
            Text(stringResource(R.string.action_retry))
        }
    }
}

@Composable
private fun ArticleDetailDto.metaText(): String =
    listOfNotNull(
        formatArticleDate(publishedAt),
        sourceName?.takeIf { it.isNotBlank() },
        author?.takeIf { it.isNotBlank() }?.let { stringResource(R.string.article_author_format, it) },
        editor?.takeIf { it.isNotBlank() }?.let { stringResource(R.string.article_editor_format, it) },
    ).joinToString(" · ")

@get:StringRes
private val ArticleCategory.labelRes: Int
    get() = when (this) {
        ArticleCategory.News -> R.string.category_news
        ArticleCategory.Forum -> R.string.category_forum
        ArticleCategory.SpecialTopic -> R.string.category_special_topic
    }

@Preview(showBackground = true)
@Composable
private fun ArticleDetailScreenPreview() {
    HeritageTheme {
        ArticleDetailScreen(
            uiState = ArticleDetailUiState(
                isLoading = false,
                article = ArticleDetailDto(
                    id = "article-1",
                    title = "传统技艺在城市街巷里重新发芽",
                    summary = "文章摘要用于预览详情页排版。",
                    sourceName = "E迹",
                    contentBlocks = listOf(
                        ArticleContentBlockDto(
                            type = ArticleContentBlockType.Heading,
                            text = "小标题",
                        ),
                        ArticleContentBlockDto(
                            type = ArticleContentBlockType.Text,
                            text = "这是一段正文，用来检查详情页在 Compose 中的阅读节奏。",
                        ),
                    ),
                    relatedArticles = listOf(
                        ArticleReferenceDto(title = "相关新闻标题"),
                    ),
                ),
            ),
            onBack = {},
            onRetry = {},
            onRelatedArticleSelected = { _, _ -> },
        )
    }
}
