package com.duckylife.heritage.modern.core.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.duckylife.heritage.modern.core.database.HeritageDatabase
import com.duckylife.heritage.modern.core.database.entity.DirectoryItemEntity
import com.duckylife.heritage.modern.core.database.entity.DirectoryRemoteKeyEntity
import com.duckylife.heritage.modern.core.database.mapper.queryKey
import com.duckylife.heritage.modern.core.database.mapper.toEntity
import com.duckylife.heritage.modern.core.network.DirectoryItemQuery
import com.duckylife.heritage.modern.core.network.HeritageApiClient
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class DirectoryRemoteMediator(
    private val query: DirectoryItemQuery,
    private val database: HeritageDatabase,
    private val apiClient: HeritageApiClient,
) : RemoteMediator<Int, DirectoryItemEntity>() {
    private val directoryItemDao = database.directoryItemDao()
    private val remoteKeyDao = database.directoryRemoteKeyDao()
    private val queryKey = query.queryKey()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, DirectoryItemEntity>,
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> 1
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> {
                val remoteKey = remoteKeyDao.remoteKey(queryKey)
                if (remoteKey?.hasMore == false || remoteKey?.nextPage == null) {
                    return MediatorResult.Success(endOfPaginationReached = true)
                }
                remoteKey.nextPage
            }
        }

        return try {
            val pageSize = state.config.pageSize.coerceAtLeast(query.pageSize)
            val pageQuery = query.copy(page = page, pageSize = pageSize)
            val response = apiClient.getDirectoryItems(pageQuery)
            val entities = response.items.mapIndexed { index, item ->
                item.toEntity(
                    query = query,
                    page = page,
                    positionInPage = index,
                )
            }

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    remoteKeyDao.clearByQuery(queryKey)
                    directoryItemDao.clearByQuery(queryKey)
                }
                directoryItemDao.upsertAll(entities)
                remoteKeyDao.upsert(
                    DirectoryRemoteKeyEntity(
                        queryKey = queryKey,
                        nextPage = if (response.hasMore) page + 1 else null,
                        hasMore = response.hasMore,
                    ),
                )
            }

            MediatorResult.Success(endOfPaginationReached = !response.hasMore)
        } catch (throwable: IOException) {
            MediatorResult.Error(throwable)
        } catch (throwable: RuntimeException) {
            MediatorResult.Error(throwable)
        }
    }
}
