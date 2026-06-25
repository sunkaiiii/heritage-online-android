package com.duckylife.heritage.modern.feature.graph.format

import com.duckylife.heritage.modern.core.network.dto.advanced.GraphRelationType
import com.duckylife.heritage.modern.feature.graph.model.GraphEdgeUiModel

/**
 * 知识图谱关系类型的中文可读格式化器。
 *
 * 关系文案集中在这里维护，UI 不直接消费后端英文/内部 relation type。
 */
object GraphRelationFormatter {

    fun label(relationType: GraphRelationType): String = when (relationType) {
        GraphRelationType.RelatedTo -> "相关内容"
        GraphRelationType.InheritsProject -> "传承项目"
        GraphRelationType.Similar -> "相似内容"
        GraphRelationType.Topic -> "同属主题"
        GraphRelationType.AiInferred -> "AI 推断"
        GraphRelationType.Unknown -> "其他关系"
    }

    fun label(edge: GraphEdgeUiModel): String =
        edge.label?.takeIf { it.isNotBlank() } ?: label(edge.relationType)

    /**
     * 判断当前关系是否应被“包含 AI 推断关系”开关控制。
     */
    fun isControlledByAiToggle(relationType: GraphRelationType): Boolean =
        relationType == GraphRelationType.AiInferred
}
