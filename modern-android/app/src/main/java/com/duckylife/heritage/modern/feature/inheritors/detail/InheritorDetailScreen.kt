package com.duckylife.heritage.modern.feature.inheritors.detail

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.ImageLoader
import com.duckylife.heritage.modern.R
import com.duckylife.heritage.modern.core.image.rememberHeritageImageLoader
import com.duckylife.heritage.modern.core.image.rememberHeritageUrlResolver
import com.duckylife.heritage.modern.core.network.resolvedPreviewUrl
import com.duckylife.heritage.modern.core.network.dto.ArticleContentBlockDto
import com.duckylife.heritage.modern.core.network.dto.ArticleContentBlockType
import com.duckylife.heritage.modern.core.network.dto.BlendedRecommendationResponseDto
import com.duckylife.heritage.modern.core.network.dto.ContentDigestDto
import com.duckylife.heritage.modern.core.network.dto.DetailContextDto
import com.duckylife.heritage.modern.core.network.dto.DirectoryReferenceDto
import com.duckylife.heritage.modern.core.network.dto.InheritorDetailDto
import com.duckylife.heritage.modern.feature.articles.detail.isStandaloneSectionTitle
import com.duckylife.heritage.modern.feature.directory.localizedKindLabel
import com.duckylife.heritage.modern.core.data.ReadingPathContentRef
import com.duckylife.heritage.modern.feature.detail.DetailExploreSource
import com.duckylife.heritage.modern.feature.detail.DetailExploreTargetClick
import com.duckylife.heritage.modern.feature.detail.ReadingPathRecorderViewModel
import com.duckylife.heritage.modern.feature.detail.intelligence.ContentIntelligenceUiState
import com.duckylife.heritage.modern.feature.detail.DetailContinueExploreSection
import com.duckylife.heritage.modern.feature.detail.intelligence.DetailIntelligenceSection
import com.duckylife.heritage.modern.ui.component.DetailContextSection
import com.duckylife.heritage.modern.ui.component.DetailExploreSection
import com.duckylife.heritage.modern.ui.component.HeritageContentCard
import com.duckylife.heritage.modern.ui.component.HeritageDetailImage
import com.duckylife.heritage.modern.ui.component.HeritageFact
import com.duckylife.heritage.modern.ui.component.HeritageFactCard
import com.duckylife.heritage.modern.ui.component.HeritageMetaChip
import com.duckylife.heritage.modern.ui.component.HeritageReferenceCard
import com.duckylife.heritage.modern.ui.component.HeritageSectionHeader
import com.duckylife.heritage.modern.ui.error.ErrorKind
import com.duckylife.heritage.modern.ui.error.fallbackResId
import com.duckylife.heritage.modern.ui.preview.ImagePreviewOverlay
import com.duckylife.heritage.modern.ui.preview.buildPreviewUrls
import com.duckylife.heritage.modern.ui.preview.previewUrl
import com.duckylife.heritage.modern.ui.error.toUiError
import com.duckylife.heritage.modern.ui.theme.HeritageTheme
import kotlinx.coroutines.launch

