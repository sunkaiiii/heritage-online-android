package com.duckylife.heritage.modern.feature.discovery.deepdive

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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.duckylife.heritage.modern.R
import com.duckylife.heritage.modern.core.network.dto.DiscoveryItemDto
import com.duckylife.heritage.modern.ui.component.DiscoveryItemRow
import com.duckylife.heritage.modern.ui.component.HeritagePageBackground
import com.duckylife.heritage.modern.ui.component.HeritagePageHeader
import com.duckylife.heritage.modern.ui.component.HeritageSectionHeader
import com.duckylife.heritage.modern.ui.error.ErrorKind
import com.duckylife.heritage.modern.ui.error.fallbackResId

@Composable
fun DeepDiveRoute(
    seedType: String,
    seedId: String,
    onBack: () -> Unit,
    onItemClick: (DiscoveryItemDto) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DeepDiveViewModel = hiltViewModel<DeepDiveViewModel, DeepDiveViewModel.Factory>(
        key = "deep-dive-${seedType}-${seedId}",
        creationCallback = { factory -> factory.create(seedType = seedType, seedId = seedId) },
    ),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    DeepDiveScreen(
        uiState = uiState,
        onBack = onBack,
        onRetry = viewModel::load,
        onItemClick = onItemClick,
        onDeepDiveAgain = viewModel::deepDiveAgain,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeepDiveScreen(
    uiState: DeepDiveUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onItemClick: (DiscoveryItemDto) -> Unit,
    onDeepDiveAgain: (DiscoveryItemDto) -> Unit,
    modifier: Modifier = Modifier,
) {
    HeritagePageBackground(modifier = modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.errorKind != null -> {
                    DeepDiveErrorContent(
                        errorKind = uiState.errorKind,
                        onRetry = onRetry,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
                else -> {
                    DeepDiveContent(
                        seed = uiState.seed,
                        related = uiState.related,
                        onBack = onBack,
                        onItemClick = onItemClick,
                        onDeepDiveAgain = onDeepDiveAgain,
                    )
                }
            }
        }
    }
}

@Composable
private fun DeepDiveContent(
    seed: DiscoveryItemDto?,
    related: List<DiscoveryItemDto>,
    onBack: () -> Unit,
    onItemClick: (DiscoveryItemDto) -> Unit,
    onDeepDiveAgain: (DiscoveryItemDto) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 18.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
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
                    title = stringResource(R.string.deep_dive_title),
                    subtitle = null,
                )
            }
        }

        if (seed != null) {
            item {
                DiscoveryItemRow(
                    item = seed,
                    onClick = { onItemClick(seed) },
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
        }

        if (related.isNotEmpty()) {
            item {
                HeritageSectionHeader(
                    title = stringResource(R.string.deep_dive_related),
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }

            items(
                items = related,
                key = { it.id.orEmpty() },
            ) { item ->
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    DiscoveryItemRow(
                        item = item,
                        onClick = { onItemClick(item) },
                    )
                    TextButton(
                        onClick = { onDeepDiveAgain(item) },
                        modifier = Modifier.align(Alignment.End),
                    ) {
                        Text(text = stringResource(R.string.deep_dive_again))
                    }
                }
            }
        }
    }
}

@Composable
private fun DeepDiveErrorContent(
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
