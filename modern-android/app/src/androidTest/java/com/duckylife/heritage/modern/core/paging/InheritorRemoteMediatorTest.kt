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
import com.duckylife.heritage.modern.core.database.entity.InheritorEntity
import com.duckylife.heritage.modern.core.database.entity.InheritorRemoteKeyEntity
import com.duckylife.heritage.modern.core.database.mapper.queryKey
import com.duckylife.heritage.modern.core.network.InheritorQuery
import com.duckylife.heritage.modern.core.network.dto.InheritorSummaryDto
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
class InheritorRemoteMediatorTest {

    private lateinit var database: HeritageDatabase
    private lateinit var apiClient: FakeHeritageApiClient
    private val query = InheritorQuery(pageSize = 20)
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
        apiClient.inheritorsResult = PagedResult(
            items = listOf(testInheritor(id = "i1", name = "张三")),
            page = 1,
            pageSize = 20,
            hasMore = true,
            total = 30,
        )
        val mediator = createMediator()
        val result = mediator.load(LoadType.REFRESH, pagingState())

        assertTrue(result is MediatorResult.Success)
        assertFalse((result as MediatorResult.Success).endOfPaginationReached)

        // 传承人字段正确写入 Room
        val items = database.inheritorDao().pagingSource(queryKey).loadSinglePage()
        assertEquals(1, items.size)
        assertEquals("张三", items.first().name)

        val remoteKey = database.inheritorRemoteKeyDao().remoteKey(queryKey)
        assertNotNull(remoteKey)
        assertEquals(2, remoteKey?.nextPage)
        assertTrue(remoteKey?.hasMore == true)
    }

    @Test
    fun refreshClearsOldDataBeforeInsert() = runTest {
        // 预填充同 queryKey 的旧数据
        database.inheritorDao().upsertAll(
            listOf(
                InheritorEntity(
                    id = "old-1",
                    queryKey = queryKey,
                    name = "旧传承人",
                    gender = null,
                    birthDateText = null,
                    ethnicity = null,
                    category = null,
                    projectCode = null,
                    projectName = null,
                    region = null,
                    batch = null,
                    description = null,
                    coverImageJson = null,
                    sourceUrl = null,
                    page = 1,
                    positionInPage = 0,
                ),
            ),
        )

        apiClient.inheritorsResult = PagedResult(
            items = listOf(testInheritor(id = "new-1", name = "新传承人")),
            page = 1,
            pageSize = 20,
            hasMore = false,
        )
        val mediator = createMediator()
        val result = mediator.load(LoadType.REFRESH, pagingState())

        assertTrue(result is MediatorResult.Success)
        val items = database.inheritorDao().pagingSource(queryKey).loadSinglePage()
        assertEquals(1, items.size)
        assertEquals("新传承人", items.first().name)
    }

    @Test
    fun refreshReturnsSuccessWithEndOfPaginationWhenNoMorePages() = runTest {
        apiClient.inheritorsResult = PagedResult(
            items = listOf(testInheritor(id = "only", name = "唯一")),
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
        database.inheritorRemoteKeyDao().upsert(
            InheritorRemoteKeyEntity(
                queryKey = queryKey,
                nextPage = 2,
                hasMore = true,
            ),
        )

        apiClient.inheritorsResult = PagedResult(
            items = listOf(testInheritor(id = "i2", name = "第二页传承人")),
            page = 2,
            pageSize = 20,
            hasMore = false,
        )
        val mediator = createMediator()
        val result = mediator.load(LoadType.APPEND, pagingState())

        assertTrue(result is MediatorResult.Success)
        assertTrue((result as MediatorResult.Success).endOfPaginationReached)

        assertEquals(1, apiClient.inheritorRequests.size)
        assertEquals(2, apiClient.inheritorRequests.first().page)
    }

    @Test
    fun appendEndsPaginationWhenRemoteKeyHasNoMore() = runTest {
        database.inheritorRemoteKeyDao().upsert(
            InheritorRemoteKeyEntity(
                queryKey = queryKey,
                nextPage = null,
                hasMore = false,
            ),
        )
        val mediator = createMediator()
        val result = mediator.load(LoadType.APPEND, pagingState())

        assertTrue(result is MediatorResult.Success)
        assertTrue((result as MediatorResult.Success).endOfPaginationReached)
        assertTrue(apiClient.inheritorRequests.isEmpty())
    }

    @Test
    fun appendReturnsErrorOnNetworkFailure() = runTest {
        database.inheritorRemoteKeyDao().upsert(
            InheritorRemoteKeyEntity(
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
        apiClient.inheritorsResult = PagedResult(
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
        val otherKey = InheritorQuery(keywords = "剪纸", pageSize = 20).queryKey()

        // 写入另一个 queryKey 的数据
        database.inheritorDao().upsertAll(
            listOf(
                InheritorEntity(
                    id = "other-1",
                    queryKey = otherKey,
                    name = "李四",
                    gender = "女",
                    birthDateText = null,
                    ethnicity = null,
                    category = null,
                    projectCode = null,
                    projectName = null,
                    region = null,
                    batch = null,
                    description = null,
                    coverImageJson = null,
                    sourceUrl = null,
                    page = 1,
                    positionInPage = 0,
                ),
            ),
        )

        apiClient.inheritorsResult = PagedResult(
            items = listOf(testInheritor(id = "new-1", name = "新传承人")),
            page = 1,
            pageSize = 20,
            hasMore = false,
        )
        val mediator = createMediator()
        mediator.load(LoadType.REFRESH, pagingState())

        val currentItems = database.inheritorDao().pagingSource(queryKey).loadSinglePage()
        assertEquals(1, currentItems.size)
        assertEquals("新传承人", currentItems.first().name)

        // 另一个 queryKey 的数据未被清理
        val otherItems = database.inheritorDao().pagingSource(otherKey).loadSinglePage()
        assertEquals(1, otherItems.size)
        assertEquals("李四", otherItems.first().name)
    }

    // endregion

    private fun createMediator() = InheritorRemoteMediator(
        query = query,
        database = database,
        apiClient = apiClient,
    )

    private fun pagingState() = PagingState<Int, InheritorEntity>(
        pages = emptyList(),
        anchorPosition = null,
        config = PagingConfig(pageSize = 20),
        leadingPlaceholderCount = 0,
    )

    private fun testInheritor(id: String, name: String) = InheritorSummaryDto(
        id = id,
        name = name,
        gender = "男",
        category = "传统美术",
        region = "北京",
        projectName = "剪纸",
        sourceUrl = "https://src.test/$id",
    )

    // 从 PagingSource 加载单页数据
    private suspend fun androidx.paging.PagingSource<Int, InheritorEntity>.loadSinglePage(): List<InheritorEntity> {
        val loadResult = this.load(
            androidx.paging.PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 100,
                placeholdersEnabled = false,
            ),
        )
        @Suppress("UNCHECKED_CAST")
        return (loadResult as androidx.paging.PagingSource.LoadResult.Page<Int, InheritorEntity>).data
    }
}
