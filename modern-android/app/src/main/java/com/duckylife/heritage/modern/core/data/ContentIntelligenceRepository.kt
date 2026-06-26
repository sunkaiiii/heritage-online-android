package com.duckylife.heritage.modern.core.data

import com.duckylife.heritage.modern.core.network.V3ContentPageQuery
import com.duckylife.heritage.modern.core.network.api.ContentIntelligenceApi
import com.duckylife.heritage.modern.core.network.isServiceUnavailable
import com.duckylife.heritage.modern.core.network.dto.BlendedRecommendationItemDto
import com.duckylife.heritage.modern.core.network.dto.BlendedRecommendationQueryDto
import com.duckylife.heritage.modern.core.network.dto.BlendedRecommendationResponseDto
import com.duckylife.heritage.modern.core.network.dto.ContentDigestDto
import com.duckylife.heritage.modern.core.network.dto.DetailContextDto
import com.duckylife.heritage.modern.core.network.dto.DigestFactDto
import com.duckylife.heritage.modern.core.network.dto.GraphDto
import com.duckylife.heritage.modern.core.network.dto.MediaAssetDto
import com.duckylife.heritage.modern.core.network.dto.RecommendationDto
import com.duckylife.heritage.modern.core.network.dto.RelatedSummaryDto
import com.duckylife.heritage.modern.core.network.dto.SearchResultType
import com.duckylife.heritage.modern.core.network.dto.advanced.AiCardDto
import com.duckylife.heritage.modern.core.network.dto.advanced.ContentDigestSectionDto
import com.duckylife.heritage.modern.core.network.dto.advanced.ContentRefDto
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphEdgeDto
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphNeighborsDto
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphNodeDto
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
 * V3 聚合页不能覆盖时，旧详情增强接口应继续承担的责任。
 *
 * 规则只在 repository 中计算，三个详情页无需各自理解 V3 的 section status。
 */
data class LegacyDetailFallback(
    val loadDigest: Boolean,
    val loadBlendedRecommendations: Boolean,
    val loadContext: Boolean,
) {
    companion object {
        val All = LegacyDetailFallback(
            loadDigest = true,
            loadBlendedRecommendations = true,
            loadContext = true,
        )
    }
}

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
    val learningRoutesAvailable: Boolean = false,
    /** 已映射到旧详情探索组件可消费的 V3 digest，ready 时替代旧 digest 请求。 */
    val detailDigest: ContentDigestDto? = null,
    /** 已映射到旧详情探索组件可消费的 V3 推荐，ready 时替代旧 blended 请求。 */
    val detailRecommendations: BlendedRecommendationResponseDto? = null,
    /** V3 图谱、相关内容、推荐均 ready 时使用，避免重复调用旧 context。 */
    val detailContext: DetailContextDto? = null,
    val legacyFallback: LegacyDetailFallback = LegacyDetailFallback.All,
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
        return try {
            api.getV3ContentPage(
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
            ).toPage(ref)
        } catch (throwable: Throwable) {
            if (throwable.isServiceUnavailable()) unavailableContentPage(ref) else throw throwable
        }
    }
}

private fun unavailableContentPage(ref: ContentIntelligenceRef): ContentIntelligencePage =
    ContentIntelligencePage(
        ref = ref,
        pageType = ref.type,
        aiSection = IntelligenceSection(SectionStatus.Unavailable),
        graphSection = IntelligenceSection(SectionStatus.Unavailable),
        recommendationSection = IntelligenceSection(SectionStatus.Unavailable),
        relatedContentSection = IntelligenceSection(SectionStatus.Unavailable),
        digestSection = IntelligenceSection(SectionStatus.Unavailable),
        localState = null,
        sectionStatus = mapOf(
            "aicard" to SectionStatus.Unavailable,
            "graph" to SectionStatus.Unavailable,
            "recommendations" to SectionStatus.Unavailable,
            "relatedcontent" to SectionStatus.Unavailable,
            "digest" to SectionStatus.Unavailable,
        ),
        warnings = emptyList(),
        learningRoutesAvailable = false,
        legacyFallback = LegacyDetailFallback.All,
    )

