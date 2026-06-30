package com.duckylife.heritage.modern.ui.text

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.duckylife.heritage.modern.R
import com.duckylife.heritage.modern.core.network.dto.advanced.LearningRouteDifficulty

/**
 * 将内容类型 wire value 映射为本地化显示文案。
 *
 * 覆盖 article / directoryItem / inheritor / collection / topic。
 * 未知值原样返回，不崩溃。
 */
@Composable
fun localizedContentType(type: String?): String =
    when (type) {
        "article" -> stringResource(R.string.search_type_article)
        "directoryItem" -> stringResource(R.string.search_type_directory)
        "inheritor" -> stringResource(R.string.search_type_inheritor)
        "collection" -> stringResource(R.string.context_collections)
        "topic" -> stringResource(R.string.context_explore_topics)
        else -> type.orEmpty()
    }

/**
 * 将文章分类 wire value 映射为本地化显示文案。
 *
 * 覆盖 news / forum / specialTopic。
 * 未知值返回 null。
 */
@Composable
fun localizedArticleCategory(category: String?): String? =
    when (category.normalizedWireName()) {
        "news" -> stringResource(R.string.category_news)
        "forum" -> stringResource(R.string.category_forum)
        "specialtopic" -> stringResource(R.string.category_special_topic)
        else -> category?.takeIf { it.isNotBlank() }
    }

/**
 * 将名录种类 wire value 映射为本地化显示文案。
 *
 * 覆盖 nationalProject / culturalEcoZone / productiveProtectionBase / unescoEntry / chinaUnescoEntry / contractingState。
 * 未知值返回 null。
 */
@Composable
fun localizedDirectoryKind(kind: String?): String? =
    when (kind.normalizedWireName()) {
        "nationalproject" -> stringResource(R.string.directory_kind_national_project)
        "culturalecozone" -> stringResource(R.string.directory_kind_cultural_eco_zone)
        "productiveprotectionbase" -> stringResource(R.string.directory_kind_productive_protection_base)
        "unescoentry" -> stringResource(R.string.directory_kind_unesco_entry)
        "chinaunescoentry" -> stringResource(R.string.directory_kind_china_unesco_entry)
        "contractingstate" -> stringResource(R.string.directory_kind_contracting_state)
        else -> kind?.takeIf { it.isNotBlank() }
    }

/**
 * 将图谱主题类型 wire value 映射为本地化显示文案。
 */
@Composable
fun localizedTopicType(type: String?): String =
    when (type.normalizedWireName()) {
        "category" -> stringResource(R.string.dimension_category)
        "region" -> stringResource(R.string.dimension_region)
        "year" -> stringResource(R.string.dimension_year)
        "kind" -> stringResource(R.string.dimension_kind)
        "projectcode" -> stringResource(R.string.directory_field_project_code)
        else -> localizedContentType(type).ifBlank { type.orEmpty() }
    }

/**
 * 将合集类型 wire value 映射为本地化显示文案。
 */
@Composable
fun localizedCollectionType(type: String?): String? =
    when (type.normalizedWireName()) {
        "static" -> stringResource(R.string.collection_type_static)
        "specialtopic" -> stringResource(R.string.collection_type_special_topic)
        else -> type?.takeIf { it.isNotBlank() }
    }

/**
 * 统一处理高级接口中常见的分类/kind/topic 裸 wire value，避免英文界面出现
 * `specialTopic`、`nationalProject` 这类内部字段名。
 */
@Composable
fun localizedHeritageFacetLabel(value: String?): String? {
    if (value.isNullOrBlank()) return null
    return when (value.normalizedWireName()) {
        "news", "forum", "specialtopic" -> localizedArticleCategory(value)
        "nationalproject",
        "culturalecozone",
        "productiveprotectionbase",
        "unescoentry",
        "chinaunescoentry",
        "contractingstate",
        -> localizedDirectoryKind(value)
        "category", "region", "year", "kind", "projectcode" -> localizedTopicType(value)
        else -> value
    }
}

/**
 * 将阅读路径来源 wire value 映射为本地化显示文案。
 */
