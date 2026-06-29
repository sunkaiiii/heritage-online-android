package com.duckylife.heritage.modern.ui.text

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

/**
 * 将后端返回的 ISO 日期/时间字符串格式化为本地化中等长度日期。
 *
 * 支持的输入格式包括：
 * - `ZonedDateTime` / `OffsetDateTime`（含时区偏移）
 * - `Instant`（UTC，以 `Z` 结尾）
 * - `LocalDateTime`
 * - `LocalDate`
 *
 * 无法解析时回退到原始文本；空值返回 `null`。
 */
fun formatIsoDate(
    value: String?,
    locale: Locale = Locale.getDefault(),
): String? {
    val text = value?.trim().orEmpty()
    if (text.isBlank()) return null

    val formatter = DateTimeFormatter
        .ofLocalizedDate(FormatStyle.MEDIUM)
        .withLocale(locale)

    return runCatching {
        ZonedDateTime.parse(text)
            .withZoneSameInstant(ChinaDateZone)
            .toLocalDate()
            .format(formatter)
    }.recoverCatching {
        OffsetDateTime.parse(text)
            .toInstant()
            .atZone(ChinaDateZone)
            .toLocalDate()
            .format(formatter)
    }.recoverCatching {
        Instant.parse(text)
            .atZone(ChinaDateZone)
            .toLocalDate()
            .format(formatter)
    }.recoverCatching {
        LocalDateTime.parse(text)
            .toLocalDate()
            .format(formatter)
    }.recoverCatching {
        LocalDate.parse(text)
            .format(formatter)
    }.getOrElse {
        text
    }
}

private val ChinaDateZone: ZoneId = ZoneId.of("Asia/Shanghai")
