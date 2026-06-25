package com.duckylife.heritage.modern.feature.graph.format

import com.duckylife.heritage.modern.R
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphRelationType
import com.duckylife.heritage.modern.feature.graph.model.AssociationLevel

/**
 * 知识图谱关系类型的显示辅助。
 *
 * 关系文案通过 string resource 渲染，避免在 model 层固化单一语言。
 */
object GraphRelationFormatter {

    fun labelResId(relationType: GraphRelationType): Int = when (relationType) {
        GraphRelationType.RelatedTo -> R.string.graph_relation_type_related_to
        GraphRelationType.InheritsProject -> R.string.graph_relation_type_inherits_project
        GraphRelationType.Similar -> R.string.graph_relation_type_similar
        GraphRelationType.Topic -> R.string.graph_relation_type_topic
        GraphRelationType.AiInferred -> R.string.graph_relation_type_ai_inferred
        GraphRelationType.Unknown -> R.string.graph_relation_type_unknown
    }

    fun associationLevel(score: Double): AssociationLevel = when {
        score >= 0.75 -> AssociationLevel.High
        score >= 0.45 -> AssociationLevel.Medium
        else -> AssociationLevel.Low
    }

    fun associationLevelLabelResId(level: AssociationLevel): Int = when (level) {
        AssociationLevel.High -> R.string.graph_association_level_high
        AssociationLevel.Medium -> R.string.graph_association_level_medium
        AssociationLevel.Low -> R.string.graph_association_level_low
    }

    fun confidenceLabelResId(confidence: Double): Int = when {
        confidence >= 0.75 -> R.string.graph_confidence_high
        confidence >= 0.45 -> R.string.graph_confidence_medium
        else -> R.string.graph_confidence_low
    }

    /**
     * 判断当前关系是否应被“包含 AI 推断关系”开关控制。
     */
    fun isControlledByAiToggle(relationType: GraphRelationType): Boolean =
        relationType == GraphRelationType.AiInferred
}
