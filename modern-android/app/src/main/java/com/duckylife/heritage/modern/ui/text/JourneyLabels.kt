package com.duckylife.heritage.modern.ui.text

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.duckylife.heritage.modern.R
import com.duckylife.heritage.modern.core.network.dto.advanced.JourneyStrategy

@Composable
fun localizedJourneyStrategy(strategy: JourneyStrategy): String = when (strategy) {
    JourneyStrategy.Balanced -> stringResource(R.string.journeys_strategy_balanced)
    JourneyStrategy.Continue -> stringResource(R.string.journeys_strategy_continue)
    JourneyStrategy.Novelty -> stringResource(R.string.journeys_strategy_novelty)
    JourneyStrategy.DeepDive -> stringResource(R.string.journeys_strategy_deep_dive)
    JourneyStrategy.Unknown -> stringResource(R.string.journeys_strategy_balanced)
}

fun journeyWarningResId(warning: String?): Int = when (warning) {
    "cold_start" -> R.string.journeys_warning_cold_start
    "graph_unavailable" -> R.string.journeys_warning_graph_unavailable
    null -> 0
    else -> R.string.journeys_warning_generic
}
