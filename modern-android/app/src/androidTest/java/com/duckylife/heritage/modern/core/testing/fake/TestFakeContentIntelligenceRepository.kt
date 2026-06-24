package com.duckylife.heritage.modern.core.testing.fake

import com.duckylife.heritage.modern.core.data.ContentIntelligencePage
import com.duckylife.heritage.modern.core.data.ContentIntelligenceRef
import com.duckylife.heritage.modern.core.data.ContentIntelligenceRepository
import com.duckylife.heritage.modern.core.data.IntelligenceSection
import com.duckylife.heritage.modern.core.network.dto.advanced.AiCardDto
import com.duckylife.heritage.modern.core.network.dto.advanced.ContentDigestSectionDto
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphNeighborsDto
import com.duckylife.heritage.modern.core.network.dto.advanced.SectionStatus

/**
 * 用于 instrumented 测试的 V3 content page 仓库假实现。
 *
 * 默认返回所有板块为空/禁用的页面，避免在测试导航到详情页时触发真实网络请求。
 */
class TestFakeContentIntelligenceRepository : ContentIntelligenceRepository {
    override suspend fun loadContentPage(ref: ContentIntelligenceRef): ContentIntelligencePage =
        ContentIntelligencePage(
            ref = ref,
            pageType = ref.type,
            aiSection = IntelligenceSection(SectionStatus.Disabled, AiCardDto()),
            graphSection = IntelligenceSection(SectionStatus.Disabled, GraphNeighborsDto()),
            recommendationSection = IntelligenceSection(SectionStatus.Disabled, emptyList()),
            relatedContentSection = IntelligenceSection(SectionStatus.Disabled, emptyList()),
            digestSection = IntelligenceSection(SectionStatus.Disabled, ContentDigestSectionDto()),
            localState = null,
            sectionStatus = emptyMap(),
            warnings = emptyList(),
        )
}
