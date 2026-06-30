package com.duckylife.heritage.modern.core.data

import androidx.paging.PagingData
import com.duckylife.heritage.modern.core.database.HeritageDatabase
import com.duckylife.heritage.modern.core.database.mapper.toDto
import com.duckylife.heritage.modern.core.database.mapper.toEntity
import com.duckylife.heritage.modern.core.network.ArticleQuery
import com.duckylife.heritage.modern.core.network.DirectoryItemQuery
import com.duckylife.heritage.modern.core.network.HeritageApiClient
import com.duckylife.heritage.modern.core.network.InheritorQuery
import com.duckylife.heritage.modern.core.network.SearchV2Query
import com.duckylife.heritage.modern.core.network.TimelineV2Query
import com.duckylife.heritage.modern.core.network.BlendedRecommendationQuery
import com.duckylife.heritage.modern.core.network.DiscoveryDeepDiveQuery
import com.duckylife.heritage.modern.core.network.DiscoverySerendipityQuery
import com.duckylife.heritage.modern.core.network.TaxonomyRegionSort
import com.duckylife.heritage.modern.core.network.dto.ArticleCategory
import com.duckylife.heritage.modern.core.network.dto.ArticleDetailDto
import com.duckylife.heritage.modern.core.network.dto.ArticleSummaryDto
import com.duckylife.heritage.modern.core.network.dto.BlendedRecommendationResponseDto
import com.duckylife.heritage.modern.core.network.dto.CollectionDto
import com.duckylife.heritage.modern.core.network.dto.CompareResultDto
import com.duckylife.heritage.modern.core.network.dto.ContentDigestDto
import com.duckylife.heritage.modern.core.network.dto.DataStoryDto
import com.duckylife.heritage.modern.core.network.dto.DetailContextDto
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemDetailDto
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemKind
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemSummaryDto
import com.duckylife.heritage.modern.core.network.dto.DirectoryStatisticDimension
import com.duckylife.heritage.modern.core.network.dto.DirectoryStatisticDimensionDto
import com.duckylife.heritage.modern.core.network.dto.DirectoryStatisticsOverviewDto
import com.duckylife.heritage.modern.core.network.dto.DiscoveryDeepDiveDto
import com.duckylife.heritage.modern.core.network.dto.DiscoveryItemDto
import com.duckylife.heritage.modern.core.network.dto.DiscoveryTodayDto
import com.duckylife.heritage.modern.core.network.dto.DiscoveryTrendingDto
import com.duckylife.heritage.modern.core.network.dto.DiscoveryWeeklyDto
import com.duckylife.heritage.modern.core.network.dto.ExploreIndexDto
import com.duckylife.heritage.modern.core.network.dto.ExploreTopicInfoDto
import com.duckylife.heritage.modern.core.network.dto.ExploreTopicV2Dto
import com.duckylife.heritage.modern.core.network.dto.FeaturedCollectionDto
import com.duckylife.heritage.modern.core.network.dto.HomeBannerDto
import com.duckylife.heritage.modern.core.network.dto.HomeFeedDto
import com.duckylife.heritage.modern.core.network.dto.InheritorDetailDto
import com.duckylife.heritage.modern.core.network.dto.InheritorSummaryDto
import com.duckylife.heritage.modern.core.network.dto.LearningPathDetailDto
import com.duckylife.heritage.modern.core.network.dto.LearningPathDto
import com.duckylife.heritage.modern.core.network.dto.PagedResult
import com.duckylife.heritage.modern.core.network.dto.RegionAtlasDetailDto
import com.duckylife.heritage.modern.core.network.dto.RegionAtlasDto
import com.duckylife.heritage.modern.core.network.dto.SearchSuggestionDto
import com.duckylife.heritage.modern.core.network.dto.SearchV2ResponseDto
import com.duckylife.heritage.modern.core.network.dto.SearchResultType
import com.duckylife.heritage.modern.core.network.dto.TaxonomyCategoryDetailDto
import com.duckylife.heritage.modern.core.network.dto.TaxonomyIndexDto
import com.duckylife.heritage.modern.core.network.dto.TaxonomyKindDto
import com.duckylife.heritage.modern.core.network.dto.TaxonomyRegionDetailDto
import com.duckylife.heritage.modern.core.network.dto.TaxonomyTopicDto
import com.duckylife.heritage.modern.core.network.dto.TimelineV2ResponseDto
import com.duckylife.heritage.modern.core.network.dto.TimelineYearBucketDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

