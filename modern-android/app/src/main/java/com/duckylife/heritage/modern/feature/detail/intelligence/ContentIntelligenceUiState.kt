package com.duckylife.heritage.modern.feature.detail.intelligence

import com.duckylife.heritage.modern.core.data.ContentIntelligencePage
import com.duckylife.heritage.modern.core.data.IntelligenceSection
import com.duckylife.heritage.modern.core.network.dto.advanced.AiCardDto
import com.duckylife.heritage.modern.core.network.dto.advanced.ContentDigestSectionDto
import com.duckylife.heritage.modern.core.network.dto.advanced.ContentRefDto
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphNeighborsDto
import com.duckylife.heritage.modern.core.network.dto.advanced.SectionStatus
import com.duckylife.heritage.modern.ui.error.ErrorKind

/**
 * 详情页“智能内容”增强层的 UI 状态。
 *
 * 每个板块独立维护状态，增强服务不可用时只影响对应区块，不阻断主详情阅读。
 */
data class ContentIntelligenceUiState(
    val page: ContentIntelligencePage? = null,
    val isLoading: Boolean = false,
    val loadError: ErrorKind? = null,
    val aiSection: IntelligenceSection<AiCardDto> = IntelligenceSection(SectionStatus.Disabled),
    val graphSection: IntelligenceSection<GraphNeighborsDto> = IntelligenceSection(SectionStatus.Disabled),
    val recommendationSection: IntelligenceSection<List<ContentRefDto>> = IntelligenceSection(SectionStatus.Disabled),
    val digestSection: IntelligenceSection<ContentDigestSectionDto> = IntelligenceSection(SectionStatus.Disabled),
    val learningRoutesAvailable: Boolean = false,
    val warnings: List<String> = emptyList(),
)
