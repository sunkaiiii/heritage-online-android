package com.duckylife.heritage.modern.core.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PagedResult<T>(
    val items: List<T> = emptyList(),
    val page: Int = 1,
    val pageSize: Int = 20,
    val hasMore: Boolean = false,
    val total: Long = 0,
)

@Serializable
data class MediaAssetDto(
    val sourceUrl: String? = null,
    val originalUrl: String? = null,
    val displayUrl: String? = null,
    val thumbnailUrl: String? = null,
    val altText: String? = null,
)

@Serializable
enum class ArticleCategory(val wireName: String) {
    @SerialName("news")
    News("news"),

    @SerialName("forum")
    Forum("forum"),

    @SerialName("specialTopic")
    SpecialTopic("specialTopic"),
}

@Serializable
enum class ArticleContentBlockType {
    @SerialName("text")
    Text,

    @SerialName("image")
    Image,

    @SerialName("heading")
    Heading,
}

@Serializable
data class ArticleContentBlockDto(
    val type: ArticleContentBlockType = ArticleContentBlockType.Text,
    val text: String? = null,
    val image: MediaAssetDto? = null,
)

@Serializable
data class ArticleReferenceDto(
    val title: String? = null,
    val detailUrl: String? = null,
    val sourceId: String? = null,
    val publishedAt: String? = null,
)

@Serializable
data class ArticleSummaryDto(
    val id: String? = null,
    val category: ArticleCategory = ArticleCategory.News,
    val title: String? = null,
    val summary: String? = null,
    val publishedAt: String? = null,
    val coverImage: MediaAssetDto? = null,
    val sourceUrl: String? = null,
)

@Serializable
data class ArticleDetailDto(
    val id: String? = null,
    val category: ArticleCategory = ArticleCategory.News,
    val title: String? = null,
    val summary: String? = null,
    val publishedAt: String? = null,
    val coverImage: MediaAssetDto? = null,
    val sourceUrl: String? = null,
    val sourceName: String? = null,
    val author: String? = null,
    val editor: String? = null,
    val contentBlocks: List<ArticleContentBlockDto> = emptyList(),
    val relatedArticles: List<ArticleReferenceDto> = emptyList(),
)

@Serializable
enum class DirectoryItemKind(val wireName: String) {
    @SerialName("nationalProject")
    NationalProject("nationalProject"),

    @SerialName("culturalEcoZone")
    CulturalEcoZone("culturalEcoZone"),

    @SerialName("productiveProtectionBase")
    ProductiveProtectionBase("productiveProtectionBase"),

    @SerialName("unescoEntry")
    UnescoEntry("unescoEntry"),

    @SerialName("chinaUnescoEntry")
    ChinaUnescoEntry("chinaUnescoEntry"),

    @SerialName("contractingState")
    ContractingState("contractingState"),
}

@Serializable
data class DirectoryReferenceDto(
    val title: String? = null,
    val detailUrl: String? = null,
    val sourceId: String? = null,
    val kind: String? = null,
    val category: String? = null,
    val region: String? = null,
    val publishedYear: Int? = null,
)

@Serializable
data class DirectoryItemSummaryDto(
    val id: String? = null,
    val kind: DirectoryItemKind = DirectoryItemKind.NationalProject,
    val title: String? = null,
    val summary: String? = null,
    val category: String? = null,
    val region: String? = null,
    val projectCode: String? = null,
    val batch: String? = null,
    val publishedYear: Int? = null,
    val listType: String? = null,
    val coverImage: MediaAssetDto? = null,
    val sourceUrl: String? = null,
)

@Serializable
data class DirectoryItemDetailDto(
    val id: String? = null,
    val kind: DirectoryItemKind = DirectoryItemKind.NationalProject,
    val title: String? = null,
    val summary: String? = null,
    val category: String? = null,
    val region: String? = null,
    val projectCode: String? = null,
    val batch: String? = null,
    val publishedYear: Int? = null,
    val listType: String? = null,
    val coverImage: MediaAssetDto? = null,
    val sourceUrl: String? = null,
    val nominationType: String? = null,
    val protectionUnit: String? = null,
    val gallery: List<MediaAssetDto> = emptyList(),
    val contentBlocks: List<ArticleContentBlockDto> = emptyList(),
    val relatedProjects: List<DirectoryReferenceDto> = emptyList(),
    val relatedInheritors: List<DirectoryReferenceDto> = emptyList(),
    val relatedDocuments: List<DirectoryReferenceDto> = emptyList(),
)

@Serializable
data class InheritorSummaryDto(
    val id: String? = null,
    val name: String? = null,
    val gender: String? = null,
    val birthDateText: String? = null,
    val ethnicity: String? = null,
    val category: String? = null,
    val projectCode: String? = null,
    val projectName: String? = null,
    val region: String? = null,
    val batch: String? = null,
    val description: String? = null,
    val coverImage: MediaAssetDto? = null,
    val sourceUrl: String? = null,
)

@Serializable
data class InheritorDetailDto(
    val id: String? = null,
    val name: String? = null,
    val gender: String? = null,
    val birthDateText: String? = null,
    val ethnicity: String? = null,
    val category: String? = null,
    val projectCode: String? = null,
    val projectName: String? = null,
    val region: String? = null,
    val batch: String? = null,
    val description: String? = null,
    val coverImage: MediaAssetDto? = null,
    val sourceUrl: String? = null,
    val contentBlocks: List<ArticleContentBlockDto> = emptyList(),
    val relatedProjects: List<DirectoryReferenceDto> = emptyList(),
    val relatedInheritors: List<DirectoryReferenceDto> = emptyList(),
)

@Serializable
data class HomeBannerDto(
    val id: String? = null,
    val sortOrder: Int = 0,
    val targetUrl: String? = null,
    val displayImage: MediaAssetDto? = null,
    val mobileImage: MediaAssetDto? = null,
    val desktopImage: MediaAssetDto? = null,
)

@Serializable
data class ProblemDetailsDto(
    val type: String? = null,
    val title: String? = null,
    val status: Int? = null,
    val detail: String? = null,
    val instance: String? = null,
)
