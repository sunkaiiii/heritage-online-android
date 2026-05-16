package com.duckylife.heritage.modern.feature.articles.detail

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Locale

class ArticleDetailFormattingTest {
    @Test
    fun formatArticleDateUsesChinaCalendarDateForInstant() {
        assertEquals(
            "2026年4月22日",
            formatArticleDate("2026-04-22T14:30:00Z", Locale.SIMPLIFIED_CHINESE),
        )
    }

    @Test
    fun formatArticleDateSupportsPlainLocalDate() {
        assertEquals(
            "2026年4月22日",
            formatArticleDate("2026-04-22", Locale.SIMPLIFIED_CHINESE),
        )
    }

    @Test
    fun formatArticleDateSupportsEnglishLocale() {
        assertEquals(
            "Apr 22, 2026",
            formatArticleDate("2026-04-22", Locale.US),
        )
    }

    @Test
    fun formatArticleDateKeepsUnknownText() {
        assertEquals("2026年春", formatArticleDate("2026年春"))
        assertNull(formatArticleDate(null))
    }

    @Test
    fun isStandaloneSectionTitleRecognizesShortHeadings() {
        assertTrue(isStandaloneSectionTitle("开班仪式：凝心聚力"))
        assertTrue(isStandaloneSectionTitle("现场教学"))
        assertFalse(isStandaloneSectionTitle("这是一个完整的正文段落，用来说明培训现场的情况。"))
    }
}