@Composable
fun localizedReadingPathSource(source: String): String =
    when (source) {
        "blendedRecommendation" -> stringResource(R.string.blended_title)
        "related" -> stringResource(R.string.context_related)
        "recommendation" -> stringResource(R.string.context_recommendations)
        "semanticRecommendation" -> stringResource(R.string.context_semantic_recommendations)
        "collection" -> stringResource(R.string.context_collections)
        "graph" -> stringResource(R.string.context_graph)
        "exploreTopic" -> stringResource(R.string.context_explore_topics)
        "graphTrail" -> stringResource(R.string.reading_path_source_graph_trail)
        "journey" -> stringResource(R.string.reading_path_source_journey)
        "learningRoute" -> stringResource(R.string.reading_path_source_learning_route)
        "ranking" -> stringResource(R.string.reading_path_source_ranking)
        "list" -> stringResource(R.string.reading_path_source_list)
        else -> source
    }

/**
 * 将详情页关系线索中的关系类型映射为本地化显示文案。
 *
 * 同时覆盖 V3 图谱枚举 wire name（如 `RELATED_TO`）与旧版 Context API 的
 * 业务关系码（如 `relatedArticle`、`semanticSimilarity`）。
 * 当 [relationType] 无法识别时，回退到 [label]；两者都为空则返回空字符串。
 */
@Composable
fun localizedRelationLabel(relationType: String?, label: String?): String {
    val mappedResId = when (relationType) {
        "RELATED_TO" -> R.string.graph_relation_type_related_to
        "INHERITS_PROJECT" -> R.string.graph_relation_type_inherits_project
        "SIMILAR" -> R.string.graph_relation_type_similar
        "TOPIC" -> R.string.graph_relation_type_topic
        "AI_INFERRED" -> R.string.graph_relation_type_ai_inferred
        "unknown" -> R.string.graph_relation_type_unknown
        "relatedArticle" -> R.string.graph_relation_related_article
        "relatedDirectoryItem" -> R.string.graph_relation_related_directory_item
        "relatedInheritor" -> R.string.graph_relation_related_inheritor
        "semanticSimilarity" -> R.string.graph_relation_semantic_similarity
        "sameCategory" -> R.string.graph_relation_same_category
        "sameRegion" -> R.string.graph_relation_same_region
        "sameTopic" -> R.string.graph_relation_same_topic
        else -> null
    }
    return mappedResId?.let { stringResource(it) }
        ?: label?.takeIf { it.isNotBlank() }
        ?: ""
}

/**
 * 将学习路线难度 wire value 映射为本地化显示文案。
 */
@Composable
fun localizedLearningRouteDifficulty(difficulty: LearningRouteDifficulty): String =
    when (difficulty) {
        LearningRouteDifficulty.All -> stringResource(R.string.learning_route_difficulty_all)
        LearningRouteDifficulty.Beginner -> stringResource(R.string.learning_route_difficulty_beginner)
        LearningRouteDifficulty.Intermediate -> stringResource(R.string.learning_route_difficulty_intermediate)
        LearningRouteDifficulty.Deep -> stringResource(R.string.learning_route_difficulty_deep)
        LearningRouteDifficulty.Unknown -> stringResource(R.string.learning_route_difficulty_unknown)
    }

/**
 * 格式化传承人批次字段。
 *
 * 后端常见格式如 `2025(2025年（第六批）)`，直接展示显得冗余。
 * 这里提取括号内的人可读批次名（如“第六批”）；若不存在则原样返回。
 */
fun localizedInheritorBatch(batch: String?): String? {
    if (batch.isNullOrBlank()) return null
    val trimmed = batch.trim()
    // 优先提取最后一组全角或半角括号内的内容
    val match = Regex("""[（(]([^（()）)]+)[）)]""").findAll(trimmed).lastOrNull()
    return match?.groupValues?.get(1)?.trim()?.takeIf { it.isNotBlank() } ?: trimmed
}

/**
 * 按内容类型返回占位符文字，用于无缩略图时区分内容类型。
 */
@Composable
fun contentTypeFallbackText(type: String?): String =
    when (type.normalizedWireName()) {
        "article" -> stringResource(R.string.graph_node_placeholder_article)
        "directoryitem", "directory" -> stringResource(R.string.graph_node_placeholder_directory)
        "inheritor" -> stringResource(R.string.graph_node_placeholder_inheritor)
        "topic" -> stringResource(R.string.graph_node_placeholder_topic)
        else -> stringResource(R.string.brand_fallback)
    }

private fun String?.normalizedWireName(): String? =
    this
        ?.takeIf { it.isNotBlank() }
        ?.replace("_", "")
        ?.replace("-", "")
        ?.lowercase()
