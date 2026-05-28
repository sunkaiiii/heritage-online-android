package com.duckylife.heritage.modern.core

import kotlinx.coroutines.CancellationException

suspend inline fun <R> runCatchingCancellable(block: suspend () -> R): Result<R> {
    return try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }
}
