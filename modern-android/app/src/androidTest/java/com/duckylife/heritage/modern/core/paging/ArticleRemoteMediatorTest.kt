package com.duckylife.heritage.modern.core.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.RemoteMediator.MediatorResult
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.duckylife.heritage.modern.core.database.HeritageDatabase
import com.duckylife.heritage.modern.core.database.entity.ArticleEntity
import com.duckylife.heritage.modern.core.database.entity.ArticleRemoteKeyEntity
import com.duckylife.heritage.modern.core.database.mapper.queryKey
import com.duckylife.heritage.modern.core.network.ArticleQuery
import com.duckylife.heritage.modern.core.network.dto.ArticleCategory
import com.duckylife.heritage.modern.core.network.dto.ArticleSummaryDto
import com.duckylife.heritage.modern.core.network.dto.PagedResult
import com.duckylife.heritage.modern.core.paging.testhelpers.FakeHeritageApiClient
import java.io.IOException
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalPagingApi::class)
@RunWith(AndroidJUnit4::class)
class ArticleRemoteMediatorTest {

    private lateinit var database: HeritageDatabase
    private lateinit var apiClient: FakeHeritageApiClient
    private val query = ArticleQuery(
        category = ArticleCategory.News,
        pageSize = 20,
    )
    private val queryKey = query.queryKey()

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            HeritageDatabase::class.java,
        ).build()
        apiClient = FakeHeritageApiClient()
    }

    @After
    fun teardown() {
        database.close()
    }

    // region REFRESH

    @Test
    fun refreshInsertsFirstPageAndRemoteKey() = runTest {
        apiClient.articlesResult = PagedResult(
            items = listOf(testArticle(id = "a1", title = "文章1")),
            page = 1,
            pageSize = 20,
            hasMore = true,
            total = 30,
        )
        val mediator = createMediator()
        val result = mediator.load(LoadType.REFRESH, pagingState())

        assertTrue(result is MediatorResult.Success)
        assertFalse((result as MediatorResult.Success).endOfPaginationReached)

        // 数据已写入 Room
        val items = database.articleDao().pagingSource(queryKey).loadSinglePage()
        assertEquals(1, items.size)
        assertEquals("文章1", items.first().title)

        // remote key 记录了下一页
        val remoteKey = database.articleRemoteKeyDao().remoteKey(queryKey)
        assertNotNull(remoteKey)
        assertEquals(2, remoteKey?.nextPage)
        assertTrue(remoteKey?.hasMore == true)
    }

    @Test
    fun refreshClearsOldDataBeforeInsert() = runTest {
        // 预填充同 queryKey 的旧数据
        database.articleDao().upsertAll(
            listOf(
                ArticleEntity(
                    id = "old-1",
                    queryKey = queryKey,
                    category = "news",
                    title = "旧文章",
                    summary = null,
                    publishedAt = null,
                    coverImageJson = null,
                    sourceUrl = null,
                    page = 1,
                    positionInPage = 0,
                ),
            ),
        )

        apiClient.articlesResult = PagedResult(
            items = listOf(testArticle(id = "new-1", title = "新文章")),
            page = 1,
            pageSize = 20,
            hasMore = false,
        )
        val mediator = createMediator()
        val result = mediator.load(LoadType.REFRESH, pagingState())

        assertTrue(result is MediatorResult.Success)
        val items = database.articleDao().pagingSource(queryKey).loadSinglePage()
        assertEquals(1, items.size)
        assertEquals("新文章", items.first().title)
    }

    @Test
    fun refreshReturnsSuccessWithEndOfPaginationWhenNoMorePages() = runTest {
        apiClient.articlesResult = PagedResult(
            items = listOf(testArticle(id = "only", title = "唯一")),
            page = 1,
            pageSize = 20,
            hasMore = false,
            total = 1,
        )
        val mediator = createMediator()
        val result = mediator.load(LoadType.REFRESH, pagingState())

        assertTrue(result is MediatorResult.Success)
        assertTrue((result as MediatorResult.Success).endOfPaginationReached)
    }

    @Test
    fun refreshReturnsErrorOnNetworkFailure() = runTest {
        apiClient.failure = IOException("网络不可用")
        val mediator = createMediator()
        val result = mediator.load(LoadType.REFRESH, pagingState())

        assertTrue(result is MediatorResult.Error)
        assertEquals("网络不可用", (result as MediatorResult.Error).throwable.message)
    }

    // endregion

    // region APPEND

    @Test
    fun appendUsesRemoteKeyNextPage() = runTest {
        // 模拟 REFRESH 已写入 remote key
        database.articleRemoteKeyDao().upsert(
            ArticleRemoteKeyEntity(
                queryKey = queryKey,
                nextPage = 2,
                hasMore = true,
            ),
        )

        apiClient.articlesResult = PagedResult(
            items = listOf(testArticle(id = "a2", title = "第二页文章")),
            page = 2,
            pageSize = 20,
            hasMore = false,
        )
        val mediator = createMediator()
        val result = mediator.load(LoadType.APPEND, pagingState())

        assertTrue(result is MediatorResult.Success)
        assertTrue((result as MediatorResult.Success).endOfPaginationReached)

        assertEquals(1, apiClient.articleRequests.size)
        assertEquals(2, apiClient.articleRequests.first().page)
    }

    @Test
    fun appendEndsPaginationWhenRemoteKeyHasNoMore() = runTest {
        database.articleRemoteKeyDao().upsert(
            ArticleRemoteKeyEntity(
                queryKey = queryKey,
                nextPage = null,
                hasMore = false,
            ),
        )
        val mediator = createMediator()
        val result = mediator.load(LoadType.APPEND, pagingState())

        assertTrue(result is MediatorResult.Success)
        assertTrue((result as MediatorResult.Success).endOfPaginationReached)
        // 不应发起 API 请求
        assertTrue(apiClient.articleRequests.isEmpty())
    }

    @Test
    fun appendReturnsErrorOnNetworkFailure() = runTest {
        database.articleRemoteKeyDao().upsert(
            ArticleRemoteKeyEntity(
                queryKey = queryKey,
                nextPage = 2,
                hasMore = true,
            ),
        )
        apiClient.failure = IOException("超时")
        val mediator = createMediator()
        val result = mediator.load(LoadType.APPEND, pagingState())

        assertTrue(result is MediatorResult.Error)
    }

    // endregion

    // region PREPEND

    @Test
    fun prependReturnsSuccessWithEndOfPagination() = runTest {
        val mediator = createMediator()
        val result = mediator.load(LoadType.PREPEND, pagingState())

        assertTrue(result is MediatorResult.Success)
        assertTrue((result as MediatorResult.Success).endOfPaginationReached)
    }

    // endregion

    // region Empty pages

    @Test
    fun refreshWithEmptyResultSetsEndOfPagination() = runTest {
        apiClient.articlesResult = PagedResult(
            items = emptyList(),
            page = 1,
            pageSize = 20,
            hasMore = false,
            total = 0,
        )
        val mediator = createMediator()
        val result = mediator.load(LoadType.REFRESH, pagingState())

        assertTrue(result is MediatorResult.Success)
        assertTrue((result as MediatorResult.Success).endOfPaginationReached)
    }

    // endregion

    // region 不同 queryKey 数据隔离

    @Test
    fun refreshOnlyClearsSameQueryKey() = runTest {
        val otherKey = ArticleQuery(category = ArticleCategory.Forum, pageSize = 20).queryKey()

        // 写入另一个 queryKey 的数据
        database.articleDao().upsertAll(
            listOf(
                ArticleEntity(
                    id = "other-1",
                    queryKey = otherKey,
                    category = "forum",
                    title = "论坛文章",
                    summary = null,
                    publishedAt = null,
                    coverImageJson = null,
                    sourceUrl = null,
                    page = 1,
                    positionInPage = 0,
                ),
            ),
        )

        apiClient.articlesResult = PagedResult(
            items = listOf(testArticle(id = "new-1", title = "新闻")),
            page = 1,
            pageSize = 20,
            hasMore = false,
        )
        val mediator = createMediator()
        mediator.load(LoadType.REFRESH, pagingState())

        // 当前 queryKey 的数据已刷新
        val currentItems = database.articleDao().pagingSource(queryKey).loadSinglePage()
        assertEquals(1, currentItems.size)
        assertEquals("新闻", currentItems.first().title)

        // 另一个 queryKey 的数据未被清理
        val otherItems = database.articleDao().pagingSource(otherKey).loadSinglePage()
        assertEquals(1, otherItems.size)
        assertEquals("论坛文章", otherItems.first().title)
    }

    // endregion

    private fun createMediator() = ArticleRemoteMediator(
        query = query,
        database = database,
        apiClient = apiClient,
    )

    private fun pagingState() = PagingState<Int, ArticleEntity>(
        pages = emptyList(),
        anchorPosition = null,
        config = PagingConfig(pageSize = 20),
        leadingPlaceholderCount = 0,
    )

    private fun testArticle(id: String, title: String) = ArticleSummaryDto(
        id = id,
        category = ArticleCategory.News,
        title = title,
        summary = "摘要",
        publishedAt = "2025-01-01",
        sourceUrl = "https://src.test/$id",
    )

    // 从 PagingSource 加载单页数据
    private suspend fun androidx.paging.PagingSource<Int, ArticleEntity>.loadSinglePage(): List<ArticleEntity> {
        val loadResult = this.load(
            androidx.paging.PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 100,
                placeholdersEnabled = false,
            ),
        )
        @Suppress("UNCHECKED_CAST")
        return (loadResult as androidx.paging.PagingSource.LoadResult.Page<Int, ArticleEntity>).data
    }
}
