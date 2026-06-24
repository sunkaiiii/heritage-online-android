package com.duckylife.heritage.modern.core.data

import com.duckylife.heritage.modern.core.network.V3ContentPageQuery
import com.duckylife.heritage.modern.core.network.api.ContentIntelligenceApi
import com.duckylife.heritage.modern.core.network.dto.SearchResultType
import com.duckylife.heritage.modern.core.network.dto.advanced.AiCardDto
import com.duckylife.heritage.modern.core.network.dto.advanced.ContentDigestSectionDto
import com.duckylife.heritage.modern.core.network.dto.advanced.ContentRefDto
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphNeighborsDto
import com.duckylife.heritage.modern.core.network.dto.advanced.LocalStateSectionDto
import com.duckylife.heritage.modern.core.network.dto.advanced.SectionStatus
import com.duckylife.heritage.modern.core.network.dto.advanced.V3ContentPageDto
import com.duckylife.heritage.modern.core.profile.LocalProfileRepository
import javax.inject.Inject

/**
 * 详情页增强内容（V3 content page）的引用。
 */
data class ContentIntelligenceRef(
    val type: SearchResultType,
    val id: String,
) {
    init {
        require(id.isNotBlank()) { "content id must not be blank" }
    }
}

/**
 * 单个增强板块的状态与数据。
 */
data class IntelligenceSection<T>(
    val status: SectionStatus,
    val data: T? = null,
)

/**
 * 已映射为领域模型的 V3 content page。
 */
data class ContentIntelligencePage(
    val ref: ContentIntelligenceRef,
    val pageType: SearchResultType,
    val aiSection: IntelligenceSection<AiCardDto>,
    val graphSection: IntelligenceSection<GraphNeighborsDto>,
    val recommendationSection: IntelligenceSection<List<ContentRefDto>>,
    val relatedContentSection: IntelligenceSection<List<ContentRefDto>>,
    val digestSection: IntelligenceSection<ContentDigestSectionDto>,
    val localState: LocalStateSectionDto?,
    val sectionStatus: Map<String, SectionStatus>,
    val warnings: List<String>,
)

/**
 * V3 智能内容仓库。
 *
 * 负责把后端 V3 content page 的原始响应映射为每个增强板块独立的状态，
 * 让 ViewModel 可以按板块降级，而不是把增强层失败升级为整页错误。
 */
interface ContentIntelligenceRepository {
    suspend fun loadContentPage(ref: ContentIntelligenceRef): ContentIntelligencePage
}

class DefaultContentIntelligenceRepository @Inject constructor(
    private val api: ContentIntelligenceApi,
    private val profileRepository: LocalProfileRepository,
) : ContentIntelligenceRepository {

    override suspend fun loadContentPage(ref: ContentIntelligenceRef): ContentIntelligencePage {
        val profileId = profileRepository.currentProfileId()
        val dto = api.getV3ContentPage(
            V3ContentPageQuery(
                contentType = ref.type,
                id = ref.id,
                profileId = profileId,
                includeAi = true,
                includeGraph = true,
                includeRecommendations = true,
                includeLocalState = true,
                includeDigest = true,
                includeExportHints = false,
                recommendationLimit = 8,
                neighborLimit = 12,
            ),
        )
        return dto.toPage(ref)
    }
}

private fun V3ContentPageDto.toPage(ref: ContentIntelligenceRef): ContentIntelligencePage {
    val statusMap = sectionStatus.associate { it.section.lowercase() to it.status }

    return ContentIntelligencePage(
        ref = ref,
        pageType = ref.type,
        aiSection = aiCard.toSection(
            statusMap = statusMap,
            sectionName = "aiCard",
            derive = { card ->
                when {
                    card?.hasAi != true -> SectionStatus.Empty
                    else -> SectionStatus.Ready
                }
            },
        ),
        graphSection = graph.toSection(
            statusMap = statusMap,
            sectionName = "graph",
            derive = { g ->
                when {
                    g == null -> SectionStatus.Missing
                    g.nodes.isEmpty() -> SectionStatus.Empty
                    else -> SectionStatus.Ready
                }
            },
        ),
        recommendationSection = recommendations.toListSection(
            statusMap = statusMap,
            sectionName = "recommendations",
        ),
        relatedContentSection = relatedContent.toListSection(
            statusMap = statusMap,
            sectionName = "relatedContent",
        ),
        digestSection = digest.toSection(
            statusMap = statusMap,
            sectionName = "digest",
            derive = { d ->
                when {
                    d == null -> SectionStatus.Missing
                    else -> SectionStatus.Ready
                }
            },
        ),
        localState = localState,
        sectionStatus = statusMap,
        warnings = warnings,
    )
}

private inline fun <T : Any> T?.toSection(
    statusMap: Map<String, SectionStatus>,
    sectionName: String,
    derive: (T?) -> SectionStatus,
): IntelligenceSection<T> {
    val explicitStatus = statusMap[sectionName.lowercase()]
    val effectiveStatus = when (explicitStatus) {
        SectionStatus.Disabled,
        SectionStatus.Unavailable,
        -> explicitStatus

        else -> explicitStatus ?: derive(this)
    }
    return IntelligenceSection(
        status = effectiveStatus,
        data = this?.takeIf { effectiveStatus == SectionStatus.Ready },
    )
}

private fun List<ContentRefDto>.toListSection(
    statusMap: Map<String, SectionStatus>,
    sectionName: String,
): IntelligenceSection<List<ContentRefDto>> {
    val explicitStatus = statusMap[sectionName.lowercase()]
    val effectiveStatus = when (explicitStatus) {
        SectionStatus.Disabled,
        SectionStatus.Unavailable,
        -> explicitStatus

        else -> explicitStatus ?: if (isEmpty()) SectionStatus.Empty else SectionStatus.Ready
    }
    return IntelligenceSection(
        status = effectiveStatus,
        data = takeIf { effectiveStatus == SectionStatus.Ready },
    )
}
