package com.duckylife.heritage.modern.ui.state

import com.duckylife.heritage.modern.ui.error.ErrorKind

/**
 * 通用异步数据区段状态。
 */
data class AsyncState<T>(
    val isLoading: Boolean = false,
    val data: T? = null,
    val errorKind: ErrorKind? = null,
) {
    fun <R> map(transform: (T) -> R): AsyncState<R> = AsyncState(
        isLoading = isLoading,
        data = data?.let(transform),
        errorKind = errorKind,
    )
}
