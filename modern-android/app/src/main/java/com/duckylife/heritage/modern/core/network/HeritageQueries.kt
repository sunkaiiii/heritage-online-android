package com.duckylife.heritage.modern.core.network

import com.duckylife.heritage.modern.core.network.dto.ArticleCategory
import com.duckylife.heritage.modern.core.network.dto.CollectionTopicType
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemKind
import com.duckylife.heritage.modern.core.network.dto.ExploreTopicListType
import com.duckylife.heritage.modern.core.network.dto.ExploreTopicType
import com.duckylife.heritage.modern.core.network.dto.SearchResultType

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
    val types: Set<SearchResultType> = emptySet(),
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
    val types: Set<SearchResultType> = emptySet(),
    val page: Int = 1,
    val pageSize: Int = 20,
    val category: String? = null,
    val region: String? = null,
    val kind: DirectoryItemKind? = null,
    val hasImage: Boolean? = null,
)

enum class TaxonomyRegionSort(val wireName: String) {
    Total("total"),
    DirectoryItem("directoryItem"),
    Inheritor("inheritor"),
}

enum class CompareType(val wireName: String) {
    Region("region"),
    Category("category"),
    Kind("kind"),
}

data class DiscoverySerendipityQuery(
    val type: SearchResultType = SearchResultType.DirectoryItem,
    val hasImage: Boolean? = null,
    val region: String? = null,
    val category: String? = null,
)

data class DiscoveryDeepDiveQuery(
    val seedType: SearchResultType,
    val seedId: String,
    val limit: Int = 10,
)

data class BlendedRecommendationQuery(
    val type: SearchResultType,
    val id: String,
    val limit: Int = 10,
    val ruleWeight: Double = 1.0,
    val semanticWeight: Double = 1.0,
    val sameCategoryWeight: Double = 1.0,
    val sameRegionWeight: Double = 1.0,
    val diversify: Boolean = true,
)