data class ArticleDetailLookup(
    val articleId: String? = null,
    val sourceId: String? = null,
    val sourceUrl: String? = null,
    val category: ArticleCategory = ArticleCategory.News,
)

data class DirectoryDetailLookup(
    val itemId: String? = null,
    val sourceId: String? = null,
    val kind: DirectoryItemKind = DirectoryItemKind.NationalProject,
)

data class InheritorDetailLookup(
    val inheritorId: String? = null,
    val sourceId: String? = null,
)

// Repository 同时提供一次性接口和 Room 支撑的缓存流。
// 列表走 Paging，详情页先观察缓存，再从后端刷新。
interface HeritageRepository {
    suspend fun homeBanners(): List<HomeBannerDto>

    fun cachedHomeBanners(): Flow<List<HomeBannerDto>>

    suspend fun refreshHomeBanners(): List<HomeBannerDto>

    suspend fun homeFeed(): HomeFeedDto

    suspend fun articles(query: ArticleQuery = ArticleQuery()): PagedResult<ArticleSummaryDto>

    fun pagedArticles(query: ArticleQuery = ArticleQuery()): Flow<PagingData<ArticleSummaryDto>>

    suspend fun article(id: String): ArticleDetailDto

    suspend fun articleBySourceId(sourceId: String, category: ArticleCategory): ArticleDetailDto

    suspend fun articleBySourceUrl(sourceUrl: String, category: ArticleCategory): ArticleDetailDto

    fun cachedArticleDetail(lookup: ArticleDetailLookup): Flow<ArticleDetailDto?>

    suspend fun refreshArticleDetail(lookup: ArticleDetailLookup): ArticleDetailDto

    suspend fun articleContext(id: String): DetailContextDto

    suspend fun directoryItems(
        query: DirectoryItemQuery = DirectoryItemQuery(),
    ): PagedResult<DirectoryItemSummaryDto>

    suspend fun directoryStatisticsOverview(
        kind: DirectoryItemKind = DirectoryItemKind.NationalProject,
    ): DirectoryStatisticsOverviewDto

    suspend fun directoryStatisticsBreakdown(
        kind: DirectoryItemKind = DirectoryItemKind.NationalProject,
        dimension: DirectoryStatisticDimension,
        limit: Int = 50,
    ): DirectoryStatisticDimensionDto

    fun pagedDirectoryItems(query: DirectoryItemQuery = DirectoryItemQuery()): Flow<PagingData<DirectoryItemSummaryDto>>

    suspend fun directoryItem(id: String): DirectoryItemDetailDto

    suspend fun directoryItemBySourceId(sourceId: String, kind: DirectoryItemKind): DirectoryItemDetailDto

    fun cachedDirectoryDetail(lookup: DirectoryDetailLookup): Flow<DirectoryItemDetailDto?>

    suspend fun refreshDirectoryDetail(lookup: DirectoryDetailLookup): DirectoryItemDetailDto

    suspend fun directoryItemContext(id: String): DetailContextDto

    suspend fun inheritors(query: InheritorQuery = InheritorQuery()): PagedResult<InheritorSummaryDto>

    fun pagedInheritors(query: InheritorQuery = InheritorQuery()): Flow<PagingData<InheritorSummaryDto>>

    suspend fun inheritor(id: String): InheritorDetailDto

    suspend fun inheritorBySourceId(sourceId: String): InheritorDetailDto

    fun cachedInheritorDetail(lookup: InheritorDetailLookup): Flow<InheritorDetailDto?>

    suspend fun refreshInheritorDetail(lookup: InheritorDetailLookup): InheritorDetailDto

    suspend fun inheritorContext(id: String): DetailContextDto

    suspend fun searchV2(query: SearchV2Query): SearchV2ResponseDto

    suspend fun searchSuggestions(prefix: String, limit: Int = 10): List<SearchSuggestionDto>

    suspend fun timelineV2(query: TimelineV2Query): TimelineV2ResponseDto

    suspend fun timelineYears(): List<TimelineYearBucketDto>

    suspend fun exploreIndex(): ExploreIndexDto

