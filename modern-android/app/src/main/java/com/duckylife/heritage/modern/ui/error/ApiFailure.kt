package com.duckylife.heritage.modern.ui.error

import com.duckylife.heritage.modern.core.network.HeritageJson
import com.duckylife.heritage.modern.core.network.dto.ProblemDetailsDto
import io.ktor.client.plugins.ResponseException
import io.ktor.client.statement.bodyAsText
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * 后端 API 错误的统一表示。
 *
 * 网络层/Repository 捕获异常后，先映射为 [ApiFailure]，再交给 UI 层转换为可读的 [UiErrorMessage]。
 * 这样增强接口（V3 智能、图谱、分析）的 503/404 可以被降级为 section 级别错误，
 * 而不会影响核心详情页的展示。
 */
sealed interface ApiFailure {

    /**
     * 该错误是否适合触发重试（如网络断开、5xx、429）。
     */
    val isRetryable: Boolean

    /**
     * HTTP 错误，包含从后端 ProblemDetails 解析出的信息。
     *
     * @param statusCode HTTP 状态码。
     * @param problemTitle 后端 ProblemDetails 的 `title`，可能为空。
     * @param detail 后端 ProblemDetails 的 `detail`，可能为空。
     * @param traceId 后端返回的追踪 ID，优先从 ProblemDetails 读取，其次从响应头读取。
     */
    data class Http(
        val statusCode: Int,
        val problemTitle: String?,
        val detail: String?,
        val traceId: String?,
    ) : ApiFailure {
        override val isRetryable: Boolean
            get() = statusCode in 500..599 || statusCode == 429
    }

    /**
     * 网络不可用或连接失败。
     */
    data class Network(val cause: Throwable) : ApiFailure {
        override val isRetryable: Boolean = true
    }

    /**
     * 请求超时（如 [SocketTimeoutException]）。
     */
    data class Timeout(val cause: Throwable) : ApiFailure {
        override val isRetryable: Boolean = true
    }

    /**
     * 未知错误，通常不应该直接展示给用户。
     */
    data class Unknown(val cause: Throwable) : ApiFailure {
        override val isRetryable: Boolean = false
    }
}

/**
 * 将任意异常解析为 [ApiFailure]。
 *
 * 对于 [ResponseException]，会读取响应体并尝试按 ProblemDetails 解析 `title/detail/traceId`；
 * 解析失败时这些字段为空，不会抛出二次异常。
 */
suspend fun Throwable.toApiFailure(): ApiFailure = when (this) {
    is ResponseException -> toApiFailureHttp()
    is SocketTimeoutException -> ApiFailure.Timeout(this)
    is UnknownHostException, is ConnectException, is IOException -> ApiFailure.Network(this)
    else -> ApiFailure.Unknown(this)
}

private suspend fun ResponseException.toApiFailureHttp(): ApiFailure.Http {
    val bodyText = runCatching { response.bodyAsText() }.getOrNull().orEmpty()
    val problem = runCatching {
        bodyText.takeIf { it.isNotBlank() }
            ?.let { HeritageJson.decodeFromString<ProblemDetailsDto>(it) }
    }.getOrNull()
    return ApiFailure.Http(
        statusCode = response.status.value,
        problemTitle = problem?.title,
        detail = problem?.detail,
        traceId = problem?.traceId ?: response.headers.traceId(),
    )
}

private fun io.ktor.http.Headers.traceId(): String? {
    return get("X-Trace-Id")
        ?: get("x-trace-id")
        ?: get("Request-Id")
        ?: get("request-id")
}

/**
 * 把 [ApiFailure] 映射为 [ErrorKind]，用于 UI 选择错误文案和是否致命。
 */
fun ApiFailure.toErrorKind(): ErrorKind = when (this) {
    is ApiFailure.Http -> statusCode.toErrorKind()
    is ApiFailure.Network -> ErrorKind.NetworkUnavailable
    is ApiFailure.Timeout -> ErrorKind.Timeout
    is ApiFailure.Unknown -> ErrorKind.Unknown
}

/**
 * 把 [ApiFailure] 转换为带文案的 [UiErrorMessage]。
 */
fun ApiFailure.toUiErrorMessage(): UiErrorMessage {
    val kind = toErrorKind()
    return UiErrorMessage(kind, kind.fallbackResId())
}
