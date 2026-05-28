package com.duckylife.heritage.modern.ui.error

import android.util.Log
import io.ktor.client.plugins.ResponseException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

private const val TAG = "HeritageError"

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
    Log.w(TAG, "toUiError: ${this::class.simpleName}: ${this.message}", this)
    val kind = when {
        this is UnknownHostException || this is ConnectException -> ErrorKind.NetworkUnavailable
        this is SocketTimeoutException -> ErrorKind.Timeout
        this is IOException -> ErrorKind.NetworkUnavailable
        this is ResponseException -> {
            val code = response.status.value
            Log.w(TAG, "ResponseException HTTP $code")
            when {
                code == 404 -> ErrorKind.NotFound
                code in 500..599 -> ErrorKind.ServerError
                else -> ErrorKind.NetworkUnavailable
            }
        }

        message?.contains("404") == true || message?.contains("Not Found", ignoreCase = true) == true ->
            ErrorKind.NotFound
        message?.contains("50") == true && (message?.contains("500") == true || message?.contains("502") == true || message?.contains("503") == true) ->
            ErrorKind.ServerError
        else -> defaultKind
    }
    Log.w(TAG, "toUiError result: $kind")
    return UiErrorMessage(kind, kind.fallbackResId())
}

fun ErrorKind.fallbackResId(): Int = when (this) {
    ErrorKind.NetworkUnavailable -> com.duckylife.heritage.modern.R.string.error_network_unavailable
    ErrorKind.Timeout -> com.duckylife.heritage.modern.R.string.error_timeout
    ErrorKind.ServerError -> com.duckylife.heritage.modern.R.string.error_server_unavailable
    ErrorKind.NotFound -> com.duckylife.heritage.modern.R.string.content_not_available
    ErrorKind.Unknown -> com.duckylife.heritage.modern.R.string.content_load_failed
}
