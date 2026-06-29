package com.duckylife.heritage.modern.ui.text

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.duckylife.heritage.modern.R
import com.duckylife.heritage.modern.core.network.dto.advanced.TrailStrategy

/**
 * 将图谱 trails / journeys 策略 wire value 映射为本地化显示文案。
 */
@Composable
fun localizedTrailStrategy(strategy: TrailStrategy): String =
    when (strategy) {
        TrailStrategy.Mixed -> stringResource(R.string.trail_strategy_mixed)
        TrailStrategy.TopicLadder -> stringResource(R.string.trail_strategy_topic_ladder)
        TrailStrategy.HiddenGem -> stringResource(R.string.trail_strategy_hidden_gem)
        TrailStrategy.BridgeWalk -> stringResource(R.string.trail_strategy_bridge_walk)
        TrailStrategy.Similar -> stringResource(R.string.trail_strategy_similar)
        TrailStrategy.Bridge -> stringResource(R.string.trail_strategy_bridge)
        TrailStrategy.Representative -> stringResource(R.string.trail_strategy_representative)
        TrailStrategy.Diverse -> stringResource(R.string.trail_strategy_diverse)
        TrailStrategy.Unknown -> stringResource(R.string.trail_strategy_unknown)
    }
