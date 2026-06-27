package com.duckylife.heritage.modern.feature.rankings.model

/**
 * 排行榜筛选条件。
 */
data class RankingFilters(
    val targetType: String? = "all",
    val region: String? = null,
    val category: String? = null,
    val year: Int? = null,
    val limit: Int = 20,
) : java.io.Serializable

data class RankingDefinitionUiModel(
    val rankingId: String,
    val title: String,
    val description: String? = null,
    val metric: String? = null,
    val targetType: String? = null,
    val refreshHint: String? = null,
)

data class RankingDetailUiModel(
    val rankingId: String,
    val title: String,
    val description: String? = null,
    val metric: String? = null,
    val items: List<RankingItemUiModel> = emptyList(),
    val generatedAt: String? = null,
)

data class RankingItemUiModel(
    val rank: Int = 0,
    val targetType: String? = null,
    val targetId: String? = null,
    val title: String,
    val subtitle: String? = null,
    val score: Double = 0.0,
    val metrics: List<RankingMetricUiModel> = emptyList(),
    val reasons: List<String> = emptyList(),
    val contentId: String? = null,
    val contentType: String? = null,
)

data class RankingMetricUiModel(
    val key: String,
    val label: String? = null,
    val value: Double = 0.0,
    val weight: Double? = null,
)
