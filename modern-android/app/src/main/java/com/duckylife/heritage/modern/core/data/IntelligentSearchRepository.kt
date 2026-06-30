package com.duckylife.heritage.modern.core.data

import com.duckylife.heritage.modern.core.network.IntelligentSearchQuery
import com.duckylife.heritage.modern.core.network.api.ContentIntelligenceApi
import com.duckylife.heritage.modern.core.network.dto.advanced.IntelligentSearchResponseDto
import javax.inject.Inject

/**
 * 只读取后端已生成的智能搜索索引。
 *
 * 此接口不创建 AI 任务、不调用本地 LLM；它只为搜索 feature 提供稳定、可替换的读取边界。
 */
interface IntelligentSearchRepository {
    suspend fun search(query: IntelligentSearchQuery): IntelligentSearchResponseDto
}

class DefaultIntelligentSearchRepository @Inject constructor(
    private val api: ContentIntelligenceApi,
) : IntelligentSearchRepository {
    override suspend fun search(query: IntelligentSearchQuery): IntelligentSearchResponseDto =
        api.intelligentSearch(query)
}
