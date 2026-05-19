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
import com.duckylife.heritage.modern.core.database.entity.DirectoryItemEntity
import com.duckylife.heritage.modern.core.database.entity.DirectoryRemoteKeyEntity
import com.duckylife.heritage.modern.core.database.mapper.queryKey
import com.duckylife.heritage.modern.core.network.DirectoryItemQuery
import com.duckylife.heritage.modern.core.network.HeritageApiClient
import com.duckylife.heritage.modern.core.network.dto.ArticleCategory
import com.duckylife.heritage.modern.core.network.dto.ArticleDetailDto
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemDetailDto
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemKind
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemSummaryDto
import com.duckylife.heritage.modern.core.network.dto.HomeBannerDto
import com.duckylife.heritage.modern.core.network.dto.InheritorDetailDto
import com.duckylife.heritage.modern.core.network.dto.InheritorSummaryDto
import com.duckylife.heritage.modern.core.network.dto.PagedResult
import java.io.IOException
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalPagingApi::class)
@RunWith(AndroidJUnit4::class)
class DirectoryRemoteMediatorTest {

    private lateinit var database: HeritageDatabase
    private lateinit var apiClient: FakeApiClient
    private val query = DirectoryItemQuery(
        kind = DirectoryItemKind.NationalProject,
        pageSize = 20,
    )
    private val queryKey = query.queryKey()

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            HeritageDatabase::class.java,
        ).build()
        apiClient = FakeApiClient()
    }

    @After
    fun teardown() {
        database.close()
    }

    // region REFRESH

    @Test
    fun refreshInsertsFirstPageAndRemoteKey() = runTest {
        apiClient.directoryItemsResult = PagedResult(
            items = listOf(testDirectoryItem(id = "d1", title = "项目1")),
            page = 1,
            pageSize = 20,
            hasMore = true,
            total = 30,
        )
        val mediator = createMediator()

        val pagingState = pagingState()
        val result = mediator.load(LoadType.REFRESH, pagingState)

        assertTrue(result is MediatorResult.Success)
        assertFalse((result as MediatorResult.Success).endOfPaginationReached)

        // Verify items were inserted
        val items = database.directoryItemDao().pagingSource(queryKey).loadSinglePage()
        assertEquals(1, items.size)
        assertEquals("项目1", items.first().title)

        // Verify remote key was stored
        val remoteKey = database.directoryRemoteKeyDao().remoteKey(queryKey)
        assertNotNull(remoteKey)
        assertEquals(2, remoteKey?.nextPage)
        assertTrue(remoteKey?.hasMore == true)
    }

    @Test
    fun refreshClearsOldDataBeforeInsert() = runTest {
        // Pre-populate with old data for the same queryKey
        database.directoryItemDao().upsertAll(
            listOf(
                DirectoryItemEntity(
                    id = "old-1",
                    queryKey = queryKey,
                    kind = "nationalProject",
                    title = "旧数据",
                    summary = null,
                    category = null,
                    region = null,
                    projectCode = null,
                    batch = null,
                    publishedYear = null,
                    listType = null,
                    coverImageJson = null,
                    sourceUrl = null,
                    page = 1,
                    positionInPage = 0,
                ),
            ),
        )

        // Now refresh with new data
        apiClient.directoryItemsResult = PagedResult(
            items = listOf(testDirectoryItem(id = "new-1", title = "新数据")),
            page = 1,
            pageSize = 20,
            hasMore = false,
        )
        val mediator = createMediator()
        val result = mediator.load(LoadType.REFRESH, pagingState())

        assertTrue(result is MediatorResult.Success)
        val items = database.directoryItemDao().pagingSource(queryKey).loadSinglePage()
        assertEquals(1, items.size)
        assertEquals("新数据", items.first().title)
    }

    @Test
    fun refreshReturnsSuccessWithEndOfPaginationWhenNoMorePages() = runTest {
        apiClient.directoryItemsResult = PagedResult(
            items = listOf(testDirectoryItem(id = "only", title = "唯一")),
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
        apiClient.failure = IOException("Network unavailable")
        val mediator = createMediator()
        val result = mediator.load(LoadType.REFRESH, pagingState())

        assertTrue(result is MediatorResult.Error)
        assertEquals("Network unavailable", (result as MediatorResult.Error).throwable.message)
    }

    // endregion

    // region APPEND

    @Test
    fun appendUsesRemoteKeyNextPage() = runTest {
        // First, simulate a REFRESH that stored a remote key
        database.directoryRemoteKeyDao().upsert(
            DirectoryRemoteKeyEntity(
                queryKey = queryKey,
                nextPage = 2,
                hasMore = true,
            ),
        )

        apiClient.directoryItemsResult = PagedResult(
            items = listOf(testDirectoryItem(id = "d2", title = "第二页项目")),
            page = 2,
            pageSize = 20,
            hasMore = false,
        )
        val mediator = createMediator()
        val result = mediator.load(LoadType.APPEND, pagingState())

        assertTrue(result is MediatorResult.Success)
        assertTrue((result as MediatorResult.Success).endOfPaginationReached)

        // Verify the correct page was requested
        assertEquals(1, apiClient.directoryItemRequests.size)
        assertEquals(2, apiClient.directoryItemRequests.first().page)
    }

    @Test
    fun appendEndsPaginationWhenRemoteKeyHasNoMore() = runTest {
        database.directoryRemoteKeyDao().upsert(
            DirectoryRemoteKeyEntity(
                queryKey = queryKey,
                nextPage = null,
                hasMore = false,
            ),
        )
        val mediator = createMediator()
        val result = mediator.load(LoadType.APPEND, pagingState())

        assertTrue(result is MediatorResult.Success)
        assertTrue((result as MediatorResult.Success).endOfPaginationReached)
        // Should not have made any API call
        assertTrue(apiClient.directoryItemRequests.isEmpty())
    }

    @Test
    fun appendReturnsErrorOnNetworkFailure() = runTest {
        database.directoryRemoteKeyDao().upsert(
            DirectoryRemoteKeyEntity(
                queryKey = queryKey,
                nextPage = 2,
                hasMore = true,
            ),
        )
        apiClient.failure = IOException("Timeout")
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
        apiClient.directoryItemsResult = PagedResult(
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

    private fun createMediator() = DirectoryRemoteMediator(
        query = query,
        database = database,
        apiClient = apiClient,
    )

    private fun pagingState() = PagingState<Int, DirectoryItemEntity>(
        pages = emptyList(),
        anchorPosition = null,
        config = PagingConfig(pageSize = 20),
        leadingPlaceholderCount = 0,
    )

    private fun testDirectoryItem(id: String, title: String) = DirectoryItemSummaryDto(
        id = id,
        kind = DirectoryItemKind.NationalProject,
        title = title,
        summary = "摘要",
        category = "传统美术",
        region = "北京",
        projectCode = "VII-001",
        batch = "第一批",
        publishedYear = 2006,
        listType = "representative",
        sourceUrl = "https://src.test/$id",
    )

    // Helper to load a single page from PagingSource
    private suspend fun androidx.paging.PagingSource<Int, DirectoryItemEntity>.loadSinglePage(): List<DirectoryItemEntity> {
        val loadResult = this.load(
            androidx.paging.PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 100,
                placeholdersEnabled = false,
            ),
        )
        @Suppress("UNCHECKED_CAST")
        return (loadResult as androidx.paging.PagingSource.LoadResult.Page<Int, DirectoryItemEntity>).data
    }
}

private class FakeApiClient : HeritageApiClient {
    var directoryItemsResult: PagedResult<DirectoryItemSummaryDto> = PagedResult()
    val directoryItemRequests = mutableListOf<DirectoryItemQuery>()
    var failure: Throwable? = null

    override suspend fun getHomeBanners(): List<HomeBannerDto> = emptyList()
    override suspend fun getArticles(query: com.duckylife.heritage.modern.core.network.ArticleQuery) = PagedResult<com.duckylife.heritage.modern.core.network.dto.ArticleSummaryDto>()
    override suspend fun getArticle(id: String) = ArticleDetailDto()
    override suspend fun getArticleBySourceId(sourceId: String, category: ArticleCategory) = ArticleDetailDto()
    override suspend fun getArticleBySourceUrl(sourceUrl: String, category: ArticleCategory) = ArticleDetailDto()
    override suspend fun getDirectoryItems(query: DirectoryItemQuery): PagedResult<DirectoryItemSummaryDto> {
        failure?.let { throw it }
        directoryItemRequests.add(query)
        return directoryItemsResult
    }

    override suspend fun getDirectoryItem(id: String) = DirectoryItemDetailDto()
    override suspend fun getDirectoryItemBySourceId(sourceId: String, kind: DirectoryItemKind) = DirectoryItemDetailDto()
    override suspend fun getInheritors(query: com.duckylife.heritage.modern.core.network.InheritorQuery) = PagedResult<InheritorSummaryDto>()
    override suspend fun getInheritor(id: String) = InheritorDetailDto()
    override suspend fun getInheritorBySourceId(sourceId: String) = InheritorDetailDto()
}
