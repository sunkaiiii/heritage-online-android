package com.duckylife.heritage.modern.feature.taxonomy

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.duckylife.heritage.modern.R
import com.duckylife.heritage.modern.core.network.dto.TaxonomyKindDto
import com.duckylife.heritage.modern.core.network.dto.TaxonomyTopicDto
import com.duckylife.heritage.modern.ui.component.HeritagePageBackground
import com.duckylife.heritage.modern.ui.component.HeritagePageHeader
import com.duckylife.heritage.modern.ui.component.HeritageContentCard
import com.duckylife.heritage.modern.ui.component.MetricPill
import com.duckylife.heritage.modern.ui.error.ErrorKind
import com.duckylife.heritage.modern.ui.error.fallbackResId

@Composable
fun TaxonomyRoute(
    onBack: () -> Unit,
    onTopicClick: (String, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TaxonomyViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    TaxonomyScreen(
        uiState = uiState,
        onBack = onBack,
        onRetry = viewModel::loadAll,
        onTopicClick = onTopicClick,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaxonomyScreen(
    uiState: TaxonomyUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onTopicClick: (String, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    HeritagePageBackground(modifier = modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.errorKind != null -> {
                    TaxonomyErrorContent(
                        errorKind = uiState.errorKind,
                        onRetry = onRetry,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
                else -> {
                    TaxonomyContent(
                        uiState = uiState,
                        onBack = onBack,
                        onTopicClick = onTopicClick,
                    )
                }
            }
        }
    }
}

@Composable
private fun TaxonomyContent(
    uiState: TaxonomyUiState,
    onBack: () -> Unit,
    onTopicClick: (String, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val tabTitles = listOf(
        stringResource(R.string.taxonomy_tab_categories),
        stringResource(R.string.taxonomy_tab_regions),
        stringResource(R.string.taxonomy_tab_kinds),
    )

    Column(modifier = modifier.fillMaxSize()) {
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
                title = stringResource(R.string.taxonomy_title),
                subtitle = stringResource(R.string.taxonomy_subtitle),
            )
        }

        PrimaryTabRow(selectedTabIndex = selectedTab) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) },
                )
            }
        }

        when (selectedTab) {
            0 -> TopicList(
                topics = uiState.categories,
                onTopicClick = { topic -> onTopicClick(topic.type, topic.key) },
            )
            1 -> TopicList(
                topics = uiState.regions,
                onTopicClick = { topic -> onTopicClick(topic.type, topic.key) },
            )
            2 -> KindList(
                kinds = uiState.kinds,
                onKindClick = { kind -> onTopicClick("kind", kind.key) },
            )
        }
    }
}

@Composable
private fun TopicList(
    topics: List<TaxonomyTopicDto>,
    onTopicClick: (TaxonomyTopicDto) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(topics) { topic ->
            HeritageContentCard(onClick = { onTopicClick(topic) }) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = topic.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    if (!topic.subtitle.isNullOrBlank()) {
                        Text(
                            text = topic.subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        MetricPill(
                            label = stringResource(R.string.taxonomy_stat_directory_items),
                            value = topic.directoryItemCount.toString(),
                        )
                        MetricPill(
                            label = stringResource(R.string.taxonomy_stat_inheritors),
                            value = topic.inheritorCount.toString(),
                        )
                        MetricPill(
                            label = stringResource(R.string.taxonomy_stat_total),
                            value = topic.total.toString(),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun KindList(
    kinds: List<TaxonomyKindDto>,
    onKindClick: (TaxonomyKindDto) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(kinds) { kind ->
            HeritageContentCard(onClick = { onKindClick(kind) }) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = kind.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        MetricPill(
                            label = stringResource(R.string.taxonomy_stat_directory_items),
                            value = kind.directoryItemCount.toString(),
                        )
                        MetricPill(
                            label = stringResource(R.string.taxonomy_stat_inheritors),
                            value = kind.inheritorCount.toString(),
                        )
                        MetricPill(
                            label = stringResource(R.string.taxonomy_stat_total),
                            value = kind.total.toString(),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TaxonomyErrorContent(
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