@Composable
fun InheritorDetailRoute(
    inheritorId: String?,
    sourceId: String?,
    onBack: () -> Unit,
    onRelatedProjectSelected: (DirectoryReferenceDto) -> Unit,
    onRelatedInheritorSelected: (DirectoryReferenceDto) -> Unit,
    onExploreTargetClick: (DetailExploreTargetClick) -> Unit,
    onGraphExploreClick: () -> Unit = {},
    onSimilarClick: () -> Unit = {},
    onLearningRoutesClick: () -> Unit = {},
    onKeywordSearch: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    readingPathRecorder: ReadingPathRecorderViewModel = hiltViewModel(),
) {
    val viewModel: InheritorDetailViewModel = hiltViewModel<InheritorDetailViewModel, InheritorDetailViewModel.Factory>(
        key = "inheritor-detail-${inheritorId.orEmpty()}-${sourceId.orEmpty()}",
        creationCallback = { factory ->
            factory.create(
                inheritorId = inheritorId,
                sourceId = sourceId,
            )
        },
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val intelligenceUiState by viewModel.intelligenceUiState.collectAsStateWithLifecycle()

    // 包装回调，记录阅读路径
    fun currentFromRef(): ReadingPathContentRef? {
        val item = uiState.item ?: return null
        return ReadingPathContentRef(
            type = "inheritor",
            id = item.id ?: inheritorId.orEmpty(),
            title = item.name.orEmpty(),
            sourceId = sourceId,
        )
    }

    val wrappedExploreTargetClick: (DetailExploreTargetClick) -> Unit = { click ->
        currentFromRef()?.let { from ->
            readingPathRecorder.record(
                from = from,
                to = click.toReadingPathContentRef(),
                source = click.source.wireName,
            )
        }
        onExploreTargetClick(click)
    }

    val wrappedRelatedProjectSelected: (DirectoryReferenceDto) -> Unit = { reference ->
        currentFromRef()?.let { from ->
            val to = ReadingPathContentRef(
                type = "directoryItem",
                id = reference.sourceId.orEmpty(),
                title = reference.title.orEmpty(),
                kind = reference.kind,
                sourceId = reference.sourceId,
            )
            readingPathRecorder.record(from = from, to = to, source = DetailExploreSource.Related.wireName)
        }
        onRelatedProjectSelected(reference)
    }

    val wrappedRelatedInheritorSelected: (DirectoryReferenceDto) -> Unit = { reference ->
        currentFromRef()?.let { from ->
            val to = ReadingPathContentRef(
                type = "inheritor",
                id = reference.sourceId.orEmpty(),
                title = reference.title.orEmpty(),
                sourceId = reference.sourceId,
            )
            readingPathRecorder.record(from = from, to = to, source = DetailExploreSource.Related.wireName)
        }
        onRelatedInheritorSelected(reference)
    }

    InheritorDetailScreen(
        uiState = uiState,
        intelligenceUiState = intelligenceUiState,
        onBack = onBack,
        onRetry = viewModel::refresh,
        onToggleFavorite = viewModel::toggleFavorite,
        onRelatedProjectSelected = wrappedRelatedProjectSelected,
        onRelatedInheritorSelected = wrappedRelatedInheritorSelected,
        onContextRetry = viewModel::loadContext,
        onDigestRetry = viewModel::retryDigest,
        onExploreTargetClick = wrappedExploreTargetClick,
        onRetryIntelligence = viewModel::retryIntelligence,
        onGraphExploreClick = onGraphExploreClick,
        onSimilarClick = onSimilarClick,
        onLearningRoutesClick = onLearningRoutesClick,
        onKeywordSearch = onKeywordSearch,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InheritorDetailScreen(
    uiState: InheritorDetailUiState,
    intelligenceUiState: ContentIntelligenceUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onToggleFavorite: () -> Unit,
    onRelatedProjectSelected: (DirectoryReferenceDto) -> Unit,
    onRelatedInheritorSelected: (DirectoryReferenceDto) -> Unit,
    onContextRetry: () -> Unit = {},
    onDigestRetry: () -> Unit = {},
    onExploreTargetClick: (DetailExploreTargetClick) -> Unit = {},
    onRetryIntelligence: () -> Unit = {},
    onGraphExploreClick: () -> Unit = {},
    onSimilarClick: () -> Unit = {},
    onLearningRoutesClick: () -> Unit = {},
    onKeywordSearch: (String) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val imageLoader = rememberHeritageImageLoader()
    val resolver = rememberHeritageUrlResolver()
    val uriHandler = LocalUriHandler.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val sourceOpenFailedMessage = stringResource(R.string.source_open_failed)

    val previewUrls = remember(uiState.item, resolver) {
        uiState.item?.let { item ->
            buildPreviewUrls(
                resolver = resolver,
                coverImage = item.coverImage,
                contentBlocks = item.contentBlocks,
            )
        } ?: emptyList()
    }
    var showPreview by remember { mutableStateOf(false) }
    var previewIndex by remember { mutableIntStateOf(0) }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = modifier,
            containerColor = MaterialTheme.colorScheme.background,
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.inheritor_detail_title)) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    ),
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                                contentDescription = stringResource(R.string.action_back),
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = onToggleFavorite) {
                            Icon(
                                imageVector = if (uiState.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = if (uiState.isFavorite) {
                                    stringResource(R.string.action_unfavorite)
                                } else {
                                    stringResource(R.string.action_favorite)
                                },
                                tint = if (uiState.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
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
                uiState.isLoading -> LoadingContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding),
                )

                uiState.errorKind != null -> StatusContent(
                    title = stringResource(R.string.content_load_failed),
                    message = stringResource(uiState.errorKind.fallbackResId()),
                    actionLabel = stringResource(R.string.action_retry),
                    onAction = onRetry,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding),
                )

                uiState.item != null -> InheritorDetailContent(
                    item = uiState.item,
                    imageLoader = imageLoader,
                    isContentStale = uiState.isContentStale,
                    onRetry = onRetry,
                    onOpenSource = { sourceUrl ->
                        runCatching {
                            uriHandler.openUri(sourceUrl)
                        }.onFailure {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(sourceOpenFailedMessage)
                            }
                        }
                    },
                    onRelatedProjectSelected = onRelatedProjectSelected,
                    onRelatedInheritorSelected = onRelatedInheritorSelected,
                    onPreviewImage = { index ->
                        previewIndex = index
                        showPreview = true
                    },
                    contextLoading = uiState.contextLoading,
                    context = uiState.context,
                    contextErrorKind = uiState.contextErrorKind,
                    onContextRetry = onContextRetry,
                    digest = uiState.digest,
                    digestLoading = uiState.digestLoading,
                    digestErrorKind = uiState.digestErrorKind,
                    onDigestRetry = onDigestRetry,
                    blendedRecommendations = uiState.blendedRecommendations,
                    onExploreTargetClick = onExploreTargetClick,
                    intelligenceUiState = intelligenceUiState,
                    onRetryIntelligence = onRetryIntelligence,
                    onGraphExploreClick = onGraphExploreClick,
                    onSimilarClick = onSimilarClick,
                    onLearningRoutesClick = onLearningRoutesClick,
                    onKeywordSearch = onKeywordSearch,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding),
                )
            }
        }

        if (showPreview && previewUrls.isNotEmpty()) {
            ImagePreviewOverlay(
                imageUrls = previewUrls,
                initialIndex = previewIndex,
                imageLoader = imageLoader,
                onDismiss = { showPreview = false },
            )
        }
    }
}