    suspend fun exploreTopics(type: String? = null, limit: Int = 20): List<ExploreTopicInfoDto>

    suspend fun exploreTopic(type: String, key: String, limit: Int = 6): ExploreTopicV2Dto

    suspend fun learningPaths(): List<LearningPathDto>

    suspend fun learningPathDetail(id: String, limit: Int = 6): LearningPathDetailDto

    suspend fun regionAtlas(): RegionAtlasDto

    suspend fun regionAtlasDetail(region: String, limit: Int = 6): RegionAtlasDetailDto

    suspend fun featuredCollections(): List<FeaturedCollectionDto>

    suspend fun collection(id: String, limit: Int = 10): CollectionDto

    suspend fun topicCollection(type: String, key: String, limit: Int = 10): CollectionDto

    // Discovery v2
    suspend fun discoveryToday(): DiscoveryTodayDto

    suspend fun discoveryRandom(type: SearchResultType): DiscoveryItemDto

    suspend fun discoveryTrending(limit: Int = 10): DiscoveryTrendingDto

    suspend fun discoveryWeekly(): DiscoveryWeeklyDto

    suspend fun discoverySerendipity(query: DiscoverySerendipityQuery): DiscoveryItemDto

    suspend fun discoveryDeepDive(query: DiscoveryDeepDiveQuery): DiscoveryDeepDiveDto

    // Data Stories
    suspend fun regionStory(region: String): DataStoryDto

    suspend fun categoryStory(category: String): DataStoryDto

    suspend fun yearStory(year: Int): DataStoryDto

    // Taxonomy
    suspend fun taxonomyCategories(limit: Int = 50): TaxonomyIndexDto<TaxonomyTopicDto>

    suspend fun taxonomyRegions(
        limit: Int = 50,
        sort: TaxonomyRegionSort = TaxonomyRegionSort.Total,
    ): TaxonomyIndexDto<TaxonomyTopicDto>

    suspend fun taxonomyKinds(): TaxonomyIndexDto<TaxonomyKindDto>

    suspend fun taxonomyCategoryDetail(category: String, limit: Int = 6): TaxonomyCategoryDetailDto

    suspend fun taxonomyRegionDetail(region: String, limit: Int = 6): TaxonomyRegionDetailDto

    // Compare
    suspend fun compareRegions(left: String, right: String, limit: Int = 6): CompareResultDto

    suspend fun compareCategories(left: String, right: String, limit: Int = 6): CompareResultDto

    suspend fun compareKinds(
        left: DirectoryItemKind,
        right: DirectoryItemKind,
        limit: Int = 6,
    ): CompareResultDto

    // Content Digest
    suspend fun articleDigest(id: String): ContentDigestDto

    suspend fun directoryItemDigest(id: String): ContentDigestDto

    suspend fun inheritorDigest(id: String): ContentDigestDto

    // Blended Recommendations
    suspend fun blendedRecommendations(
        query: BlendedRecommendationQuery,
    ): BlendedRecommendationResponseDto
}

