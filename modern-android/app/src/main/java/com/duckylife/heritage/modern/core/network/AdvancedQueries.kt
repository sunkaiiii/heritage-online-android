package com.duckylife.heritage.modern.core.network

import com.duckylife.heritage.modern.core.network.dto.DirectoryItemKind
import com.duckylife.heritage.modern.core.network.dto.SearchResultType
import com.duckylife.heritage.modern.core.network.dto.advanced.AnalyticsDimension
import com.duckylife.heritage.modern.core.network.dto.advanced.ContentTargetType
import com.duckylife.heritage.modern.core.network.dto.advanced.ExportFormat
import com.duckylife.heritage.modern.core.network.dto.advanced.ExportScopeType
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphEvidenceSource
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphNodeType
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphRelationType
import com.duckylife.heritage.modern.core.network.dto.advanced.JourneyStrategy
import com.duckylife.heritage.modern.core.network.dto.advanced.LearningRouteDifficulty
import com.duckylife.heritage.modern.core.network.dto.advanced.LearningRouteSeedType
import com.duckylife.heritage.modern.core.network.dto.advanced.LocalUserTargetType
import com.duckylife.heritage.modern.core.network.dto.advanced.RankingMetric
import com.duckylife.heritage.modern.core.network.dto.advanced.SpacetimeDimension
import com.duckylife.heritage.modern.core.network.dto.advanced.TrailStrategy

/**
 * 本地用户相关查询参数。
 */
data class LocalUserFavoritesQuery(
    val targetType: LocalUserTargetType? = null,
    val page: Int = 1,
    val pageSize: Int = 20,
) {
    init {
        require(page > 0) { "page must be > 0" }
        require(pageSize in 1..100) { "pageSize must be in 1..100" }
    }
}

data class LocalUserHistoryQuery(
    val targetType: LocalUserTargetType? = null,
    val page: Int = 1,
    val pageSize: Int = 20,
) {
    init {
        require(page > 0) { "page must be > 0" }
        require(pageSize in 1..100) { "pageSize must be in 1..100" }
    }
}

data class LearningRoutesListQuery(
    val difficulty: LearningRouteDifficulty = LearningRouteDifficulty.All,
    val limit: Int = 20,
) {
    init {
        require(limit in 1..50) { "limit must be in 1..50" }
    }
}

data class LearningRouteDetailQuery(
    val routeId: String,
    val limit: Int = 10,
    val includeAi: Boolean = true,
) {
    init {
        require(routeId.isNotBlank()) { "routeId must not be blank" }
        require(limit in 1..20) { "limit must be in 1..20" }
    }
}

data class LearningRouteBuildQuery(
    val seedType: LearningRouteSeedType,
    val seedKey: String,
    val difficulty: LearningRouteDifficulty = LearningRouteDifficulty.Beginner,
    val limit: Int = 8,
    val includeArticles: Boolean = true,
    val includeDirectoryItems: Boolean = true,
    val includeInheritors: Boolean = true,
) {
    init {
        require(seedType != LearningRouteSeedType.Unknown) { "seedType must be specified" }
        require(seedKey.isNotBlank()) { "seedKey must not be blank" }
        require(difficulty != LearningRouteDifficulty.All && difficulty != LearningRouteDifficulty.Unknown) {
            "difficulty must be beginner/intermediate/deep"
        }
        require(limit in 1..20) { "limit must be in 1..20" }
    }
}

data class LearningRouteNextQuery(
    val routeId: String,
    val completedStepIds: List<String> = emptyList(),
    val profileId: String? = null,
) {
    init {
        require(routeId.isNotBlank()) { "routeId must not be blank" }
    }
}

/**
 * V3 AI 产品化查询参数。
 */
data class V3ContentPageQuery(
    val contentType: SearchResultType,
    val id: String,
    val profileId: String? = null,
    val includeAi: Boolean = true,
    val includeGraph: Boolean = true,
    val includeRecommendations: Boolean = true,
    val includeLocalState: Boolean = true,
    val includeDigest: Boolean = true,
    val includeExportHints: Boolean = false,
    val recommendationLimit: Int = 8,
    val neighborLimit: Int = 12,
) {
    init {
        require(id.isNotBlank()) { "id must not be blank" }
        require(recommendationLimit in 1..20) { "recommendationLimit must be in 1..20" }
        require(neighborLimit in 1..30) { "neighborLimit must be in 1..30" }
        if (includeLocalState) {
            require(!profileId.isNullOrBlank()) { "profileId is required when includeLocalState=true" }
        }
    }
}

