package com.duckylife.heritage.modern.core.network.api

import com.duckylife.heritage.modern.core.network.LocalUserFavoritesQuery
import com.duckylife.heritage.modern.core.network.LocalUserHistoryQuery
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
import com.duckylife.heritage.modern.core.network.dto.advanced.JourneyStrategy

/**
 * `/api/local-user/...` 端点契约。
 *
 * 所有接口均通过 [com.duckylife.heritage.modern.core.profile.LocalProfileRepository]
 * 生成的 `X-Heritage-Profile-Id` header 区分用户。
 */
interface LocalUserApi {
    suspend fun getLocalUserProfile(): LocalUserProfileDto

    suspend fun getLocalUserSummary(): LocalUserSummaryDto

    suspend fun getLocalUserFavorites(query: LocalUserFavoritesQuery = LocalUserFavoritesQuery()): PagedResult<LocalFavoriteDto>

    suspend fun addLocalUserFavorite(request: FavoriteCreateRequestDto): LocalFavoriteDto

    suspend fun removeLocalUserFavorite(targetType: String, targetId: String)

    suspend fun getLocalUserHistory(query: LocalUserHistoryQuery = LocalUserHistoryQuery()): PagedResult<LocalHistoryDto>

    suspend fun recordLocalUserHistory(request: HistoryRecordRequestDto): LocalHistoryDto

    suspend fun clearLocalUserHistory(): Int

    suspend fun getLocalUserLearningProgress(): List<LocalLearningProgressDto>

    suspend fun updateLocalUserLearningProgress(
        routeId: String,
        request: LearningProgressUpdateDto,
    ): LocalLearningProgressDto

    suspend fun getLocalUserJourneys(
        strategy: JourneyStrategy = JourneyStrategy.Balanced,
        limit: Int = 8,
        includeAiInferred: Boolean = false,
        includeTrail: Boolean = true,
    ): JourneyResponseDto

    suspend fun getLocalUserJourneySignals(): JourneySignalsDto
}