@Composable
private fun InheritorDetailContent(
    item: InheritorDetailDto,
    imageLoader: ImageLoader,
    isContentStale: Boolean,
    onRetry: () -> Unit,
    onOpenSource: (String) -> Unit,
    onRelatedProjectSelected: (DirectoryReferenceDto) -> Unit,
    onRelatedInheritorSelected: (DirectoryReferenceDto) -> Unit,
    onPreviewImage: (Int) -> Unit,
    contextLoading: Boolean = false,
    context: DetailContextDto? = null,
    contextErrorKind: ErrorKind? = null,
    onContextRetry: () -> Unit = {},
    // Content Digest
    digest: ContentDigestDto? = null,
    digestLoading: Boolean = false,
    digestErrorKind: ErrorKind? = null,
    onDigestRetry: () -> Unit = {},
    // Blended Recommendations
    blendedRecommendations: BlendedRecommendationResponseDto? = null,
    onExploreTargetClick: (DetailExploreTargetClick) -> Unit = {},
    intelligenceUiState: ContentIntelligenceUiState,
    onRetryIntelligence: () -> Unit = {},
    onGraphExploreClick: () -> Unit = {},
    onSimilarClick: () -> Unit = {},
    onLearningRoutesClick: () -> Unit = {},
    onKeywordSearch: (String) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val resolver = rememberHeritageUrlResolver()
    val unnamedInheritor = stringResource(R.string.unnamed_inheritor)
    val relatedProjectsTitle = stringResource(R.string.inheritor_related_projects_title)
    val relatedInheritorsTitle = stringResource(R.string.inheritor_related_inheritors_title)

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        if (isContentStale) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                ) {
                    Row(Modifier.padding(14.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                        Text(
                            text = stringResource(R.string.content_may_be_stale),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.weight(1f),
                        )
                        Button(onClick = onRetry) { Text(stringResource(R.string.action_retry)) }
                    }
                }
            }
        }
        item {
            InheritorHero(
                item = item,
                unnamedInheritor = unnamedInheritor,
                imageLoader = imageLoader,
                onOpenSource = onOpenSource,
                onCoverImageClick = item.coverImage?.resolvedPreviewUrl(resolver)?.let { { onPreviewImage(0) } },
            )
        }

        item {
            InheritorFacts(item)
        }

        if (!item.description.isNullOrBlank()) {
            item {
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 28.sp,
                )
            }
        }

        var imageBlockIndex = if (item.coverImage?.resolvedPreviewUrl(resolver) != null) 1 else 0
        items(item.contentBlocks) { block ->
            ContentBlock(
                block = block,
                imageLoader = imageLoader,
                onImageClick = if (block.type == ArticleContentBlockType.Image && block.image?.resolvedPreviewUrl(resolver) != null) {
                    val idx = imageBlockIndex++
                    { onPreviewImage(idx) }
                } else null,
            )
        }

        ReferenceSection(
            title = relatedProjectsTitle,
            references = item.relatedProjects,
            onReferenceSelected = onRelatedProjectSelected,
        )
        ReferenceSection(
            title = relatedInheritorsTitle,
            references = item.relatedInheritors,
            onReferenceSelected = onRelatedInheritorSelected,
        )

        // 智能解读区块（V3 content page 的 AI 板块）
        item {
            DetailIntelligenceSection(
                uiState = intelligenceUiState,
                onKeywordClick = onKeywordSearch,
                onRetry = onRetryIntelligence,
            )
        }

        // 继续探索 action bar（关系图谱 / 相似内容 / 学习路线）
        item {
            DetailContinueExploreSection(
                uiState = intelligenceUiState,
                onGraphClick = onGraphExploreClick,
                onSimilarClick = onSimilarClick,
                onLearningRoutesClick = onLearningRoutesClick,
            )
        }

        // 探索区块：Digest -> Blended -> Context
        item {
            DetailExploreSection(
                digest = digest,
                digestLoading = digestLoading,
                digestErrorKind = digestErrorKind,
                onDigestRetry = onDigestRetry,
                blendedRecommendations = blendedRecommendations,
                context = context,
                contextLoading = contextLoading,
                contextErrorKind = contextErrorKind,
                onContextRetry = onContextRetry,
                onExploreTargetClick = onExploreTargetClick,
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun InheritorHero(
    item: InheritorDetailDto,
    unnamedInheritor: String,
    imageLoader: ImageLoader,
    onOpenSource: (String) -> Unit,
    onCoverImageClick: (() -> Unit)? = null,
) {
    val fallbackText = stringResource(R.string.brand_fallback)
    HeritageContentCard {
        Column {
            item.coverImage?.let { coverImage ->
                val coverUrl = coverImage.previewUrl()
                HeritageDetailImage(
                    imageUrl = coverUrl,
                    imageLoader = imageLoader,
                    fallbackText = fallbackText,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .then(
                            if (onCoverImageClick != null && coverUrl != null) {
                                Modifier.clickable(onClick = onCoverImageClick)
                            } else {
                                Modifier
                            },
                        ),
                )
            }
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = stringResource(R.string.nav_inheritors),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = item.name.orEmpty().ifBlank { unnamedInheritor },
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                if (!item.projectName.isNullOrBlank()) {
                    Text(
                        text = item.projectName,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                InheritorMetaChips(item)
                if (!item.sourceUrl.isNullOrBlank()) {
                    TextButton(
                        onClick = { onOpenSource(item.sourceUrl) },
                        contentPadding = PaddingValues(horizontal = 0.dp),
                    ) {
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
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun InheritorMetaChips(item: InheritorDetailDto) {
    val labels = listOfNotNull(
        item.gender?.takeIf { it.isNotBlank() },
        item.ethnicity?.takeIf { it.isNotBlank() },
        item.category?.takeIf { it.isNotBlank() },
        item.region?.takeIf { it.isNotBlank() },
        item.batch?.takeIf { it.isNotBlank() },
    )
    if (labels.isNotEmpty()) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            labels.forEach { label ->
                HeritageMetaChip(
                    text = label,
                    modifier = Modifier.widthIn(max = 280.dp),
                )
            }
        }
    }
}

@Composable
private fun InheritorFacts(item: InheritorDetailDto) {
    val facts = listOfNotNull(
        item.projectName?.takeIf { it.isNotBlank() }?.let { HeritageFact(stringResource(R.string.inheritor_field_project_name), it) },
        item.projectCode?.takeIf { it.isNotBlank() }?.let { HeritageFact(stringResource(R.string.inheritor_field_project_code), it) },
        item.birthDateText?.takeIf { it.isNotBlank() }?.let { HeritageFact(stringResource(R.string.inheritor_field_birth_date), it) },
        item.batch?.takeIf { it.isNotBlank() }?.let { HeritageFact(stringResource(R.string.inheritor_field_batch), it) },
    )
    HeritageFactCard(facts = facts)
}

@Composable
private fun ContentBlock(
    block: ArticleContentBlockDto,
    imageLoader: ImageLoader,
    onImageClick: (() -> Unit)? = null,
) {
    when (block.type) {
        ArticleContentBlockType.Heading -> {
            SectionTitle(text = block.text.orEmpty())
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
            val blockImageUrl = block.image?.previewUrl()
            HeritageDetailImage(
                imageUrl = blockImageUrl,
                imageLoader = imageLoader,
                fallbackText = stringResource(R.string.brand_fallback),
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f / 3f)
                    .clip(RoundedCornerShape(8.dp))
                    .then(
                        if (onImageClick != null && blockImageUrl != null) {
                            Modifier.clickable(onClick = onImageClick)
                        } else {
                            Modifier
                        },
                    ),
            )
        }
    }
}

private fun LazyListScope.ReferenceSection(
    title: String,
    references: List<DirectoryReferenceDto>,
    onReferenceSelected: (DirectoryReferenceDto) -> Unit,
) {
    if (references.isEmpty()) {
        return
    }
    item {
        SectionTitle(
            text = title,
            modifier = Modifier.padding(top = 2.dp),
        )
    }
    items(references) { reference ->
        HeritageReferenceCard(
            title = reference.title.orEmpty().ifBlank { stringResource(R.string.unnamed_directory_item) },
            meta = reference.toReferenceMeta(),
            onClick = onReferenceSelected
                .takeIf { reference.canOpenDetail }
                ?.let { { it(reference) } },
        )
    }
}

private val DirectoryReferenceDto.canOpenDetail: Boolean
    get() = !sourceId.isNullOrBlank()

@Composable
private fun DirectoryReferenceDto.toReferenceMeta(): String =
    listOfNotNull(
        localizedKindLabel(),
        category?.takeIf { it.isNotBlank() },
        region?.takeIf { it.isNotBlank() },
        publishedYear?.let { stringResource(R.string.directory_year_format, it) },
    ).joinToString(" · ")

@Composable
private fun SectionTitle(
    text: String,
    modifier: Modifier = Modifier,
) {
    HeritageSectionHeader(
        title = text,
        modifier = modifier,
    )
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
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
        modifier = modifier.padding(28.dp),
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

@Preview(showBackground = true)
@Composable
private fun InheritorDetailScreenPreview() {
    HeritageTheme {}
}