data class ContentIntelligenceQuery(
    val contentType: SearchResultType,
    val id: String,
    val includeAi: Boolean = true,
    val includeGraph: Boolean = true,
    val includeRecommendations: Boolean = true,
    val recommendationLimit: Int = 6,
    val neighborLimit: Int = 12,
) {
    init {
        require(id.isNotBlank()) { "id must not be blank" }
        require(recommendationLimit in 1..20) { "recommendationLimit must be in 1..20" }
        require(neighborLimit in 1..30) { "neighborLimit must be in 1..30" }
    }
}

data class IntelligentSearchQuery(
    val keywords: String,
    val types: Set<SearchResultType> = emptySet(),
    val page: Int = 1,
    val pageSize: Int = 20,
    val region: String? = null,
    val category: String? = null,
    val year: Int? = null,
    val kind: DirectoryItemKind? = null,
    val includeAi: Boolean = true,
    val includeGraph: Boolean = false,
    val includeHighlights: Boolean = true,
    val minScore: Int = 0,
) {
    init {
        require(keywords.isNotBlank()) { "keywords must not be blank" }
        require(page > 0) { "page must be > 0" }
        require(pageSize in 1..100) { "pageSize must be in 1..100" }
        require(minScore >= 0) { "minScore must be >= 0" }
    }
}

/**
 * 知识图谱查询参数。
 */
data class KnowledgeGraphNeighborsQuery(
    val contentType: SearchResultType,
    val id: String,
    val limit: Int = 12,
    val relationType: GraphRelationType? = null,
    val source: GraphEvidenceSource? = null,
    val includeTopics: Boolean = false,
) {
    init {
        require(id.isNotBlank()) { "id must not be blank" }
        require(limit in 1..100) { "limit must be in 1..100" }
    }
}

data class KnowledgeGraphSimilarQuery(
    val contentType: SearchResultType,
    val id: String,
    val limit: Int = 12,
    val includeTopics: Boolean = true,
) {
    init {
        require(id.isNotBlank()) { "id must not be blank" }
        require(limit in 1..100) { "limit must be in 1..100" }
    }
}

data class KnowledgeGraphExploreQuery(
    val contentType: SearchResultType,
    val id: String,
    val depth: Int = 2,
    val limit: Int = 50,
    val includeTopics: Boolean = false,
) {
    init {
        require(id.isNotBlank()) { "id must not be blank" }
        require(depth in 1..2) { "depth must be 1 or 2" }
        require(limit in 1..100) { "limit must be in 1..100" }
    }
}

data class KnowledgeGraphEvidenceQuery(
    val contentType: SearchResultType,
    val id: String,
    val includeAiInferred: Boolean = false,
    val limit: Int = 20,
) {
    init {
        require(id.isNotBlank()) { "id must not be blank" }
        require(limit in 1..50) { "limit must be in 1..50" }
    }
}

data class KnowledgeGraphAiInferredQuery(
    val contentType: SearchResultType,
    val id: String,
    val entityType: String? = null,
    val minConfidence: Double = 0.0,
    val includeStale: Boolean = false,
    val limit: Int = 100,
) {
    init {
        require(id.isNotBlank()) { "id must not be blank" }
        require(minConfidence in 0.0..1.0) { "minConfidence must be in 0..1" }
        require(limit in 1..100) { "limit must be in 1..100" }
    }
}

data class KnowledgeGraphBridgeQuery(
    val fromType: SearchResultType,
    val fromId: String,
    val toType: GraphNodeType,
    val toId: String,
    val limit: Int = 10,
) {
    init {
        require(fromId.isNotBlank()) { "fromId must not be blank" }
        require(toId.isNotBlank()) { "toId must not be blank" }
        require(limit in 1..100) { "limit must be in 1..100" }
    }
}

