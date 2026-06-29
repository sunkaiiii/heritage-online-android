package com.duckylife.heritage.modern.core.network.dto.advanced

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement

@Serializable
data class SpacetimeOverviewDto(
    val filters: SpacetimeFilterDto? = null,
    val metrics: SpacetimeMetricsDto? = null,
    val topRegions: List<NamedCountDto> = emptyList(),
    val topCategories: List<NamedCountDto> = emptyList(),
    val yearTimeline: List<YearCountDto> = emptyList(),
    val generatedAt: String? = null,
)

@Serializable
data class SpacetimeFilterDto(
    val fromYear: Int? = null,
    val toYear: Int? = null,
    val region: String? = null,
    val category: String? = null,
    val kind: String? = null,
    val targetType: String? = null,
)

@Serializable(with = SpacetimeMetricsDtoSerializer::class)
data class SpacetimeMetricsDto(
    val total: Int = 0,
    val articleCount: Int = 0,
    val directoryItemCount: Int = 0,
    val inheritorCount: Int = 0,
)

/**
 * `/api/spacetime/overview` 的 metrics 在早期契约中是对象，当前后端会返回按类型拆分的数组：
 * article / directoryItem / inheritor / total。这里同时支持两种格式，避免整个时空页失败。
 */
object SpacetimeMetricsDtoSerializer : KSerializer<SpacetimeMetricsDto> {
    override val descriptor: SerialDescriptor = SpacetimeMetricsDtoSurrogate.serializer().descriptor

    override fun serialize(encoder: Encoder, value: SpacetimeMetricsDto) {
        val surrogate = SpacetimeMetricsDtoSurrogate(
            total = value.total,
            articleCount = value.articleCount,
            directoryItemCount = value.directoryItemCount,
            inheritorCount = value.inheritorCount,
        )
        if (encoder is JsonEncoder) {
            encoder.encodeJsonElement(encoder.json.encodeToJsonElement(surrogate))
        } else {
            encoder.encodeSerializableValue(SpacetimeMetricsDtoSurrogate.serializer(), surrogate)
        }
    }

    override fun deserialize(decoder: Decoder): SpacetimeMetricsDto {
        if (decoder !is JsonDecoder) {
            return decoder.decodeSerializableValue(SpacetimeMetricsDtoSurrogate.serializer()).toDto()
        }
        return when (val element: JsonElement = decoder.decodeJsonElement()) {
            is JsonArray -> decoder.json.decodeFromJsonElement(
                ListSerializer(SpacetimeMetricsDtoSurrogate.serializer()),
                element,
            ).toMetricsDto()
            is JsonObject -> decoder.json.decodeFromJsonElement(
                SpacetimeMetricsDtoSurrogate.serializer(),
                element,
            ).toDto()
            else -> SpacetimeMetricsDto()
        }
    }
}

@Serializable
private data class SpacetimeMetricsDtoSurrogate(
    val key: String? = null,
    val label: String? = null,
    val count: Int = 0,
    val total: Int = 0,
    val articleCount: Int = 0,
    val directoryItemCount: Int = 0,
    val inheritorCount: Int = 0,
)

private fun SpacetimeMetricsDtoSurrogate.toDto(): SpacetimeMetricsDto {
    val resolvedTotal = total.takeIf { it != 0 } ?: count
    return SpacetimeMetricsDto(
        total = resolvedTotal,
        articleCount = articleCount,
        directoryItemCount = directoryItemCount,
        inheritorCount = inheritorCount,
    )
}

private fun List<SpacetimeMetricsDtoSurrogate>.toMetricsDto(): SpacetimeMetricsDto {
    firstOrNull { it.key.equals("total", ignoreCase = true) }?.let { return it.toDto() }
    return SpacetimeMetricsDto(
        total = sumOf { it.total.takeIf { total -> total != 0 } ?: it.count },
        articleCount = sumOf { it.articleCount },
        directoryItemCount = sumOf { it.directoryItemCount },
        inheritorCount = sumOf { it.inheritorCount },
    )
}

@Serializable
data class NamedCountDto(
    val key: String? = null,
    val label: String? = null,
    val count: Int = 0,
    val total: Int = 0,
    val regionKey: String? = null,
    val regionLabel: String? = null,
    val categoryKey: String? = null,
    val categoryLabel: String? = null,
    val kindKey: String? = null,
    val kindLabel: String? = null,
    val targetType: String? = null,
    val targetTypeLabel: String? = null,
    val articleCount: Int = 0,
    val directoryItemCount: Int = 0,
    val inheritorCount: Int = 0,
)

@Serializable
data class YearCountDto(
    val year: Int,
    val count: Int = 0,
    val total: Int = 0,
    val articleCount: Int = 0,
    val directoryItemCount: Int = 0,
    val inheritorCount: Int = 0,
)

@Serializable
data class SpacetimeHeatmapDto(
    val x: SpacetimeDimension = SpacetimeDimension.Unknown,
    val y: SpacetimeDimension = SpacetimeDimension.Unknown,
    val targetType: String? = null,
    val cells: List<SpacetimeHeatmapCellDto> = emptyList(),
    val generatedAt: String? = null,
)