private fun V3ContentPageDto.toPage(ref: ContentIntelligenceRef): ContentIntelligencePage {
    val statusMap = sectionStatus.associate { it.section.lowercase() to it.status }
    val aiSection = aiCard.toSection(
        statusMap = statusMap,
        sectionName = "aiCard",
        derive = { card ->
            when {
                card == null -> SectionStatus.Missing
                card.hasDisplayableContent -> SectionStatus.Ready
                else -> SectionStatus.Empty
            }
        },
    )
    val graphSection = graph.toSection(
        statusMap = statusMap,
        sectionName = "graph",
        derive = { graph ->
            when {
                graph == null -> SectionStatus.Missing
                graph.nodes.isEmpty() -> SectionStatus.Empty
                else -> SectionStatus.Ready
            }
        },
    )
    val recommendationSection = recommendations.toListSection(
        statusMap = statusMap,
        sectionName = "recommendations",
    )
    val relatedContentSection = relatedContent.toListSection(
        statusMap = statusMap,
        sectionName = "relatedContent",
    )
    val digestSection = digest.toSection(
        statusMap = statusMap,
        sectionName = "digest",
        derive = { digest ->
            when {
                digest == null -> SectionStatus.Missing
                else -> SectionStatus.Ready
            }
        },
    )

    val hasV3Recommendations = recommendationSection.status == SectionStatus.Ready ||
        relatedContentSection.status == SectionStatus.Ready
    val hasCompleteV3Context = graphSection.status == SectionStatus.Ready &&
        recommendationSection.status == SectionStatus.Ready &&
        relatedContentSection.status == SectionStatus.Ready

    return ContentIntelligencePage(
        ref = ref,
        pageType = ref.type,
        aiSection = aiSection,
        graphSection = graphSection,
        recommendationSection = recommendationSection,
        relatedContentSection = relatedContentSection,
        digestSection = digestSection,
        localState = localState,
        sectionStatus = statusMap,
        warnings = warnings,
        learningRoutesAvailable = ref.type in LEARNABLE_CONTENT_TYPES,
        detailDigest = digestSection.data?.toDetailDigest(ref),
        detailRecommendations = if (hasV3Recommendations) {
            (recommendationSection.data.orEmpty() + relatedContentSection.data.orEmpty())
                .distinctBy { it.type to it.id }
                .toDetailRecommendations(ref)
        } else {
            null
        },
        detailContext = if (hasCompleteV3Context) {
            DetailContextDto(
                related = relatedContentSection.data.orEmpty().map { it.toRelatedSummary() },
                graph = graphSection.data?.toDetailGraph(),
                recommendations = recommendationSection.data.orEmpty().map { it.toRecommendation() },
            )
        } else {
            null
        },
        legacyFallback = LegacyDetailFallback(
            loadDigest = digestSection.status != SectionStatus.Ready,
            loadBlendedRecommendations = !hasV3Recommendations,
            loadContext = !hasCompleteV3Context,
        ),
    )
}

private fun ContentDigestSectionDto.toDetailDigest(ref: ContentIntelligenceRef): ContentDigestDto =
    ContentDigestDto(
        type = ref.type.wireName,
        id = ref.id,
        quickRead = summary,
        highlights = highlights,
        keyFacts = keyFacts.map { DigestFactDto(value = it) },
        keywords = keywords,
    )

private fun List<ContentRefDto>.toDetailRecommendations(
    ref: ContentIntelligenceRef,
): BlendedRecommendationResponseDto = BlendedRecommendationResponseDto(
    items = map { item ->
        BlendedRecommendationItemDto(
            id = item.id,
            type = item.type.wireName,
            title = item.title.orEmpty(),
            subtitle = item.subtitle,
            category = item.category,
            region = item.region,
            coverImage = item.coverImageUrl?.let { MediaAssetDto(displayUrl = it) },
        )
    },
    query = BlendedRecommendationQueryDto(type = ref.type.wireName, id = ref.id),
)

private fun ContentRefDto.toRelatedSummary(): RelatedSummaryDto = RelatedSummaryDto(
    id = id,
    type = type.wireName,
    title = title,
    category = category,
    kind = kind,
    region = region,
)

private fun ContentRefDto.toRecommendation(): RecommendationDto = RecommendationDto(
    id = id,
    type = type.wireName,
    title = title,
    subtitle = subtitle,
    category = category,
    region = region,
    coverImage = coverImageUrl?.let { MediaAssetDto(displayUrl = it) },
)

private fun GraphNeighborsDto.toDetailGraph(): GraphDto = GraphDto(
    nodes = nodes.map { it.toDetailGraphNode() },
    edges = edges.map { it.toDetailGraphEdge() },
)

private fun GraphNodeDto.toDetailGraphNode() =
    com.duckylife.heritage.modern.core.network.dto.GraphNodeDto(
        id = id ?: nodeKey,
        type = type.wireName,
        title = title,
        subtitle = subtitle,
        category = category,
        region = region,
        coverImage = coverImageUrl?.let { MediaAssetDto(displayUrl = it) },
    )

private fun GraphEdgeDto.toDetailGraphEdge() =
    com.duckylife.heritage.modern.core.network.dto.GraphEdgeDto(
        fromId = from,
        toId = to,
        label = label,
        relationType = type.wireName,
        reason = reason,
        source = source?.wireName,
        weight = weight ?: 0.0,
    )

private val AiCardDto.hasDisplayableContent: Boolean
    get() = !summary.isNullOrBlank() ||
        !shortSummary.isNullOrBlank() ||
        highlights.isNotEmpty() ||
        keywords.isNotEmpty() ||
        entities.isNotEmpty()

private val LEARNABLE_CONTENT_TYPES = setOf(
    SearchResultType.Article,
    SearchResultType.DirectoryItem,
    SearchResultType.Inheritor,
)

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
