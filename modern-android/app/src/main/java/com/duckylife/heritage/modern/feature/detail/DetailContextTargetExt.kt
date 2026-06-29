package com.duckylife.heritage.modern.feature.detail

import android.util.Log
import com.duckylife.heritage.modern.core.network.dto.SearchResultType
import com.duckylife.heritage.modern.feature.graph.model.GraphNodeUiModel
import com.duckylife.heritage.modern.feature.learningroutes.model.LearningRouteStepUiModel
import com.duckylife.heritage.modern.feature.my.JourneyTrailStepUiItem
import com.duckylife.heritage.modern.feature.my.JourneyUiItem
import com.duckylife.heritage.modern.feature.rankings.model.RankingItemUiModel

private const val TAG = "DetailContextTarget"

/**
 * 将图谱节点转换为详情页目标。
 *
 * 仅内容节点（article / directoryItem / inheritor）可转换；
 * 主题节点、未知节点或缺少 id 的节点返回 null，并在 debug 日志记录原因。
 */
fun GraphNodeUiModel.toDetailContextTarget(): DetailContextTarget? {
    val safeId = id?.takeIf { it.isNotBlank() } ?: run {
        Log.d(TAG, "GraphNode $nodeKey has no id, cannot navigate")
        return null
    }
    return when (type.wireName) {
        SearchResultType.Article.wireName -> DetailContextTarget.Article(safeId)
        SearchResultType.DirectoryItem.wireName -> DetailContextTarget.DirectoryItem(safeId)
        SearchResultType.Inheritor.wireName -> DetailContextTarget.Inheritor(safeId)
        else -> {
            Log.d(TAG, "GraphNode $nodeKey has unsupported type ${type.wireName}, cannot navigate")
            null
        }
    }
}

/**
 * 将排行榜项转换为详情页目标。
 */
fun RankingItemUiModel.toDetailContextTarget(): DetailContextTarget? {
    val type = contentType?.takeIf { it.isNotBlank() }
    val safeId = contentId?.takeIf { it.isNotBlank() }
    if (type == null || safeId == null) {
        Log.d(TAG, "RankingItem $title has no content id/type, cannot navigate")
        return null
    }
    return contextItemTarget(safeId, type)
}

/**
 * 将学习路线步骤转换为详情页目标。
 */
fun LearningRouteStepUiModel.toDetailContextTarget(): DetailContextTarget? {
    val type = targetType?.takeIf { it.isNotBlank() }
    val safeId = targetId?.takeIf { it.isNotBlank() }
    if (type == null || safeId == null) {
        Log.d(TAG, "LearningRouteStep $stepId has no target id/type, cannot navigate")
        return null
    }
    return contextItemTarget(safeId, type)
}

/**
 * 将旅程推荐项转换为详情页目标。
 */
fun JourneyUiItem.toDetailContextTarget(): DetailContextTarget? {
    val safeId = targetId?.takeIf { it.isNotBlank() }
    if (safeId == null) {
        Log.d(TAG, "JourneyUiItem $title has no target id, cannot navigate")
        return null
    }
    return contextItemTarget(safeId, contentType)
}

/**
 * 将旅程 trail 步骤转换为详情页目标。
 */
fun JourneyTrailStepUiItem.toDetailContextTarget(): DetailContextTarget? {
    val type = contentType?.takeIf { it.isNotBlank() }
    val safeId = targetId?.takeIf { it.isNotBlank() }
    if (type == null || safeId == null) {
        Log.d(TAG, "JourneyTrailStep $nodeKey has no target id/type, cannot navigate")
        return null
    }
    return contextItemTarget(safeId, type)
}

/**
 * 兼容旧版直接字符串 type 的转换。
 */
fun contextItemTargetFromType(type: String?, id: String?): DetailContextTarget? {
    val safeId = id?.takeIf { it.isNotBlank() } ?: return null
    return when (SearchResultType.fromWireName(type)) {
        SearchResultType.Article -> DetailContextTarget.Article(safeId)
        SearchResultType.DirectoryItem -> DetailContextTarget.DirectoryItem(safeId)
        SearchResultType.Inheritor -> DetailContextTarget.Inheritor(safeId)
        null -> {
            Log.d(TAG, "Unsupported content type $type for id $safeId")
            null
        }
    }
}


