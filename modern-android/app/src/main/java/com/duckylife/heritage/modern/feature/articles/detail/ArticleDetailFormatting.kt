package com.duckylife.heritage.modern.feature.articles.detail

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.FormatStyle
import java.time.format.DateTimeFormatter
import java.util.Locale

private val ArticleDateZone: ZoneId = ZoneId.of("Asia/Shanghai")

fun formatArticleDate(
    value: String?,
    locale: Locale = Locale.getDefault(),
): String? {
    val text = value?.trim().orEmpty()
    if (text.isBlank()) {
        return null
    }

    val formatter = DateTimeFormatter
        .ofLocalizedDate(FormatStyle.MEDIUM)
        .withLocale(locale)

    return runCatching {
        Instant.parse(text)
            .atZone(ArticleDateZone)
            .toLocalDate()
            .format(formatter)
    }.recoverCatching {
        LocalDate.parse(text)
            .format(formatter)
    }.getOrElse {
        text
    }
}

fun isStandaloneSectionTitle(value: String?): Boolean {
    val text = value?.trim().orEmpty()
    if (text.isBlank() || text.length > 32) {
        return false
    }

    val hasSentencePunctuation = text.any { it in listOf('。', '！', '？', '.', '!', '?') }
    return text.endsWith("：") ||
        text.endsWith(":") ||
        (!hasSentencePunctuation && text.length <= 18)
}
