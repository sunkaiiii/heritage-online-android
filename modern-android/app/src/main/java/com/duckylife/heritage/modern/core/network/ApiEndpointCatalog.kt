package com.duckylife.heritage.modern.core.network

/**
 * API 端点目录。
 *
 * 本文件不是运行时路由表，也不拼接 URL；所有真实 URL 仍然只出现在 [KtorHeritageApiClient] 中。
 * 它的作用是用 KDoc 把本计划新增的 endpoint、DTO 文件、Repository 和 UI feature 对齐，
 * 防止日后同一路径被多个 feature 随意复制。
 *
 * 后续新增只读后端能力时，应首先在此文件中补充条目，说明所属分组、HTTP 方法、
 * 身份方式、主要 DTO 和对应 UI 入口，然后再实现 client/repository/screen。
 */
object ApiEndpointCatalog {

    /**
     * ## LocalUser
     *
     * 本地匿名用户档案、服务端收藏/历史/学习进度与个性化旅程。
     *
     * 身份方式：所有端点通过请求头 `X-Heritage-Profile-Id` 区分用户。
     * 客户端首次安装时生成 `android_<UUID>` 并持久化到 DataStore。
     *
     * | 方法 | 路径 | 主要 DTO | UI 入口 |
     * |---|---|---|---|
     * | GET | `/api/local-user/profile` | — | 我的页 Profile 概览 |
     * | GET | `/api/local-user/summary` | [LocalUserSummary][com.duckylife.heritage.modern.core.network.dto.advanced.LocalUserSummaryDto] | 我的页同步状态 |
     * | GET | `/api/local-user/favorites` | [LocalFavorite][com.duckylife.heritage.modern.core.network.dto.advanced.LocalFavoriteDto] | 我的 -> 收藏 |
     * | POST | `/api/local-user/favorites` | [FavoriteCreateRequest][com.duckylife.heritage.modern.core.network.dto.advanced.FavoriteCreateRequestDto] | 详情页收藏按钮 |
     * | DELETE | `/api/local-user/favorites/{targetType}/{targetId}` | — | 取消收藏 |
     * | GET | `/api/local-user/history` | [LocalHistory][com.duckylife.heritage.modern.core.network.dto.advanced.LocalHistoryDto] | 我的 -> 浏览 |
     * | POST | `/api/local-user/history` | [HistoryRecordRequest][com.duckylife.heritage.modern.core.network.dto.advanced.HistoryRecordRequestDto] | 详情页可见后记录 |
     * | DELETE | `/api/local-user/history` | Int（清除条数） | 浏览页清空确认 |
     * | GET | `/api/local-user/learning-progress` | [LocalLearningProgress][com.duckylife.heritage.modern.core.network.dto.advanced.LocalLearningProgressDto] | 我的 -> 学习 |
     * | PUT | `/api/local-user/learning-progress/{routeId}` | [LearningProgressUpdate][com.duckylife.heritage.modern.core.network.dto.advanced.LearningProgressUpdateDto] | 路线详情 step checkbox |
     * | GET | `/api/local-user/journeys` | [JourneyResponse][com.duckylife.heritage.modern.core.network.dto.advanced.JourneyResponseDto] | 我的 -> 旅程 |
     * | GET | `/api/local-user/journeys/signals` | [JourneySignals][com.duckylife.heritage.modern.core.network.dto.advanced.JourneySignalsDto] | 旅程页信号摘要 |
     */
    object LocalUser

    /**
     * ## AiProduct
     *
     * V3 智能详情页、AI 卡片、内容智能与智能搜索。
     *
     * 身份方式：V3 page 的 `includeLocalState=true` 通过 **query 参数** `profileId` 传入；
     * AI card / intelligence / intelligent search 使用 `X-Heritage-Profile-Id` header。
     *
     * | 方法 | 路径 | 主要 DTO | UI 入口 |
     * |---|---|---|---|
     * | GET | `/api/v3/pages/article/{id}` | [V3ContentPage][com.duckylife.heritage.modern.core.network.dto.advanced.V3ContentPageDto] | 文章详情增强层 |
     * | GET | `/api/v3/pages/directory-item/{id}` | [V3ContentPage][com.duckylife.heritage.modern.core.network.dto.advanced.V3ContentPageDto] | 名录详情增强层 |
     * | GET | `/api/v3/pages/inheritor/{id}` | [V3ContentPage][com.duckylife.heritage.modern.core.network.dto.advanced.V3ContentPageDto] | 传承人详情增强层 |
     * | GET | `/api/v3/content/{type}/{id}/intelligence` | [ContentIntelligence][com.duckylife.heritage.modern.core.network.dto.advanced.ContentIntelligenceDto] | 智能解读区 |
     * | GET | `/api/v3/articles/{id}/ai-card` | [AiCard][com.duckylife.heritage.modern.core.network.dto.advanced.AiCardDto] | 文章详情 AI 卡 |
     * | GET | `/api/v3/directory-items/{id}/ai-card` | [AiCard][com.duckylife.heritage.modern.core.network.dto.advanced.AiCardDto] | 名录详情 AI 卡 |
     * | GET | `/api/v3/inheritors/{id}/ai-card` | [AiCard][com.duckylife.heritage.modern.core.network.dto.advanced.AiCardDto] | 传承人详情 AI 卡 |
     * | GET | `/api/v3/search/intelligent` | [IntelligentSearchResponse][com.duckylife.heritage.modern.core.network.dto.advanced.IntelligentSearchResponseDto] | 搜索页“智能搜索”模式 |
     */
    object AiProduct

