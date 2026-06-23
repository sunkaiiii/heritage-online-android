package com.duckylife.heritage.modern.core.network.api

import com.duckylife.heritage.modern.core.network.dto.advanced.ExportContentResultDto
import com.duckylife.heritage.modern.core.network.dto.advanced.ExportPreviewDto
import com.duckylife.heritage.modern.core.network.dto.advanced.ExportRequestDto
import com.duckylife.heritage.modern.core.network.dto.advanced.ExportTemplateDto

/**
 * 内容导出端点契约（只读，不调用 LLM）。
 */
interface ContentExportApi {
    suspend fun getExportTemplates(): List<ExportTemplateDto>

    suspend fun previewExport(request: ExportRequestDto): ExportPreviewDto

    suspend fun exportContent(request: ExportRequestDto): ExportContentResultDto
}