@Serializable
data class SpacetimeHeatmapCellDto(
    val xKey: String,
    val xLabel: String? = null,
    val yKey: String,
    val yLabel: String? = null,
    val articleCount: Int = 0,
    val directoryItemCount: Int = 0,
    val inheritorCount: Int = 0,
    val total: Int = 0,
    val intensity: Double = 0.0,
)

@Serializable
data class SpacetimeTimelineDto(
    val key: String? = null,
    val label: String? = null,
    val buckets: List<YearCountDto> = emptyList(),
    val generatedAt: String? = null,
)

@Serializable
data class SpacetimeRegionMapDto(
    val year: Int,
    val regions: List<NamedCountDto> = emptyList(),
    val generatedAt: String? = null,
)

@Serializable
data class AnalyticsFacetsDto(
    val dimensions: List<String> = emptyList(),
    val regions: List<AnalyticsFacetBucketDto> = emptyList(),
    val categories: List<AnalyticsFacetBucketDto> = emptyList(),
    val years: List<AnalyticsFacetBucketDto> = emptyList(),
    val kinds: List<AnalyticsFacetBucketDto> = emptyList(),
    val targetTypes: List<AnalyticsFacetBucketDto> = emptyList(),
    val generatedAt: String? = null,
)

@Serializable
data class AnalyticsFacetBucketDto(
    val key: String,
    val label: String? = null,
    val count: Int = 0,
    val articleCount: Int = 0,
    val directoryItemCount: Int = 0,
    val inheritorCount: Int = 0,
)

@Serializable
data class AnalyticsBreakdownDto(
    val groupBy: AnalyticsDimension = AnalyticsDimension.Unknown,
    val targetType: String? = null,
    val filters: AnalyticsFilterSnapshotDto? = null,
    val buckets: List<AnalyticsBreakdownBucketDto> = emptyList(),
    val total: Int = 0,
    val generatedAt: String? = null,
)

@Serializable
data class AnalyticsBreakdownBucketDto(
    val key: String,
    val label: String? = null,
    val articleCount: Int = 0,
    val directoryItemCount: Int = 0,
    val inheritorCount: Int = 0,
    val total: Int = 0,
)

@Serializable
data class AnalyticsFilterSnapshotDto(
    val region: String? = null,
    val category: String? = null,
    val year: Int? = null,
    val kind: String? = null,
    val targetType: String? = null,
    val hasImage: Boolean? = null,
    val hasAiResult: Boolean? = null,
)

@Serializable
data class AnalyticsCrosstabDto(
    val x: AnalyticsDimension = AnalyticsDimension.Unknown,
    val y: AnalyticsDimension = AnalyticsDimension.Unknown,
    val targetType: String? = null,
    val cells: List<AnalyticsCrosstabCellDto> = emptyList(),
    val xBuckets: List<String> = emptyList(),
    val yBuckets: List<String> = emptyList(),
    val total: Int = 0,
    val generatedAt: String? = null,
)

@Serializable
data class AnalyticsCrosstabCellDto(
    val xKey: String,
    val xLabel: String? = null,
    val yKey: String,
    val yLabel: String? = null,
    val articleCount: Int = 0,
    val directoryItemCount: Int = 0,
    val inheritorCount: Int = 0,
    val total: Int = 0,
)

@Serializable
data class AnalyticsCompareDto(
    val dimension: AnalyticsDimension = AnalyticsDimension.Unknown,
    val metric: RankingMetric = RankingMetric.Total,
    val items: List<AnalyticsCompareItemDto> = emptyList(),
    val winnerKey: String? = null,
    val generatedAt: String? = null,
)

@Serializable
data class AnalyticsCompareItemDto(
    val key: String,
    val label: String? = null,
    val value: Double = 0.0,
    val articleCount: Int = 0,
    val directoryItemCount: Int = 0,
    val inheritorCount: Int = 0,
    val total: Int = 0,
)

@Serializable
data class AnalyticsOutliersDto(
    val dimension: AnalyticsDimension = AnalyticsDimension.Unknown,
    val key: String,
    val label: String? = null,
    val metric: RankingMetric = RankingMetric.Total,
    val value: Double = 0.0,
    val average: Double = 0.0,
    val ratioToAverage: Double = 0.0,
    val reason: String? = null,
)

@Serializable
data class RankingDefinitionDto(
    val rankingId: String,
    val title: String? = null,
    val description: String? = null,
    val metric: String? = null,
    val targetType: String? = null,
    val refreshHint: String? = null,
)

@Serializable
data class RankingDetailDto(
    val rankingId: String,
    val title: String? = null,
    val description: String? = null,
    val metric: String? = null,
    val items: List<RankingItemDto> = emptyList(),
    val generatedAt: String? = null,
)

@Serializable
data class RankingItemDto(
    val rank: Int = 0,
    val targetType: String? = null,
    val targetId: String? = null,
    val title: String? = null,
    val subtitle: String? = null,
    val score: Double = 0.0,
    val metrics: List<RankingMetricDto> = emptyList(),
    val reasons: List<String> = emptyList(),
    val content: ContentRefDto? = null,
)

@Serializable
data class RankingMetricDto(
    val key: String,
    val label: String? = null,
    val value: Double = 0.0,
    val weight: Double? = null,
)
