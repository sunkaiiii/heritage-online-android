package com.duckylife.heritage.modern.feature.detail.intelligence

import com.duckylife.heritage.modern.core.data.ContentIntelligencePage
import com.duckylife.heritage.modern.core.network.dto.BlendedRecommendationResponseDto
import com.duckylife.heritage.modern.core.network.dto.ContentDigestDto
import com.duckylife.heritage.modern.core.network.dto.DetailContextDto
import com.duckylife.heritage.modern.ui.error.ErrorKind

/**
 * 旧详情探索组件共用的状态片段。
 *
 * 文章、名录、传承人详情页的主体数据不同，但 digest/context/blended 三个增强区域
 * 使用同一套 V3 覆盖与旧接口回退规则。
 */
interface DetailIntelligenceEnhancementState<T : DetailIntelligenceEnhancementState<T>> {
    val contextLoading: Boolean
    val context: DetailContextDto?
    val contextErrorKind: ErrorKind?
    val digest: ContentDigestDto?
    val digestLoading: Boolean
    val digestErrorKind: ErrorKind?
    val blendedRecommendations: BlendedRecommendationResponseDto?
    val blendedLoading: Boolean

    fun copyWithDetailIntelligenceEnhancements(
        context: DetailContextDto?,
        contextLoading: Boolean,
        contextErrorKind: ErrorKind?,
        digest: ContentDigestDto?,
        digestLoading: Boolean,
        digestErrorKind: ErrorKind?,
        blendedRecommendations: BlendedRecommendationResponseDto?,
        blendedLoading: Boolean,
    ): T
}

internal fun <T : DetailIntelligenceEnhancementState<T>> ContentIntelligencePage.applyDetailEnhancements(
    updateState: (((T) -> T) -> Unit),
    loadDigest: (String?) -> Unit,
    loadBlendedRecommendations: (String?) -> Unit,
    loadContext: (String?) -> Unit,
) {
    updateState { state -> mergeIntoDetailEnhancementState(state) }
    loadMissingLegacyDetailEnhancements(
        loadDigest = loadDigest,
        loadBlendedRecommendations = loadBlendedRecommendations,
        loadContext = loadContext,
    )
}

internal fun loadAllLegacyDetailEnhancements(
    id: String,
    loadContext: (String?) -> Unit,
    loadDigest: (String?) -> Unit,
    loadBlendedRecommendations: (String?) -> Unit,
) {
    loadContext(id)
    loadDigest(id)
    loadBlendedRecommendations(id)
}

private fun ContentIntelligencePage.loadMissingLegacyDetailEnhancements(
    loadDigest: (String?) -> Unit,
    loadBlendedRecommendations: (String?) -> Unit,
    loadContext: (String?) -> Unit,
) {
    val fallback = legacyFallback
    if (fallback.loadDigest) loadDigest(ref.id)
    if (fallback.loadBlendedRecommendations) loadBlendedRecommendations(ref.id)
    if (fallback.loadContext) loadContext(ref.id)
}

private fun <T : DetailIntelligenceEnhancementState<T>> ContentIntelligencePage.mergeIntoDetailEnhancementState(
    state: T,
): T {
    val fallback = legacyFallback
    return state.copyWithDetailIntelligenceEnhancements(
        digest = detailDigest ?: state.digest,
        digestLoading = if (fallback.loadDigest) state.digestLoading else false,
        digestErrorKind = if (fallback.loadDigest) state.digestErrorKind else null,
        blendedRecommendations = detailRecommendations ?: state.blendedRecommendations,
        blendedLoading = if (fallback.loadBlendedRecommendations) state.blendedLoading else false,
        context = detailContext ?: state.context,
        contextLoading = if (fallback.loadContext) state.contextLoading else false,
        contextErrorKind = if (fallback.loadContext) state.contextErrorKind else null,
    )
}
