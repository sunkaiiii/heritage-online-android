package com.duckylife.heritage.modern.core.network

import com.duckylife.heritage.modern.core.network.dto.ArticleCategory
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemKind

data class ArticleQuery(
    val category: ArticleCategory = ArticleCategory.News,
    val page: Int = 1,
    val pageSize: Int = 20,
    val year: Int? = null,
    val keywords: String? = null,
)

data class DirectoryItemQuery(
    val kind: DirectoryItemKind = DirectoryItemKind.NationalProject,
    val page: Int = 1,
    val pageSize: Int = 20,
    val keywords: String? = null,
    val region: String? = null,
    val category: String? = null,
    val year: Int? = null,
    val listType: String? = null,
)

data class InheritorQuery(
    val page: Int = 1,
    val pageSize: Int = 20,
    val keywords: String? = null,
    val region: String? = null,
    val category: String? = null,
    val year: Int? = null,
    val gender: String? = null,
)

data class SearchV2Query(
    val keywords: String,
    val types: String? = null,
    val page: Int = 1,
    val pageSize: Int = 20,
    val region: String? = null,
    val category: String? = null,
    val year: Int? = null,
    val kind: DirectoryItemKind? = null,
    val hasImage: Boolean? = null,
)

data class TimelineV2Query(
    val year: Int? = null,
    val types: String? = null,
    val page: Int = 1,
    val pageSize: Int = 20,
    val category: String? = null,
    val region: String? = null,
    val kind: DirectoryItemKind? = null,
    val hasImage: Boolean? = null,
)
