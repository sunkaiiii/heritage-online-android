package com.duckylife.heritage.modern.core.network

import com.duckylife.heritage.modern.core.network.dto.ArticleDetailDto
import com.duckylife.heritage.modern.core.network.dto.ArticleCategory
import com.duckylife.heritage.modern.core.network.dto.ArticleSummaryDto
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemDetailDto
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemKind
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemSummaryDto
import com.duckylife.heritage.modern.core.network.dto.HomeBannerDto
import com.duckylife.heritage.modern.core.network.dto.InheritorDetailDto
import com.duckylife.heritage.modern.core.network.dto.InheritorSummaryDto
import com.duckylife.heritage.modern.core.network.dto.PagedResult
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

interface HeritageApiClient {
    suspend fun getHomeBanners(): List<HomeBannerDto>

    suspend fun getArticles(query: ArticleQuery = ArticleQuery()): PagedResult<ArticleSummaryDto>

    suspend fun getArticle(id: String): ArticleDetailDto

    suspend fun getArticleBySourceId(
        sourceId: String,
        category: ArticleCategory = ArticleCategory.News,
    ): ArticleDetailDto

    suspend fun getArticleBySourceUrl(
        sourceUrl: String,
        category: ArticleCategory = ArticleCategory.News,
    ): ArticleDetailDto

    suspend fun getDirectoryItems(
        query: DirectoryItemQuery = DirectoryItemQuery(),
    ): PagedResult<DirectoryItemSummaryDto>

    suspend fun getDirectoryItem(id: String): DirectoryItemDetailDto

    suspend fun getDirectoryItemBySourceId(
        sourceId: String,
        kind: DirectoryItemKind = DirectoryItemKind.NationalProject,
    ): DirectoryItemDetailDto

    suspend fun getInheritors(query: InheritorQuery = InheritorQuery()): PagedResult<InheritorSummaryDto>

    suspend fun getInheritor(id: String): InheritorDetailDto

    suspend fun getInheritorBySourceId(sourceId: String): InheritorDetailDto
}

class KtorHeritageApiClient(
    private val httpClient: HttpClient,
    private val baseUrl: String = LocalDevBaseUrl,
) : HeritageApiClient {
    override suspend fun getHomeBanners(): List<HomeBannerDto> =
        httpClient.get(endpoint("api/home-banners")).body()

    override suspend fun getArticles(query: ArticleQuery): PagedResult<ArticleSummaryDto> =
        httpClient.get(endpoint("api/articles")) {
            optionalParameter("category", query.category.wireName)
            optionalParameter("page", query.page)
            optionalParameter("pageSize", query.pageSize)
            optionalParameter("year", query.year)
            optionalParameter("keywords", query.keywords)
        }.body()

    override suspend fun getArticle(id: String): ArticleDetailDto =
        httpClient.get(endpoint("api/articles/$id")).body()

    override suspend fun getArticleBySourceId(
        sourceId: String,
        category: ArticleCategory,
    ): ArticleDetailDto =
        httpClient.get(endpoint("api/articles/source/$sourceId")) {
            optionalParameter("category", category.wireName)
        }.body()

    override suspend fun getArticleBySourceUrl(
        sourceUrl: String,
        category: ArticleCategory,
    ): ArticleDetailDto =
        httpClient.get(endpoint("api/articles/source")) {
            optionalParameter("category", category.wireName)
            optionalParameter("sourceUrl", sourceUrl)
        }.body()

    override suspend fun getDirectoryItems(
        query: DirectoryItemQuery,
    ): PagedResult<DirectoryItemSummaryDto> =
        httpClient.get(endpoint("api/directory-items")) {
            optionalParameter("kind", query.kind.wireName)
            optionalParameter("page", query.page)
            optionalParameter("pageSize", query.pageSize)
            optionalParameter("keywords", query.keywords)
            optionalParameter("region", query.region)
            optionalParameter("category", query.category)
            optionalParameter("year", query.year)
            optionalParameter("listType", query.listType)
        }.body()

    override suspend fun getDirectoryItem(id: String): DirectoryItemDetailDto =
        httpClient.get(endpoint("api/directory-items/$id")).body()

    override suspend fun getDirectoryItemBySourceId(
        sourceId: String,
        kind: DirectoryItemKind,
    ): DirectoryItemDetailDto =
        httpClient.get(endpoint("api/directory-items/source/$sourceId")) {
            optionalParameter("kind", kind.wireName)
        }.body()

    override suspend fun getInheritors(query: InheritorQuery): PagedResult<InheritorSummaryDto> =
        httpClient.get(endpoint("api/inheritors")) {
            optionalParameter("page", query.page)
            optionalParameter("pageSize", query.pageSize)
            optionalParameter("keywords", query.keywords)
            optionalParameter("region", query.region)
            optionalParameter("category", query.category)
            optionalParameter("year", query.year)
            optionalParameter("gender", query.gender)
        }.body()

    override suspend fun getInheritor(id: String): InheritorDetailDto =
        httpClient.get(endpoint("api/inheritors/$id")).body()

    override suspend fun getInheritorBySourceId(sourceId: String): InheritorDetailDto =
        httpClient.get(endpoint("api/inheritors/source/$sourceId")).body()

    private fun endpoint(path: String): String = "${baseUrl.trimEnd('/')}/${path.trimStart('/')}"
}

fun createHeritageHttpClient(
    config: HeritageApiConfig = HeritageApiConfig(),
): HttpClient = HttpClient(OkHttp) {
    expectSuccess = true

    engine {
        if (config.trustSelfSignedCertificates) {
            val trustManager = trustAllCertificatesManager()
            val socketFactory = trustAllSslSocketFactory(trustManager)
            config {
                sslSocketFactory(socketFactory, trustManager)
                hostnameVerifier { _, _ -> true }
            }
        }
    }

    install(ContentNegotiation) {
        json(HeritageJson)
    }

    defaultRequest {
        accept(ContentType.Application.Json)
    }
}

fun createHeritageApiClient(
    config: HeritageApiConfig = HeritageApiConfig(),
): HeritageApiClient = KtorHeritageApiClient(
    httpClient = createHeritageHttpClient(config),
    baseUrl = config.baseUrl,
)

private fun HttpRequestBuilder.optionalParameter(name: String, value: Any?) {
    if (value != null) {
        parameter(name, value)
    }
}

private fun trustAllCertificatesManager(): X509TrustManager =
    object : X509TrustManager {
        override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) = Unit

        override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) = Unit

        override fun getAcceptedIssuers(): Array<X509Certificate> = emptyArray()
    }

private fun trustAllSslSocketFactory(trustManager: X509TrustManager): SSLSocketFactory {
    val sslContext = SSLContext.getInstance("TLS")
    sslContext.init(null, arrayOf(trustManager), SecureRandom())
    return sslContext.socketFactory
}
