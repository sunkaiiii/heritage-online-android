package com.duckylife.heritage.modern.ui.text

import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class IsoDateFormatterTest {

    @Test
    fun formatIsoDateParsesInstant() {
        assertEquals(
            "2026年4月22日",
            formatIsoDate("2026-04-22T14:30:00Z", Locale.SIMPLIFIED_CHINESE),
        )
    }

    @Test
    fun formatIsoDateParsesOffsetDateTime() {
        assertEquals(
            "2026年4月22日",
            formatIsoDate("2026-04-22T22:30:00.000+08:00", Locale.SIMPLIFIED_CHINESE),
        )
    }

    @Test
    fun formatIsoDateParsesPlainLocalDate() {
        assertEquals(
            "2026年4月22日",
            formatIsoDate("2026-04-22", Locale.SIMPLIFIED_CHINESE),
        )
    }

    @Test
    fun formatIsoDateSupportsEnglishLocale() {
        assertEquals(
            "Apr 22, 2026",
            formatIsoDate("2026-04-22", Locale.US),
        )
    }

    @Test
    fun formatIsoDateKeepsUnknownText() {
        assertEquals("2026年春", formatIsoDate("2026年春"))
    }

    @Test
    fun formatIsoDateReturnsNullForBlank() {
        assertNull(formatIsoDate(" "))
        assertNull(formatIsoDate(null))
    }
}
