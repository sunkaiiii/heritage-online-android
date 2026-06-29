package com.duckylife.heritage.modern.core.network.dto.advanced

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * 跨 AI、图谱、旅程共用的内容引用。
 */
@Serializable(with = ContentRefDtoSerializer::class)
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
 * 后端在不同高级 API 中历史上同时出现过两套内容引用字段：
 *
 * - V3 内容页：`type` / `id`
 * - 学习路线、排行榜等探索接口：`targetType` / `targetId`
 *
 * UI 层只需要稳定的 [ContentRefDto.type] 与 [ContentRefDto.id]，因此在 DTO 边界做兼容，
 * 避免某个接口字段别名导致整页反序列化失败。
 */
object ContentRefDtoSerializer : KSerializer<ContentRefDto> {
    override val descriptor: SerialDescriptor = ContentRefDtoSurrogate.serializer().descriptor

    override fun serialize(encoder: Encoder, value: ContentRefDto) {
        encoder.encodeSerializableValue(
            ContentRefDtoSurrogate.serializer(),
            ContentRefDtoSurrogate(
                type = value.type,
                id = value.id,
                title = value.title,
                subtitle = value.subtitle,
                category = value.category,
                region = value.region,
                year = value.year,
                kind = value.kind,
                coverImageUrl = value.coverImageUrl,
            ),
        )
    }

    override fun deserialize(decoder: Decoder): ContentRefDto {
        val surrogate = decoder.decodeSerializableValue(ContentRefDtoSurrogate.serializer())
        val resolvedType = surrogate.type
            ?.takeUnless { it == GraphNodeType.Unknown }
            ?: surrogate.targetType
            ?: GraphNodeType.Unknown
        val resolvedId = surrogate.id
            ?.takeIf { it.isNotBlank() }
            ?: surrogate.targetId.orEmpty()
        return ContentRefDto(
            type = resolvedType,
            id = resolvedId,
            title = surrogate.title,
            subtitle = surrogate.subtitle,
            category = surrogate.category,
            region = surrogate.region,
            year = surrogate.year,
            kind = surrogate.kind,
            coverImageUrl = surrogate.coverImageUrl,
        )
    }
}

@Serializable
private data class ContentRefDtoSurrogate(
    val type: GraphNodeType? = null,
    val targetType: GraphNodeType? = null,
    val id: String? = null,
    val targetId: String? = null,
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
