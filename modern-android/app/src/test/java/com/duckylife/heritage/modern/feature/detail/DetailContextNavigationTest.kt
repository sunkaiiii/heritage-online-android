package com.duckylife.heritage.modern.feature.detail

import com.duckylife.heritage.modern.core.network.dto.BlendedRecommendationItemDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DetailContextNavigationTest {

    @Test
    fun `article type maps to Article target`() {
        val target = contextItemTarget("a1", "article")
        assertEquals(DetailContextTarget.Article("a1"), target)
    }

    @Test
    fun `directoryItem type maps to DirectoryItem target`() {
        val target = contextItemTarget("d1", "directoryItem")
        assertEquals(DetailContextTarget.DirectoryItem("d1"), target)
    }

    @Test
    fun `inheritor type maps to Inheritor target`() {
        val target = contextItemTarget("i1", "inheritor")
        assertEquals(DetailContextTarget.Inheritor("i1"), target)
    }

    @Test
    fun `unknown type returns null`() {
        val target = contextItemTarget("x1", "unknown")
        assertNull(target)
    }

    @Test
    fun `null type returns null`() {
        val target = contextItemTarget("a1", null)
        assertNull(target)
    }

    @Test
    fun `blank id returns null`() {
        val target = contextItemTarget("", "article")
        assertNull(target)
    }

    @Test
    fun `null id returns null`() {
        val target = contextItemTarget(null, "article")
        assertNull(target)
    }

    @Test
    fun `blended recommendation item maps to Article target`() {
        val item = BlendedRecommendationItemDto(id = "a1", type = "article", title = "Test")
        val target = item.toDetailContextTarget()
        assertEquals(DetailContextTarget.Article("a1"), target)
    }

    @Test
    fun `blended recommendation item maps to DirectoryItem target`() {
        val item = BlendedRecommendationItemDto(id = "d1", type = "directoryItem", title = "Test")
        val target = item.toDetailContextTarget()
        assertEquals(DetailContextTarget.DirectoryItem("d1"), target)
    }

    @Test
    fun `blended recommendation item maps to Inheritor target`() {
        val item = BlendedRecommendationItemDto(id = "i1", type = "inheritor", title = "Test")
        val target = item.toDetailContextTarget()
        assertEquals(DetailContextTarget.Inheritor("i1"), target)
    }

    @Test
    fun `blended recommendation item with unknown type returns null`() {
        val item = BlendedRecommendationItemDto(id = "x1", type = "unknown", title = "Test")
        val target = item.toDetailContextTarget()
        assertNull(target)
    }

    @Test
    fun `blended recommendation item with blank id returns null`() {
        val item = BlendedRecommendationItemDto(id = "", type = "article", title = "Test")
        val target = item.toDetailContextTarget()
        assertNull(target)
    }
}