class DefaultHeritageRepository @Inject constructor(
    private val articlePagingRepository: ArticlePagingRepository,
    private val directoryPagingRepository: DirectoryPagingRepository,
    private val inheritorPagingRepository: InheritorPagingRepository,
    private val apiClient: HeritageApiClient,
    private val database: HeritageDatabase,
) : HeritageRepository {
    override suspend fun homeBanners(): List<HomeBannerDto> =
        refreshHomeBanners()

    override fun cachedHomeBanners(): Flow<List<HomeBannerDto>> =
        database.homeBannerDao().observeAll().map { entities ->
            entities.map { it.toDto() }
        }

    override suspend fun refreshHomeBanners(): List<HomeBannerDto> {
        val dtos = apiClient.getHomeBanners()
        val dao = database.homeBannerDao()
        dao.deleteAll()
        val now = System.currentTimeMillis()
        dao.upsertAll(dtos.map { it.toEntity(now) })
        return dtos.sortedBy { it.sortOrder }
    }

    override suspend fun homeFeed(): HomeFeedDto =
        apiClient.getHomeFeed()

    override suspend fun articles(query: ArticleQuery): PagedResult<ArticleSummaryDto> =
        apiClient.getArticles(query)

    override fun pagedArticles(query: ArticleQuery): Flow<PagingData<ArticleSummaryDto>> =
        articlePagingRepository.pagedArticles(query)

    override suspend fun article(id: String): ArticleDetailDto =
        refreshArticleDetail(ArticleDetailLookup(articleId = id))

    override suspend fun articleBySourceId(sourceId: String, category: ArticleCategory): ArticleDetailDto =
        refreshArticleDetail(
            ArticleDetailLookup(
                sourceId = sourceId,
                category = category,
            ),
        )

    override suspend fun articleBySourceUrl(sourceUrl: String, category: ArticleCategory): ArticleDetailDto =
        refreshArticleDetail(
            ArticleDetailLookup(
                sourceUrl = sourceUrl,
                category = category,
            ),
        )

    override fun cachedArticleDetail(lookup: ArticleDetailLookup): Flow<ArticleDetailDto?> {
        val detailDao = database.articleDetailDao()
        // 详情页入口可能来自列表 id、关联 sourceId 或原站 URL。
        // lookup 保持显式字段，方便每条路由使用自己手上最可靠的 key。
        val cachedArticle = when {
            !lookup.articleId.isNullOrBlank() -> detailDao.observeById(lookup.articleId)
            !lookup.sourceId.isNullOrBlank() -> detailDao.observeBySourceId(
                sourceId = lookup.sourceId,
                category = lookup.category.wireName,
            )

            !lookup.sourceUrl.isNullOrBlank() -> detailDao.observeBySourceUrl(
                sourceUrl = lookup.sourceUrl,
                category = lookup.category.wireName,
            )

            else -> error("Missing article lookup key")
        }
        return cachedArticle.map { it?.toDto() }
    }

    override suspend fun refreshArticleDetail(lookup: ArticleDetailLookup): ArticleDetailDto {
        // 刷新成功后统一写入 Room；界面再从缓存流拿到同一份数据，
        // 这样在线、离线和重试路径的状态来源是一致的。
        val article = when {
            !lookup.articleId.isNullOrBlank() -> apiClient.getArticle(lookup.articleId)
            !lookup.sourceId.isNullOrBlank() -> apiClient.getArticleBySourceId(
                sourceId = lookup.sourceId,
                category = lookup.category,
            )

            !lookup.sourceUrl.isNullOrBlank() -> apiClient.getArticleBySourceUrl(
                sourceUrl = lookup.sourceUrl,
                category = lookup.category,
            )

            else -> error("Missing article lookup key")
        }

        database.articleDetailDao().upsert(
            article.toEntity(
                category = article.category,
                sourceId = lookup.sourceId,
                sourceUrl = lookup.sourceUrl,
                updatedAtEpochMillis = System.currentTimeMillis(),
            ),
        )
        return article
    }

    override suspend fun articleContext(id: String): DetailContextDto =
        apiClient.getArticleContext(id)

    override suspend fun directoryItems(
        query: DirectoryItemQuery,
    ): PagedResult<DirectoryItemSummaryDto> =
        apiClient.getDirectoryItems(query)

    override suspend fun directoryStatisticsOverview(
        kind: DirectoryItemKind,
    ): DirectoryStatisticsOverviewDto =
        apiClient.getDirectoryStatisticsOverview(kind)

    override suspend fun directoryStatisticsBreakdown(
        kind: DirectoryItemKind,
        dimension: DirectoryStatisticDimension,
        limit: Int,
    ): DirectoryStatisticDimensionDto =
        apiClient.getDirectoryStatisticsBreakdown(
            kind = kind,
            dimension = dimension,
            limit = limit,
        )

    override fun pagedDirectoryItems(query: DirectoryItemQuery): Flow<PagingData<DirectoryItemSummaryDto>> =
        directoryPagingRepository.pagedDirectoryItems(query)

    override suspend fun directoryItem(id: String): DirectoryItemDetailDto =
        refreshDirectoryDetail(DirectoryDetailLookup(itemId = id))

    override suspend fun directoryItemBySourceId(
        sourceId: String,
        kind: DirectoryItemKind,
    ): DirectoryItemDetailDto =
        refreshDirectoryDetail(
            DirectoryDetailLookup(
                sourceId = sourceId,
                kind = kind,
            ),
        )

    override fun cachedDirectoryDetail(lookup: DirectoryDetailLookup): Flow<DirectoryItemDetailDto?> {
        val detailDao = database.directoryDetailDao()
        // 名录 sourceId 只在 kind 内唯一，所以缓存查询必须带上 kind。
        val cachedDirectory = when {
            !lookup.itemId.isNullOrBlank() -> detailDao.observeById(lookup.itemId)
            !lookup.sourceId.isNullOrBlank() -> detailDao.observeBySourceId(
                sourceId = lookup.sourceId,
                kind = lookup.kind.wireName,
            )

            else -> error("Missing directory lookup key")
        }
        return cachedDirectory.map { it?.toDto() }
    }

    override suspend fun refreshDirectoryDetail(lookup: DirectoryDetailLookup): DirectoryItemDetailDto {
        val detail = when {
            !lookup.itemId.isNullOrBlank() -> apiClient.getDirectoryItem(lookup.itemId)
            !lookup.sourceId.isNullOrBlank() -> apiClient.getDirectoryItemBySourceId(
                sourceId = lookup.sourceId,
                kind = lookup.kind,
            )

            else -> error("Missing directory lookup key")
        }

        database.directoryDetailDao().upsert(
            detail.toEntity(
                kind = detail.kind,
                sourceId = lookup.sourceId,
                updatedAtEpochMillis = System.currentTimeMillis(),
            ),
        )
        return detail
    }

    override suspend fun directoryItemContext(id: String): DetailContextDto =
        apiClient.getDirectoryItemContext(id)

    override suspend fun inheritors(query: InheritorQuery): PagedResult<InheritorSummaryDto> =
        apiClient.getInheritors(query)

    override fun pagedInheritors(query: InheritorQuery): Flow<PagingData<InheritorSummaryDto>> =
        inheritorPagingRepository.pagedInheritors(query)

    override suspend fun inheritor(id: String): InheritorDetailDto =
        refreshInheritorDetail(InheritorDetailLookup(inheritorId = id))

    override suspend fun inheritorBySourceId(sourceId: String): InheritorDetailDto =
        refreshInheritorDetail(InheritorDetailLookup(sourceId = sourceId))

    override fun cachedInheritorDetail(lookup: InheritorDetailLookup): Flow<InheritorDetailDto?> {
        val detailDao = database.inheritorDetailDao()
        // 当前后端的传承人 sourceId 在集合内是全局唯一的。
        val cachedInheritor = when {
            !lookup.inheritorId.isNullOrBlank() -> detailDao.observeById(lookup.inheritorId)
            !lookup.sourceId.isNullOrBlank() -> detailDao.observeBySourceId(lookup.sourceId)
            else -> error("Missing inheritor lookup key")
        }
        return cachedInheritor.map { it?.toDto() }
    }

    override suspend fun refreshInheritorDetail(lookup: InheritorDetailLookup): InheritorDetailDto {
        val detail = when {
            !lookup.inheritorId.isNullOrBlank() -> apiClient.getInheritor(lookup.inheritorId)
            !lookup.sourceId.isNullOrBlank() -> apiClient.getInheritorBySourceId(lookup.sourceId)
            else -> error("Missing inheritor lookup key")
        }

        database.inheritorDetailDao().upsert(
            detail.toEntity(
                sourceId = lookup.sourceId,
                updatedAtEpochMillis = System.currentTimeMillis(),
            ),
        )
        return detail
    }

    override suspend fun inheritorContext(id: String): DetailContextDto =
        apiClient.getInheritorContext(id)

    override suspend fun searchV2(query: SearchV2Query): SearchV2ResponseDto =
        apiClient.searchV2(query)

    override suspend fun searchSuggestions(prefix: String, limit: Int): List<SearchSuggestionDto> =
        apiClient.getSearchSuggestions(prefix, limit)

    override suspend fun timelineV2(query: TimelineV2Query): TimelineV2ResponseDto =
        apiClient.getTimelineV2(query)

    override suspend fun timelineYears(): List<TimelineYearBucketDto> =
        apiClient.getTimelineYears()

    override suspend fun exploreIndex(): ExploreIndexDto =
        apiClient.getExploreIndex()

    override suspend fun exploreTopics(type: String?, limit: Int): List<ExploreTopicInfoDto> =
        apiClient.getExploreTopics(type, limit)

    override suspend fun exploreTopic(type: String, key: String, limit: Int): ExploreTopicV2Dto =
        apiClient.getExploreTopic(type, key, limit)

    override suspend fun learningPaths(): List<LearningPathDto> =
        apiClient.getLearningPaths()

    override suspend fun learningPathDetail(id: String, limit: Int): LearningPathDetailDto =
        apiClient.getLearningPathDetail(id, limit)

    override suspend fun regionAtlas(): RegionAtlasDto =
        apiClient.getRegionAtlas()

    override suspend fun regionAtlasDetail(region: String, limit: Int): RegionAtlasDetailDto =
        apiClient.getRegionAtlasDetail(region, limit)

    override suspend fun featuredCollections(): List<FeaturedCollectionDto> =
        apiClient.getFeaturedCollections()

    override suspend fun collection(id: String, limit: Int): CollectionDto =
        apiClient.getCollection(id, limit)

    override suspend fun topicCollection(type: String, key: String, limit: Int): CollectionDto =
        apiClient.getTopicCollection(type, key, limit)

    // Discovery v2
    override suspend fun discoveryToday(): DiscoveryTodayDto =
        apiClient.getDiscoveryToday()

    override suspend fun discoveryRandom(type: SearchResultType): DiscoveryItemDto =
        apiClient.getDiscoveryRandom(type)

    override suspend fun discoveryTrending(limit: Int): DiscoveryTrendingDto =
        apiClient.getDiscoveryTrending(limit)

    override suspend fun discoveryWeekly(): DiscoveryWeeklyDto =
        apiClient.getDiscoveryWeekly()

    override suspend fun discoverySerendipity(query: DiscoverySerendipityQuery): DiscoveryItemDto =
        apiClient.getDiscoverySerendipity(query)

    override suspend fun discoveryDeepDive(query: DiscoveryDeepDiveQuery): DiscoveryDeepDiveDto =
        apiClient.getDiscoveryDeepDive(query)

    // Data Stories
    override suspend fun regionStory(region: String): DataStoryDto =
        apiClient.getRegionStory(region)

    override suspend fun categoryStory(category: String): DataStoryDto =
        apiClient.getCategoryStory(category)

    override suspend fun yearStory(year: Int): DataStoryDto =
        apiClient.getYearStory(year)

    // Taxonomy
    override suspend fun taxonomyCategories(limit: Int): TaxonomyIndexDto<TaxonomyTopicDto> =
        apiClient.getTaxonomyCategories(limit)

    override suspend fun taxonomyRegions(
        limit: Int,
        sort: TaxonomyRegionSort,
    ): TaxonomyIndexDto<TaxonomyTopicDto> =
        apiClient.getTaxonomyRegions(limit, sort)

    override suspend fun taxonomyKinds(): TaxonomyIndexDto<TaxonomyKindDto> =
        apiClient.getTaxonomyKinds()

    override suspend fun taxonomyCategoryDetail(
        category: String,
        limit: Int,
    ): TaxonomyCategoryDetailDto =
        apiClient.getTaxonomyCategoryDetail(category, limit)

    override suspend fun taxonomyRegionDetail(
        region: String,
        limit: Int,
    ): TaxonomyRegionDetailDto =
        apiClient.getTaxonomyRegionDetail(region, limit)

    // Compare
    override suspend fun compareRegions(
        left: String,
        right: String,
        limit: Int,
    ): CompareResultDto =
        apiClient.compareRegions(left, right, limit)

    override suspend fun compareCategories(
        left: String,
        right: String,
        limit: Int,
    ): CompareResultDto =
        apiClient.compareCategories(left, right, limit)

    override suspend fun compareKinds(
        left: DirectoryItemKind,
        right: DirectoryItemKind,
        limit: Int,
    ): CompareResultDto =
        apiClient.compareKinds(left, right, limit)

    // Content Digest
    override suspend fun articleDigest(id: String): ContentDigestDto =
        apiClient.getArticleDigest(id)

    override suspend fun directoryItemDigest(id: String): ContentDigestDto =
        apiClient.getDirectoryItemDigest(id)

    override suspend fun inheritorDigest(id: String): ContentDigestDto =
        apiClient.getInheritorDigest(id)

    // Blended Recommendations
    override suspend fun blendedRecommendations(
        query: BlendedRecommendationQuery,
    ): BlendedRecommendationResponseDto =
        apiClient.getBlendedRecommendations(query)
}
