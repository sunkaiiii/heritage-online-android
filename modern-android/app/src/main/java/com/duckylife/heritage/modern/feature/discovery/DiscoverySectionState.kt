package com.duckylife.heritage.modern.feature.discovery

import com.duckylife.heritage.modern.ui.error.ErrorKind

data class DiscoverySectionState<T>(
    val isLoading: Boolean = false,
    val data: T? = null,
    val errorKind: ErrorKind? = null,
) {
    val hasData: Boolean get() = data != null
    val hasError: Boolean get() = errorKind != null
    val hasFatalError: Boolean get() = errorKind != null && !hasData
}
