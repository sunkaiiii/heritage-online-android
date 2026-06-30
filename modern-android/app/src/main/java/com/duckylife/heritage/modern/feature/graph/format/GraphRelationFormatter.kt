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

    /**
     * 将后端原始关系类型字符串映射为本地化资源 ID。
     *
     * 同时覆盖 V3 图谱枚举 wire name（如 `RELATED_TO`）与旧版 Context API 的
     * 业务关系码（如 `relatedArticle`、`semanticSimilarity`）。未知值返回 `null`，
     * 调用方应回退到后端提供的 [label] 或隐藏该字段。
     */
    fun labelResId(rawRelationType: String?): Int? = when (rawRelationType) {
        GraphRelationType.RelatedTo.wireName -> R.string.graph_relation_type_related_to
        GraphRelationType.InheritsProject.wireName -> R.string.graph_relation_type_inherits_project
        GraphRelationType.Similar.wireName -> R.string.graph_relation_type_similar
        GraphRelationType.Topic.wireName -> R.string.graph_relation_type_topic
        GraphRelationType.AiInferred.wireName -> R.string.graph_relation_type_ai_inferred
        GraphRelationType.Unknown.wireName -> R.string.graph_relation_type_unknown
        "relatedArticle" -> R.string.graph_relation_related_article
        "relatedDirectoryItem" -> R.string.graph_relation_related_directory_item
        "relatedInheritor" -> R.string.graph_relation_related_inheritor
        "semanticSimilarity" -> R.string.graph_relation_semantic_similarity
        "sameCategory" -> R.string.graph_relation_same_category
        "sameRegion" -> R.string.graph_relation_same_region
        "sameTopic" -> R.string.graph_relation_same_topic
        else -> null
    }
}
