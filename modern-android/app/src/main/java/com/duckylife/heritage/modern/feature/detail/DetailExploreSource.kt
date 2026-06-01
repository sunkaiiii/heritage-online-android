package com.duckylife.heritage.modern.feature.detail

import com.duckylife.heritage.modern.core.data.ReadingPathContentRef

/**
 * 详情页探索区块的来源类型。
 */
enum class DetailExploreSource(val wireName: String) {
    BlendedRecommendation("blendedRecommendation"),
    Related("related"),
    Recommendation("recommendation"),
    SemanticRecommendation("semanticRecommendation"),
    Collection("collection"),
    Graph("graph"),
    ExploreTopic("exploreTopic"),
}

/**
 * 详情页探索区块的点击事件，携带来源和目标信息。
 */
data class DetailExploreTargetClick(
    val target: DetailContextTarget,
    val source: DetailExploreSource,
    val title: String? = null,
    val subtitle: String? = null,
    val category: String? = null,
    val region: String? = null,
    val kind: String? = null,
    val sourceId: String? = null,
    val sourceUrl: String? = null,
    val imageUrl: String? = null,
) {
    fun toReadingPathContentRef(): ReadingPathContentRef =
        ReadingPathContentRef(
            type = when (target) {
                is DetailContextTarget.Article -> "article"
                is DetailContextTarget.DirectoryItem -> "directoryItem"
                is DetailContextTarget.Inheritor -> "inheritor"
                is DetailContextTarget.Collection -> "collection"
                is DetailContextTarget.Topic -> "topic"
            },
            id = when (target) {
                is DetailContextTarget.Article -> target.id
                is DetailContextTarget.DirectoryItem -> target.id
                is DetailContextTarget.Inheritor -> target.id
                is DetailContextTarget.Collection -> target.id
                is DetailContextTarget.Topic -> target.key
            },
            title = title ?: "",
            category = category,
            kind = kind,
            sourceId = sourceId,
            sourceUrl = sourceUrl,
            subtitle = subtitle,
            imageUrl = imageUrl,
        )
}
