package com.duckylife.heritage.modern.core.data

import com.duckylife.heritage.modern.core.network.api.ContentExportApi
import com.duckylife.heritage.modern.core.network.dto.advanced.ExportContentResultDto
import com.duckylife.heritage.modern.core.network.dto.advanced.ExportPreviewDto
import com.duckylife.heritage.modern.core.network.dto.advanced.ExportRequestDto
import com.duckylife.heritage.modern.core.network.dto.advanced.ExportTemplateDto
import javax.inject.Inject

/**
 * 内容导出仓库。
 *
 * 只封装 `ContentExportApi` 的只读/导出能力，不做额外业务决策。
 */
interface ContentExportRepository {
    suspend fun getTemplates(): List<ExportTemplateDto>
    suspend fun previewExport(request: ExportRequestDto): ExportPreviewDto
    suspend fun exportContent(request: ExportRequestDto): ExportContentResultDto
}

class DefaultContentExportRepository @Inject constructor(
    private val api: ContentExportApi,
) : ContentExportRepository {

    override suspend fun getTemplates(): List<ExportTemplateDto> = api.getExportTemplates()

    override suspend fun previewExport(request: ExportRequestDto): ExportPreviewDto =
        api.previewExport(request)

    override suspend fun exportContent(request: ExportRequestDto): ExportContentResultDto =
        api.exportContent(request)
}
