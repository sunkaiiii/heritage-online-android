package com.duckylife.heritage.modern.ui.error

import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

enum class ErrorKind {
    NetworkUnavailable,
    Timeout,
    ServerError,
    NotFound,
    Unknown,
}

data class UiErrorMessage(
    val kind: ErrorKind,
    val fallbackResId: Int,
)

fun Throwable.toUiError(defaultKind: ErrorKind = ErrorKind.Unknown): UiErrorMessage {
    val kind = when {
        this is UnknownHostException || this is ConnectException -> ErrorKind.NetworkUnavailable
        this is SocketTimeoutException -> ErrorKind.Timeout
        this is IOException -> ErrorKind.NetworkUnavailable
        message?.contains("404") == true || message?.contains("Not Found", ignoreCase = true) == true ->
            ErrorKind.NotFound
        message?.contains("50") == true && (message?.contains("500") == true || message?.contains("502") == true || message?.contains("503") == true) ->
            ErrorKind.ServerError
        else -> defaultKind
    }
    return UiErrorMessage(kind, kind.fallbackResId())
}

fun ErrorKind.fallbackResId(): Int = when (this) {
    ErrorKind.NetworkUnavailable -> com.duckylife.heritage.modern.R.string.error_network_unavailable
    ErrorKind.Timeout -> com.duckylife.heritage.modern.R.string.error_timeout
    ErrorKind.ServerError -> com.duckylife.heritage.modern.R.string.error_server_unavailable
    ErrorKind.NotFound -> com.duckylife.heritage.modern.R.string.content_not_available
    ErrorKind.Unknown -> com.duckylife.heritage.modern.R.string.content_load_failed
}