data class KnowledgeGraphPathExplainQuery(
    val fromType: SearchResultType,
    val fromId: String,
    val toType: GraphNodeType,
    val toId: String,
    val maxDepth: Int = 3,
    val includeAiInferred: Boolean = false,
) {
    init {
        require(fromId.isNotBlank()) { "fromId must not be blank" }
        require(toId.isNotBlank()) { "toId must not be blank" }
        require(maxDepth in 1..5) { "maxDepth must be in 1..5" }
    }
}

data class KnowledgeGraphCommunitiesQuery(
    val limit: Int = 12,
    val minSize: Int = 3,
) {
    init {
        require(limit in 1..100) { "limit must be in 1..100" }
        require(minSize in 2..500) { "minSize must be in 2..500" }
    }
}

data class TopicGraphMapQuery(
    val topicType: GraphNodeType,
    val topicKey: String,
    val limit: Int = 50,
) {
    init {
        require(topicType != GraphNodeType.Unknown) { "topicType must be specified" }
        require(topicKey.isNotBlank()) { "topicKey must not be blank" }
        require(limit in 1..100) { "limit must be in 1..100" }
    }
}

data class GraphTrailRandomQuery(
    val strategy: TrailStrategy = TrailStrategy.Mixed,
    val type: SearchResultType? = null,
    val limit: Int = 6,
) {
    init {
        require(limit in 3..10) { "limit must be in 3..10" }
    }
}

data class GraphTrailFromContentQuery(
    val contentType: SearchResultType,
    val id: String,
    val strategy: TrailStrategy = TrailStrategy.Mixed,
    val limit: Int = 6,
    val includeTopics: Boolean = true,
) {
    init {
        require(id.isNotBlank()) { "id must not be blank" }
        require(limit in 3..10) { "limit must be in 3..10" }
    }
}

data class GraphTrailFromTopicQuery(
    val topicType: GraphNodeType,
    val topicKey: String,
    val strategy: TrailStrategy = TrailStrategy.Representative,
    val limit: Int = 6,
) {
    init {
        require(topicType != GraphNodeType.Unknown) { "topicType must be specified" }
        require(topicKey.isNotBlank()) { "topicKey must not be blank" }
        require(limit in 3..10) { "limit must be in 3..10" }
    }
}

/**
 * 数据探索查询参数。
 */
data class SpacetimeOverviewQuery(
    val fromYear: Int? = null,
    val toYear: Int? = null,
    val region: String? = null,
    val category: String? = null,
    val kind: String? = null,
    val targetType: ContentTargetType? = null,
    val limit: Int = 20,
) {
    init {
        if (fromYear != null && toYear != null) {
            require(fromYear <= toYear) { "fromYear must be <= toYear" }
        }
        require(limit in 1..100) { "limit must be in 1..100" }
    }
}

data class SpacetimeHeatmapQuery(
    val x: SpacetimeDimension,
    val y: SpacetimeDimension,
    val targetType: ContentTargetType? = null,
    val fromYear: Int? = null,
    val toYear: Int? = null,
    val limit: Int = 50,
) {
    init {
        require(x != SpacetimeDimension.Unknown && y != SpacetimeDimension.Unknown) {
            "x and y dimensions must be specified"
        }
        require(x != y) { "x and y must be different" }
        if (fromYear != null && toYear != null) {
            require(fromYear <= toYear) { "fromYear must be <= toYear" }
        }
        require(limit in 1..200) { "limit must be in 1..200" }
    }
}

data class SpacetimeRegionTimelineQuery(
    val region: String,
) {
    init {
        require(region.isNotBlank()) { "region must not be blank" }
    }
}

data class SpacetimeYearMapQuery(
    val year: Int,
) {
    init {
        require(year in 1..3000) { "year must be in 1..3000" }
    }
}

data class SpacetimeCategoryTimelineQuery(
    val category: String,
) {
    init {
        require(category.isNotBlank()) { "category must not be blank" }
    }
}

data class AnalyticsFilters(
    val targetType: ContentTargetType? = null,
    val region: String? = null,
    val category: String? = null,
    val year: Int? = null,
    val kind: String? = null,
    val hasImage: Boolean? = null,
    val hasAiResult: Boolean? = null,
)

