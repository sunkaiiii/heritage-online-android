package com.duckylife.heritage.modern.ui.text

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.duckylife.heritage.modern.R

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
    when (category) {
        "news" -> stringResource(R.string.category_news)
        "forum" -> stringResource(R.string.category_forum)
        "specialTopic" -> stringResource(R.string.category_special_topic)
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
    when (kind) {
        "nationalProject" -> stringResource(R.string.directory_kind_national_project)
        "culturalEcoZone" -> stringResource(R.string.directory_kind_cultural_eco_zone)
        "productiveProtectionBase" -> stringResource(R.string.directory_kind_productive_protection_base)
        "unescoEntry" -> stringResource(R.string.directory_kind_unesco_entry)
        "chinaUnescoEntry" -> stringResource(R.string.directory_kind_china_unesco_entry)
        "contractingState" -> stringResource(R.string.directory_kind_contracting_state)
        else -> kind?.takeIf { it.isNotBlank() }
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
        "list" -> stringResource(R.string.reading_path_source_list)
        else -> source
    }