    /**
     * ## KnowledgeGraph
     *
     * 知识图谱只读展示：邻居、相似、探索、证据、AI 推断边、路径解释、主题地图与漫游。
     *
     * 身份方式：只读端点不需要 `X-Heritage-Profile-Id`，也不使用 Admin Key。
     *
     * | 方法 | 路径 | 主要 DTO | UI 入口 |
     * |---|---|---|---|
     * | GET | `/api/knowledge-graph/{type}/{id}/neighbors` | [GraphNeighbors][com.duckylife.heritage.modern.core.network.dto.advanced.GraphNeighborsDto] | 关系页“关联”tab |
     * | GET | `/api/knowledge-graph/{type}/{id}/similar` | [GraphSimilar][com.duckylife.heritage.modern.core.network.dto.advanced.GraphSimilarDto] | 关系页“相似”tab |
     * | GET | `/api/knowledge-graph/{type}/{id}/explore` | [GraphExplore][com.duckylife.heritage.modern.core.network.dto.advanced.GraphExploreDto] | 关系页“探索”tab |
     * | GET | `/api/knowledge-graph/{type}/{id}/evidence` | [GraphEvidence][com.duckylife.heritage.modern.core.network.dto.advanced.GraphEvidenceDto] | 关系页“证据”tab |
     * | GET | `/api/knowledge-graph/{type}/{id}/ai-inferred` | [AiInferredEdges][com.duckylife.heritage.modern.core.network.dto.advanced.AiInferredEdgesDto] | 证据 tab AI 推断开关 |
     * | GET | `/api/knowledge-graph/bridge` | [GraphBridge][com.duckylife.heritage.modern.core.network.dto.advanced.GraphBridgeDto] | 路径解释 bottom sheet |
     * | GET | `/api/knowledge-graph/path/explain` | [PathExplain][com.duckylife.heritage.modern.core.network.dto.advanced.PathExplainDto] | 路径解释 bottom sheet |
     * | GET | `/api/knowledge-graph/communities` | [GraphCommunities][com.duckylife.heritage.modern.core.network.dto.advanced.GraphCommunitiesDto] | 知识图谱首页 |
     * | GET | `/api/knowledge-graph/topics/{topicType}/{topicKey}/map` | [TopicGraphMap][com.duckylife.heritage.modern.core.network.dto.advanced.TopicGraphMapDto] | 主题地图页 |
     * | GET | `/api/knowledge-graph/trails/random` | [GraphTrail][com.duckylife.heritage.modern.core.network.dto.advanced.GraphTrailDto] | 随机漫游 |
     * | GET | `/api/knowledge-graph/trails/from/{type}/{id}` | [GraphTrail][com.duckylife.heritage.modern.core.network.dto.advanced.GraphTrailDto] | 从内容出发漫游 |
     * | GET | `/api/knowledge-graph/trails/topic/{topicType}/{topicKey}` | [GraphTrail][com.duckylife.heritage.modern.core.network.dto.advanced.GraphTrailDto] | 从主题出发漫游 |
     */
    object KnowledgeGraph

    /**
     * ## LearningRoutes
     *
     * 带进度与下一步的新学习路线。
     *
     * 身份方式：`/next` 通过 query 参数 `profileId` 读取；其余端点使用 `X-Heritage-Profile-Id` header。
     *
     * | 方法 | 路径 | 主要 DTO | UI 入口 |
     * |---|---|---|---|
     * | GET | `/api/learning-routes` | [LearningRouteSummary][com.duckylife.heritage.modern.core.network.dto.advanced.LearningRouteSummaryDto] | 学习路线首页 |
     * | GET | `/api/learning-routes/{routeId}` | [LearningRouteDetail][com.duckylife.heritage.modern.core.network.dto.advanced.LearningRouteDetailDto] | 路线详情 |
     * | GET | `/api/learning-routes/build` | [LearningRouteDetail][com.duckylife.heritage.modern.core.network.dto.advanced.LearningRouteDetailDto] | 详情页“从此开始学习” |
     * | GET | `/api/learning-routes/{routeId}/next` | [LearningRouteNext][com.duckylife.heritage.modern.core.network.dto.advanced.LearningRouteNextDto] | 路线详情“继续下一步” |
     */
    object LearningRoutes

