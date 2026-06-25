package com.duckylife.heritage.modern.feature.graph.model

import com.duckylife.heritage.modern.core.network.dto.advanced.GraphNodeType

/**
 * 知识图谱节点在 UI 层的共享模型。
 *
 * 保留 [nodeKey] 作为列表 key 与边对齐的稳定标识；title 仅用于展示，不能作为 key。
 */
data class GraphNodeUiModel(
    val nodeKey: String,
    val type: GraphNodeType,
    val id: String? = null,
    val title: String? = null,
    val subtitle: String? = null,
    val category: String? = null,
    val region: String? = null,
    val coverImageUrl: String? = null,
) {
    val isContentNode: Boolean
        get() = type == GraphNodeType.Article ||
            type == GraphNodeType.DirectoryItem ||
            type == GraphNodeType.Inheritor

    val displayTitle: String
        get() = title?.takeIf { it.isNotBlank() } ?: nodeKey
}
