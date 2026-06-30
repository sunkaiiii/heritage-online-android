package com.duckylife.heritage.modern.feature.export

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duckylife.heritage.modern.core.data.ContentExportRepository
import com.duckylife.heritage.modern.core.network.dto.SearchResultType
import com.duckylife.heritage.modern.core.network.dto.advanced.ExportFormat
import com.duckylife.heritage.modern.core.network.dto.advanced.ExportPreviewDto
import com.duckylife.heritage.modern.core.network.dto.advanced.ExportRequestDto
import com.duckylife.heritage.modern.core.network.dto.advanced.ExportSampleItemDto
import com.duckylife.heritage.modern.core.network.dto.advanced.ExportScopeType
import com.duckylife.heritage.modern.core.network.dto.advanced.ExportTemplateDto
import com.duckylife.heritage.modern.core.runCatchingCancellable
import com.duckylife.heritage.modern.ui.error.toUiError
import com.duckylife.heritage.modern.ui.state.AsyncState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** 导出内容大小安全阈值：1 MB（UTF-8 字符串长度近似）。 */
internal const val MAX_EXPORT_CONTENT_BYTES = 1 * 1024 * 1024

/**
 * 内容导出 bottom sheet UI 状态。
 */
data class ContentExportUiState(
    val contentId: String = "",
    val targetType: SearchResultType = SearchResultType.Article,
    val templates: AsyncState<Unit> = AsyncState(),
    val supportedFormats: Set<ExportFormat> = setOf(ExportFormat.Markdown, ExportFormat.Json, ExportFormat.Csv),
    val selectedFormat: ExportFormat = ExportFormat.Markdown,
    val includeSources: Boolean = true,
    val includeImages: Boolean = false,
    val includeAiSummary: Boolean = false,
    val preview: AsyncState<ExportPreviewUiModel> = AsyncState(),
    val oversizedWarning: Boolean = false,
    val exportError: AsyncState<Unit> = AsyncState(),
) {
    val canRequestExport: Boolean
        get() = contentId.isNotBlank() &&
            !templates.isLoading &&
            (templates.data != null || templates.errorKind != null)
}

/**
 * 导出预览 UI 模型。
 */
data class ExportPreviewUiModel(
    val estimatedItemCount: Int,
    val estimatedSize: String,
    val warnings: List<String>,
    val samples: List<ExportSampleItemDto>,
)