data class AnalyticsFacetsQuery(
    val filters: AnalyticsFilters = AnalyticsFilters(),
)

data class AnalyticsBreakdownQuery(
    val groupBy: AnalyticsDimension,
    val limit: Int = 50,
    val filters: AnalyticsFilters = AnalyticsFilters(),
) {
    init {
        require(groupBy != AnalyticsDimension.Unknown) { "groupBy must be specified" }
        require(limit in 1..100) { "limit must be in 1..100" }
    }
}

data class AnalyticsCrosstabQuery(
    val x: AnalyticsDimension,
    val y: AnalyticsDimension,
    val limit: Int = 100,
    val filters: AnalyticsFilters = AnalyticsFilters(),
) {
    init {
        require(x != AnalyticsDimension.Unknown && y != AnalyticsDimension.Unknown) {
            "x and y dimensions must be specified"
        }
        require(x != y) { "x and y must be different" }
        require(limit in 1..200) { "limit must be in 1..200" }
    }
}

data class AnalyticsCompareQuery(
    val dimension: AnalyticsDimension,
    val keys: List<String>,
    val metric: RankingMetric = RankingMetric.Total,
    val filters: AnalyticsFilters = AnalyticsFilters(),
) {
    init {
        require(dimension != AnalyticsDimension.Unknown) { "dimension must be specified" }
        require(keys.size in 1..10) { "keys must be 1..10" }
    }
}

data class AnalyticsOutliersQuery(
    val dimension: AnalyticsDimension,
    val metric: RankingMetric = RankingMetric.Total,
    val limit: Int = 20,
    val filters: AnalyticsFilters = AnalyticsFilters(),
) {
    init {
        require(dimension != AnalyticsDimension.Unknown) { "dimension must be specified" }
        require(limit in 1..100) { "limit must be in 1..100" }
    }
}

/**
 * 排行榜查询参数。
 */
data class RankingDetailQuery(
    val rankingId: String,
    val targetType: ContentTargetType? = null,
    val region: String? = null,
    val category: String? = null,
    val year: Int? = null,
    val limit: Int = 20,
) {
    init {
        require(rankingId.isNotBlank()) { "rankingId must not be blank" }
        require(limit in 1..100) { "limit must be in 1..100" }
    }
}

data class RankingContentQuery(
    val metric: RankingMetric,
    val targetType: ContentTargetType? = null,
    val region: String? = null,
    val category: String? = null,
    val year: Int? = null,
    val limit: Int = 20,
) {
    init {
        require(metric != RankingMetric.Unknown) { "metric must be specified" }
        require(limit in 1..100) { "limit must be in 1..100" }
    }
}

/**
 * 研究资料查询参数（只读）。
 */

/** 允许的 artifact 文件名：禁止控制字符、路径分隔符与目录遍历，长度 1–128。 */
internal val ArtifactNameRegex = Regex("^[^\\p{Cntrl}/\\\\]{1,128}$")

internal fun isAllowedArtifactName(name: String): Boolean =
    name.isNotBlank() && name != "." && name != ".." && ArtifactNameRegex.matches(name)

data class ResearchPackageDetailQuery(
    val packageId: String,
) {
    init {
        require(packageId.isNotBlank()) { "packageId must not be blank" }
    }
}

data class ResearchArtifactQuery(
    val packageId: String,
    val artifactName: String,
) {
    init {
        require(packageId.isNotBlank()) { "packageId must not be blank" }
        require(isAllowedArtifactName(artifactName)) {
            "artifactName must be a safe file name (no path separators, control chars, or traversal, 1-128 chars)"
        }
    }
}

data class ResearchReportDetailQuery(
    val reportId: String,
) {
    init {
        require(reportId.isNotBlank()) { "reportId must not be blank" }
    }
}

data class ResearchReportByPackageQuery(
    val packageId: String,
) {
    init {
        require(packageId.isNotBlank()) { "packageId must not be blank" }
    }
}

/**
 * 内容导出查询参数。
 */
data class ExportPreviewQuery(
    val request: com.duckylife.heritage.modern.core.network.dto.advanced.ExportRequestDto,
)

data class ExportContentQuery(
    val request: com.duckylife.heritage.modern.core.network.dto.advanced.ExportRequestDto,
)
