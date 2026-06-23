package com.duckylife.heritage.modern.core.network.dto.advanced

import kotlinx.serialization.Serializable

/**
 * 跨 AI、图谱、旅程共用的内容引用。
 */
@Serializable
data class ContentRefDto(
    val type: GraphNodeType,
    val id: String,
    val title: String? = null,
    val subtitle: String? = null,
    val category: String? = null,
    val region: String? = null,
    val year: Int? = null,
    val kind: String? = null,
    val coverImageUrl: String? = null,
)

/**
 * 图谱专用的节点/内容引用，保留 [nodeKey] 以便与边对齐。
 */
@Serializable
data class GraphNodeDto(
    val nodeKey: String,
    val type: GraphNodeType,
    val id: String? = null,
    val title: String? = null,
    val subtitle: String? = null,
    val category: String? = null,
    val region: String? = null,
    val year: Int? = null,
    val kind: String? = null,
    val projectCode: String? = null,
    val coverImageUrl: String? = null,
)

/**
 * 图谱边。
 */
@Serializable
data class GraphEdgeDto(
    val from: String,
    val to: String,
    val type: GraphRelationType,
    val label: String? = null,
    val reason: String? = null,
    val source: GraphEvidenceSource? = null,
    val weight: Double? = null,
    val aiConfidence: Double? = null,
)

/**
 * 通用原因/证据项。
 */
@Serializable
data class GraphReasonDto(
    val code: String? = null,
    val message: String? = null,
    val severity: String? = null,
)
