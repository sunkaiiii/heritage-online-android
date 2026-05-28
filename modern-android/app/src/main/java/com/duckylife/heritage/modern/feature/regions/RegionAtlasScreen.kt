package com.duckylife.heritage.modern.feature.regions

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.duckylife.heritage.modern.core.network.dto.RegionAtlasDto
import com.duckylife.heritage.modern.core.network.dto.RegionAtlasItemDto
import com.duckylife.heritage.modern.ui.component.HeritageMetaChip
import com.duckylife.heritage.modern.ui.component.HeritagePageBackground
import com.duckylife.heritage.modern.ui.component.HeritagePageHeader
import com.duckylife.heritage.modern.ui.error.ErrorKind
import com.duckylife.heritage.modern.ui.error.fallbackResId

@Composable
fun RegionAtlasRoute(
    onBack: () -> Unit,
    onRegionSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RegionViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    RegionAtlasScreen(
        uiState = uiState,
        onBack = onBack,
        onRetry = viewModel::loadAtlas,
        onRegionSelected = onRegionSelected,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegionAtlasScreen(
    uiState: RegionAtlasUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onRegionSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    HeritagePageBackground(modifier = modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.errorKind != null -> {
                    RegionErrorContent(
                        errorKind = uiState.errorKind,
                        onRetry = onRetry,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
                uiState.atlas != null -> {
                    RegionAtlasContent(
                        atlas = uiState.atlas,
                        onBack = onBack,
                        onRegionSelected = onRegionSelected,
                    )
                }
            }
        }
    }
}

@Composable
private fun RegionAtlasContent(
    atlas: RegionAtlasDto,
    onBack: () -> Unit,
    onRegionSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
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
                title = stringResource(R.string.discovery_region_atlas),
                subtitle = stringResource(R.string.discovery_items_count, atlas.totals?.regionCount ?: 0),
            )
        }

        // Totals bar
        if (atlas.totals != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                TotalsChip(
                    label = stringResource(R.string.region_total_directory),
                    count = atlas.totals.directoryItemCount,
                    modifier = Modifier.weight(1f),
                )
                TotalsChip(
                    label = stringResource(R.string.region_total_inheritors),
                    count = atlas.totals.inheritorCount,
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Grid of regions
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 18.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(atlas.regions) { region ->
                RegionCard(
                    region = region,
                    onClick = {
                        val regionKey = region.region ?: return@RegionCard
                        onRegionSelected(regionKey)
                    },
                )
            }
        }
    }
}

@Composable
private fun TotalsChip(
    label: String,
    count: Long,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}

@Composable
private fun RegionCard(
    region: RegionAtlasItemDto,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        ),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = region.displayName ?: region.region.orEmpty(),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                HeritageMetaChip(text = stringResource(R.string.region_items_format, region.directoryItemCount))
                HeritageMetaChip(text = stringResource(R.string.region_inheritors_format, region.inheritorCount))
            }
            if (region.topCategories.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = region.topCategories.take(2).mapNotNull { it.key }.joinToString("、"),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (region.topKinds.isNotEmpty()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = region.topKinds.take(2).mapNotNull { it.key }.joinToString("、"),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun RegionErrorContent(
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
