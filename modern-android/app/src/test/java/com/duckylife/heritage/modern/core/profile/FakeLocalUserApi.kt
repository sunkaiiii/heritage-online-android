package com.duckylife.heritage.modern.core.profile

import com.duckylife.heritage.modern.core.network.LocalUserFavoritesQuery
import com.duckylife.heritage.modern.core.network.LocalUserHistoryQuery
import com.duckylife.heritage.modern.core.network.api.LocalUserApi
import com.duckylife.heritage.modern.core.network.dto.PagedResult
import com.duckylife.heritage.modern.core.network.dto.advanced.FavoriteCreateRequestDto
import com.duckylife.heritage.modern.core.network.dto.advanced.HistoryRecordRequestDto
import com.duckylife.heritage.modern.core.network.dto.advanced.JourneyResponseDto
import com.duckylife.heritage.modern.core.network.dto.advanced.JourneySignalsDto
import com.duckylife.heritage.modern.core.network.dto.advanced.LearningProgressUpdateDto
import com.duckylife.heritage.modern.core.network.dto.advanced.LocalFavoriteDto
import com.duckylife.heritage.modern.core.network.dto.advanced.LocalHistoryDto
import com.duckylife.heritage.modern.core.network.dto.advanced.LocalLearningProgressDto
import com.duckylife.heritage.modern.core.network.dto.advanced.LocalUserProfileDto
import com.duckylife.heritage.modern.core.network.dto.advanced.LocalUserSummaryDto
import com.duckylife.heritage.modern.core.network.dto.advanced.LocalUserTargetType
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.get
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf

class FakeLocalUserApi : LocalUserApi {

    var pullFailure: ResponseException? = null
    var addFavoriteFailure: ResponseException? = null
    var addFavoriteResult: LocalFavoriteDto? = null
    var favorites: List<LocalFavoriteDto> = emptyList()
    var recordHistoryResult: LocalHistoryDto? = null
    var history: List<LocalHistoryDto> = emptyList()
    var progress: List<LocalLearningProgressDto> = emptyList()
    var updateProgressResult: LocalLearningProgressDto? = null
    var clearHistoryCount: Int = 0

    override suspend fun getLocalUserProfile(): LocalUserProfileDto {
        pullFailure?.let { throw it }
        return LocalUserProfileDto(profileId = "android_test")
    }

    override suspend fun getLocalUserSummary(): LocalUserSummaryDto {
        pullFailure?.let { throw it }
        return LocalUserSummaryDto(profileId = "android_test")
    }

    override suspend fun getLocalUserFavorites(
        query: LocalUserFavoritesQuery,
    ): PagedResult<LocalFavoriteDto> {
        pullFailure?.let { throw it }
        return PagedResult(items = favorites, hasMore = false)
    }

    override suspend fun addLocalUserFavorite(request: FavoriteCreateRequestDto): LocalFavoriteDto {
        addFavoriteFailure?.let { throw it }
        return addFavoriteResult ?: LocalFavoriteDto(
            id = "fav:${request.targetType}:${request.targetId}",
            targetType = request.targetType,
            targetId = request.targetId,
        )
    }

    override suspend fun removeLocalUserFavorite(targetType: LocalUserTargetType, targetId: String) = Unit

    override suspend fun getLocalUserHistory(
        query: LocalUserHistoryQuery,
    ): PagedResult<LocalHistoryDto> {
        pullFailure?.let { throw it }
        return PagedResult(items = history, hasMore = false)
    }

    override suspend fun recordLocalUserHistory(request: HistoryRecordRequestDto): LocalHistoryDto {
        return recordHistoryResult ?: LocalHistoryDto(
            id = "hist:${request.targetType}:${request.targetId}",
            targetType = request.targetType,
            targetId = request.targetId,
        )
    }

    override suspend fun clearLocalUserHistory(): Int {
        clearHistoryCount++
        return 0
    }

    override suspend fun getLocalUserLearningProgress(): List<LocalLearningProgressDto> {
        pullFailure?.let { throw it }
        return progress
    }

    override suspend fun updateLocalUserLearningProgress(
        routeId: String,
        request: LearningProgressUpdateDto,
    ): LocalLearningProgressDto {
        return updateProgressResult ?: LocalLearningProgressDto(
            routeId = routeId,
            completedStepIds = request.completedStepIds,
            currentStepId = request.currentStepId,
        )
    }

    override suspend fun getLocalUserJourneys(
        strategy: com.duckylife.heritage.modern.core.network.dto.advanced.JourneyStrategy,
        limit: Int,
        includeAiInferred: Boolean,
        includeTrail: Boolean,
    ): JourneyResponseDto = JourneyResponseDto()

    override suspend fun getLocalUserJourneySignals(): JourneySignalsDto = JourneySignalsDto()

    companion object {
        suspend fun createResponseException(status: HttpStatusCode): ResponseException {
            val mockEngine = MockEngine { _ ->
                respond(
                    content = "{}",
                    status = status,
                    headers = headersOf(HttpHeaders.ContentType, "application/json"),
                )
            }
            val client = HttpClient(mockEngine) { expectSuccess = true }
            return try {
                client.get("/test")
                error("Expected exception")
            } catch (e: ResponseException) {
                e
            } finally {
                client.close()
            }
        }
    }
}
