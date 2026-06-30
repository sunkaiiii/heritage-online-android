package com.duckylife.heritage.modern.feature.export

import android.content.ActivityNotFoundException
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.duckylife.heritage.modern.R
import com.duckylife.heritage.modern.core.network.dto.SearchResultType
import com.duckylife.heritage.modern.core.network.dto.advanced.ExportFormat
import com.duckylife.heritage.modern.core.network.dto.advanced.ExportSampleItemDto
import com.duckylife.heritage.modern.ui.component.HeritageErrorState

/**
 * 内容导出 bottom sheet。
 *
 * @param contentId 要导出的内容 ID。
 * @param targetType 内容类型，对应后端 `targetType`。
 * @param onDismiss 用户关闭 sheet 后的回调。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentExportBottomSheet(
    contentId: String,
    targetType: SearchResultType,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ContentExportViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current

    LaunchedEffect(contentId, targetType) {
        viewModel.initialize(contentId, targetType)
    }

    LaunchedEffect(Unit) {
        viewModel.shareEvent.collect { content ->
            val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(android.content.Intent.EXTRA_TEXT, content)
            }
            val chooser = android.content.Intent.createChooser(
                intent,
                context.getString(R.string.export_share_title),
            )
            try {
                context.startActivity(chooser)
            } catch (_: ActivityNotFoundException) {
                Toast.makeText(
                    context,
                    context.getString(R.string.export_share_no_app),
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = {
            viewModel.dismiss()
            onDismiss()
        },
        sheetState = sheetState,
        modifier = modifier,
    ) {
        ContentExportSheetContent(
            uiState = uiState,
            onFormatSelected = viewModel::selectFormat,
            onIncludeSourcesChanged = viewModel::setIncludeSources,
            onIncludeImagesChanged = viewModel::setIncludeImages,
            onIncludeAiSummaryChanged = viewModel::setIncludeAiSummary,
            onPreview = viewModel::loadPreview,
            onExport = viewModel::exportAndShare,
            onRetryTemplates = viewModel::loadTemplates,
            onRetryPreview = viewModel::loadPreview,
        )
    }
}

@Composable
internal fun ContentExportSheetContent(
    uiState: ContentExportUiState,
    onFormatSelected: (ExportFormat) -> Unit,
    onIncludeSourcesChanged: (Boolean) -> Unit,
    onIncludeImagesChanged: (Boolean) -> Unit,
    onIncludeAiSummaryChanged: (Boolean) -> Unit,
    onPreview: () -> Unit,
    onExport: () -> Unit,
    onRetryTemplates: () -> Unit,
    onRetryPreview: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showExportConfirmDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Text(
                text = stringResource(R.string.export_sheet_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
            )
        }

        if (uiState.templates.isLoading && uiState.templates.data == null) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    Text(
                        text = stringResource(R.string.export_templates_loading),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        if (uiState.templates.errorKind != null) {
            item {
                HeritageErrorState(
                    errorKind = uiState.templates.errorKind,
                    onRetry = onRetryTemplates,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        item {
            FormatSelector(
                formats = uiState.supportedFormats.toList(),
                selected = uiState.selectedFormat,
                onSelected = onFormatSelected,
            )
        }

        item {
            OptionSwitch(
                label = stringResource(R.string.export_include_sources),
                checked = uiState.includeSources,
                onCheckedChange = onIncludeSourcesChanged,
            )
        }

        item {
            OptionSwitch(
                label = stringResource(R.string.export_include_images),
                checked = uiState.includeImages,
                onCheckedChange = onIncludeImagesChanged,
            )
        }

        item {
            OptionSwitch(
                label = stringResource(R.string.export_include_ai_summary),
                checked = uiState.includeAiSummary,
                onCheckedChange = onIncludeAiSummaryChanged,
            )
        }

        item {
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        }

        item {
            PreviewSection(
                preview = uiState.preview,
                enabled = uiState.canRequestExport,
                onPreview = onPreview,
                onRetry = onRetryPreview,
            )
        }

        item {
            ExportButtonSection(
                enabled = uiState.canRequestExport && !uiState.exportError.isLoading,
                isLoading = uiState.exportError.isLoading,
                oversized = uiState.oversizedWarning,
                onExport = { showExportConfirmDialog = true },
            )
        }

    }

    if (showExportConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showExportConfirmDialog = false },
            title = { Text(stringResource(R.string.export_confirm_title)) },
            text = { Text(stringResource(R.string.export_confirm_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showExportConfirmDialog = false
                        onExport()
                    },
                ) { Text(stringResource(R.string.confirm)) }
            },
            dismissButton = {
                TextButton(onClick = { showExportConfirmDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }
}

@Composable
private fun FormatSelector(
    formats: List<ExportFormat>,
    selected: ExportFormat,
    onSelected: (ExportFormat) -> Unit,
    modifier: Modifier = Modifier,
) {
    val formats = formats.filter { it != ExportFormat.Unknown }
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(R.string.export_format_label),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
        )

        if (formats.size <= 3) {
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                formats.forEachIndexed { index, format ->
                    SegmentedButton(
                        shape = androidx.compose.material3.SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = formats.size,
                        ),
                        onClick = { onSelected(format) },
                        selected = selected == format,
                    ) {
                        Text(format.localizedLabel())
                    }
                }
            }
        } else {
            FormatDropdown(
                formats = formats,
                selected = selected,
                onSelected = onSelected,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormatDropdown(
    formats: List<ExportFormat>,
    selected: ExportFormat,
    onSelected: (ExportFormat) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier.fillMaxWidth(),
    ) {
        TextField(
            value = selected.localizedLabel(),
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.export_format_label)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            formats.forEach { format ->
                DropdownMenuItem(
                    text = { Text(format.localizedLabel()) },
                    onClick = {
                        onSelected(format)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun OptionSwitch(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun PreviewSection(
    preview: com.duckylife.heritage.modern.ui.state.AsyncState<ExportPreviewUiModel>,
    enabled: Boolean,
    onPreview: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.export_preview_title),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
            )
            TextButton(onClick = onPreview, enabled = enabled && !preview.isLoading) {
                Text(stringResource(R.string.export_preview_button))
            }
        }

        when {
            preview.isLoading && preview.data == null -> CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            preview.errorKind != null && preview.data == null -> HeritageErrorState(
                errorKind = preview.errorKind,
                onRetry = onRetry,
                modifier = Modifier.fillMaxWidth(),
            )

            preview.data != null -> PreviewResultCard(preview = preview.data)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PreviewResultCard(
    preview: ExportPreviewUiModel,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            PreviewChip(text = stringResource(R.string.export_preview_count, preview.estimatedItemCount))
            PreviewChip(text = stringResource(R.string.export_preview_size, preview.estimatedSize))
        }

        if (preview.warnings.isNotEmpty()) {
            Text(
                text = stringResource(R.string.export_preview_warnings),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
            )
            preview.warnings.forEach { warning ->
                Text(
                    text = "• $warning",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        if (preview.samples.isNotEmpty()) {
            Text(
                text = stringResource(R.string.export_preview_samples),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
            )
            preview.samples.forEach { sample ->
                SampleItem(sample = sample)
            }
        }
    }
}

@Composable
private fun PreviewChip(text: String, modifier: Modifier = Modifier) {
    androidx.compose.material3.AssistChip(
        onClick = {},
        label = { Text(text) },
        modifier = modifier,
    )
}

@Composable
private fun SampleItem(
    sample: ExportSampleItemDto,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
    ) {
        Text(
            text = sample.title?.takeIf { it.isNotBlank() }
                ?: stringResource(R.string.export_sample_untitled),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
        )
        sample.subtitle?.takeIf { it.isNotBlank() }?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ExportButtonSection(
    enabled: Boolean,
    isLoading: Boolean,
    oversized: Boolean,
    onExport: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Button(
            onClick = onExport,
            enabled = enabled && !isLoading,
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(end = 8.dp).size(18.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }
            Text(stringResource(R.string.export_generate_and_share))
        }

        if (oversized) {
            Text(
                text = stringResource(R.string.export_oversized_warning),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
            )
        }
    }
}
