package com.duckylife.heritage.modern.feature.directory.detail

import androidx.annotation.StringRes
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.outlined.Refresh
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import coil3.compose.AsyncImage
import com.duckylife.heritage.modern.R
import com.duckylife.heritage.modern.core.image.rememberHeritageImageLoader
import com.duckylife.heritage.modern.core.network.dto.ArticleContentBlockDto
import com.duckylife.heritage.modern.core.network.dto.ArticleContentBlockType
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemDetailDto
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemKind
import com.duckylife.heritage.modern.core.network.dto.DirectoryReferenceDto
import com.duckylife.heritage.modern.core.network.dto.MediaAssetDto
import com.duckylife.heritage.modern.feature.articles.detail.isStandaloneSectionTitle
import com.duckylife.heritage.modern.feature.directory.localizedKindLabel
import com.duckylife.heritage.modern.ui.component.HeritageContentCard
import com.duckylife.heritage.modern.ui.component.HeritageMetaChip
import com.duckylife.heritage.modern.ui.component.HeritageSectionHeader
import com.duckylife.heritage.modern.ui.theme.HeritageTheme
import kotlinx.coroutines.launch

@Composable
fun DirectoryDetailRoute(
    itemId: String?,
    sourceId: String?,
    kind: DirectoryItemKind,
    onBack: () -> Unit,
    onRelatedProjectSelected: (DirectoryReferenceDto, DirectoryItemKind) -> Unit,
    onRelatedInheritorSelected: (DirectoryReferenceDto) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: DirectoryDetailViewModel = hiltViewModel<DirectoryDetailViewModel, DirectoryDetailViewModel.Factory>(
        key = "directory-detail-${itemId.orEmpty()}-${sourceId.orEmpty()}-${kind.wireName}",
        creationCallback = { factory ->
            factory.create(
                itemId = itemId,
                sourceId = sourceId,
                kind = kind,
            )
        },
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    DirectoryDetailScreen(
        uiState = uiState,
        onBack = onBack,
        onRetry = viewModel::refresh,
        onRelatedProjectSelected = onRelatedProjectSelected,
        onRelatedInheritorSelected = onRelatedInheritorSelected,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DirectoryDetailScreen(
    uiState: DirectoryDetailUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onRelatedProjectSelected: (DirectoryReferenceDto, DirectoryItemKind) -> Unit,
    onRelatedInheritorSelected: (DirectoryReferenceDto) -> Unit,
    modifier: Modifier = Modifier,
) {
    val imageLoader = rememberHeritageImageLoader()
    val uriHandler = LocalUriHandler.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val sourceOpenFailedMessage = stringResource(R.string.source_open_failed)
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.directory_detail_title)) },
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

            uiState.errorMessage != null -> StatusContent(
                title = stringResource(R.string.content_load_failed),
                message = friendlyDetailErrorMessage(
                    errorMessage = uiState.errorMessage,
                    fallbackMessage = stringResource(R.string.directory_detail_load_failed),
                ),
                actionLabel = stringResource(R.string.action_retry),
                onAction = onRetry,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
            )

            uiState.item != null -> DirectoryDetailContent(
                item = uiState.item,
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
                onRelatedProjectSelected = onRelatedProjectSelected,
                onRelatedInheritorSelected = onRelatedInheritorSelected,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
            )
        }
    }
}

