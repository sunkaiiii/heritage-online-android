package com.duckylife.heritage.modern.feature.graph.model

import com.duckylife.heritage.modern.core.network.dto.advanced.GraphEdgeDto
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphNodeDto
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphNodeType

/**
 * 将后端图谱 DTO 映射为 UI 共享模型，并完成去重与 dangling 边过滤。
 */
fun GraphNodeDto.toGraphNodeUiModel(): GraphNodeUiModel = GraphNodeUiModel(
    nodeKey = nodeKey,
    type = type,
    id = id?.takeIf { it.isNotBlank() } ?: nodeKey.extractNodeId(type),
    title = title,
    subtitle = subtitle,
    category = category,
    region = region,
    coverImageUrl = coverImageUrl,
)

/**
 * 从 [nodeKey] 中提取节点 ID。
 *
 * 后端图谱节点通常以 `{type}:{id}` 形式返回 [nodeKey]，而单独的 [id] 字段可能缺失。
 * 对于内容节点（article/directoryItem/inheritor）和主题节点（category/region/year/kind/projectCode），
 * 均取 `:` 之后的部分作为有效标识；无法提取时回退整个 [nodeKey]。
 */
private fun String.extractNodeId(type: GraphNodeType): String? {
    if (isBlank()) return null
    val prefix = "${type.wireName}:"
    return when {
        startsWith(prefix) -> removePrefix(prefix)
        contains(":") -> substringAfter(":")
        else -> this
    }
}

/**
 * 节点列表按 [nodeKey] 去重；同一 key 取第一个。
 */
fun List<GraphNodeDto>.toGraphNodeUiModels(): List<GraphNodeUiModel> =
    map { it.toGraphNodeUiModel() }
        .distinctBy { it.nodeKey }

fun GraphEdgeDto.toGraphEdgeUiModel(): GraphEdgeUiModel = GraphEdgeUiModel(
    fromNodeKey = from,
    toNodeKey = to,
    relationType = type,
    label = label,
    reason = reason,
    source = source,
    weight = weight,
)

/**
 * 边列表映射后过滤 dangling 边并按 `(from, to, relationType)` 去重。
 *
 * @param availableNodeKeys 当前已加载的节点 key 集合，边两端必须同时存在。
 */
fun List<GraphEdgeDto>.toGraphEdgeUiModels(
    availableNodeKeys: Set<String>,
): List<GraphEdgeUiModel> = map { it.toGraphEdgeUiModel() }
    .filter { it.fromNodeKey in availableNodeKeys && it.toNodeKey in availableNodeKeys }
    .distinctBy { Triple(it.fromNodeKey, it.toNodeKey, it.relationType) }
