package com.duckylife.heritage.modern.core.profile

import com.duckylife.heritage.modern.core.network.dto.advanced.JourneyResponseDto
import com.duckylife.heritage.modern.core.network.dto.advanced.JourneySignalsDto
import com.duckylife.heritage.modern.core.network.dto.advanced.JourneyStrategy
import kotlinx.coroutines.delay

class FakeJourneyRepository : JourneyRepository {

    var signalsResponse: JourneySignalsDto? = null
    var journeysResponse: JourneyResponseDto? = null
    var journeysException: Throwable? = null
    var signalsDelayMs: Long = 0
    var journeysDelayMs: Long = 0
    var lastRequestedStrategy: JourneyStrategy? = null
    var loadJourneysCallCount = 0
        private set

    override suspend fun loadJourneys(
        strategy: JourneyStrategy,
        limit: Int,
    ): JourneyResponseDto {
        lastRequestedStrategy = strategy
        loadJourneysCallCount++
        if (journeysDelayMs > 0) delay(journeysDelayMs)
        journeysException?.let { throw it }
        return journeysResponse ?: JourneyResponseDto(strategy = strategy)
    }

    override suspend fun loadSignals(): JourneySignalsDto {
        if (signalsDelayMs > 0) delay(signalsDelayMs)
        return signalsResponse ?: JourneySignalsDto()
    }
}