@HiltViewModel
class ContentExportViewModel @Inject constructor(
    private val repository: ContentExportRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ContentExportUiState())
    val uiState: StateFlow<ContentExportUiState> = _uiState.asStateFlow()

    private val _shareEvent = Channel<String>(Channel.BUFFERED)
    val shareEvent: Flow<String> = _shareEvent.receiveAsFlow()

    private var templatesJob: Job? = null
    private var previewJob: Job? = null
    private var exportJob: Job? = null

    /**
     * 每次打开 bottom sheet 时调用，传入当前要导出的内容 ID 与类型。
     */
    fun initialize(contentId: String, targetType: SearchResultType) {
        if (contentId.isBlank()) return
        val current = _uiState.value
        val needsReload = current.contentId != contentId || current.targetType != targetType
        if (needsReload) {
            cancelPendingJobs()
            _uiState.update {
                ContentExportUiState(
                    contentId = contentId,
                    targetType = targetType,
                )
            }
            loadTemplates()
        } else if (current.templates.errorKind != null || shouldLoadMissingTemplates(current)) {
            loadTemplates()
        }
    }

    fun loadTemplates() {
        templatesJob?.cancel()
        templatesJob = viewModelScope.launch {
            _uiState.update { it.copy(templates = AsyncState(isLoading = true)) }
            runCatchingCancellable { repository.getTemplates() }
                .onSuccess { templates ->
                    val supported = deriveSupportedFormats(templates)
                    _uiState.update { state ->
                        state.copy(
                            templates = AsyncState(data = Unit),
                            supportedFormats = supported,
                            selectedFormat = defaultFormat(supported, state.selectedFormat),
                        )
                    }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(templates = AsyncState(errorKind = throwable.toUiError().kind)) }
                }
        }
    }

    fun selectFormat(format: ExportFormat) {
        if (format in _uiState.value.supportedFormats) {
            _uiState.update { it.copy(selectedFormat = format, preview = AsyncState()) }
        }
    }

    fun setIncludeSources(value: Boolean) {
        _uiState.update { it.copy(includeSources = value, preview = AsyncState()) }
    }

    fun setIncludeImages(value: Boolean) {
        _uiState.update { it.copy(includeImages = value, preview = AsyncState()) }
    }

    fun setIncludeAiSummary(value: Boolean) {
        _uiState.update { it.copy(includeAiSummary = value, preview = AsyncState()) }
    }

    fun loadPreview() {
        val state = _uiState.value
        if (!state.canRequestExport) return
        previewJob?.cancel()
        previewJob = viewModelScope.launch {
            _uiState.update {
                it.copy(
                    preview = it.preview.copy(isLoading = true),
                    oversizedWarning = false,
                )
            }
            val request = buildRequest(state)
            runCatchingCancellable { repository.previewExport(request) }
                .onSuccess { preview ->
                    _uiState.update {
                        it.copy(preview = AsyncState(data = preview.toUiModel()))
                    }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(preview = AsyncState(errorKind = throwable.toUiError().kind))
                    }
                }
        }
    }

    fun exportAndShare() {
        val state = _uiState.value
        if (!state.canRequestExport) return
        exportJob?.cancel()
        exportJob = viewModelScope.launch {
            _uiState.update { it.copy(exportError = AsyncState(isLoading = true), oversizedWarning = false) }
            val request = buildRequest(state)
            runCatchingCancellable { repository.exportContent(request) }
                .onSuccess { result ->
                    val content = result.content
                    val oversized = content != null && content.toByteArray(Charsets.UTF_8).size > MAX_EXPORT_CONTENT_BYTES
                    _uiState.update {
                        it.copy(
                            exportError = AsyncState(),
                            oversizedWarning = oversized,
                        )
                    }
                    if (!oversized && content != null) {
                        _shareEvent.trySend(content)
                    }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(exportError = AsyncState(errorKind = throwable.toUiError().kind)) }
                }
        }
    }

    fun dismiss() {
        cancelPendingJobs()
        _uiState.update {
            it.copy(
                templates = if (it.templates.isLoading) AsyncState() else it.templates,
                preview = AsyncState(),
                exportError = AsyncState(),
                oversizedWarning = false,
            )
        }
    }

    private fun cancelPendingJobs() {
        templatesJob?.cancel()
        previewJob?.cancel()
        exportJob?.cancel()
    }

    private fun shouldLoadMissingTemplates(state: ContentExportUiState): Boolean =
        state.templates.data == null && templatesJob?.isActive != true

    override fun onCleared() {
        cancelPendingJobs()
    }

    private fun buildRequest(state: ContentExportUiState): ExportRequestDto =
        ExportRequestDto(
            scopeType = ExportScopeType.Ids,
            format = state.selectedFormat,
            targetType = state.targetType.wireName,
            ids = listOf(state.contentId),
            includeAiSummary = state.includeAiSummary,
            includeSources = state.includeSources,
            includeImages = state.includeImages,
            limit = 1,
        )

    private fun deriveSupportedFormats(templates: List<ExportTemplateDto>): Set<ExportFormat> {
        val idsTemplates = templates.filter { it.scopeType == ExportScopeType.Ids }
        val formats = idsTemplates.flatMap { it.supportedFormats }
            .filter { it != ExportFormat.Unknown }
            .toSet()
        return formats.takeIf { it.isNotEmpty() }
            ?: setOf(ExportFormat.Markdown, ExportFormat.Json, ExportFormat.Csv)
    }

    private fun defaultFormat(supported: Set<ExportFormat>, current: ExportFormat): ExportFormat =
        if (current in supported) current else supported.firstOrNull { it == ExportFormat.Markdown }
            ?: supported.firstOrNull()
            ?: ExportFormat.Markdown

    private fun ExportPreviewDto.toUiModel(): ExportPreviewUiModel =
        ExportPreviewUiModel(
            estimatedItemCount = estimatedItemCount,
            estimatedSize = formatEstimatedBytes(estimatedBytes),
            warnings = warnings,
            samples = sampleItems.take(3),
        )

    private fun formatEstimatedBytes(bytes: Long): String =
        when {
            bytes >= 1024 * 1024 -> "%.1f MB".format(bytes / (1024.0 * 1024.0))
            bytes >= 1024 -> "%.0f KB".format(bytes / 1024.0)
            else -> "$bytes B"
        }
}