    /**
     * ## DataExplore
     *
     * 时空探索、分析与排行榜。
     *
     * 身份方式：只读端点，不使用 profile header。
     *
     * | 方法 | 路径 | 主要 DTO | UI 入口 |
     * |---|---|---|---|
     * | GET | `/api/spacetime/overview` | [SpacetimeOverview][com.duckylife.heritage.modern.core.network.dto.advanced.SpacetimeOverviewDto] | 发现 -> 数据探索 |
     * | GET | `/api/spacetime/heatmap` | [SpacetimeHeatmap][com.duckylife.heritage.modern.core.network.dto.advanced.SpacetimeHeatmapDto] | 时空探索“分布热力” |
     * | GET | `/api/spacetime/regions/{region}/timeline` | [SpacetimeTimeline][com.duckylife.heritage.modern.core.network.dto.advanced.SpacetimeTimelineDto] | 时空探索子视图 |
     * | GET | `/api/spacetime/years/{year}/map` | [SpacetimeYearMap][com.duckylife.heritage.modern.core.network.dto.advanced.SpacetimeYearMapDto] | 时空探索子视图 |
     * | GET | `/api/spacetime/categories/{category}/timeline` | [SpacetimeTimeline][com.duckylife.heritage.modern.core.network.dto.advanced.SpacetimeTimelineDto] | 时空探索子视图 |
     * | GET | `/api/analytics/facets` | [AnalyticsFacets][com.duckylife.heritage.modern.core.network.dto.advanced.AnalyticsFacetsDto] | 数据探索“进一步分析” |
     * | GET | `/api/analytics/breakdown` | [AnalyticsBreakdown][com.duckylife.heritage.modern.core.network.dto.advanced.AnalyticsBreakdownDto] | 进一步分析 |
     * | GET | `/api/analytics/crosstab` | [AnalyticsCrosstab][com.duckylife.heritage.modern.core.network.dto.advanced.AnalyticsCrosstabDto] | 交叉分布 |
     * | GET | `/api/analytics/compare` | [AnalyticsCompare][com.duckylife.heritage.modern.core.network.dto.advanced.AnalyticsCompareDto] | 比较所选项 |
     * | GET | `/api/analytics/outliers` | [AnalyticsOutliers][com.duckylife.heritage.modern.core.network.dto.advanced.AnalyticsOutliersDto] | 值得留意 |
     * | GET | `/api/rankings` | [RankingDefinition][com.duckylife.heritage.modern.core.network.dto.advanced.RankingDefinitionDto] | 发现 -> 排行榜 |
     * | GET | `/api/rankings/{rankingId}` | [RankingDetail][com.duckylife.heritage.modern.core.network.dto.advanced.RankingDetailDto] | 排行榜详情 |
     * | GET | `/api/rankings/content` | [RankingDetail][com.duckylife.heritage.modern.core.network.dto.advanced.RankingDetailDto] | 按指标查看内容排行 |
     */
    object DataExplore

    /**
     * ## Research
     *
     * 已生成的研究资料包与研究报告，只读浏览。
     *
     * 身份方式：只读 GET；创建/重试/取消/删除等写入端点不进入普通 App。
     *
     * | 方法 | 路径 | 主要 DTO | UI 入口 |
     * |---|---|---|---|
     * | GET | `/api/research-packages` | [ResearchPackageListResult][com.duckylife.heritage.modern.core.network.dto.advanced.ResearchPackageListResultDto] | 我的 -> 资料 -> 资料包 |
     * | GET | `/api/research-packages/{packageId}` | [ResearchPackage][com.duckylife.heritage.modern.core.network.dto.advanced.ResearchPackageDto] | 资料包详情 |
     * | GET | `/api/research-packages/{packageId}/artifacts/{artifactName}` | ByteArray / 文本 | 资料包 artifact 查看/分享 |
     * | GET | `/api/research-reports` | [ResearchReportListResult][com.duckylife.heritage.modern.core.network.dto.advanced.ResearchReportListResultDto] | 我的 -> 资料 -> 研究报告 |
     * | GET | `/api/research-reports/{reportId}` | [ResearchReport][com.duckylife.heritage.modern.core.network.dto.advanced.ResearchReportDto] | 研究报告详情 |
     * | GET | `/api/research-packages/{packageId}/research-report` | [ResearchReport][com.duckylife.heritage.modern.core.network.dto.advanced.ResearchReportDto] | 资料包详情“查看报告” |
     */
    object Research

    /**
     * ## Export
     *
     * 内容导出，用户主动触发。
     *
     * 身份方式：单内容 `ids` scope 可不传 header；`favorites` scope 需要 `profileId`。
     *
     * | 方法 | 路径 | 主要 DTO | UI 入口 |
     * |---|---|---|---|
     * | GET | `/api/exports/templates` | [ExportTemplate][com.duckylife.heritage.modern.core.network.dto.advanced.ExportTemplateDto] | 详情页导出 bottom sheet |
     * | POST | `/api/exports/preview` | [ExportPreview][com.duckylife.heritage.modern.core.network.dto.advanced.ExportPreviewDto] | 导出预览 |
     * | POST | `/api/exports/content` | [ExportContent][com.duckylife.heritage.modern.core.network.dto.advanced.ExportContentResultDto] | 生成并分享 |
     */
    object Export
}
