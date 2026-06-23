package com.duckylife.heritage.modern.ui.error

import io.ktor.client.plugins.ResponseException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

enum class ErrorKind {
    NetworkUnavailable,
    Timeout,
    ServerError,
    NotFound,
    BadRequest,
    Unauthorized,
    Conflict,
    PayloadTooLarge,
    TooManyRequests,
    Unknown,
}

data class UiErrorMessage(
    val kind: ErrorKind,
    val fallbackResId: Int,
)

/**
 * 把异常映射为 UI 错误信息（非 suspend 版本）。
 *
 * 该版本不读取响应体，仅按 HTTP 状态码做快速映射，适合在 Composable 和已有 ViewModel 回调中使用。
 * 如果需要从后端 ProblemDetails 中解析 `title/detail/traceId`，请使用 [Throwable.toApiFailure]。
 */
fun Throwable.toUiError(defaultKind: ErrorKind = ErrorKind.Unknown): UiErrorMessage {
    val kind = when {
        this is UnknownHostException || this is ConnectException -> ErrorKind.NetworkUnavailable
        this is SocketTimeoutException -> ErrorKind.Timeout
        this is IOException -> ErrorKind.NetworkUnavailable
        this is ResponseException -> response.status.value.toErrorKind()
        else -> defaultKind
    }
    return UiErrorMessage(kind, kind.fallbackResId())
}

/**
 * 把 HTTP 状态码映射为 [ErrorKind]。
 *
 * 该函数在 [toUiError] 与 [ApiFailure.toErrorKind] 之间共享，避免新增状态码时需要在两处同步修改。
 */
internal fun Int.toErrorKind(): ErrorKind = when (this) {
    400 -> ErrorKind.BadRequest
    401, 403 -> ErrorKind.Unauthorized
    404 -> ErrorKind.NotFound
    409 -> ErrorKind.Conflict
    413 -> ErrorKind.PayloadTooLarge
    429 -> ErrorKind.TooManyRequests
    in 500..599 -> ErrorKind.ServerError
    else -> ErrorKind.Unknown
}

fun ErrorKind.fallbackResId(): Int = when (this) {
    ErrorKind.NetworkUnavailable -> com.duckylife.heritage.modern.R.string.error_network_unavailable
    ErrorKind.Timeout -> com.duckylife.heritage.modern.R.string.error_timeout
    ErrorKind.ServerError -> com.duckylife.heritage.modern.R.string.error_server_unavailable
    ErrorKind.NotFound -> com.duckylife.heritage.modern.R.string.content_not_available
    ErrorKind.BadRequest -> com.duckylife.heritage.modern.R.string.error_bad_request
    ErrorKind.Unauthorized -> com.duckylife.heritage.modern.R.string.error_unauthorized
    ErrorKind.Conflict -> com.duckylife.heritage.modern.R.string.error_conflict
    ErrorKind.PayloadTooLarge -> com.duckylife.heritage.modern.R.string.error_payload_too_large
    ErrorKind.TooManyRequests -> com.duckylife.heritage.modern.R.string.error_too_many_requests
    ErrorKind.Unknown -> com.duckylife.heritage.modern.R.string.content_load_failed
}
