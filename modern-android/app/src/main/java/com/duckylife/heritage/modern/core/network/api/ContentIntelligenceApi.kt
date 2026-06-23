package com.duckylife.heritage.modern.core.network.api

import com.duckylife.heritage.modern.core.network.ContentIntelligenceQuery
import com.duckylife.heritage.modern.core.network.IntelligentSearchQuery
import com.duckylife.heritage.modern.core.network.V3ContentPageQuery
import com.duckylife.heritage.modern.core.network.dto.SearchResultType
import com.duckylife.heritage.modern.core.network.dto.advanced.AiCardDto
import com.duckylife.heritage.modern.core.network.dto.advanced.ContentIntelligenceDto
import com.duckylife.heritage.modern.core.network.dto.advanced.IntelligentSearchResponseDto
import com.duckylife.heritage.modern.core.network.dto.advanced.V3ContentPageDto

/**
 * V3 AI 产品化端点契约（不调用 LLM，只读取已有结果）。
 */
interface ContentIntelligenceApi {
    suspend fun getV3ContentPage(query: V3ContentPageQuery): V3ContentPageDto

    suspend fun getContentIntelligence(query: ContentIntelligenceQuery): ContentIntelligenceDto

    suspend fun getArticleAiCard(id: String): AiCardDto

    suspend fun getDirectoryItemAiCard(id: String): AiCardDto

    suspend fun getInheritorAiCard(id: String): AiCardDto

    suspend fun intelligentSearch(query: IntelligentSearchQuery): IntelligentSearchResponseDto
}
