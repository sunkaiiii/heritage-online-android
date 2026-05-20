package com.duckylife.heritage.modern.core.data

import com.duckylife.heritage.modern.core.network.dto.ArticleCategory
import com.duckylife.heritage.modern.core.network.dto.ArticleDetailDto
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemDetailDto
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemKind
import com.duckylife.heritage.modern.core.network.dto.DirectoryStatisticDimension
import com.duckylife.heritage.modern.core.network.dto.DirectoryStatisticDimensionDto
import com.duckylife.heritage.modern.core.network.dto.DirectoryStatisticItemDto
import com.duckylife.heritage.modern.core.network.dto.DirectoryStatisticsOverviewDto
import com.duckylife.heritage.modern.core.network.dto.InheritorDetailDto
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class HeritageRepositoryDetailCacheTest {

    // region Article detail caching

    @Test
    fun refreshArticleDetailDispatchesToGetArticleWhenArticleIdPresent() = runTest {
        val repo = FakeHeritageRepository(
            articleDetails = mapOf("article-1" to ArticleDetailDto(id = "article-1", title = "Test Article")),
        )
        val result = repo.refreshArticleDetail(ArticleDetailLookup(articleId = "article-1"))
        assertEquals("Test Article", result.title)
        // Verify it didn't use sourceId or sourceUrl paths
        assertTrue(repo.articleSourceIdQueries.isEmpty())
        assertTrue(repo.articleSourceUrlQueries.isEmpty())
    }

    @Test
    fun refreshArticleDetailDispatchesToGetArticleBySourceIdWhenSourceIdPresent() = runTest {
        val repo = FakeHeritageRepository(
            articleDetailsBySourceId = mapOf("sid-1" to ArticleDetailDto(id = "a1", title = "By Source Id")),
        )
        val result = repo.refreshArticleDetail(
            ArticleDetailLookup(sourceId = "sid-1", category = ArticleCategory.Forum),
        )
        assertEquals("By Source Id", result.title)
        assertEquals(1, repo.articleSourceIdQueries.size)
        assertEquals("sid-1", repo.articleSourceIdQueries.first().first)
        assertEquals(ArticleCategory.Forum, repo.articleSourceIdQueries.first().second)
    }

    @Test
    fun refreshArticleDetailDispatchesToGetArticleBySourceUrlWhenSourceUrlPresent() = runTest {
        val repo = FakeHeritageRepository(
            articleDetailsBySourceUrl = mapOf("https://test.test/a" to ArticleDetailDto(id = "a1", title = "By Source Url")),
        )
        val result = repo.refreshArticleDetail(
            ArticleDetailLookup(sourceUrl = "https://test.test/a", category = ArticleCategory.SpecialTopic),
        )
        assertEquals("By Source Url", result.title)
        assertEquals(1, repo.articleSourceUrlQueries.size)
        assertEquals("https://test.test/a", repo.articleSourceUrlQueries.first().first)
        assertEquals(ArticleCategory.SpecialTopic, repo.articleSourceUrlQueries.first().second)
    }

    @Test
    fun refreshArticleDetailPrefersArticleIdOverSourceId() = runTest {
        val repo = FakeHeritageRepository(
            articleDetails = mapOf("article-1" to ArticleDetailDto(id = "article-1", title = "By Id")),
            articleDetailsBySourceId = mapOf("sid-1" to ArticleDetailDto(id = "sid-1", title = "By Source Id")),
        )
        val result = repo.refreshArticleDetail(
            ArticleDetailLookup(articleId = "article-1", sourceId = "sid-1"),
        )
        // Should prefer articleId
        assertEquals("By Id", result.title)
    }

    @Test
    fun cachedArticleDetailReturnsNullWhenNotInCache() = runTest {
        val repo = FakeHeritageRepository(
            cachedArticleDetails = mapOf(ArticleDetailLookup(articleId = "missing") to null),
        )
        val result = repo.cachedArticleDetail(ArticleDetailLookup(articleId = "missing")).first()
        assertNull(result)
    }

    @Test
    fun cachedArticleDetailReturnsCachedValue() = runTest {
        val dto = ArticleDetailDto(id = "cached-1", title = "Cached Article")
        val lookup = ArticleDetailLookup(articleId = "cached-1")
        val repo = FakeHeritageRepository(cachedArticleDetails = mapOf(lookup to dto))
        val result = repo.cachedArticleDetail(lookup).first()
        assertNotNull(result)
        assertEquals("Cached Article", result?.title)
    }

    // endregion

    // region Directory detail caching

    @Test
    fun refreshDirectoryDetailUsesSourceIdAndKind() = runTest {
        val repo = FakeHeritageRepository(
            directoryDetailsBySourceId = mapOf(
                "src-dir-1" to DirectoryItemDetailDto(id = "d1", title = "Dir By Source"),
            ),
        )
        val result = repo.refreshDirectoryDetail(
            DirectoryDetailLookup(sourceId = "src-dir-1", kind = DirectoryItemKind.CulturalEcoZone),
        )
        assertEquals("Dir By Source", result.title)
        assertEquals(1, repo.directorySourceIdQueries.size)
        assertEquals("src-dir-1", repo.directorySourceIdQueries.first().first)
        assertEquals(DirectoryItemKind.CulturalEcoZone, repo.directorySourceIdQueries.first().second)
    }

    @Test
    fun refreshDirectoryDetailSourceIdKindIsolation() = runTest {
        // Different kinds should produce different lookups
        val repo = FakeHeritageRepository(
            directoryDetailsBySourceId = mapOf(
                "shared-src" to DirectoryItemDetailDto(id = "d1", title = "Shared Source"),
            ),
        )
        val result1 = repo.refreshDirectoryDetail(
            DirectoryDetailLookup(sourceId = "shared-src", kind = DirectoryItemKind.NationalProject),
        )
        assertEquals("Shared Source", result1.title)

        val result2 = repo.refreshDirectoryDetail(
            DirectoryDetailLookup(sourceId = "shared-src", kind = DirectoryItemKind.UnescoEntry),
        )
        assertEquals("Shared Source", result2.title)

        // Both lookups should have been sent with their respective kinds
        assertEquals(2, repo.directorySourceIdQueries.size)
        assertEquals(DirectoryItemKind.NationalProject, repo.directorySourceIdQueries[0].second)
        assertEquals(DirectoryItemKind.UnescoEntry, repo.directorySourceIdQueries[1].second)
    }

    @Test
    fun refreshDirectoryDetailPrefersItemIdOverSourceId() = runTest {
        val repo = FakeHeritageRepository(
            directoryDetails = mapOf("dir-1" to DirectoryItemDetailDto(id = "dir-1", title = "By Item Id")),
            directoryDetailsBySourceId = mapOf("src-1" to DirectoryItemDetailDto(id = "src-1", title = "By Source")),
        )
        val result = repo.refreshDirectoryDetail(
            DirectoryDetailLookup(itemId = "dir-1", sourceId = "src-1"),
        )
        assertEquals("By Item Id", result.title)
    }

    @Test
    fun cachedDirectoryDetailReturnsNullWhenNotInCache() = runTest {
        val repo = FakeHeritageRepository(
            cachedDirectoryDetails = mapOf(DirectoryDetailLookup(itemId = "missing") to null),
        )
        val result = repo.cachedDirectoryDetail(DirectoryDetailLookup(itemId = "missing")).first()
        assertNull(result)
    }

    @Test
    fun cachedDirectoryDetailReturnsCachedValue() = runTest {
        val dto = DirectoryItemDetailDto(id = "cached-dir", title = "Cached Directory", kind = DirectoryItemKind.NationalProject)
        val lookup = DirectoryDetailLookup(itemId = "cached-dir")
        val repo = FakeHeritageRepository(cachedDirectoryDetails = mapOf(lookup to dto))
        val result = repo.cachedDirectoryDetail(lookup).first()
        assertNotNull(result)
        assertEquals("Cached Directory", result?.title)
    }

    // endregion

    // region Directory statistics

    @Test
    fun directoryStatisticsOverviewUsesKind() = runTest {
        val repo = FakeHeritageRepository(
            directoryStatisticsOverview = DirectoryStatisticsOverviewDto(
                kind = "nationalProject",
                total = 950,
            ),
        )

        val result = repo.directoryStatisticsOverview(DirectoryItemKind.NationalProject)

        assertEquals(950, result.total)
        assertEquals(1, repo.directoryStatisticsOverviewQueries.size)
        assertEquals(DirectoryItemKind.NationalProject, repo.directoryStatisticsOverviewQueries.first())
    }

    @Test
    fun directoryStatisticsBreakdownUsesKindDimensionAndLimit() = runTest {
        val repo = FakeHeritageRepository(
            directoryStatisticsBreakdowns = mapOf(
                DirectoryStatisticDimension.Region to DirectoryStatisticDimensionDto(
                    dimension = "region",
                    items = listOf(DirectoryStatisticItemDto(key = "西藏自治区", name = "西藏自治区", value = 7)),
                ),
            ),
        )

        val result = repo.directoryStatisticsBreakdown(
            kind = DirectoryItemKind.NationalProject,
            dimension = DirectoryStatisticDimension.Region,
            limit = 20,
        )

        assertEquals("region", result.dimension)
        assertEquals(7, result.items.first().value)
        assertEquals(1, repo.directoryStatisticsBreakdownQueries.size)
        assertEquals(
            Triple(DirectoryItemKind.NationalProject, DirectoryStatisticDimension.Region, 20),
            repo.directoryStatisticsBreakdownQueries.first(),
        )
    }

    // endregion

    // region Inheritor detail caching

    @Test
    fun refreshInheritorDetailUsesSourceId() = runTest {
        val repo = FakeHeritageRepository(
            inheritorDetailsBySourceId = mapOf("src-inh-1" to InheritorDetailDto(id = "inh-1", name = "张三")),
        )
        val result = repo.refreshInheritorDetail(InheritorDetailLookup(sourceId = "src-inh-1"))
        assertEquals("张三", result.name)
        assertEquals(1, repo.inheritorSourceIdQueries.size)
        assertEquals("src-inh-1", repo.inheritorSourceIdQueries.first())
    }

    @Test
    fun refreshInheritorDetailPrefersIdOverSourceId() = runTest {
        val repo = FakeHeritageRepository(
            inheritorDetails = mapOf("inh-1" to InheritorDetailDto(id = "inh-1", name = "By Id")),
            inheritorDetailsBySourceId = mapOf("src-1" to InheritorDetailDto(id = "src-1", name = "By Source")),
        )
        val result = repo.refreshInheritorDetail(
            InheritorDetailLookup(inheritorId = "inh-1", sourceId = "src-1"),
        )
        assertEquals("By Id", result.name)
    }

    @Test
    fun cachedInheritorDetailReturnsNullWhenNotInCache() = runTest {
        val repo = FakeHeritageRepository(
            cachedInheritorDetails = mapOf(InheritorDetailLookup(inheritorId = "missing") to null),
        )
        val result = repo.cachedInheritorDetail(InheritorDetailLookup(inheritorId = "missing")).first()
        assertNull(result)
    }

    @Test
    fun cachedInheritorDetailReturnsCachedValue() = runTest {
        val dto = InheritorDetailDto(id = "cached-inh", name = "Cached Inheritor")
        val lookup = InheritorDetailLookup(inheritorId = "cached-inh")
        val repo = FakeHeritageRepository(cachedInheritorDetails = mapOf(lookup to dto))
        val result = repo.cachedInheritorDetail(lookup).first()
        assertNotNull(result)
        assertEquals("Cached Inheritor", result?.name)
    }

    // endregion

    // region Paging query tracking

    @Test
    fun pagedArticlesTracksQuery() = runTest {
        val repo = FakeHeritageRepository()
        val query = com.duckylife.heritage.modern.core.network.ArticleQuery(
            category = ArticleCategory.Forum,
            keywords = "非遗",
        )
        repo.pagedArticles(query)
        assertEquals(1, repo.pagedArticleQueries.size)
        assertEquals(ArticleCategory.Forum, repo.pagedArticleQueries.first().category)
        assertEquals("非遗", repo.pagedArticleQueries.first().keywords)
    }

    @Test
    fun pagedDirectoryItemsTracksQuery() = runTest {
        val repo = FakeHeritageRepository()
        val query = com.duckylife.heritage.modern.core.network.DirectoryItemQuery(
            kind = DirectoryItemKind.UnescoEntry,
            keywords = "世界遗产",
        )
        repo.pagedDirectoryItems(query)
        assertEquals(1, repo.pagedDirectoryItemQueries.size)
        assertEquals(DirectoryItemKind.UnescoEntry, repo.pagedDirectoryItemQueries.first().kind)
    }

    @Test
    fun pagedInheritorsTracksQuery() = runTest {
        val repo = FakeHeritageRepository()
        val query = com.duckylife.heritage.modern.core.network.InheritorQuery(
            keywords = "剪纸",
            region = "河北",
        )
        repo.pagedInheritors(query)
        assertEquals(1, repo.pagedInheritorQueries.size)
        assertEquals("剪纸", repo.pagedInheritorQueries.first().keywords)
        assertEquals("河北", repo.pagedInheritorQueries.first().region)
    }

    // endregion
}
