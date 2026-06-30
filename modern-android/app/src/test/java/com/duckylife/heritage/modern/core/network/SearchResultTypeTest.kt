package com.duckylife.heritage.modern.core.network

import com.duckylife.heritage.modern.core.network.dto.SearchResultType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SearchResultTypeTest {

    @Test
    fun `fromWireName parses known values`() {
        assertEquals(SearchResultType.Article, SearchResultType.fromWireName("article"))
        assertEquals(SearchResultType.DirectoryItem, SearchResultType.fromWireName("directoryItem"))
        assertEquals(SearchResultType.Inheritor, SearchResultType.fromWireName("inheritor"))
    }

    @Test
    fun `fromWireName returns Unknown for the unknown wire value`() {
        assertEquals(SearchResultType.Unknown, SearchResultType.fromWireName("unknown"))
    }

    @Test
    fun `fromWireName returns null for unknown values`() {
        assertNull(SearchResultType.fromWireName(""))
        assertNull(SearchResultType.fromWireName("Article"))
        assertNull(SearchResultType.fromWireName("DIRECTORYITEM"))
    }

    @Test
    fun `fromWireName returns null for null`() {
        assertNull(SearchResultType.fromWireName(null))
    }
}
