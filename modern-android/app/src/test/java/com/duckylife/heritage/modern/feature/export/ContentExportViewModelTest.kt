package com.duckylife.heritage.modern.feature.export

import com.duckylife.heritage.modern.core.data.ContentExportRepository
import com.duckylife.heritage.modern.core.network.dto.SearchResultType
import com.duckylife.heritage.modern.core.network.dto.advanced.ExportContentResultDto
import com.duckylife.heritage.modern.core.network.dto.advanced.ExportFormat
import com.duckylife.heritage.modern.core.network.dto.advanced.ExportPreviewDto
import com.duckylife.heritage.modern.core.network.dto.advanced.ExportRequestDto
import com.duckylife.heritage.modern.core.network.dto.advanced.ExportSampleItemDto
import com.duckylife.heritage.modern.core.network.dto.advanced.ExportScopeType
import com.duckylife.heritage.modern.core.network.dto.advanced.ExportTemplateDto
import com.duckylife.heritage.modern.core.testing.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ContentExportViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val fakeRepository = FakeContentExportRepository()

    @Test
    fun `initialize loads templates and derives supported formats`() = runTest {
        fakeRepository.templates = listOf(
            ExportTemplateDto(
                templateId = "t1",
                scopeType = ExportScopeType.Ids,
                supportedFormats = listOf(ExportFormat.Markdown, ExportFormat.Csv),
            ),
        )
        val viewModel = ContentExportViewModel(fakeRepository)

        viewModel.initialize("a1", SearchResultType.Article)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.templates.isLoading)
        assertEquals(setOf(ExportFormat.Markdown, ExportFormat.Csv), state.supportedFormats)
        assertEquals(ExportFormat.Markdown, state.selectedFormat)
        assertEquals(SearchResultType.Article, state.targetType)
        assertEquals("a1", state.contentId)
    }

    @Test
    fun `selectFormat ignores unsupported formats`() = runTest {
        fakeRepository.templates = listOf(
            ExportTemplateDto(
                templateId = "t1",
                scopeType = ExportScopeType.Ids,
                supportedFormats = listOf(ExportFormat.Markdown),
            ),
        )
        val viewModel = ContentExportViewModel(fakeRepository)
        viewModel.initialize("a1", SearchResultType.Article)
        advanceUntilIdle()

        viewModel.selectFormat(ExportFormat.Json)

        assertEquals(ExportFormat.Markdown, viewModel.uiState.value.selectedFormat)
    }

    @Test
    fun `toggles reset existing preview`() = runTest {
        fakeRepository.previewResult = ExportPreviewDto(estimatedItemCount = 1)
        val viewModel = ContentExportViewModel(fakeRepository)
        viewModel.initialize("a1", SearchResultType.Article)
        advanceUntilIdle()
        viewModel.loadPreview()
        advanceUntilIdle()
        assertNotNull(viewModel.uiState.value.preview.data)

        viewModel.setIncludeImages(true)

        assertNull(viewModel.uiState.value.preview.data)
    }

    @Test
    fun `loadPreview builds ids request and updates state`() = runTest {
        fakeRepository.templates = listOf(
            ExportTemplateDto(
                templateId = "t1",
                scopeType = ExportScopeType.Ids,
                supportedFormats = listOf(ExportFormat.Markdown),
            ),
        )
        fakeRepository.previewResult = ExportPreviewDto(
            estimatedItemCount = 1,
            estimatedBytes = 2048,
            sampleItems = listOf(ExportSampleItemDto(title = "Sample")),
        )
        val viewModel = ContentExportViewModel(fakeRepository)
        viewModel.initialize("a1", SearchResultType.Article)
        advanceUntilIdle()

        viewModel.setIncludeSources(true)
        viewModel.setIncludeImages(false)
        viewModel.setIncludeAiSummary(true)
        viewModel.loadPreview()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.preview.isLoading)
        assertEquals(1, state.preview.data?.estimatedItemCount)
        assertEquals(1, state.preview.data?.samples?.size)

        with(fakeRepository.capturedPreviewRequest) {
            assertNotNull(this)
            assertEquals(ExportScopeType.Ids, this?.scopeType)
            assertEquals(ExportFormat.Markdown, this?.format)
            assertEquals(listOf("a1"), this?.ids)
            assertEquals(SearchResultType.Article.wireName, this?.targetType)
            assertTrue(this?.includeSources == true)
            assertFalse(this?.includeImages == true)
            assertTrue(this?.includeAiSummary == true)
        }
    }

    @Test
    fun `exportAndShare emits shareContent for normal size`() = runTest {
        fakeRepository.exportResult = ExportContentResultDto(
            content = "# Article",
            format = ExportFormat.Markdown,
            itemCount = 1,
        )
        val viewModel = ContentExportViewModel(fakeRepository)
        viewModel.initialize("a1", SearchResultType.Article)
        advanceUntilIdle()

        viewModel.exportAndShare()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.exportError.isLoading)
        assertFalse(state.oversizedWarning)
        assertEquals("# Article", state.shareContent)

        with(fakeRepository.capturedExportRequest) {
            assertNotNull(this)
            assertEquals(ExportScopeType.Ids, this?.scopeType)
            assertEquals(listOf("a1"), this?.ids)
        }
    }

    @Test
    fun `exportAndShare sets oversized warning for huge content`() = runTest {
        fakeRepository.exportResult = ExportContentResultDto(
            content = "x".repeat(MAX_EXPORT_CONTENT_BYTES + 1),
            format = ExportFormat.Markdown,
            itemCount = 1,
        )
        val viewModel = ContentExportViewModel(fakeRepository)
        viewModel.initialize("a1", SearchResultType.Article)
        advanceUntilIdle()

        viewModel.exportAndShare()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.oversizedWarning)
        assertNull(state.shareContent)
    }

    @Test
    fun `exportAndShare maps error to exportError`() = runTest {
        fakeRepository.exportFailure = IllegalStateException("export failed")
        val viewModel = ContentExportViewModel(fakeRepository)
        viewModel.initialize("a1", SearchResultType.Article)
        advanceUntilIdle()

        viewModel.exportAndShare()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull(state.exportError.errorKind)
    }

    @Test
    fun `dismiss resets preview and share state`() = runTest {
        fakeRepository.previewResult = ExportPreviewDto(estimatedItemCount = 1)
        fakeRepository.exportResult = ExportContentResultDto(content = "content")
        val viewModel = ContentExportViewModel(fakeRepository)
        viewModel.initialize("a1", SearchResultType.Article)
        advanceUntilIdle()
        viewModel.loadPreview()
        viewModel.exportAndShare()
        advanceUntilIdle()

        viewModel.dismiss()

        val state = viewModel.uiState.value
        assertNull(state.preview.data)
        assertNull(state.shareContent)
        assertFalse(state.oversizedWarning)
    }

    private class FakeContentExportRepository : ContentExportRepository {
        var templates: List<ExportTemplateDto> = emptyList()
        var previewResult: ExportPreviewDto = ExportPreviewDto()
        var exportResult: ExportContentResultDto = ExportContentResultDto()
        var exportFailure: Throwable? = null
        var capturedPreviewRequest: ExportRequestDto? = null
        var capturedExportRequest: ExportRequestDto? = null

        override suspend fun getTemplates(): List<ExportTemplateDto> = templates

        override suspend fun previewExport(request: ExportRequestDto): ExportPreviewDto {
            capturedPreviewRequest = request
            return previewResult
        }

        override suspend fun exportContent(request: ExportRequestDto): ExportContentResultDto {
            capturedExportRequest = request
            exportFailure?.let { throw it }
            return exportResult
        }
    }
}
