package com.duckylife.heritage.modern.core.data

import com.duckylife.heritage.modern.core.network.api.ContentExportApi
import com.duckylife.heritage.modern.core.network.dto.advanced.ExportContentResultDto
import com.duckylife.heritage.modern.core.network.dto.advanced.ExportFormat
import com.duckylife.heritage.modern.core.network.dto.advanced.ExportPreviewDto
import com.duckylife.heritage.modern.core.network.dto.advanced.ExportRequestDto
import com.duckylife.heritage.modern.core.network.dto.advanced.ExportScopeType
import com.duckylife.heritage.modern.core.network.dto.advanced.ExportTemplateDto
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class ContentExportRepositoryTest {

    private val fakeApi = FakeContentExportApi()
    private val repository: ContentExportRepository = DefaultContentExportRepository(api = fakeApi)

    @Test
    fun `getTemplates returns api result`() = runTest {
        val templates = listOf(
            ExportTemplateDto(
                templateId = "ids-markdown",
                scopeType = ExportScopeType.Ids,
                supportedFormats = listOf(ExportFormat.Markdown, ExportFormat.Json),
            ),
        )
        fakeApi.templates = templates

        val result = repository.getTemplates()

        assertSame(templates, result)
    }

    @Test
    fun `previewExport passes request and returns preview`() = runTest {
        val preview = ExportPreviewDto(estimatedItemCount = 1, estimatedBytes = 1024)
        fakeApi.previewResult = preview
        val request = ExportRequestDto(
            scopeType = ExportScopeType.Ids,
            format = ExportFormat.Markdown,
            ids = listOf("a1"),
        )

        val result = repository.previewExport(request)

        assertSame(preview, result)
        assertEquals(request, fakeApi.capturedPreviewRequest)
    }

    @Test
    fun `exportContent passes request and returns result`() = runTest {
        val exportResult = ExportContentResultDto(
            content = "# Title",
            format = ExportFormat.Markdown,
            itemCount = 1,
        )
        fakeApi.exportResult = exportResult
        val request = ExportRequestDto(
            scopeType = ExportScopeType.Ids,
            format = ExportFormat.Json,
            ids = listOf("d1"),
        )

        val result = repository.exportContent(request)

        assertSame(exportResult, result)
        assertEquals(request, fakeApi.capturedExportRequest)
    }

    private class FakeContentExportApi : ContentExportApi {
        var templates: List<ExportTemplateDto> = emptyList()
        var previewResult: ExportPreviewDto = ExportPreviewDto()
        var exportResult: ExportContentResultDto = ExportContentResultDto()
        var capturedPreviewRequest: ExportRequestDto? = null
        var capturedExportRequest: ExportRequestDto? = null

        override suspend fun getExportTemplates(): List<ExportTemplateDto> = templates

        override suspend fun previewExport(request: ExportRequestDto): ExportPreviewDto {
            capturedPreviewRequest = request
            return previewResult
        }

        override suspend fun exportContent(request: ExportRequestDto): ExportContentResultDto {
            capturedExportRequest = request
            return exportResult
        }
    }
}
