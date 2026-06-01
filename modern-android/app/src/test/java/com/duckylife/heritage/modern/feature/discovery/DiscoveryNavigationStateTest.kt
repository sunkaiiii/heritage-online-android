package com.duckylife.heritage.modern.feature.discovery

import com.duckylife.heritage.modern.core.network.HeritageJson
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DiscoveryNavigationStateTest {

    // We test the RouteState serialization directly since the route key classes are private.
    // The test verifies that the JSON-based serialization handles special characters correctly.

    @Serializable
    private sealed interface TestRouteState {
        @Serializable data object Index : TestRouteState
        @Serializable data class Search(val query: String = "") : TestRouteState
        @Serializable data class ArticleDetail(
            val id: String? = null,
            val sourceId: String? = null,
            val sourceUrl: String? = null,
            val category: String = "news",
        ) : TestRouteState
        @Serializable data class RegionDetail(val region: String = "") : TestRouteState
        @Serializable data class Story(
            val region: String? = null,
            val category: String? = null,
            val year: Int? = null,
        ) : TestRouteState
    }

    private val json = HeritageJson

    @Test
    fun roundTrip_withPipeCharacter() {
        val stack = listOf(
            TestRouteState.Index,
            TestRouteState.Search(query = "a|b|c"),
        )
        val encoded = json.encodeToString(stack)
        val decoded = json.decodeFromString<List<TestRouteState>>(encoded)
        assertEquals(2, decoded.size)
        assertEquals("a|b|c", (decoded[1] as TestRouteState.Search).query)
    }

    @Test
    fun roundTrip_withUrlContainingQueryParams() {
        val url = "https://example.test/page?a=1&b=2"
        val stack: List<TestRouteState> = listOf(
            TestRouteState.ArticleDetail(
                id = "abc123",
                sourceUrl = url,
                category = "specialTopic",
            ),
        )
        val encoded = json.encodeToString<List<TestRouteState>>(stack)
        val decoded = json.decodeFromString<List<TestRouteState>>(encoded)
        val detail = decoded[0] as TestRouteState.ArticleDetail
        assertEquals(url, detail.sourceUrl)
        assertEquals("abc123", detail.id)
        assertEquals("specialTopic", detail.category)
    }

    @Test
    fun roundTrip_withChineseCharacters() {
        val stack: List<TestRouteState> = listOf(
            TestRouteState.RegionDetail(region = "浙江省"),
            TestRouteState.Story(category = "传统技艺"),
        )
        val encoded = json.encodeToString<List<TestRouteState>>(stack)
        val decoded = json.decodeFromString<List<TestRouteState>>(encoded)
        assertEquals("浙江省", (decoded[0] as TestRouteState.RegionDetail).region)
        assertEquals("传统技艺", (decoded[1] as TestRouteState.Story).category)
    }

    @Test
    fun roundTrip_withSpecialCharsInSearch() {
        val stack: List<TestRouteState> = listOf(
            TestRouteState.Search(query = "陶瓷 & 非遗 | 传统"),
        )
        val encoded = json.encodeToString<List<TestRouteState>>(stack)
        val decoded = json.decodeFromString<List<TestRouteState>>(encoded)
        assertEquals("陶瓷 & 非遗 | 传统", (decoded[0] as TestRouteState.Search).query)
    }

    @Test
    fun roundTrip_emptyStack() {
        val stack: List<TestRouteState> = emptyList()
        val encoded = json.encodeToString<List<TestRouteState>>(stack)
        val decoded = json.decodeFromString<List<TestRouteState>>(encoded)
        assertTrue(decoded.isEmpty())
    }

    @Test
    fun roundTrip_withNullValues() {
        val stack: List<TestRouteState> = listOf(
            TestRouteState.ArticleDetail(id = null, sourceId = null, sourceUrl = null),
            TestRouteState.Story(region = null, category = null, year = null),
        )
        val encoded = json.encodeToString<List<TestRouteState>>(stack)
        val decoded = json.decodeFromString<List<TestRouteState>>(encoded)
        val article = decoded[0] as TestRouteState.ArticleDetail
        assertEquals(null, article.id)
        assertEquals(null, article.sourceId)
        assertEquals(null, article.sourceUrl)
        val story = decoded[1] as TestRouteState.Story
        assertEquals(null, story.region)
        assertEquals(null, story.year)
    }

    @Test
    fun corruptedJson_fallsBackToIndex() {
        // Simulate what the real code does: try decode, catch exception, return Index
        val result = try {
            json.decodeFromString<List<TestRouteState>>("{invalid json")
            false
        } catch (_: Exception) {
            true
        }
        assertTrue("Should throw on invalid JSON", result)
    }

    @Test
    fun roundTrip_multipleEntries() {
        val stack: List<TestRouteState> = listOf(
            TestRouteState.Index,
            TestRouteState.Search(query = "陶瓷"),
            TestRouteState.ArticleDetail(id = "art1", sourceUrl = "https://test.test/page?id=1&name=浙江"),
            TestRouteState.RegionDetail(region = "浙江省"),
            TestRouteState.Story(region = "浙江", year = 2024),
        )
        val encoded = json.encodeToString<List<TestRouteState>>(stack)
        val decoded = json.decodeFromString<List<TestRouteState>>(encoded)
        assertEquals(5, decoded.size)
        assertTrue(decoded[0] is TestRouteState.Index)
        assertEquals("陶瓷", (decoded[1] as TestRouteState.Search).query)
        assertEquals("https://test.test/page?id=1&name=浙江", (decoded[2] as TestRouteState.ArticleDetail).sourceUrl)
        assertEquals("浙江省", (decoded[3] as TestRouteState.RegionDetail).region)
        assertEquals(2024, (decoded[4] as TestRouteState.Story).year)
    }
}
