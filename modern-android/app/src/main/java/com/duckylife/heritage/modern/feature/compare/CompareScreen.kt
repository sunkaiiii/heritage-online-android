package com.duckylife.heritage.modern.feature.compare

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.duckylife.heritage.modern.R
import com.duckylife.heritage.modern.core.network.CompareType
import com.duckylife.heritage.modern.core.network.dto.CollectionItemDto
import com.duckylife.heritage.modern.core.network.dto.CompareResultDto
import com.duckylife.heritage.modern.core.network.dto.CompareSideDto
import com.duckylife.heritage.modern.core.network.dto.DiscoveryItemDto
import com.duckylife.heritage.modern.ui.component.DiscoveryItemRow
import com.duckylife.heritage.modern.ui.component.HeritagePageBackground
import com.duckylife.heritage.modern.feature.directory.labelRes
import com.duckylife.heritage.modern.ui.component.HeritagePageHeader
import com.duckylife.heritage.modern.ui.component.HeritageSectionHeader
import com.duckylife.heritage.modern.ui.component.MetricPillRow
import com.duckylife.heritage.modern.ui.error.fallbackResId

@Composable
fun CompareRoute(
    onBack: () -> Unit,
    onItemClick: (DiscoveryItemDto) -> Unit,
    initialType: String? = null,
    initialLeft: String? = null,
    initialRight: String? = null,
    modifier: Modifier = Modifier,
    viewModel: CompareViewModel = hiltViewModel(),
) {
    // Apply initial parameters if provided
    val hasInitialParams = initialType != null || initialLeft != null || initialRight != null
    if (hasInitialParams) {
        val compareType = when (initialType) {
            "region" -> CompareType.Region
            "category" -> CompareType.Category
            "kind" -> CompareType.Kind
            else -> CompareType.Region
        }
        androidx.compose.runtime.LaunchedEffect(Unit) {
            viewModel.updateType(compareType)
            if (initialLeft != null) viewModel.updateLeft(initialLeft)
            if (initialRight != null) viewModel.updateRight(initialRight)
        }
    }

    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    CompareScreen(
        uiState = uiState,
        onBack = onBack,
        onTypeChange = viewModel::updateType,
        onLeftChange = viewModel::updateLeft,
        onRightChange = viewModel::updateRight,
        onCompare = viewModel::compare,
        onItemClick = onItemClick,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompareScreen(
    uiState: CompareUiState,
    onBack: () -> Unit,
    onTypeChange: (CompareType) -> Unit,
    onLeftChange: (String) -> Unit,
    onRightChange: (String) -> Unit,
    onCompare: () -> Unit,
    onItemClick: (DiscoveryItemDto) -> Unit,
    modifier: Modifier = Modifier,
) {
    HeritagePageBackground(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 返回按钮 + 标题
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
                    title = stringResource(R.string.compare_title),
                    subtitle = stringResource(R.string.compare_subtitle),
                )
            }

            // 对比类型选择
            val typeOptions = listOf(
                CompareType.Region to stringResource(R.string.compare_type_region),
                CompareType.Category to stringResource(R.string.compare_type_category),
                CompareType.Kind to stringResource(R.string.compare_type_kind),
            )
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            ) {
                typeOptions.forEachIndexed { index, (type, label) ->
                    SegmentedButton(
                        selected = uiState.selectedType == type,
                        onClick = { onTypeChange(type) },
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = typeOptions.size,
                        ),
                    ) {
                        Text(label)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 输入区域（Kind 模式用 Dropdown，其他用文本框）
            if (uiState.selectedType == CompareType.Kind) {
                KindDropdownSelector(
                    selectedLeft = uiState.leftInput,
                    selectedRight = uiState.rightInput,
                    onLeftChange = onLeftChange,
                    onRightChange = onRightChange,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            } else {
                // 左侧输入
                OutlinedTextField(
                    value = uiState.leftInput,
                    onValueChange = onLeftChange,
                    label = { Text(stringResource(R.string.compare_left_label)) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 右侧输入
                OutlinedTextField(
                    value = uiState.rightInput,
                    onValueChange = onRightChange,
                    label = { Text(stringResource(R.string.compare_right_label)) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 开始对比按钮
            FilledTonalButton(
                onClick = onCompare,
                enabled = !uiState.isLoading &&
                    uiState.leftInput.isNotBlank() &&
                    uiState.rightInput.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            ) {
                Text(text = stringResource(R.string.compare_start))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 错误提示
            if (uiState.errorMessage != null) {
                val errorText = when (uiState.errorMessage) {
                    "empty" -> stringResource(R.string.compare_empty_input)
                    "same" -> stringResource(R.string.compare_same_error)
                    "invalid_kind" -> stringResource(R.string.compare_invalid_kind)
                    else -> stringResource(R.string.compare_load_failed)
                }
                Text(
                    text = errorText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            if (uiState.errorKind != null) {
                Text(
                    text = stringResource(uiState.errorKind.fallbackResId()),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // 内容区域
            Box(modifier = Modifier.weight(1f)) {
                when {
                    uiState.isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                        )
                    }
                    uiState.result != null -> {
                        CompareResultContent(
                            result = uiState.result,
                            onItemClick = onItemClick,
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun KindDropdownSelector(
    selectedLeft: String,
    selectedRight: String,
    onLeftChange: (String) -> Unit,
    onRightChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val kinds = com.duckylife.heritage.modern.core.network.dto.DirectoryItemKind.entries
    var leftExpanded by remember { mutableStateOf(false) }
    var rightExpanded by remember { mutableStateOf(false) }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // 左侧 Dropdown
        ExposedDropdownMenuBox(
            expanded = leftExpanded,
            onExpandedChange = { leftExpanded = it },
        ) {
            OutlinedTextField(
                value = kinds.firstOrNull { it.wireName == selectedLeft }?.let { matched ->
                    stringResource(matched.labelRes)
                } ?: selectedLeft,
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.compare_left_label)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = leftExpanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
            )
            ExposedDropdownMenu(
                expanded = leftExpanded,
                onDismissRequest = { leftExpanded = false },
            ) {
                kinds.forEach { kind ->
                    DropdownMenuItem(
                        text = { Text(stringResource(kind.labelRes)) },
                        onClick = {
                            onLeftChange(kind.wireName)
                            leftExpanded = false
                        },
                    )
                }
            }
        }

        // 右侧 Dropdown
        ExposedDropdownMenuBox(
            expanded = rightExpanded,
            onExpandedChange = { rightExpanded = it },
        ) {
            OutlinedTextField(
                value = kinds.firstOrNull { it.wireName == selectedRight }?.let { matched ->
                    stringResource(matched.labelRes)
                } ?: selectedRight,
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.compare_right_label)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = rightExpanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
            )
            ExposedDropdownMenu(
                expanded = rightExpanded,
                onDismissRequest = { rightExpanded = false },
            ) {
                kinds.forEach { kind ->
                    DropdownMenuItem(
                        text = { Text(stringResource(kind.labelRes)) },
                        onClick = {
                            onRightChange(kind.wireName)
                            rightExpanded = false
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun CompareResultContent(
    result: CompareResultDto,
    onItemClick: (DiscoveryItemDto) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // 两侧概览卡片
        item {
            CompareOverviewSection(left = result.left, right = result.right)
        }

        // 胜出总结
        item {
            CompareWinnerSummary(summary = result.summary)
        }

        // 共有分类
        if (result.sharedCategories.isNotEmpty()) {
            item {
                CompareChipSection(
                    title = stringResource(R.string.compare_shared) +
                        stringResource(R.string.compare_type_category),
                    items = result.sharedCategories,
                )
            }
        }

        // 左侧独有分类
        if (result.leftUniqueCategories.isNotEmpty()) {
            item {
                CompareChipSection(
                    title = stringResource(R.string.compare_unique_left) +
                        stringResource(R.string.compare_type_category),
                    items = result.leftUniqueCategories,
                )
            }
        }

        // 右侧独有分类
        if (result.rightUniqueCategories.isNotEmpty()) {
            item {
                CompareChipSection(
                    title = stringResource(R.string.compare_unique_right) +
                        stringResource(R.string.compare_type_category),
                    items = result.rightUniqueCategories,
                )
            }
        }

        // 共有地区
        if (result.sharedRegions.isNotEmpty()) {
            item {
                CompareChipSection(
                    title = stringResource(R.string.compare_shared) +
                        stringResource(R.string.compare_type_region),
                    items = result.sharedRegions,
                )
            }
        }

        // 左侧独有地区
        if (result.leftUniqueRegions.isNotEmpty()) {
            item {
                CompareChipSection(
                    title = stringResource(R.string.compare_unique_left) +
                        stringResource(R.string.compare_type_region),
                    items = result.leftUniqueRegions,
                )
            }
        }

        // 右侧独有地区
        if (result.rightUniqueRegions.isNotEmpty()) {
            item {
                CompareChipSection(
                    title = stringResource(R.string.compare_unique_right) +
                        stringResource(R.string.compare_type_region),
                    items = result.rightUniqueRegions,
                )
            }
        }

        // 左侧精选
        if (result.leftFeaturedItems.isNotEmpty()) {
            item {
                HeritageSectionHeader(
                    title = stringResource(R.string.compare_featured_left),
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
            items(result.leftFeaturedItems) { item ->
                val discoveryItem = item.toDiscoveryItem()
                DiscoveryItemRow(
                    item = discoveryItem,
                    onClick = { onItemClick(discoveryItem) },
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
        }

        // 右侧精选
        if (result.rightFeaturedItems.isNotEmpty()) {
            item {
                HeritageSectionHeader(
                    title = stringResource(R.string.compare_featured_right),
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
            items(result.rightFeaturedItems) { item ->
                val discoveryItem = item.toDiscoveryItem()
                DiscoveryItemRow(
                    item = discoveryItem,
                    onClick = { onItemClick(discoveryItem) },
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
        }
    }
}

@Composable
private fun CompareOverviewSection(
    left: CompareSideDto,
    right: CompareSideDto,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        CompareSideCard(side = left)
        CompareSideCard(side = right)
    }
}

@Composable
private fun CompareSideCard(
    side: CompareSideDto,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = side.title.ifBlank { side.key },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            MetricPillRow(
                metrics = listOf(
                    Pair(stringResource(R.string.compare_metric_directory_items), side.directoryItemCount.toString()),
                    Pair(stringResource(R.string.compare_metric_inheritors), side.inheritorCount.toString()),
                    Pair(stringResource(R.string.compare_metric_articles), side.articleCount.toString()),
                    Pair(stringResource(R.string.compare_metric_total), side.total.toString()),
                ),
            )
        }
    }
}

@Composable
private fun CompareWinnerSummary(
    summary: com.duckylife.heritage.modern.core.network.dto.CompareSummaryDto,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = stringResource(R.string.compare_winner),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
        )
        summary.winnerByTotal?.let {
            Text(
                text = "${stringResource(R.string.compare_metric_total)}: $it",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        summary.winnerByDirectoryItems?.let {
            Text(
                text = "${stringResource(R.string.compare_metric_directory_items)}: $it",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        summary.winnerByInheritors?.let {
            Text(
                text = "${stringResource(R.string.compare_metric_inheritors)}: $it",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        summary.winnerByArticles?.let {
            Text(
                text = "${stringResource(R.string.compare_metric_articles)}: $it",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun CompareChipSection(
    title: String,
    items: List<String>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.height(8.dp))
        androidx.compose.foundation.layout.FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            items.forEach { label ->
                FilterChip(
                    selected = false,
                    onClick = {},
                    label = { Text(label) },
                )
            }
        }
    }
}

private fun CollectionItemDto.toDiscoveryItem(): DiscoveryItemDto {
    return DiscoveryItemDto(
        id = id,
        type = type ?: "",
        title = title ?: "",
        summary = summary,
        category = category,
        region = region,
        publishedAt = publishedAt,
        publishedYear = publishedYear,
        coverImage = coverImage,
        sourceUrl = sourceUrl ?: "",
    )
}
