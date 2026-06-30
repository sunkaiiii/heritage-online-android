package com.duckylife.heritage.modern.core.profile

import com.duckylife.heritage.modern.core.network.api.LocalUserApi
import com.duckylife.heritage.modern.core.network.dto.advanced.JourneyResponseDto
import com.duckylife.heritage.modern.core.network.dto.advanced.JourneySignalsDto
import com.duckylife.heritage.modern.core.network.dto.advanced.JourneyStrategy
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 只读旅程与信号仓库。
 *
 * 旅程是服务端根据当前 profile 实时计算的推荐结果，不持久化到 Room；
 * 切换 strategy 时上层 ViewModel 负责取消旧请求。
 */
interface JourneyRepository {

    suspend fun loadJourneys(
        strategy: JourneyStrategy = JourneyStrategy.Balanced,
        limit: Int = DEFAULT_LIMIT,
    ): JourneyResponseDto

    suspend fun loadSignals(): JourneySignalsDto

    private companion object {
        const val DEFAULT_LIMIT = 8
    }
}

@Singleton
class DefaultJourneyRepository @Inject constructor(
    private val api: LocalUserApi,
) : JourneyRepository {

    override suspend fun loadJourneys(
        strategy: JourneyStrategy,
        limit: Int,
    ): JourneyResponseDto = api.getLocalUserJourneys(
        strategy = strategy,
        limit = limit.coerceIn(MIN_LIMIT, MAX_LIMIT),
        includeAiInferred = false,
        includeTrail = true,
    )

    override suspend fun loadSignals(): JourneySignalsDto = api.getLocalUserJourneySignals()

    companion object {
        private const val MIN_LIMIT = 1
        private const val MAX_LIMIT = 20
    }
}