@Composable
private fun DirectoryDetailContent(
    item: DirectoryItemDetailDto,
    imageLoader: ImageLoader,
    onOpenSource: (String) -> Unit,
    onRelatedProjectSelected: (DirectoryReferenceDto, DirectoryItemKind) -> Unit,
    onRelatedInheritorSelected: (DirectoryReferenceDto) -> Unit,
    modifier: Modifier = Modifier,
) {
    val unnamedItem = stringResource(R.string.unnamed_directory_item)
    val relatedProjectsTitle = stringResource(R.string.directory_related_projects_title)
    val relatedInheritorsTitle = stringResource(R.string.directory_related_inheritors_title)
    val relatedDocumentsTitle = stringResource(R.string.directory_related_documents_title)
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            DirectoryHero(
                item = item,
                unnamedItem = unnamedItem,
                imageLoader = imageLoader,
                onOpenSource = onOpenSource,
            )
        }

        item {
            DirectoryFacts(item)
        }

        if (!item.summary.isNullOrBlank()) {
            item {
                Text(
                    text = item.summary,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 28.sp,
                )
            }
        }

        if (item.gallery.isNotEmpty()) {
            item {
                SectionTitle(text = stringResource(R.string.directory_gallery_title))
            }
            item {
                GalleryStrip(
                    images = item.gallery,
                    imageLoader = imageLoader,
                )
            }
        }

        items(item.contentBlocks) { block ->
            ContentBlock(
                block = block,
                imageLoader = imageLoader,
            )
        }

        ReferenceSection(
            title = relatedProjectsTitle,
            references = item.relatedProjects,
            onReferenceSelected = { reference ->
                onRelatedProjectSelected(reference, item.kind)
            },
        )
        ReferenceSection(
            title = relatedInheritorsTitle,
            references = item.relatedInheritors,
            onReferenceSelected = onRelatedInheritorSelected,
        )
        ReferenceSection(
            title = relatedDocumentsTitle,
            references = item.relatedDocuments,
            onReferenceSelected = null,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DirectoryHero(
    item: DirectoryItemDetailDto,
    unnamedItem: String,
    imageLoader: ImageLoader,
    onOpenSource: (String) -> Unit,
) {
    HeritageContentCard {
        Column {
            item.coverImage?.let { coverImage ->
                DetailImage(
                    image = coverImage,
                    imageLoader = imageLoader,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f),
                )
            }
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = stringResource(item.kind.labelRes),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = item.title.orEmpty().ifBlank { unnamedItem },
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                DirectoryMetaChips(item)
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
private fun DirectoryMetaChips(item: DirectoryItemDetailDto) {
    val labels = listOfNotNull(
        item.category?.takeIf { it.isNotBlank() },
        item.region?.takeIf { it.isNotBlank() },
        item.publishedYear?.let { stringResource(R.string.directory_year_format, it) },
        item.listType?.takeIf { it.isNotBlank() },
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
private fun DirectoryFacts(item: DirectoryItemDetailDto) {
    val facts = listOfNotNull(
        item.projectCode?.takeIf { it.isNotBlank() }?.let { R.string.directory_field_project_code to it },
        item.batch?.takeIf { it.isNotBlank() }?.let { R.string.directory_field_batch to it },
        item.protectionUnit?.takeIf { it.isNotBlank() }?.let { R.string.directory_field_protection_unit to it },
        item.nominationType?.takeIf { it.isNotBlank() }?.let { R.string.directory_field_nomination_type to it },
    )
    if (facts.isEmpty()) {
        return
    }
    HeritageContentCard {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            facts.forEach { (labelRes, value) ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    Text(
                        text = stringResource(labelRes),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(0.32f),
                    )
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(0.68f),
                    )
                }
            }
        }
    }
}

@Composable
private fun GalleryStrip(
    images: List<MediaAssetDto>,
    imageLoader: ImageLoader,
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(images) { image ->
            DetailImage(
                image = image,
                imageLoader = imageLoader,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillParentMaxWidth(0.72f)
                    .aspectRatio(4f / 3f)
                    .clip(RoundedCornerShape(8.dp)),
            )
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

private fun LazyListScope.ReferenceSection(
    title: String,
    references: List<DirectoryReferenceDto>,
    onReferenceSelected: ((DirectoryReferenceDto) -> Unit)?,
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
        ReferenceRow(
            reference = reference,
            onClick = onReferenceSelected
                ?.takeIf { reference.canOpenDetail }
                ?.let { { it(reference) } },
        )
    }
}

@Composable
private fun ReferenceRow(
    reference: DirectoryReferenceDto,
    onClick: (() -> Unit)?,
) {
    HeritageContentCard(
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = reference.title.orEmpty().ifBlank { stringResource(R.string.unnamed_directory_item) },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            val meta = listOfNotNull(
                reference.localizedKindLabel(),
                reference.category?.takeIf { it.isNotBlank() },
                reference.region?.takeIf { it.isNotBlank() },
                reference.publishedYear?.let { stringResource(R.string.directory_year_format, it) },
            ).joinToString(" · ")
            if (meta.isNotBlank()) {
                Text(
                    text = meta,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

private val DirectoryReferenceDto.canOpenDetail: Boolean
    get() = !sourceId.isNullOrBlank()

@Composable
private fun friendlyDetailErrorMessage(
    errorMessage: String?,
    fallbackMessage: String,
): String {
    val rawMessage = errorMessage.orEmpty()
    return when {
        rawMessage.contains("404") || rawMessage.contains("Not Found", ignoreCase = true) ->
            stringResource(R.string.content_not_available)

        rawMessage.isBlank() -> fallbackMessage
        else -> rawMessage
    }
}

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

@get:StringRes
private val DirectoryItemKind.labelRes: Int
    get() = when (this) {
        DirectoryItemKind.NationalProject -> R.string.directory_kind_national_project
        DirectoryItemKind.CulturalEcoZone -> R.string.directory_kind_cultural_eco_zone
        DirectoryItemKind.ProductiveProtectionBase -> R.string.directory_kind_productive_protection_base
        DirectoryItemKind.UnescoEntry -> R.string.directory_kind_unesco_entry
        DirectoryItemKind.ChinaUnescoEntry -> R.string.directory_kind_china_unesco_entry
        DirectoryItemKind.ContractingState -> R.string.directory_kind_contracting_state
    }

@Preview(showBackground = true)
@Composable
private fun DirectoryDetailScreenPreview() {
    HeritageTheme {}
}
